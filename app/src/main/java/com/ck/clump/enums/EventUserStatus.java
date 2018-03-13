package com.ck.clump.enums;

/**
 * Created by Nhat on 6/8/17.
 */

public enum EventUserStatus {
    COUNT_IN("COUNT_IN"),
    COUNT_OUT("COUNT_OUT"),
    NOT_YET("NOT_YET"),
    OWNER("OWNER");

    private String value;

    EventUserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getDisplay(){
        String display = "";
        switch (this){
            case COUNT_IN:
                display = "I'm In";
                break;
            case COUNT_OUT:
                display = "I'm Out";
                break;
            case OWNER:
                display = "I'm In";
                break;
            case NOT_YET:
                display = "RSVP";
                break;
        }
        return display;
    }
}
