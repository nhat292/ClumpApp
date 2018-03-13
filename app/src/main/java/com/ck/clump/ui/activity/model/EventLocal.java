package com.ck.clump.ui.activity.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhat on 7/19/17.
 */

public class EventLocal extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String additionInfo;
    private long startTime;
    private String startTimeString;
    private String locationAddress;
    private String type;
    private String period;
    private long createdAt;
    private String status;
    private double latitude;
    private double longitude;
    private String loginUserStatus;
    private String creatorID;
    private String creatorDisplayName;
    private String creatorAvatarPath;

    public EventLocal() {
    }

    public EventLocal(String id, String name, String additionInfo, long startTime, String startTimeString, String locationAddress, String type, String period, long createdAt, String status, double latitude, double longitude, String loginUserStatus, String creatorID, String creatorDisplayName, String creatorAvatarPath) {
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
        this.creatorID = creatorID;
        this.creatorDisplayName = creatorDisplayName;
        this.creatorAvatarPath = creatorAvatarPath;
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

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorDisplayName() {
        return creatorDisplayName;
    }

    public void setCreatorDisplayName(String creatorDisplayName) {
        this.creatorDisplayName = creatorDisplayName;
    }

    public String getCreatorAvatarPath() {
        return creatorAvatarPath;
    }

    public void setCreatorAvatarPath(String creatorAvatarPath) {
        this.creatorAvatarPath = creatorAvatarPath;
    }

    public static EventLocal fromEvent(Event event) {
        return new EventLocal(event.getId(), event.getName(), event.getAdditionInfo(), event.getStartTime(), event.getStartTimeString(),
                event.getLocationAddress(), event.getType(), event.getPeriod(), event.getCreatedAt(), event.getStatus(), event.getLatitude(),
                event.getLongitude(), event.getLoginUserStatus(), event.getCreator().getId(), event.getCreator().getDisplayName(), event.getCreator().getAvatarPath());
    }
}
