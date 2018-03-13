package com.ck.clump.ui.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by anthony on 4/6/17.
 */

public class GroupModel extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lastMessageTime")
    @Expose
    private long lastMessageTime;
    @SerializedName("lastMessage")
    @Expose
    private String lastMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
