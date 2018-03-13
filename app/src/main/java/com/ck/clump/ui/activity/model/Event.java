package com.ck.clump.ui.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("additionInfo")
    @Expose
    private String additionInfo;
    @SerializedName("startTime")
    @Expose
    private long startTime;
    @SerializedName("startTimeString")
    @Expose
    private String startTimeString;
    @SerializedName("locationAddress")
    @Expose
    private String locationAddress;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("period")
    @Expose
    private String period;
    @SerializedName("createdAt")
    @Expose
    private long createdAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("loginUserStatus")
    @Expose
    private String loginUserStatus;
    @SerializedName("creator")
    @Expose
    private Creator creator;

    public Event(String id, String name, String additionInfo, long startTime, String startTimeString, String locationAddress, String type, String period, long createdAt, String status, double latitude, double longitude, String loginUserStatus, Creator creator) {
        this.id = id;
        this.name = name;
        this.additionInfo = additionInfo;
        this.startTime = startTime;
        this.startTimeString = startTimeString;
        this.locationAddress = locationAddress;
        this.type = type;
        this.period = period;
        this.createdAt = createdAt;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.loginUserStatus = loginUserStatus;
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLoginUserStatus() {
        return loginUserStatus;
    }

    public void setLoginUserStatus(String loginUserStatus) {
        this.loginUserStatus = loginUserStatus;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public static Event fromEventLocal(EventLocal eventLocal) {
        Creator creator = new Creator(eventLocal.getCreatorID(), eventLocal.getCreatorDisplayName(), eventLocal.getCreatorAvatarPath());
        return new Event(eventLocal.getId(), eventLocal.getName(), eventLocal.getAdditionInfo(), eventLocal.getStartTime(), eventLocal.getStartTimeString(),
                eventLocal.getLocationAddress(), eventLocal.getType(), eventLocal.getPeriod(), eventLocal.getCreatedAt(), eventLocal.getStatus(),
                eventLocal.getLatitude(), eventLocal.getLongitude(), eventLocal.getLoginUserStatus(), creator);
    }

    public static class Creator {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("displayName")
        @Expose
        private String displayName;
        @SerializedName("avatarPath")
        @Expose
        private String avatarPath;

        public Creator(String id, String displayName, String avatarPath) {
            this.id = id;
            this.displayName = displayName;
            this.avatarPath = avatarPath;
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
    }
}
