package com.ck.clump.model;

import com.ck.clump.ui.activity.model.Media;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 5/14/17.
 */

public class MediaGroupModel {
    @SerializedName("totalImage")
    @Expose
    private int totalImage;
    @SerializedName("images")
    @Expose
    private List<Media> medias;

    public MediaGroupModel(int totalImage, List<Media> medias) {
        this.totalImage = totalImage;
        this.medias = medias;
    }

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }
}
