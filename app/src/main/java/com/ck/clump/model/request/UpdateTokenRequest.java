package com.ck.clump.model.request;

/**
 * Created by anthony on 4/6/17.
 */

public class UpdateTokenRequest {
    private String deviceToken;
    private String deviceType;

    public UpdateTokenRequest(String deviceToken, String deviceType) {
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
    }
}
