package com.ck.clump.model.event;

/**
 * Created by Nhat on 9/15/17.
 */

public class ChatMessageEvent {
    private String name;
    private String content;

    public ChatMessageEvent(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
