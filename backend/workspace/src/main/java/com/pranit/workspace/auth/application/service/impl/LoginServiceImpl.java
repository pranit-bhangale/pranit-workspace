package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.RefreshTokenRepository;
import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.LoginService;
import com.pranit.workspace.auth.constant.Account;
import com.pranit.workspace.auth.constant.OTT;
import com.pranit.workspace.auth.domain.dto.LoginRequest;
import com.pranit.workspace.auth.domain.dto.LoginResponse;
import com.pranit.workspace.auth.domain.dto.MfaTokenRequest;
import com.pranit.workspace.auth.domain.dto.UserResponse;
import com.pranit.workspace.auth.domain.entity.RefreshToken;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.model.UserDetail;
import com.pranit.workspace.auth.exception.UnauthorizedException;
import com.pranit.workspace.auth.properties.TokenProperties;
import com.pranit.workspace.auth.properties.UrlProperties;
import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.auth.security.service.CookieService;
import com.pranit.workspace.auth.security.service.GenerateToken;
import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.EmailRouter;
import com.pranit.workspace.notification.api.MfaEvent;
import com.pranit.workspace.shared.helper.Generate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final OneTimeTokenService oneTimeTokenService;
    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GenerateToken generateToken;
    private final CookieService cookieService;
    private final UserRepository userRepository;
    private final EmailRouter emailRouter;
    private final UrlProperties properties;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LoginResponse authenticateUser
            (final LoginRequest request, final HttpServletRequest httpRequest) {
        final Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    ));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!(authentication.getPrincipal() instanceof UserDetail userDetail)) {
            throw new UnauthorizedException("Invalid authentication principal");
        }
        if (!hasAuthority(authentication, FactorGrantedAuthority.PASSWORD_AUTHORITY)) {
            throw new UnauthorizedException("Password factor was not authenticated");
        }
        final HttpSession session = httpRequest.getSession(true);
        session.setMaxInactiveInterval((int) Account.MFA_TTL.toSeconds());
        session.setAttribute(Account.MFA_USER_ID, userDetail.userId());
        session.setAttribute(Account.MFA_USERNAME, userDetail.username());
        final OneTimeToken ott = this.oneTimeTokenService.generate
                (new GenerateOneTimeTokenRequest(userDetail.username(), Account.MFA_TTL));
        final String magicLink = UriComponentsBuilder
                .fromUriString(properties.url())
                .path(properties.path())
                .queryParam(OTT.TOKEN, ott.getTokenValue())
                .build()
                .toUriString();
        final MfaEvent event = MfaEvent.builder()
                .eventId(Generate.eventId())
                .ott(magicLink)
                .email(userDetail.email())
                .username(userDetail.username())
                .expiresAt(Account.MFA_TTL)
                .build();
        this.emailRouter.send(event, EmailPurpose.OTT);
        return LoginResponse.builder()
                .mfaRequired(true)
                .username(userDetail.username())
                .roles(null)
                .message("Multi Factor Authentication is Required")
                .build();
    }

    private boolean hasAuthority(final Authentication authentication, final String required) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        Objects.equals(required, authority.getAuthority()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public LoginResponse verifyMfa
            (final MfaTokenRequest request, final HttpServletRequest httpRequest, final HttpServletResponse response) {
        final HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new UnauthorizedException("MFA session expired");
        }
        final String sessionUsername = (String) session.getAttribute(Account.MFA_USERNAME);
        final UUID userId = (UUID) session.getAttribute(Account.MFA_USER_ID);
        if (sessionUsername == null || userId == null) {
            throw new UnauthorizedException("Invalid MFA session");
        }
        final Authentication ottAuthentication;
        try {
            ottAuthentication = this.authenticationManager.authenticate
                    (new OneTimeTokenAuthenticationToken(request.mfaToken()));
            SecurityContextHolder.getContext().setAuthentication(ottAuthentication);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid or expired one-time token: " + ex);
        }
        if (!(ottAuthentication.getPrincipal() instanceof UserDetail userDetail)) {
            throw new UnauthorizedException("Invalid authentication principal");
        }
        if (!sessionUsername.equals(ottAuthentication.getName())) {
            throw new UnauthorizedException("MFA user mismatch");
        }
        if (!hasAuthority(ottAuthentication, FactorGrantedAuthority.OTT_AUTHORITY)) {
            throw new UnauthorizedException("OTT factor was not authenticated");
        }
        final LoginResponse loginResponse = createAuthenticatedSession(userDetail, response);
        session.invalidate();
        final Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return loginResponse;
    }

    private LoginResponse createAuthenticatedSession
            (final UserDetail userDetail, final HttpServletResponse response) {
        final Map<Boolean, Set<String>> grouped = userDetail.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.partitioningBy(
                        a -> a.startsWith("ROLE_"),
                        Collectors.toUnmodifiableSet()));
        final Set<String> roles = grouped.get(true);
        final String jti = Generate.jti();
        final Instant now = Instant.now();
        final Duration accessTokenTtl = Duration.ofSeconds(this.tokenProperties.accessToken().expiration());
        final Duration refreshTokenTtl = Duration.ofSeconds(this.tokenProperties.refreshToken().expiration());
        final User user = this.userRepository.getReferenceById(userDetail.userId());
        final RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .expiresAt(now.plus(refreshTokenTtl))
                .revoked(false)
                .build();
        this.refreshTokenRepository.save(rt);
        final String accessToken = this.generateToken.generateAccessToken(userDetail, jti);
        final String refreshToken = this.generateToken.generateRefreshToken(userDetail, jti);
        this.cookieService.attachAccessTokenCookie(response, accessToken, accessTokenTtl);
        this.cookieService.attachRefreshTokenCookie(response, refreshToken, refreshTokenTtl);
        this.cookieService.addNoStoreHeaders(response);
        return LoginResponse.builder()
                .mfaRequired(null)
                .username(userDetail.username())
                .roles(roles)
                .message("User is successfully authenticated")
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        final UserDetail principal = SecurityContext.getUserDetail();
        return UserResponse.builder()
                .userId(principal.userId())
                .username(principal.username())
                .email(principal.email())
                .enabled(principal.enabled())
                .fullName(principal.fullName())
                .status(principal.status())
                .build();
    }
}
