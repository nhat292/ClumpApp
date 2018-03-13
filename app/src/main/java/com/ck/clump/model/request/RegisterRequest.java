package com.ck.clump.model.request;

/**
 * Created by anthony on 3/26/17.
 */

public class RegisterRequest {
    private String phone;
    private String countryCode;

    public RegisterRequest(String phone, String countryCode) {
        this.phone = phone;
        this.countryCode = countryCode;
    }
}
