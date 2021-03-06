package com.ck.clump.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nhat on 5/21/17.
 */

public class BlockingResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private Data dATA;

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

    public Data getdATA() {
        return dATA;
    }

    public void setdATA(Data dATA) {
        this.dATA = dATA;
    }

    public static class Data {
        @SerializedName("users")
        @Expose
        private List<BlockingUser> users;

        public List<BlockingUser> getUsers() {
            return users;
        }

        public void setUsers(List<BlockingUser> users) {
            this.users = users;
        }
    }

    public static class BlockingUser {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("displayName")
        @Expose
        private String displayName;
        @SerializedName("avatarPath")
        @Expose
        private String avatarPath;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("backgroundPath")
        @Expose
        private String backgroundPath;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBackgroundPath() {
            return backgroundPath;
        }

        public void setBackgroundPath(String backgroundPath) {
            this.backgroundPath = backgroundPath;
        }
    }
}
