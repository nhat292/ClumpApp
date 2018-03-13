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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.R;
import com.ck.clump.model.CreateGroupModel;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.CreateGroupResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.FriendAdapter;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.listener.permission.CreateGroupPermissionListener;
import com.ck.clump.ui.presenter.CreateGroupPresenter;
import com.ck.clump.ui.view.CreateGroupView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

/**
 * Created by Nhat on 7/18/17.
 */

public class CreatedGroupActivity extends BaseActivity implements CreateGroupView {

    public static final int PICK_FRIENDS_REQUEST = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_GALLERY = 3;

    @Inject
    Service service;

    @Bind(R.id.edtGroupName)
    EditText edtGroupName;
    @Bind(R.id.rvMembers)
    RecyclerView rvMembers;
    @Bind(R.id.avatarImage)
    ImageView avatarImage;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvRight)
    TextView imvRight;
    @Bind(R.id.tvMembers)
    TextView tvMembers;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    private Context mContext;
    private List<Friend> mFriends;
    private FriendAdapter mAdapterMembers;
    private File imageFile;
    private Intent data;
    private SweetAlertDialog mDialog;
    private CreateGroupPresenter presenter;
    private PermissionListener cameraPermissionListener;
    private PermissionListener readstoragePermissionListener;
    private PermissionListener writeStoragePermissionListener;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectCreateGroupActivity(this);
        renderView();
        presenter = new CreateGroupPresenter(service, this);
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        createPermissionListeners();
        Intent intent = getIntent();
        mFriends = intent.getParcelableArrayListExtra("DATA");
        mFriends.add(0, new Friend("", "", "", true));

        mAdapterMembers = new FriendAdapter(mContext, mFriends, new FriendAdapter.OnItemClickListener() {
            @Override
            public void onClick(Friend Item) {
                if (Item.getId().equalsIgnoreCase("")) {
                    //Pick Friends
                    Intent intent = new Intent(mContext, PickFriendActivity.class);
                    intent.putExtra("ACTION", "PICK");
                    startActivityForResult(intent, PICK_FRIENDS_REQUEST);
                }
            }
        });
        rvMembers.setAdapter(mAdapterMembers);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtGroupName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvMembers.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtGroupName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtGroupName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        finish();
    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        String groupName = edtGroupName.getText().toString();
        if (groupName.isEmpty()) {
            showErrorMessage(getString(R.string.please_type_a_group_name), false);
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
        presenter.createGroup(groupName, memberListStr, imageFile);
    }

    @OnClick(R.id.avatarImage)
    public void onAvaterClick() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.take_a_photo));
        items.add(getString(R.string.choose_from_gallery));
        showMenu(items);
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
        CreateGroupResponse response = (CreateGroupResponse) o;
        openChat(response.getdATA());
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    private void openChat(CreateGroupModel model) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Chat chat = new Chat(model.getId(), model.getId(), model.getImagePath(), model.getName(), Chat.TYPE_PERSONAL,
                "", "", new Date());
        intent.putExtra("CHAT_ITEM", chat);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == PICK_FRIENDS_REQUEST) {
            List<Friend> friends = data.getParcelableArrayListExtra("DATA");
            for (Friend friend : friends) {
                boolean add = true;
                for (Friend friend2 : mFriends) {
                    if (friend.getId().equals(friend2.getId())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    mFriends.add(friend);
                }
            }
            rvMembers.setAdapter(mAdapterMembers);
        } else if (requestCode == REQUEST_GALLERY) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA) {
            imageFile = new File(fileUri.getPath());
            showAvatar(Uri.fromFile(imageFile));
        }
    }

    private void showMenu(List<String> items) {
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
                        if (Dexter.isRequestOngoing()) {
                            return;
                        }
                        Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
                        break;
                    case 1:
                        if (Dexter.isRequestOngoing()) {
                            return;
                        }
                        Dexter.checkPermission(readstoragePermissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
                        break;
                }
            }
        }));
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
        PermissionListener feedbackViewPermissionListener = new CreateGroupPermissionListener(this);
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
            }
        }
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
