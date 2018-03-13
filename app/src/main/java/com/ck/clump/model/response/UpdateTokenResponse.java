package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.UpdateTokenModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anthony on 4/6/17.
 */

public class UpdateTokenResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private UpdateTokenModel dATA;

    public int getCODE() {
        return cODE;
    }

    public void setCODE(int cODE) {
        this.cODE = cODE;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public UpdateTokenModel getDATA() {
        return dATA;
    }

    public void setDATA(UpdateTokenModel dATA) {
        this.dATA = dATA;
    }

}
