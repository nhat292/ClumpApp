package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 6/9/17.
 */

public class EventResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private List<Event> dATA;

    public EventResponse(int cODE, String mESSAGE, List<Event> dATA) {
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

    public List<Event> getdATA() {
        return dATA;
    }

    public void setdATA(List<Event> dATA) {
        this.dATA = dATA;
    }
}
