package com.ck.clump.enums;

/**
 * Created by Nhat on 6/8/17.
 */

public enum Period {
    MORNING("MORNING"),
    EVENING("EVENING"),
    NIGHT("NIGHT");

    private String value;

    Period(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
