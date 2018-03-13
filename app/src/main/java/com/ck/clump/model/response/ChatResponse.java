package com.ck.clump.model.response;

import com.ck.clump.model.UserGroupModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anthony on 4/22/17.
 */

public class ChatResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private UserGroupModel dATA;

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

    public UserGroupModel getDATA() {
        return dATA;
    }

    public void setDATA(UserGroupModel dATA) {
        this.dATA = dATA;
    }
}
