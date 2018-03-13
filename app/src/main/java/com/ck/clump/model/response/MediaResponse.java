package com.ck.clump.model.response;

import com.ck.clump.model.MediaGroupModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 5/14/17.
 */

public class MediaResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private MediaGroupModel dATA;

    public MediaResponse(int cODE, String mESSAGE, MediaGroupModel dATA) {
        this.cODE = cODE;
        this.mESSAGE = mESSAGE;
        this.dATA = dATA;
    }

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

    public MediaGroupModel getdATA() {
        return dATA;
    }

    public void setdATA(MediaGroupModel dATA) {
        this.dATA = dATA;
    }
}
