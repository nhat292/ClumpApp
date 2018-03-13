package com.ck.clump.ui.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nhat on 7/17/17.
 */

public class ImageFile implements Parcelable {

    private String path;
    private boolean sent;

    public ImageFile(String path, boolean sent) {
        this.path = path;
        this.sent = sent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public ImageFile(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        this.path = data[0];
        this.sent = Boolean.parseBoolean(data[1]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.path,
                String.valueOf(this.sent)
        });
    }

    public static final Parcelable.Creator<ImageFile> CREATOR = new Parcelable.Creator<ImageFile>() {
        @Override
        public ImageFile createFromParcel(Parcel source) {
            return new ImageFile(source);
        }

        @Override
        public ImageFile[] newArray(int size) {
            return new ImageFile[size];
        }
    };

}
