package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.EventModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 6/3/17.
 */

public class EventDetailResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private EventModel dATA;

    public EventDetailResponse(int cODE, String mESSAGE, EventModel dATA) {
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

    public EventModel getdATA() {
        return dATA;
    }

    public void setdATA(EventModel dATA) {
        this.dATA = dATA;
    }
}
