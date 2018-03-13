package com.ck.clump.service.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ck.clump.App;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.enums.ChatDisplayType;
import com.ck.clump.enums.ChatType;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.ui.activity.MainActivity;
import com.ck.clump.ui.activity.model.ChatMessage;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.SharedPreference;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            sendNotification(intent.getExtras());
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) {
        if (extras == null) return;
        Log.d("GCM-notif", extras.toString());
        final String content = extras.getString("content");
        final String channelId = extras.getString("channel_id");
        final ChatMessage chatMessage = ChatMessage.toChatMessage(content, channelId);
        if (chatMessage.getUserName().equals("DELETE") && chatMessage.getUserAvatarUrl().equals("DELETE") && chatMessage.getUserId().equals("DELETE"))
            return;
        String currentActivity = SharedPreference.getString(SharedPreference.KEY_CURRENT_VIEW);
        if (currentActivity != null) { //App is foreground
            if (currentActivity.equals(Constant.CHAT_ACTIVITY)) { // Chat Activity is top
                String currentChatChannelID = SharedPreference.getString(SharedPreference.CURRENT_CHAT_CHANNEL_ID);
                if (!currentChatChannelID.equals(channelId)) {
                    //Save message and show app notification
                    saveChat(chatMessage, true, content);
                }
            } else {
                //Save message and show app notification
                saveChat(chatMessage, true, content);
                boolean isChatActivityAlive = SharedPreference.getBool(SharedPreference.KEY_CHAT_ACTIVITY_ALIVE, false);
                if (isChatActivityAlive) {
                    SharedPreference.saveBool(SharedPreference.KEY_REFRESH_CHAT_WHEN_RESUME, true);
                }
            }
        } else {
            saveChat(chatMessage, false, content);
            if (!chatMessage.isRead() && chatMessage.getStatus().equals("message")) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Realm.getInstance(App.getRealmConfig()).executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                final GroupModel groupModel = Realm.getInstance(App.getRealmConfig()).where(GroupModel.class)
                                        .equalTo("channelId", chatMessage.getChannelId())
                                        .findFirst();
                                String endUrl = groupModel != null ? groupModel.getImagePath() : chatMessage.getUserAvatarUrl();

                                Glide.with(App.getInstance())
                                        .load(BuildConfig.BASEURL + endUrl)
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                                if (drawable != null) {
                                                    Bitmap bitmap = Common.drawableToBitmap(drawable);
                                                    showNotification(chatMessage, Common.getCircularBitmap(bitmap));
                                                } else {
                                                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                                            groupModel != null ? R.drawable.default_profile_group : R.drawable.default_profile_user);
                                                    showNotification(chatMessage, Common.getCircularBitmap(icon));
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }
    }

    private void showNotification(ChatMessage chatMessage, Bitmap icon) {
        String notificationBigTex = chatMessage.getUserName();
        String message = getMessageChat(chatMessage);
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = Common.getNotificationIDFromChannelID(chatMessage.getChannelId());
        //int numberOfMessage = SharedPreference.getInt(SharedPreference.KEY_NUMBER_MESSAGE_CHANNEL + "-" + chatMessage.getChannelId(), 0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("channelID", chatMessage.getChannelId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(icon)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(notificationBigTex)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationBigTex))
                        .setContentText(message)
                        //.setNumber(++numberOfMessage)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(getResources().getColor(R.color.colorNotification));
        }
        Notification pnNotif = mBuilder.build();
        mNotificationManager.notify(notificationID, pnNotif);
        //SharedPreference.saveInt(SharedPreference.KEY_NUMBER_MESSAGE_CHANNEL + "-" + chatMessage.getChannelId(), numberOfMessage);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification_color : R.drawable.ic_notification;
    }

    private void informNotificationInApp(ChatMessage chatMessage) {
        String message = getMessageChat(chatMessage);
        EventBus.getDefault().post(new ChatMessageEvent(chatMessage.getUserName(), message));
    }

    private String getMessageChat(ChatMessage chatMessage) {
        String message = "";
        if (chatMessage.getType() == ChatType.TEXT.getValue()) {
            message = Common.getChatContent(chatMessage.getContent());
            int type = SharedPreference.getInt(SharedPreference.KEY_CHAT_SHOW, ChatDisplayType.FULL.getValue());
            if (type == ChatDisplayType.SECRET.getValue()) {
                message = getString(R.string.has_sent_a_message);
            }
        } else if (chatMessage.getType() == ChatType.IMAGE.getValue()) {
            message = getString(R.string.has_sent_a_image);
        } else if (chatMessage.getType() == ChatType.STICKER.getValue()) {
            message = getString(R.string.has_sent_a_sticker);
        }
        return message;
    }

    private void saveChat(final ChatMessage chat, boolean isAppForeground, String originalContent) {
        if (chat.isRead()) { //Update read message
            if (chat.getUserId().equals(UserModel.currentUser().getId())) {
                Realm.getInstance(App.getRealmConfig()).executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ChatMessage chatMessage = Realm.getInstance(App.getRealmConfig()).where(ChatMessage.class)
                                .equalTo("channelId", chat.getChannelId())
                                .equalTo("chatId", chat.getChatId())
                                .findFirst();
                        if (chatMessage != null) {
                            chatMessage.setRead(true);
                            realm.insertOrUpdate(chatMessage);
                        }
                    }
                });
            }
        } else {
            if (chat.getUserId().equals(UserModel.currentUser().getId())) return;
            if (chat.getStatus().equals("message")) { //New Message
                if (isAppForeground) { //Send event to show popup message activity
                    informNotificationInApp(chat);
                }
                Common.playNotificationSound(getApplicationContext());
                //Message unread count
                SharedPreference.saveInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + chat.getChannelId(),
                        SharedPreference.getInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + chat.getChannelId(), 0) + 1);
                //Save last message for update read when open chat
                SharedPreference.saveString(SharedPreference.KEY_CHAT_LAST_MESSAGE_CONTENT + "-" + chat.getChannelId(), originalContent);
                Realm.getInstance(App.getRealmConfig()).executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.createObjectFromJson(ChatMessage.class, new Gson().toJson(chat));
                    }
                });
            } else { //Update Delete message
                Realm.getInstance(App.getRealmConfig()).executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        List<ChatMessage> chats = Realm.getInstance(App.getRealmConfig()).where(ChatMessage.class)
                                .equalTo("channelId", chat.getChannelId())
                                .equalTo("chatId", chat.getChatId())
                                .findAll();
                        for (ChatMessage chat : chats) {
                            chat.setType(ChatType.TEXT.getValue());
                            chat.setContent(getString(R.string.message_chat_removed));
                        }
                        realm.insertOrUpdate(chats);
                    }
                });
            }
        }
    }
}
