package com.ck.clump.enums;

/**
 * Created by Nhat on 7/19/17.
 */

public enum ChatDisplayType {
    FULL(1),
    SECRET(2);
    private final int value;
    ChatDisplayType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
