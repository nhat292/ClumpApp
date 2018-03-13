package com.ck.clump.model.request;

import java.io.File;

public class UserRequest {
    private String displayName;
    private File avatar;
    private String status;

    public UserRequest(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public File getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
