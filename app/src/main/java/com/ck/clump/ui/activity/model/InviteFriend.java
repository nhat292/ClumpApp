package com.ck.clump.ui.activity.model;

public class InviteFriend {

    public static final int TYPE_UNSELECTED = 1;
    public static final int TYPE_SELECTED = 2;
    public static final int TYPE_CLUMP = 3;

    private String phone;
    private String name;
    private int type;

    public InviteFriend(String phone, String name, int type) {
        this.phone = phone;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
