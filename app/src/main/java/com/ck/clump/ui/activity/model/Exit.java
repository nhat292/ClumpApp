package com.ck.clump.ui.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 9/25/17.
 */

public class Exit {
    @Expose
    @SerializedName("userId")
    private String userId;
    @Expose
    @SerializedName("channelId")
    private String channelId;
    @Expose
    @SerializedName("leaveDate")
    private double leaveDate;
    @Expose
    @SerializedName("rejoinDate")
    private double rejoinDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public double getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(double leaveDate) {
        this.leaveDate = leaveDate;
    }

    public double getRejoinDate() {
        return rejoinDate;
    }

    public void setRejoinDate(double rejoinDate) {
        this.rejoinDate = rejoinDate;
    }
}
