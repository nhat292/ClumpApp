package com.ck.clump.ui.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("avatarPath")
    @Expose
    private String avatarPath;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("isCreator")
    @Expose
    private boolean isCreator;

    private boolean checked = false;

    public Friend(String id, String avatarPath, String displayName, boolean isCreator) {
        this.id = id;
        this.avatarPath = avatarPath;
        this.displayName = displayName;
        this.isCreator = isCreator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Friend(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.id = data[0];
        this.avatarPath = data[1];
        this.displayName = data[2];
        this.isCreator = Boolean.parseBoolean(data[3]);
        this.checked = Boolean.parseBoolean(data[4]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.id,
                this.avatarPath,
                this.displayName,
                String.valueOf(this.isCreator),
                String.valueOf(this.checked)
        });
    }

    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {

        @Override
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
