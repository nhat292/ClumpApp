package com.ck.clump.model.response;

import com.ck.clump.model.CreateGroupModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 7/18/17.
 */

public class CreateGroupResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private CreateGroupModel dATA;

    public CreateGroupResponse(int cODE, String mESSAGE, CreateGroupModel dATA) {
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

    public CreateGroupModel getdATA() {
        return dATA;
    }

    public void setdATA(CreateGroupModel dATA) {
        this.dATA = dATA;
    }
}
