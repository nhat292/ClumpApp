package com.ck.clump.ui.activity.model;

import io.realm.RealmObject;

public class ChatMessage extends RealmObject {
    private String channelId;
    private boolean left;
    private int type;
    private String content;
    private long timeSend;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private String chatId;
    private String status; //message or delete
    private boolean read;

    public ChatMessage() {
        super();
    }

    public ChatMessage(String channelId, boolean left, int type, String content, long timeSend, String userId, String userName, String userAvatarUrl, String chatId, String status, boolean read) {
        this.channelId = channelId;
        this.left = left;
        this.type = type;
        this.content = content;
        this.timeSend = timeSend;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.chatId = chatId;
        this.status = status;
        this.read = read;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public static ChatMessage toChatMessage(String data, String channelId) {
        String[] dataSplit = data.split("@clump@");
        int type = Integer.parseInt(dataSplit[0]);
        String content = dataSplit[1];
        long timeSend = Long.parseLong(dataSplit[2]);
        String userId = dataSplit[3];
        String userName = dataSplit[4];
        String userAvatarUrl = dataSplit.length > 5 ? dataSplit[5] : "";
        String chatId = dataSplit.length > 6 ? dataSplit[6] : "empty";
        String status = dataSplit.length > 7 ? dataSplit[7] : "message";
        boolean left = !UserModel.currentUser().getId().equals(userId);
        boolean read = dataSplit.length > 8 ? dataSplit[8].equals("read") : false;
        return new ChatMessage(channelId, left, type, content, timeSend, userId, userName, userAvatarUrl, chatId, status, read);
    }
}
