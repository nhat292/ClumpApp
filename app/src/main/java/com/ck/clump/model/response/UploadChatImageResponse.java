package com.ck.clump.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anthony on 3/26/17.
 */

public class UploadChatImageResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private ImageUpload dATA;

    public int getcODE() {
        return cODE;
    }

    public void setcODE(int cODE) {
        this.cODE = cODE;
    }

    public String getmESSAGE() {
        return mESSAGE;
    }

    public void setmESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public ImageUpload getdATA() {
        return dATA;
    }

    public void setdATA(ImageUpload dATA) {
        this.dATA = dATA;
    }

    public class ImageUpload {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("path")
        @Expose
        private String path;
        @SerializedName("thumbnail_path")
        @Expose
        private String thumbnail_path;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getThumbnail_path() {
            return thumbnail_path;
        }

        public void setThumbnail_path(String thumbnail_path) {
            this.thumbnail_path = thumbnail_path;
        }
    }
}
