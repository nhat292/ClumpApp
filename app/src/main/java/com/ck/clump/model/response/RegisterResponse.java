package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.RegisterModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anthony on 3/26/17.
 */

public class RegisterResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private RegisterModel dATA;

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

    public RegisterModel getDATA() {
        return dATA;
    }

    public void setDATA(RegisterModel dATA) {
        this.dATA = dATA;
    }

}