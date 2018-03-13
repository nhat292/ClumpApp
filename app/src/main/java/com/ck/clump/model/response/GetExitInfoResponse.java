package com.ck.clump.model.response;

import com.ck.clump.ui.activity.model.Exit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 9/25/17.
 */

public class GetExitInfoResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private List<Exit> dATA;

    public GetExitInfoResponse(int cODE, String mESSAGE, List<Exit> dATA) {
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

    public List<Exit> getdATA() {
        return dATA;
    }

    public void setdATA(List<Exit> dATA) {
        this.dATA = dATA;
    }
}
