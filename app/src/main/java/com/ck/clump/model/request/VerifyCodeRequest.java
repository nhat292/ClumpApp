package com.ck.clump.model.request;

/**
 * Created by anthony on 3/26/17.
 */

public class VerifyCodeRequest {
    private String phone;
    private String activeCode;

    public VerifyCodeRequest(String phone, String activeCode) {
        this.phone = phone;
        this.activeCode = activeCode;
    }
}
