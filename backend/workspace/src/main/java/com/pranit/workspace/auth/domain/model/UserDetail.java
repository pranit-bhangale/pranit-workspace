package com.pranit.workspace.auth.domain.model;

import com.pranit.workspace.auth.domain.entity.Role;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.enums.AccountStatus;
import com.pranit.workspace.shared.api.CurrentUser;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Builder
public record UserDetail(
        UUID userId,
        String username,
        String fullName,
        String email,
        String password,
        AccountStatus status,
        Collection<? extends GrantedAuthority> authorities,
        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired
) implements UserDetails, CurrentUser {

    public static UserDetail from(final User user) {
        final List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .<GrantedAuthority>map(name -> new SimpleGrantedAuthority("ROLE_" + name))
                .distinct()
                .toList();
        return UserDetail.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getProfile().getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .enabled(user.isEnabled())
                .status(user.getStatus())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    @Override
    public @NullMarked Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public @NullMarked String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
