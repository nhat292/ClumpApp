package com.ck.clump.model.request;

public class ChannelRequest {
    private String userId;

    public ChannelRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
