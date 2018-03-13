package com.ck.clump.ui.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Audit {
    @SerializedName("id")
    @Expose
    private String id;
    @Expose
    @SerializedName("eventName")
    private String eventName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("avatarPath")
    @Expose
    private String avatarPath;
    @SerializedName("pointOfTime")
    @Expose
    private String pointOfTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getPointOfTime() {
        return pointOfTime;
    }

    public void setPointOfTime(String pointOfTime) {
        this.pointOfTime = pointOfTime;
    }
}
