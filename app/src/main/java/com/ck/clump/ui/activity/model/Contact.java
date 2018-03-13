package com.ck.clump.ui.activity.model;

public class Contact {
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_GROUP = 2;

    public String id;
    public String avatar;
    public String name;
    public int type;
    public boolean isPick;

    public Contact(String id, String avatar, String name, int type) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPick() {
        return isPick;
    }

    public void setPick(boolean pick) {
        isPick = pick;
    }
}
