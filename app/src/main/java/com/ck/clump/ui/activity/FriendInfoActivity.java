package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.MediaAdapter;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.listener.permission.FriendInfoPermissionListener;
import com.ck.clump.ui.presenter.FriendInfoPresenter;
import com.ck.clump.ui.view.FriendInfoView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class FriendInfoActivity extends BaseActivity implements FriendInfoView {

    @Inject
    public Service service;

    @Bind(R.id.imvBackground)
    ImageView imvBackground;
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.rvMedias)
    RecyclerView rvMedias;
    @Bind(R.id.tvMedias)
    TextView tvMedias;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.ivStatusTail)
    ImageView ivStatusTail;
    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private MediaAdapter mAdapterMedia;
    private List<Media> mMedias = new ArrayList<>();
    private PermissionListener callPhonePermissionListener;
    private UserModel userModel;
    private FriendInfoPresenter presenter;
    private String userId;
    private int totalImage;
    private SweetAlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectFriendInfoActivity(this);
        renderView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        /*Permission*/
        createPermissionListeners();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_friend_info);
        ButterKnife.bind(this);
        presenter = new FriendInfoPresenter(service, this);

        /*Medias*/
        mAdapterMedia = new MediaAdapter(mContext, mMedias, new MediaAdapter.OnItemClickListener() {
            @Override
            public void onClick(Media Item, int position) {
                Intent intent = new Intent(FriendInfoActivity.this, ViewMediaActivity.class);
                intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) mMedias);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rvMedias.setAdapter(mAdapterMedia);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvStatus.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvMedias.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));

        Intent intent = getIntent();
        if (intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId");
            presenter.getUserDetail(new FriendInfoRequest(userId));
        }
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
    public void onGetUserDetailSuccess(UserModel response) {
        userModel = response;
        tvName.setText(userModel.getDisplayName());
        if (userModel.getStatus() == null) {
            tvStatus.setVisibility(View.INVISIBLE);
            ivStatusTail.setVisibility(View.INVISIBLE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
            ivStatusTail.setVisibility(View.VISIBLE);
            tvStatus.setText(userModel.getStatus());
        }
        if (userModel.getBackgroundPath() != null && !userModel.getBackgroundPath().isEmpty()) {
            Glide.with(this).load(BuildConfig.BASEURL + userModel.getBackgroundPath())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25)))
                    .into(imvBackground);
        }

        RequestBuilder<Drawable> thumbnail = Glide.with(this)
                .load(R.drawable.default_profile_user);

        Glide.with(this).load(BuildConfig.BASEURL + userModel.getAvatarPath())
                .thumbnail(thumbnail)
                .into(avatarImage);

        presenter.loadFourImagesOfUser(userId);
    }

    @Override
    public void onLoadingMediaSuccess(MediaResponse response) {
        totalImage = response.getdATA().getTotalImage();
        tvMedias.setText(String.format(getString(R.string.medias), totalImage));
        if (response.getdATA().getMedias().size() > 0) {
            mMedias.addAll(response.getdATA().getMedias());
            mAdapterMedia.notifyDataSetChanged();
        }
    }

    @Override
    public void onReportSuccess(SimpleResponse response) {
        showInfoMessage(getString(R.string.message_report_user_success));
    }

    @Override
    public void onBlockUserSuccess(SimpleResponse response) {
        showInfoMessage(getString(R.string.message_block_user_success));
        SharedPreference.saveBool(SharedPreference.KEY_REFRESH_CONTACT_LIST_WHEN_RESUME, true);
    }

    @Override
    public void onGetChannelIDSuccess(ChannelResponse response) {
        openChat(response.getdATA().getChannelId());
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        finish();
    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        showMenu(mContext.getString(R.string.menu_report_user), mContext.getString(R.string.menu_block_user), false);
    }

    @OnClick(R.id.imvChat)
    public void onClickChat() {
        if (userModel.getChannelId() != null && !userModel.getChannelId().isEmpty()) {
            openChat(userModel.getChannelId());
            return;
        }
        presenter.getChannelID(new ChannelRequest(userId));
    }

    @OnClick(R.id.avatarImage)
    public void avatarImageClick() {
        Intent intent = new Intent(mContext, ViewMediaActivity.class);
        List<Media> medias = new ArrayList<>();
        medias.add(new Media("", userModel.getAvatarPath(), ""));
        intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) medias);
        intent.putExtra("position", 0);
        startActivity(intent);
    }

    @OnClick(R.id.tvMedias)
    public void onClickMedia() {
        if (totalImage == 0) return;
        Intent intent = new Intent(mContext, SentMediaActivity.class);
        intent.putExtra("displayName", userModel.getDisplayName());
        intent.putExtra("totalImage", totalImage);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    @OnClick(R.id.imvCall)
    public void onClickCall() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(callPhonePermissionListener, Manifest.permission.CALL_PHONE);
    }

    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new FriendInfoPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        callPhonePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.call_phone_permission_denied_dialog_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
        if (userModel != null) {
            Common.startActionCall(mContext, userModel.getPhone());
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    }

    private void showMenu(String item1, String item2, final boolean isSendReport) {
        mDialog = new SweetAlertDialog(mContext, SweetAlertDialog.RECYCLER_VIEW_TYPE);
        mDialog.setTitleText(mContext.getString(R.string.menu));
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        mDialog.hideSearchBar();
        mDialog.hideButtonControl();
        mDialog.hideTitleMessage();

        // Setup RecyclerView
        RecyclerView recyclerView = mDialog.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        List<String> menus = new ArrayList<>();
        menus.add(item1);
        menus.add(item2);

        recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), menus, new MenuAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(String Item, int position) {
                mDialog.dismissWithAnimation();
                switch (position) {
                    case 0:
                        if (isSendReport) {
                            presenter.reportUser(getParamsForReport("CONTENT"));
                        } else {
                            showMenu(mContext.getString(R.string.menu_report_1), mContext.getString(R.string.menu_report_2), true);
                        }
                        break;
                    case 1:
                        if (isSendReport) {
                            presenter.reportUser(getParamsForReport("PROFILE_IMG"));
                        } else {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("userId", userId);
                            presenter.blockUser(params);
                        }
                        break;
                }
            }
        }));
    }

    private Map<String, String> getParamsForReport(String reportType) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reportType", reportType);
        return params;
    }

    private void openChat(final String channelID) {
        String channelsHide = SharedPreference.getString(SharedPreference.KEY_ID_CHANNEL_HIDE);
        if (channelsHide != null) {
            channelsHide = channelsHide.replace(channelID + "@@", "");
            SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_HIDE, channelsHide);
        }
        Intent intent = new Intent(mContext, ChatActivity.class);
        Chat chat = new Chat(userId, channelID, userModel.getAvatarPath(), userModel.getDisplayName(),
                Chat.TYPE_PERSONAL, userModel.getStatus(), userModel.getLastMessage(), Common.convertTimestampToDate(userModel.getLastMessageTime()));
        intent.putExtra("CHAT_ITEM", chat);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, Constant.OTHER_ACTIVITIES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, null);
        handler.removeCallbacks(dismissNotificationRunnable);
        llNotification.setVisibility(View.GONE);
    }

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
}
