package com.ck.clump.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nhat on 5/21/17.
 */

public class ChannelResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private Channel dATA;

    public ChannelResponse(int cODE, String mESSAGE, Channel dATA) {
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

    public Channel getdATA() {
        return dATA;
    }

    public void setdATA(Channel dATA) {
        this.dATA = dATA;
    }

    public static class Channel {
        @SerializedName("channelId")
        @Expose
        private String channelId;

        public Channel(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }
    }
}
