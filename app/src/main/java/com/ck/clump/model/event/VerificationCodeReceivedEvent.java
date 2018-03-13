package com.ck.clump.model.event;

public class VerificationCodeReceivedEvent {
    private String verificationCode;

    public VerificationCodeReceivedEvent(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
