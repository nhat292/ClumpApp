package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.EventModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 7/18/17.
 */

public class IncomingEventResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private List<EventModel> dATA;

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

    public List<EventModel> getdATA() {
        return dATA;
    }

    public void setdATA(List<EventModel> dATA) {
        this.dATA = dATA;
    }
}
