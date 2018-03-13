package com.ck.clump.enums;

/**
 * Created by Nhat on 6/8/17.
 */

public enum EventStatus {
    UPCOMING("UPCOMING"),
    STARTED("STARTED"),
    PAST("PAST");

    private String value;
    EventStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
