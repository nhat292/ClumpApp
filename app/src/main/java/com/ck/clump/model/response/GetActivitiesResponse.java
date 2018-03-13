package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.Audit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 5/18/17.
 */

public class GetActivitiesResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private List<Audit> dATA;

    public GetActivitiesResponse(int cODE, String mESSAGE, List<Audit> dATA) {
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

    public List<Audit> getdATA() {
        return dATA;
    }

    public void setdATA(List<Audit> dATA) {
        this.dATA = dATA;
    }
}
