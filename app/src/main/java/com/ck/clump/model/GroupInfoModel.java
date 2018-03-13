package com.ck.clump.model;

import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.model.Media;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 5/17/17.
 */

public class GroupInfoModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("memberList")
    @Expose
    private List<Friend> members;
    @SerializedName("images")
    @Expose
    private List<Media> images;
    @SerializedName("totalImage")
    @Expose
    private int totalImage;

    public GroupInfoModel(String id, String name, String createdAt, String imagePath, List<Friend> members, List<Media> images, int totalImage) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
        this.members = members;
        this.images = images;
        this.totalImage = totalImage;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Friend> getMembers() {
        return members;
    }

    public void setMembers(List<Friend> members) {
        this.members = members;
    }

    public List<Media> getImages() {
        return images;
    }

    public void setImages(List<Media> images) {
        this.images = images;
    }

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }
}
