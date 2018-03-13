package com.ck.clump.ui.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Chat implements Parcelable {
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_GROUP = 2;

    private String id; // UserId or GroupId
    private String chanelId;
    private String avatar;
    private String name;
    private String status;
    private String lastMessage;
    private Date date;
    private int type;
    private int amount = 0;

    private ChatMessage message;

    //Swipe
    private boolean open;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        long dateLong = 0;
        if (date != null) {
            dateLong = date.getTime();
        }
        dest.writeStringArray(new String[]{
                id,
                chanelId,
                avatar,
                name,
                status,
                lastMessage,
                String.valueOf(dateLong),
                String.valueOf(type),
                String.valueOf(amount)
        });
    }

    private Chat(Parcel in){
        String[] data = new String[9];
        in.readStringArray(data);
        this.id = data[0];
        this.chanelId =  data[1];
        this.avatar =  data[2];
        this.name =  data[3];
        this.status =  data[4];
        this.lastMessage =  data[5];
        this.date = new Date(Long.parseLong(data[6]));
        this.type = Integer.parseInt(data[7]);
        this.amount = Integer.parseInt(data[8]);
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {

        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public Chat() {
    }

    public Chat(String id, String chanelId, String avatar, String name, int type, String des1, String des2, Date date) {
        this.id = id;
        this.chanelId = chanelId;
        this.avatar = avatar;
        this.name = name;
        this.status = des1;
        this.lastMessage = des2;
        this.date = date;
        this.type = type;
    }

    public Chat(String id, String chanelId, String avatar, String name, int type, String des1, String des2, Date date, int amount) {
        this.id = id;
        this.chanelId = chanelId;
        this.avatar = avatar;
        this.name = name;
        this.status = des1;
        this.lastMessage = des2;
        this.date = date;
        this.type = type;
        this.amount = amount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getChanelId() {
        return chanelId;
    }

    public void setChanelId(String chanelId) {
        this.chanelId = chanelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
