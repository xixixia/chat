package com.mallchat.register.service;

/** TODO: update docs. */
public interface VerificationCodeService {
    /** TODO: update docs. */
    void saveSmsCode(String phone, String code);

    /** TODO: update docs. */
    boolean verifySmsCode(String phone, String code);

    /** TODO: update docs. */
    void saveEmailCode(String email, String code);

    /** TODO: update docs. */
    boolean verifyEmailCode(String email, String code);
}
