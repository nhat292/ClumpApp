package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.model.GroupInfoModel;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.GroupInfoResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.FriendAdapter;
import com.ck.clump.ui.activity.adapter.MediaAdapter;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.listener.permission.GroupInfoPermissionListener;
import com.ck.clump.ui.presenter.GroupInfoPresenter;
import com.ck.clump.ui.view.GroupInfoView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class GroupInfoActivity extends BaseActivity implements GroupInfoView {

    public static final int PICK_FRIENDS_REQUEST = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_GALLERY = 3;

    @Inject
    public Service service;

    @Bind(R.id.imvBackground)
    ImageView imvBackground;
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.rvFriends)
    RecyclerView rvFriends;
    @Bind(R.id.rvMedias)
    RecyclerView rvMedias;
    @Bind(R.id.contentMembers)
    RelativeLayout contentMembers;
    @Bind(R.id.tvMedias)
    TextView tvMedias;
    @Bind(R.id.edtName)
    TextView edtName;
    @Bind(R.id.imvRight)
    ImageView imvRight;
    @Bind(R.id.txtRight)
    TextView txtRight;
    @Bind(R.id.tvMembers)
    TextView tvMembers;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private FriendAdapter mAdapterFriend;
    private List<Friend> mFriends = new ArrayList<>();
    private MediaAdapter mAdapterMedia;
    private List<Media> mMedias = new ArrayList<>();
    private GroupInfoPresenter presenter;
    private String groupId;
    private SweetAlertDialog mDialog;
    private GroupInfoModel groupInfoModel;
    private boolean addMemberAlready = false;
    private PermissionListener cameraPermissionListener;
    private PermissionListener readstoragePermissionListener;
    private PermissionListener writeStoragePermissionListener;
    private File imageFile;
    private Intent data;
    private boolean needUpdateListContact = false;
    private List<Friend> hideFriends = new ArrayList<>();
    private String userIds = "";
    private Uri fileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectGroupInfoActivity(this);
        renderView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Intent intent = getIntent();
        if (intent.hasExtra("groupId")) {
            presenter = new GroupInfoPresenter(service, this);
            groupId = intent.getStringExtra("groupId");
            presenter.getGroupInfo(groupId);
        }
        createPermissionListeners();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_group_info);
        ButterKnife.bind(this);

        edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSaveButton();
                } else {
                    txtRight.setVisibility(View.GONE);
                    checkShowOrHideOptions(groupInfoModel.getMembers());
                }
            }
        });

        /*Friends*/
        mAdapterFriend = new FriendAdapter(mContext, mFriends, new FriendAdapter.OnItemClickListener() {
            @Override
            public void onClick(Friend Item) {
                if (Item.getId().equalsIgnoreCase("")) {
                    //Pick Friends
                    Intent intent = new Intent(GroupInfoActivity.this, PickFriendActivity.class);
                    intent.putExtra("ACTION", "PICK");
                    startActivityForResult(intent, PICK_FRIENDS_REQUEST);
                } else {
                    Intent intent = new Intent(GroupInfoActivity.this, FriendInfoActivity.class);
                    intent.putExtra("userId", Item.getId());
                    startActivity(intent);
                }
            }
        });

        rvFriends.setAdapter(mAdapterFriend);

        /*Medias*/
        mAdapterMedia = new MediaAdapter(mContext, mMedias, new MediaAdapter.OnItemClickListener() {
            @Override
            public void onClick(Media Item, int position) {
                Intent intent = new Intent(GroupInfoActivity.this, ViewMediaActivity.class);
                intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) mMedias);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rvMedias.setAdapter(mAdapterMedia);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvMembers.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvMedias.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        txtRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));

        edtName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @Override
    public void showWait() {
        if (!addMemberAlready)
            showLoadingDialog();
    }

    @Override
    public void removeWait() {
        if (!addMemberAlready)
            dismissLoadingDialog();
    }

    @Override
    public void onFailure(String appErrorMessage, boolean finishActivity) {
        showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        GroupInfoResponse response = (GroupInfoResponse) o;
        groupInfoModel = response.getdATA();
        edtName.setText(groupInfoModel.getName());
        tvMedias.setText(String.format(getString(R.string.medias), groupInfoModel.getTotalImage()));
        checkShowOrHideOptions(groupInfoModel.getMembers());
        mFriends.clear();
        mMedias.clear();
        mFriends.add(new Friend("", "", "", true));
        userIds = SharedPreference.getString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + groupInfoModel.getId());
        if (userIds == null) {
            userIds = "";
        }
        if (groupInfoModel.getMembers().size() > 0) {
            for (Friend friend : groupInfoModel.getMembers()) {
                if (!userIds.contains(friend.getId())) {
                    mFriends.add(friend);
                }
            }
        }
        mMedias.addAll(groupInfoModel.getImages());
        mAdapterFriend.notifyDataSetChanged();
        mAdapterMedia.notifyDataSetChanged();
        if (groupInfoModel.getImagePath() != null && !groupInfoModel.getImagePath().isEmpty()) {
            showBG(BuildConfig.BASEURL + groupInfoModel.getImagePath());
        }
        showAvatar(BuildConfig.BASEURL + groupInfoModel.getImagePath());

    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @Override
    public void onAddMemberSuccess(SimpleResponse response) {
        addMemberAlready = true;
        presenter.getGroupInfo(groupId);
        if (hideFriends.size() > 0) {
            for (Friend friend : hideFriends) {
                userIds.replace(friend.getId() + "@@", "");
                presenter.joneChat(groupInfoModel.getId(), friend.getId());
            }
            SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + groupInfoModel.getId(), userIds);
            SharedPreference.saveBool(SharedPreference.KEY_REFRESH_CHAT_LIST_WHEN_RESUME, true);
        }
    }

    @Override
    public void onUpdateGroupSuccess(SimpleResponse response) {
        needUpdateListContact = true;
        KeyboardUtil.hideKeyboard(this);
        edtName.clearFocus();
        txtRight.setVisibility(View.GONE);
        checkShowOrHideOptions(groupInfoModel.getMembers());
        showInfoMessage(getString(R.string.message_update_success));
    }

    private void showSaveButton() {
        txtRight.setVisibility(View.VISIBLE);
        //imvRight.setVisibility(View.GONE);
    }

    private void showAvatar(String url) {
        Glide.with(this).load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_profile_group)
                        .placeholder(R.drawable.default_profile_group))
                .apply(overrideOf(200, 200))
                .into(avatarImage);
    }

    private void showBG(String url) {
        Glide.with(this).load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25)))
                .into(imvBackground);
    }

    private void showAvatar(Uri uri) {
        Glide.with(this).load(uri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.default_profile_group)
                        .placeholder(R.drawable.default_profile_group))
                .apply(overrideOf(200, 200))
                .into(avatarImage);
    }

    private void showBG(Uri uri) {
        Glide.with(this).load(uri)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25)))
                .into(imvBackground);
    }

    private void checkShowOrHideOptions(List<Friend> members) {
        /*if (members.size() == 0) {
            return;
        }
        UserModel userModel = UserModel.currentUser();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).isCreator() && userModel.getId().equals(members.get(i).getId())) {
                imvRight.setVisibility(View.VISIBLE);
                break;
            }
        }*/
    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        List<String> items = new ArrayList<>();
        items.add(mContext.getString(R.string.menu_delete_group));
        items.add(mContext.getString(R.string.menu_incoming_event));
        showMenu(items, false);
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        if (needUpdateListContact) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        onClickLeft();
    }

    @OnClick(R.id.tvMedias)
    public void onClickMedia() {
        if (groupInfoModel.getTotalImage() == 0) return;
        Intent intent = new Intent(mContext, SentMediaActivity.class);
        intent.putExtra("displayName", groupInfoModel.getName());
        intent.putExtra("totalImage", groupInfoModel.getTotalImage());
        intent.putExtra("id", groupInfoModel.getId());
        intent.putExtra("isGroup", true);
        startActivity(intent);
    }

    @OnClick(R.id.avatarImage)
    public void onClickAvatar() {
        List<String> items = new ArrayList<>();
        items.add("View picture");
        items.add("Take a photo");
        items.add("Choose from gallery");
        showMenu(items, true);
    }

    @OnClick(R.id.txtRight)
    public void onSaveClick() {
        update();
    }

    private void update() {
        KeyboardUtil.hideKeyboard(this);
        edtName.clearFocus();
        if (edtName.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.message_type_a_group_name), false);
            return;
        }
        String memberListStr = "";
        if (mFriends.size() > 0) {
            memberListStr += "[";
            for (int i = 0; i < mFriends.size(); i++) {
                memberListStr += "\"" + mFriends.get(i).getId() + "\"";
                if (i < mFriends.size() - 1) {
                    memberListStr += ",";
                }
            }
            memberListStr += "]";
        }
        Map<String, RequestBody> params = new HashMap<>();
        params.put("id", RequestBody.create(MediaType.parse("text/plain"), groupId));
        params.put("name", RequestBody.create(MediaType.parse("text/plain"), edtName.getText().toString()));
        params.put("memberList", RequestBody.create(MediaType.parse("text/plain"), memberListStr));
        presenter.updateGroup(params, imageFile);
    }


    private void showMenu(List<String> items, final boolean isImageAction) {
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

        recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), items, new MenuAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(String Item, int position) {
                mDialog.dismissWithAnimation();
                switch (position) {
                    case 0:
                        if (isImageAction) {
                            Intent intent = new Intent(mContext, ViewMediaActivity.class);
                            List<Media> medias = new ArrayList<>();
                            medias.add(new Media("", groupInfoModel.getImagePath(), ""));
                            intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) medias);
                            intent.putExtra("position", 0);
                            startActivity(intent);
                        } else {
                            //Delete group
                        }
                        break;
                    case 1:
                        if (isImageAction) {
                            if (Dexter.isRequestOngoing()) {
                                return;
                            }
                            Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
                        } else {
                            Intent intent = new Intent(mContext, IncomingEventActivity.class);
                            intent.putExtra("ID", groupId);
                            intent.putExtra("IS_GROUP", true);
                            intent.putExtra("NAME", groupInfoModel.getName());
                            intent.putExtra("AVATAR", groupInfoModel.getImagePath());
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        if (isImageAction) {
                            if (Dexter.isRequestOngoing()) {
                                return;
                            }
                            Dexter.checkPermission(readstoragePermissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        break;
                }
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == PICK_FRIENDS_REQUEST) {
            hideFriends.clear();
            List<Friend> friends = data.getParcelableArrayListExtra("DATA");
            String memberListStr = "";
            if (friends.size() > 0) {
                memberListStr += "[";
                for (int i = 0; i < friends.size(); i++) {
                    memberListStr += "\"" + friends.get(i).getId() + "\"";
                    if (i < friends.size() - 1) {
                        memberListStr += ",";
                    }
                    if (!userIds.contains(friends.get(i).getId())) {
                        hideFriends.add(friends.get(i));
                    }
                }
                memberListStr += "]";
            }
            Map<String, String> params = new HashMap<>();
            params.put("groupId", groupId);
            params.put("memberList", memberListStr);
            presenter.addMember(params);
        } else if (requestCode == REQUEST_GALLERY) {
            onSelectFromGalleryResult(data);
            showSaveButton();
        } else if (requestCode == REQUEST_CAMERA) {
            showSaveButton();
            imageFile = new File(fileUri.getPath());
            showAvatar(Uri.fromFile(imageFile));
            showBG(Uri.fromFile(imageFile));
            showSaveButton();
        }
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
        if (permission.equalsIgnoreCase(Manifest.permission.CAMERA)) {
            Dexter.checkPermission(writeStoragePermissionListener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (permission.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, mContext.getString(R.string.select_file)), REQUEST_GALLERY);
        } else if (permission.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Uri.fromFile(Common.getOutputMediaFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    }

    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new GroupInfoPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        readstoragePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
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

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getPath(this, selectedImageUri);
            if (imagePath != null) {
                imageFile = new File(imagePath);
                showAvatar(selectedImageUri);
                showBG(selectedImageUri);
                showSaveButton();
            }
        }
    }

    private String getPath(Context context, Uri uri) {
        Cursor cursor = null;
        String path = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String sel = MediaStore.Images.Media._ID + "=?";
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        proj, sel, new String[]{id}, null);
            } else {
                cursor = context.getContentResolver().query(uri, proj, null, null, null);
            }
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                path = cursor.getString(column_index);
            }
        } finally {
            cursor.close();
        }
        return path;
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
