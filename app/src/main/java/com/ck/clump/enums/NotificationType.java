package com.ck.clump.enums;

/**
 * Created by Nhat on 7/19/17.
 */

public enum NotificationType {
    SOUND(1),
    VIBRATE(2),
    MUTE(3);
    private final int value;
    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
