package com.ck.clump.model.response;

import com.ck.clump.model.GroupInfoModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 5/17/17.
 */

public class GroupInfoResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private GroupInfoModel dATA;

    public GroupInfoResponse(int cODE, String mESSAGE, GroupInfoModel dATA) {
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

    public GroupInfoModel getdATA() {
        return dATA;
    }

    public void setdATA(GroupInfoModel dATA) {
        this.dATA = dATA;
    }
}
