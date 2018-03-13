package com.ck.clump.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.App;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.enums.ChatType;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.UploadChatImageResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.ChatAdapter;
import com.ck.clump.ui.activity.adapter.EmojiAdapter;
import com.ck.clump.ui.activity.adapter.FileAdapter;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.adapter.StickerAdapter;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.ChatMessage;
import com.ck.clump.ui.activity.model.ImageFile;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.activity.model.Sticker;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.listener.BasicCallback;
import com.ck.clump.ui.listener.permission.ChatPermissionListener;
import com.ck.clump.ui.presenter.ChatPresenter;
import com.ck.clump.ui.view.ChatView;
import com.ck.clump.ui.widget.ProgressWheel;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.EmojiHandler;
import com.ck.clump.util.EmojiUnicode;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.NetworkUtil;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class ChatActivity extends BaseActivity implements ChatView {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;

    @Inject
    public Service service;

    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.tvDescription)
    TextView tvDescription;
    @Bind(R.id.contentEventInfo)
    RelativeLayout contentEventInfo;
    @Bind(R.id.tvEvent)
    TextView tvEvent;
    @Bind(R.id.tvEventDate)
    TextView tvEventDate;
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.rvChats)
    RecyclerView rvChats;
    @Bind(R.id.edtInputChat)
    EditText edtInputChat;
    @Bind(R.id.rvStickers)
    RecyclerView rvStickers;
    @Bind(R.id.contentSticker)
    LinearLayout contentSticker;
    @Bind(R.id.contentFile)
    LinearLayout contentFile;
    @Bind(R.id.rvFiles)
    RecyclerView rvFiles;
    @Bind(R.id.progressLoading)
    ProgressWheel progressLoading;
    @Bind(R.id.rvEmojis)
    RecyclerView rvEmojis;
    @Bind(R.id.progressLazyLoad)
    ProgressBar progressLazyLoad;
    @Bind(R.id.imvGallery)
    ImageView imvGallery;
    @Bind(R.id.progressLoadingHistory)
    ProgressWheel progressLoadingHistory;
    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;
    @Bind(R.id.llChatList)
    LinearLayout llChatList;
    @Bind(R.id.btnSend)
    Button btnSend;
    @Bind(R.id.txtNewMessage)
    TextView txtNewMessage;

    /*Others*/
    private Context mContext;
    private List<ChatMessage> mChats;
    private ChatAdapter mAdapter;
    private StickerAdapter mAdapterSticker;
    private List<Sticker> mStickers = new ArrayList<>();
    private FileAdapter mAdapterFile;
    private List<ImageFile> mFiles = new ArrayList<>();
    private PermissionListener cameraPermissionListener;
    private PermissionListener storagePermissionListener;
    private PermissionListener writeStoragePermissionListener;
    private Chat mChat;
    private EmojiAdapter emojiAdapter;
    private ChatPresenter presenter;
    private String eventId = "";
    private boolean isGroup;
    private UserModel userModel;
    private String currentUserId;
    private ChatMessage chat = new ChatMessage("", false, 0, "", 0, "", "", "", "", "", false);
    private final int MAX_LOAD_ITEM = 50;
    private List<ChatMessage> historyList = new ArrayList<>();
    private boolean stopCheckAddToDay = false;
    private long actionDownTime;
    private boolean cancelAction;
    private Uri fileUri;
    private File mSendFile;
    private int itemFocused;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectChatActivity(this);
        renderView();
        setupRecyclerView();
        createPermissionListeners();
        setupRecyclerViewPicture();
        setupRecyclerViewSticker();
        initPubnub();
        presenter = new ChatPresenter(service, this);
        if (mChat.getType() == Chat.TYPE_PERSONAL) {
            presenter.getNextEvent(mChat.getId(), null);
        } else {
            presenter.getNextEvent(null, mChat.getId());
        }
        SharedPreference.saveBool(SharedPreference.KEY_CHAT_ACTIVITY_ALIVE, true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.joneChat(mChat.getChanelId(), UserModel.currentUser().getId());
            }
        }, 2000);
    }

    private void updateRead() {
        String key = SharedPreference.KEY_CHAT_LAST_MESSAGE_CONTENT + "-" + mChat.getChanelId();
        String lastMessage = SharedPreference.getString(key);
        if (lastMessage != null) {
            ChatMessage chatMessage = ChatMessage.toChatMessage(lastMessage, mChat.getChanelId());
            if (!chatMessage.isRead()) {
                publish(lastMessage + "@clump@read@clump@optional1@clump@optional2");
                SharedPreference.saveInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + chat.getChannelId(), 0);
                SharedPreference.deleteKeyAndValue(key);
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        handler.removeCallbacks(runnableHideNewMessageText);
        SharedPreference.deleteKeyAndValue(SharedPreference.KEY_CHAT_ACTIVITY_ALIVE);
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownTime = System.currentTimeMillis();
                cancelAction = false;
                break;
            case MotionEvent.ACTION_MOVE:
                cancelAction = true;
                break;
            case MotionEvent.ACTION_UP:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                long currentTime = System.currentTimeMillis();
                if (currentTime - actionDownTime < 1000 && !Common.getLocationOnScreen(this, edtInputChat).contains(x, y) && !cancelAction) {
                    KeyboardUtil.hideKeyboard(this);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void renderView() {
        mChat = getIntent().getExtras().getParcelable("CHAT_ITEM");
        isGroup = mChat.getType() == Chat.TYPE_GROUP ? true : false;
        userModel = UserModel.currentUser();
        currentUserId = userModel.getId();
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        int defaultDrawable = R.drawable.default_profile_user;
        if (mChat.getType() == Chat.TYPE_GROUP) {
            defaultDrawable = R.drawable.default_profile_group;
        }

        Glide.with(this).load(BuildConfig.BASEURL + mChat.getAvatar())
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(defaultDrawable)
                        .placeholder(defaultDrawable))
                .apply(overrideOf(100, 100))
                .into(avatarImage);

        tvHeader.setText(mChat.getName());
        tvDescription.setText(mChat.getStatus());

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    scrollDown();
                } else {
                    edtInputChat.clearFocus();
                }
            }
        });

        btnSend.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDescription.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_ITALIC));
        tvEvent.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvEventDate.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtInputChat.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtInputChat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        edtInputChat.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @Override
    public void showWait() {
        showLoadingDialog();
    }

    @Override
    public void removeWait() {
        dismissLoadingDialog();
    }

    @Override
    public void onFailure(String appErrorMessage, boolean finishActivity) {
        showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @Override
    public void onUploadImageSuccess(final UploadChatImageResponse response) {
        sendMessage(ChatType.IMAGE.getValue(), response.getdATA().getPath());
    }

    @Override
    public void onNextEventSuccess(EventDetailResponse response) {
        getChatHistory();
        if (response.getdATA().getId() != null) {
            if (response.getdATA().getStartTime() > System.currentTimeMillis() - (60 * 60 * 1000)) {
                eventId = response.getdATA().getId();
                contentEventInfo.setVisibility(View.VISIBLE);
                tvEvent.setText(response.getdATA().getName());
                tvEventDate.setText(Common.showDateTime(new Date(response.getdATA().getStartTime())));
            }

        }
    }

    @OnClick(R.id.contentEventInfo)
    public void onClickEvent() {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("eventID", eventId);
        startActivity(intent);
    }

    @OnClick(R.id.avatarImage)
    public void onClickAvatar() {
        Intent intent;
        if (mChat.getType() == Chat.TYPE_PERSONAL) {
            intent = new Intent(ChatActivity.this, FriendInfoActivity.class);
            intent.putExtra("userId", mChat.getId());
        } else {
            intent = new Intent(ChatActivity.this, GroupInfoActivity.class);
            intent.putExtra("groupId", mChat.getId());
        }
        startActivity(intent);
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        KeyboardUtil.hideKeyboard(this);
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.txtNewMessage)
    public void onClickNewMessage() {
        scrollDown();
    }


    @OnClick(R.id.imvEvent)
    public void onClickIncomingEvent() {
        Intent intent = new Intent(ChatActivity.this, IncomingEventActivity.class);
        intent.putExtra("ID", mChat.getId());
        intent.putExtra("IS_GROUP", isGroup);
        intent.putExtra("NAME", mChat.getName());
        intent.putExtra("AVATAR", mChat.getAvatar());
        startActivity(intent);
    }

    private void setupRecyclerView() {
        mChats = new ArrayList<>();
        mAdapter = new ChatAdapter(mContext, mChats, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onClick(ChatMessage Item) {
                if (Item.getType() == ChatType.IMAGE.getValue()) {
                    Intent intent = new Intent(mContext, ViewMediaActivity.class);
                    intent.putExtra("image", Item.getContent());
                    List<Media> medias = new ArrayList<>();
                    medias.add(new Media("", Item.getContent(), ""));
                    intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) medias);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(final ChatMessage chatMessage, final int position) {
                if (chatMessage.getChannelId().equalsIgnoreCase("") || chatMessage.getContent().equals(getString(R.string.message_chat_removed)))
                    return;
                final SweetAlertDialog dialog = new SweetAlertDialog(mContext, SweetAlertDialog.RECYCLER_VIEW_TYPE_CHAT_MESSAGE_OPTIONS);
                dialog.setTitleText(mContext.getString(R.string.menu));
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.hideButtonControl();

                // Setup RecyclerView
                RecyclerView recyclerView = dialog.getRecyclerView();
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                final List<String> items = new ArrayList<>();
                if (!chatMessage.isLeft()) {
                    items.add(getString(R.string.delete));
                }

                if (chatMessage.getType() == ChatType.TEXT.getValue()) {
                    dialog.getTxtChatContent().setText(Common.getChatContent(chatMessage.getContent()));
                    items.add(0, getString(R.string.copy));
                } else if (chatMessage.getType() == ChatType.STICKER.getValue()) {
                    dialog.getTxtChatContent().setText(getString(R.string.sticker));
                } else {
                    dialog.getTxtChatContent().setText(getString(R.string.image));
                }
                if (items.size() == 0) return;
                recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), items, new MenuAdapter.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(String Item, int pos) {
                        dialog.dismissWithAnimation();
                        switch (pos) {
                            case 0:
                                if (Item.equals(getString(R.string.delete))) { //Delete
                                    deleteChat(position);
                                } else { //Copy
                                    Common.setClipboard(getApplicationContext(), Common.getChatContent(chatMessage.getContent()));
                                    showToast(getString(R.string.message_copied));
                                }
                                break;
                            case 1:
                                //Delete
                                deleteChat(position);
                                break;
                        }
                    }
                }));
            }

            @Override
            public void onLazyLoad(ChatMessage Item) {
                List<ChatMessage> messageList = getMessages(mChats.get(mChats.size() - 1).getTimeSend());
                if (messageList.size() == 0) {
                    boolean isStopLoad = SharedPreference.getBool(SharedPreference.KEY_STOP_LOAD_HISTORY + "-" + mChat.getChanelId(), false);
                    if (!isStopLoad) {
                        String timeSaved = SharedPreference.getString(SharedPreference.KEY_CHAT_HISTORY_START_TIME + "-" + mChat.getChanelId());
                        if (timeSaved != null) {
                            long start = Long.parseLong(timeSaved);
                            if (start != 0) {
                                loadMoreHistory(start);
                            }
                        }
                    } else {
                        mAdapter.setEnableLazyLoad(false);
                    }
                } else {
                    progressLazyLoad.setVisibility(View.VISIBLE);
                    checkAddToday(messageList);
                    mChats.addAll(messageList);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressLazyLoad.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            }
        }, isGroup);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true);
        rvChats.setHasFixedSize(true);
        rvChats.setLayoutManager(layoutManager);
        rvChats.setAdapter(mAdapter);
        rvChats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                itemFocused = layoutManager.findFirstCompletelyVisibleItemPosition();
            }
        });

        emojiAdapter = new EmojiAdapter(mContext, EmojiUnicode.getAndroidEmojiUnicode(), new EmojiAdapter.OnItemClickListener() {
            @Override
            public void onClick(String Item) {
                edtInputChat.append(Item);
            }
        });
        rvEmojis.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.HORIZONTAL, false));
        rvEmojis.setAdapter(emojiAdapter);
    }

    @OnTouch(R.id.edtInputChat)
    public boolean onClickInputChat() {
        contentSticker.setVisibility(View.GONE);
        contentFile.setVisibility(View.GONE);
        return false;
    }

    @OnClick(R.id.imvFile)
    public void onClickFile() {
        KeyboardUtil.hideKeyboard(this);
        resetRecyclerFile();
        if (contentFile.getVisibility() == View.VISIBLE) {
            contentFile.setVisibility(View.GONE);
        } else {
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermission(storagePermissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @OnClick(R.id.imvSticker)
    public void onClickSticker() {
        KeyboardUtil.hideKeyboard(this);
        if (contentSticker.getVisibility() == View.VISIBLE) {
            contentSticker.setVisibility(View.GONE);
        } else {
            contentSticker.setVisibility(View.VISIBLE);
            contentFile.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.btnSend)
    public void onClickSend() {
        String message = EmojiHandler.encodeJava(edtInputChat.getText().toString());
        List<String> emojis = Arrays.asList(EmojiUnicode.getAndroidEmojiUnicode());
        for (int i = 0; i < emojis.size(); i++) {
            String emojiEncode = EmojiHandler.encodeJava(emojis.get(i));
            if (message.contains(emojiEncode)) {
                message = message.replace(emojiEncode, EmojiUnicode.getiOSEmojiUnicode()[i]);
            }
        }
        if (!TextUtils.isEmpty(message)) {
            sendMessage(ChatType.TEXT.getValue(), message);
            edtInputChat.setText(null);
        }
    }

    @OnClick(R.id.imvCustomSticker)
    public void onClickCustomSticker() {
        contentSticker.setVisibility(View.VISIBLE);
        rvStickers.setVisibility(View.VISIBLE);
        rvEmojis.setVisibility(View.GONE);
    }

    @OnClick(R.id.imvDefaultSticker)
    public void onClickDefaultSticker() {
        contentSticker.setVisibility(View.VISIBLE);
        rvStickers.setVisibility(View.GONE);
        rvEmojis.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imvGallery)
    public void onClickGallery() {
        rvFiles.setVisibility(View.VISIBLE);
        Intent intent = new Intent(ChatActivity.this, GalleryActivity.class);
        intent.putParcelableArrayListExtra("files", (ArrayList<? extends Parcelable>) mFiles);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @OnClick(R.id.imvCamera)
    public void onClickCamera() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
    }

    @OnClick(R.id.imvVideo)
    public void onClickVideo() {

    }

    @Override
    public void onBackPressed() {
        if (contentSticker.getVisibility() == View.VISIBLE || contentFile.getVisibility() == View.VISIBLE) {
            contentSticker.setVisibility(View.GONE);
            contentFile.setVisibility(View.GONE);
        } else {
            onClickLeft();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CAMERA)
            mSendFile = new File(fileUri.getPath());
        presenter.uploadImage(mChat.getId(), null, mSendFile);
        if (requestCode == REQUEST_SELECT_IMAGE) {
            String path = data.getStringExtra("path");
            if (mFiles.size() > 0) {
                for (ImageFile file : mFiles) {
                    if (file.getPath().equals(path)) {
                        file.setSent(true);
                        break;
                    }
                }
                mAdapterFile.notifyDataSetChanged();
            }
            mSendFile = new File(path);
            presenter.uploadImage(isGroup ? null : mChat.getId(), isGroup ? mChat.getId() : null, mSendFile);
        }
    }

    private void setupRecyclerViewSticker() {
        if (mStickers.size() != 0) return;
        for (int i = 1; i <= 34; i++) {
            String mDrawableName = "clump_sticker_" + i;
            mStickers.add(new Sticker(mDrawableName));
        }

        rvStickers.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mAdapterSticker = new StickerAdapter(mContext, mStickers, new StickerAdapter.OnItemClickListener() {
            @Override
            public void onClick(Sticker Item) {
                sendMessage(ChatType.STICKER.getValue(), Item.getDrawableName());
            }
        });
        rvStickers.setAdapter(mAdapterSticker);
    }

    private void setupRecyclerViewPicture() {
        rvFiles.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mAdapterFile = new FileAdapter(mContext, mFiles, new FileAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageFile Item, int position) {
                if (!NetworkUtil.isOnline()) {
                    showToast(getString(R.string.message_no_internet_connection));
                    return;
                }
                presenter.uploadImage(isGroup ? null : mChat.getId(), isGroup ? mChat.getId() : null, new File(Item.getPath()));
                mFiles.get(position).setSent(true);
                mAdapterFile.notifyDataSetChanged();
            }
        });
        rvFiles.setAdapter(mAdapterFile);
    }

    /*PERMISSION*/
    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new ChatPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        storagePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.storage_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
        cameraPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.camera_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());

        writeStoragePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.storage_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
    }

    public void showPermissionRationale(final PermissionToken token) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.info_title)
                .setMessage(R.string.permission_rationale_message_multi_permission)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        if (permission.equalsIgnoreCase(Manifest.permission.CAMERA)) {
            Dexter.checkPermission(writeStoragePermissionListener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            contentFile.setVisibility(View.VISIBLE);
            contentSticker.setVisibility(View.GONE);
            if (mFiles.size() == 0) {
                new WorkCursor().execute();
            }
        } else if (permission.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Uri.fromFile(Common.getOutputMediaFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, Constant.CHAT_ACTIVITY);
        SharedPreference.saveString(SharedPreference.CURRENT_CHAT_CHANNEL_ID, mChat.getChanelId());
        startSubscribe();
        updateRead();
        boolean isRefresh = SharedPreference.getBool(SharedPreference.KEY_REFRESH_CHAT_WHEN_RESUME, false);
        if (isRefresh) {
            SharedPreference.deleteKeyAndValue(SharedPreference.KEY_REFRESH_CHAT_WHEN_RESUME);
            getChatHistory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, null);
        SharedPreference.deleteKeyAndValue(SharedPreference.CURRENT_CHAT_CHANNEL_ID);
        SharedPreference.saveInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + mChat.getChanelId(), 0);
        handler.removeCallbacks(dismissNotificationRunnable);
        llNotification.setVisibility(View.GONE);
        stopSubscribe();
    }

    private void scrollDown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvChats.scrollToPosition(0);
            }
        }, 500);
    }

    private void resetRecyclerFile() {
        for (ImageFile file : mFiles) {
            file.setSent(false);
        }
        mAdapterFile.notifyDataSetChanged();
    }

    /*====================START CHAT=====================*/
    private void sendMessage(int type, String content) {
        if (!NetworkUtil.isOnline()) {
            showToast(getString(R.string.message_no_internet_connection));
            return;
        }
        boolean twoItem = false;
        if (!mAdapter.isHaveTodayAlready()) {
            addToday();
            if (mAdapter.isHaveTodayAlready()) {
                twoItem = true;
            }
        }
        String chatId = Common.randomString(10);
        final long timeSend = System.currentTimeMillis() / 1000L;
        mChats.add(0, new ChatMessage(mChat.getChanelId(), false, type, content, timeSend, userModel.getId(),
                userModel.getDisplayName(), userModel.getAvatarPath(), chatId, "message", false));
        saveMessageChat(mChats.get(0));
        if (twoItem) {
            mAdapter.notifyItemRangeChanged(0, 2);
        } else {
            mAdapter.notifyItemInserted(0);
        }
        String chatContent = type + "@clump@" + content + "@clump@" + timeSend + "@clump@" + userModel.getId() + "@clump@" + userModel.getDisplayName()
                + "@clump@" + userModel.getAvatarPath() + "@clump@" + chatId + "@clump@message";
        publish(chatContent);
    }

    private void initPubnub() {
        String gcmRegId = SharedPreference.getString(SharedPreference.GCM_REG_ID);
        if (!TextUtils.isEmpty(gcmRegId)) {
            getmPubNub().enablePushNotificationsOnChannel(mChat.getChanelId(), gcmRegId, new BasicCallback());
        }
    }

    private void startSubscribe() {
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());
                try {
                    JSONObject jsonObject = new JSONObject(message.toString());
                    if (jsonObject.has("pn_other")) {
                        String content = jsonObject.getString("pn_other");
                        final ChatMessage chatMessage = ChatMessage.toChatMessage(content, mChat.getChanelId());
                        if (chatMessage.isRead()) {
                            if (!isGroup) {
                                if (chatMessage.getUserId().equals(UserModel.currentUser().getId())) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            getRealm().executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    if (!mChats.get(0).isRead()) {
                                                        mChats.get(0).setRead(true);
                                                        for (int i = 1; i < mChats.size(); i++) {
                                                            if (!mChats.get(i).isLeft()) {
                                                                mChats.get(i).setRead(false);
                                                            }
                                                        }
                                                        ChatMessage chatMessage = Realm.getInstance(App.getRealmConfig()).where(ChatMessage.class)
                                                                .equalTo("channelId", mChats.get(0).getChannelId())
                                                                .equalTo("chatId", mChats.get(0).getChatId())
                                                                .equalTo("timeSend", mChats.get(0).getTimeSend())
                                                                .findFirst();
                                                        if (chatMessage != null) {
                                                            chatMessage.setRead(true);
                                                            realm.insertOrUpdate(chatMessage);
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mAdapter.notifyItemRangeChanged(0, mChats.size());
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        } else {
                            if (chatMessage.getUserId().equals(UserModel.currentUser().getId()))
                                return;
                            if (chatMessage.getStatus().equals("message")) {
                                if (!isGroup) {
                                    publish(content + "@clump@read@clump@optional1@clump@optional2");
                                }
                                if (!chatMessage.getUserId().equals(currentUserId)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean twoItem = false;
                                            if (!mAdapter.isHaveTodayAlready()) {
                                                addToday();
                                                if (mAdapter.isHaveTodayAlready()) {
                                                    twoItem = true;
                                                }
                                            }
                                            mChats.add(0, chatMessage);
                                            saveMessageChat(mChats.get(0));
                                            if (twoItem) {
                                                mAdapter.notifyItemRangeChanged(0, 2);
                                            } else {
                                                mAdapter.notifyItemInserted(0);
                                                if (itemFocused < 2) {
                                                    scrollDown();
                                                } else {
                                                    showNewMessageComing();
                                                }
                                            }
                                        }
                                    });
                                }
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getRealm().executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                int pos = 0;
                                                for (int i = 0; i < mChats.size(); i++) {
                                                    if (mChats.get(i).getChatId().equals(chatMessage.getChatId())) {
                                                        mChats.get(i).setType(ChatType.TEXT.getValue());
                                                        mChats.get(i).setContent(getString(R.string.message_chat_removed));
                                                        pos = i;
                                                        ChatMessage chatMessage = Realm.getInstance(App.getRealmConfig()).where(ChatMessage.class)
                                                                .equalTo("channelId", mChats.get(i).getChannelId())
                                                                .equalTo("chatId", mChats.get(i).getChatId())
                                                                .equalTo("timeSend", mChats.get(i).getTimeSend())
                                                                .findFirst();
                                                        if (chatMessage != null) {
                                                            chatMessage.setType(ChatType.TEXT.getValue());
                                                            chatMessage.setContent(getString(R.string.message_chat_removed));
                                                            realm.insertOrUpdate(chatMessage);
                                                        }
                                                    }
                                                }
                                                final int changePos = pos;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mAdapter.notifyItemChanged(changePos);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe", "Connected! " + message.toString());
            }
        };
        try {
            getmPubNub().subscribe(mChat.getChanelId(), subscribeCallback);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private void stopSubscribe() {
        getmPubNub().unsubscribe(mChat.getChanelId());
    }

    public void publish(String content) {
        Map<String, Object> payload = new HashMap<>();
        JSONObject pn_gcm = new JSONObject();
        JSONObject pn_gcm_data = new JSONObject();
        JSONObject pn_apns = new JSONObject();
        JSONObject pn_apns_data = new JSONObject();
        try {
            pn_gcm_data.put("channel_id", mChat.getChanelId());
            pn_gcm_data.put("content", content);
            pn_gcm.put("data", pn_gcm_data);

            ChatMessage chatMessage = ChatMessage.toChatMessage(content, mChat.getChanelId());
            String alert = chatMessage.isRead() ? "" : chatMessage.getUserName() + " : " + Common.replaceSpecialSymbols(chatMessage.getContent());
            pn_apns_data.put("alert", alert); //alert's content is "username : content"
            pn_apns_data.put("channel_id", mChat.getChanelId());
            pn_apns.put("aps", pn_apns_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        payload.put("pn_gcm", pn_gcm);
        payload.put("pn_apns", pn_apns);
        payload.put("pn_other", content);
        payload.put("pn_debug", true);

        getmPubNub().publish(mChat.getChanelId(), new JSONObject(payload), true, new BasicCallback());
        scrollDown();
    }

    private void saveMessageChat(final ChatMessage chatMessage) {
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObjectFromJson(ChatMessage.class, new Gson().toJson(chatMessage));
            }
        });
    }

    private void getChatHistory() {
        mChats.clear();
        //Load local history
        List<ChatMessage> messageList = getMessages(System.currentTimeMillis());
        if (messageList.size() > 0) {
            mChats.addAll(messageList);
            mAdapter.notifyDataSetChanged();
            scrollDown();
            return;
        }
        //Load history from PubNub
        boolean isStopLoad = SharedPreference.getBool(SharedPreference.KEY_STOP_LOAD_HISTORY + "-" + mChat.getChanelId(), false);
        if (!isStopLoad) {
            progressLoadingHistory.setVisibility(View.VISIBLE);
            getmPubNub().history(mChat.getChanelId(), MAX_LOAD_ITEM, false, callback);
        }
    }

    private void loadMoreHistory(long start) {
        progressLazyLoad.setVisibility(View.VISIBLE);
        getmPubNub().history(mChat.getChanelId(), start, MAX_LOAD_ITEM, false, callback);
    }

    private void deleteChat(final int position) {
        if (!NetworkUtil.isOnline()) {
            showToast(getString(R.string.message_no_internet_connection));
            return;
        }
        ChatMessage chat = mChats.get(position);
        String chatContent = chat.getType() + "@clump@" + chat.getContent() + "@clump@" + chat.getTimeSend() + "@clump@" + chat.getUserId() + "@clump@" + chat.getUserName()
                + "@clump@" + chat.getUserAvatarUrl() + "@clump@" + chat.getChatId() + "@clump@delete";
        publish(chatContent);
        //Update delete local
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mChats.get(position).setType(ChatType.TEXT.getValue());
                mChats.get(position).setContent(getString(R.string.message_chat_removed));
                ChatMessage chatMessage = Realm.getInstance(App.getRealmConfig()).where(ChatMessage.class)
                        .equalTo("channelId", mChats.get(position).getChannelId())
                        .equalTo("chatId", mChats.get(position).getChatId())
                        .equalTo("userId", UserModel.currentUser().getId())
                        .findFirst();
                if (chatMessage != null) {
                    chatMessage.setType(ChatType.TEXT.getValue());
                    chatMessage.setContent(getString(R.string.message_chat_removed));
                    realm.insertOrUpdate(chatMessage);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(position);
                    }
                });
            }
        });
    }

    private Callback callback = new Callback() {
        public void successCallback(String channel, Object message) {
            mAdapter.setLoading(false);
            Log.d("History", "Success: " + message.toString());
            try {
                JSONArray jsonArray = new JSONArray(message.toString());
                historyList.clear();
                if (jsonArray.length() > 0) {
                    final JSONArray messageArray = jsonArray.getJSONArray(0);
                    if (messageArray.length() > 0) {
                        List<ChatMessage> tempList = new ArrayList<>();
                        for (int i = 0; i < messageArray.length(); i++) {
                            JSONObject messageObject = messageArray.getJSONObject(i);
                            if (messageObject.has("pn_other")) {
                                ChatMessage chatMessage = ChatMessage.toChatMessage(messageObject.getString("pn_other"), mChat.getChanelId());
                                tempList.add(chatMessage);
                            }
                        }
                        if (tempList.size() > 0) {
                            Collections.sort(tempList, new Comparator<ChatMessage>() {
                                public int compare(ChatMessage o1, ChatMessage o2) {
                                    if (o1.getTimeSend() > o2.getTimeSend()) {
                                        return -1;
                                    } else if (o1.getTimeSend() < o2.getTimeSend()) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                            for (int i = 0; i < tempList.size(); i++) {
                                ChatMessage chat = tempList.get(i);
                                if (chat.getUserName().equals("DELETE") && chat.getUserAvatarUrl().equals("DELETE") && chat.getUserId().equals("DELETE")) {
                                    SharedPreference.saveBool(SharedPreference.KEY_STOP_LOAD_HISTORY + "-" + mChat.getChanelId(), true);
                                    break;
                                }
                                historyList.add(chat);
                            }
                            if (messageArray.length() == MAX_LOAD_ITEM) {
                                SharedPreference.saveString(SharedPreference.KEY_CHAT_HISTORY_START_TIME + "-" + mChat.getChanelId(), "" + jsonArray.getLong(1));
                            } else {
                                SharedPreference.saveString(SharedPreference.KEY_CHAT_HISTORY_START_TIME + "-" + mChat.getChanelId(), "0");
                                mAdapter.setEnableLazyLoad(false);
                            }
                        }
                    }
                }
                checkAddToday(historyList);
                mChats.addAll(historyList);
                saveHandler.sendEmptyMessage(0);
                hideLoadingHistory(true);
                if (historyList.size() == 0) {
                    mAdapter.setEnableLazyLoad(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                hideLoadingHistory(false);
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            hideLoadingHistory(false);
        }
    };

    private void hideLoadingHistory(final boolean isDataChange) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressLoadingHistory.setVisibility(View.GONE);
                progressLazyLoad.setVisibility(View.GONE);
                if (isDataChange) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private Handler saveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (ChatMessage chatMessage : historyList) {
                        realm.createObjectFromJson(ChatMessage.class, new Gson().toJson(chatMessage));
                    }
                }
            });
        }
    };

    //Get local messages
    private List<ChatMessage> getMessages(long lastTime) {
        RealmResults<ChatMessage> data = getRealm().where(ChatMessage.class)
                .equalTo("channelId", mChat.getChanelId())
                .lessThan("timeSend", lastTime)
                .findAllSorted("timeSend", Sort.DESCENDING);
        List<ChatMessage> results = new ArrayList<>();
        if (data.size() < MAX_LOAD_ITEM && data.size() > 0) {
            mAdapter.setEnableLazyLoad(false);
            results.addAll(data);
        } else if (data.size() >= 50) {
            for (int i = 0; i < 50; i++) {
                results.add(data.get(i));
            }
        } else {
            results.addAll(data);
        }
        checkAddToday(results);
        return results;
    }

    private void addToday() {
        if (mChats.size() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(new Date(mChats.get(0).getTimeSend() * 1000L));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day != curDay) {
                mChats.add(0, chat);
                mAdapter.setHaveTodayAlready(true);
            }
        } else {
            mChats.add(0, chat);
            mAdapter.setHaveTodayAlready(true);
        }
    }

    private void checkAddToday(List<ChatMessage> chatMessages) {
        if (!mAdapter.isHaveTodayAlready() && !stopCheckAddToDay) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (chatMessages.size() > 0) {
                for (int i = 0; i < chatMessages.size(); i++) {
                    calendar.setTime(new Date(chatMessages.get(i).getTimeSend() * 1000L));
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    if (mChats.size() == 0) {
                        if (i == 0 && day != curDay) {
                            stopCheckAddToDay = true;
                            break;
                        }
                        if (day != curDay) {
                            chatMessages.add(i, chat);
                            mAdapter.setHaveTodayAlready(true);
                            break;
                        }
                    } else {
                        if (day != curDay) {
                            chatMessages.add(i, chat);
                            mAdapter.setHaveTodayAlready(true);
                            break;
                        }
                    }
                    if (chatMessages.size() < MAX_LOAD_ITEM && day == curDay && i == chatMessages.size() - 1) {
                        chatMessages.add(i + 1, chat);
                        mAdapter.setHaveTodayAlready(true);
                        break;
                    }
                }
            }
        }
    }

    /*END CHAT*/

    /*SHOW POPUP NOTIFICATION*/
    private Handler handler = new Handler();
    private Runnable dismissNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            llNotification.setVisibility(View.GONE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChatMessageEvent event) {
        if (llNotification.getVisibility() == View.VISIBLE) {
            handler.removeCallbacks(dismissNotificationRunnable);
        } else {
            llNotification.setVisibility(View.VISIBLE);
        }
        txtNotificationName.setText(event.getName());
        txtNotificationMessage.setText(event.getContent());
        handler.postDelayed(dismissNotificationRunnable, 2000);
    }

    /*LOAD IMAGE TO GALLERY*/
    private class WorkCursor extends AsyncTask<Cursor, Object, List<String>> {

        public WorkCursor() {
        }

        @Override
        protected void onPreExecute() {
            progressLoading.setVisibility(View.VISIBLE);
            rvFiles.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(Cursor... cursors) {

            Uri uri;
            Cursor cursor;
            int column_index_data;
            List<String> listOfAllImages = new ArrayList<>();
            String absolutePathOfImage;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = mContext.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }
            return listOfAllImages;
        }

        @Override
        protected void onPostExecute(List<String> lstFiles) {
            super.onPostExecute(lstFiles);
            progressLoading.setVisibility(View.GONE);
            rvFiles.setVisibility(View.VISIBLE);
            mFiles.clear();
            for (String file : lstFiles) {
                mFiles.add(new ImageFile(file, false));
            }
            mAdapterFile.notifyDataSetChanged();
        }
    }

    private Runnable runnableHideNewMessageText = new Runnable() {
        @Override
        public void run() {
            txtNewMessage.setVisibility(View.INVISIBLE);
        }
    };

    private void showNewMessageComing() {
        if (txtNewMessage.getVisibility() == View.INVISIBLE) {
            txtNewMessage.setVisibility(View.VISIBLE);
            handler.postDelayed(runnableHideNewMessageText, 2000);
        }
    }

}
