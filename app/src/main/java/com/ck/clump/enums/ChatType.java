package com.ck.clump.enums;

/**
 * Created by Nhat on 6/30/17.
 */

public enum ChatType {
    TEXT(0),
    STICKER(1),
    IMAGE(2);
    private int value;
    ChatType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
