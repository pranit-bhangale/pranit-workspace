package com.pranit.workspace.notification.api;


public interface OtpService {

    String sendOtp(String email, EmailPurpose purpose);

    String verifyOtp(String challengeId, String otp, EmailPurpose purpose);
}
