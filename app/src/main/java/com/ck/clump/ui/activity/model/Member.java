package com.ck.clump.ui.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("avatarPath")
    @Expose
    private String avatarPath;
    @SerializedName("eventConfirm")
    @Expose
    private String eventConfirm;
    @SerializedName("isCreator")
    @Expose
    private boolean isCreator;
    @SerializedName("phone")
    @Expose
    private String phone;

    private int plusCount;

    public Member(String id, String displayName, String avatarPath, String eventConfirm, boolean isCreator, String phone) {
        this.id = id;
        this.displayName = displayName;
        this.avatarPath = avatarPath;
        this.eventConfirm = eventConfirm;
        this.isCreator = isCreator;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getEventConfirm() {
        return eventConfirm;
    }

    public void setEventConfirm(String eventConfirm) {
        this.eventConfirm = eventConfirm;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPlusCount() {
        return plusCount;
    }

    public void setPlusCount(int plusCount) {
        this.plusCount = plusCount;
    }
}
