package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.UpdateProfileResponse;
import com.ck.clump.model.response.UploadUserBackgroundResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.listener.permission.SettingProfilePermissionListener;
import com.ck.clump.ui.presenter.SettingProfilePresenter;
import com.ck.clump.ui.view.SettingProfileView;
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
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class SettingProfileActivity extends BaseActivity implements SettingProfileView {

    @Inject
    public Service service;

    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.edtDisplayName)
    EditText edtDisplayName;
    @Bind(R.id.edtStatus)
    EditText edtStatus;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvRight)
    TextView imvRight;
    @Bind(R.id.tvDisplayName)
    TextView tvDisplayName;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.btnSetBackground)
    Button btnSetBackground;
    @Bind(R.id.tvTutorial)
    TextView tvTutorial;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private Context mContext;
    private SweetAlertDialog mDialog;
    private PermissionListener cameraPermissionListener;
    private PermissionListener readStoragePermissionListener;
    private PermissionListener writeStoragePermissionListener;
    private SettingProfilePresenter presenter;
    private File imageFile;
    private boolean isUploadBackground = false;
    private UserModel userModel;
    private boolean hasView = false;
    private Uri fileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectSettingProfileActivity(this);
        renderView();
        /*Permissions*/
        createPermissionListeners();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_setting_profile);
        ButterKnife.bind(this);

        userModel = UserModel.currentUser();
        edtDisplayName.setText(userModel.getDisplayName());
        edtStatus.setText(userModel.getStatus());

        RequestBuilder<Drawable> thumbnail = Glide.with(this)
                .load(R.drawable.default_profile_user);


        Glide.with(this)
                .load(BuildConfig.BASEURL + userModel.getAvatarPath())
                .thumbnail(thumbnail)
                .into(avatarImage);

        presenter = new SettingProfilePresenter(service, this);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvDisplayName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtDisplayName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvStatus.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtStatus.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnSetBackground.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvTutorial.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtDisplayName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtStatus.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        edtDisplayName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtStatus.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
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
    public void onUpdateProfileSuccess(final UpdateProfileResponse response) {
        showInfoMessage(getString(R.string.message_profile_update_success));
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userModel.setStatus(response.getdATA().getStatus());
                userModel.setAvatarPath(response.getdATA().getAvatarPath());
            }
        });
        imageFile = null;
    }

    @Override
    public void onUploadUserBackgroundSuccess(final UploadUserBackgroundResponse response) {
        showInfoMessage(getString(R.string.message_profile_background_update_success));
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userModel.setBackgroundPath(response.getdATA().getBackgroundPath());
            }
        });
        imageFile = null;
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        finish();
    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        if (edtDisplayName.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.message_please_input_display_name), false);
            return;
        }
        presenter.updateProfile(edtDisplayName.getText().toString(), edtStatus.getText().toString(), imageFile);
    }

    @OnClick(R.id.avatarImage)
    public void onClickAvatar() {
        isUploadBackground = false;
        showMenu();
    }

    @OnClick(R.id.btnSetBackground)
    public void onClickSetBackground() {
        isUploadBackground = true;
        showMenu();
    }

    private void showMenu() {
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
        if (isUploadBackground) {
            if (userModel.getBackgroundPath() != null) {
                if (!userModel.getBackgroundPath().isEmpty()) {
                    menus.add(mContext.getString(R.string.view_picture));
                    hasView = true;
                }
            }
        } else {
            if (userModel.getAvatarPath() != null) {
                if (!userModel.getAvatarPath().isEmpty()) {
                    menus.add(mContext.getString(R.string.view_picture));
                    hasView = true;
                }
            }
        }

        menus.add(mContext.getString(R.string.take_a_photo));
        menus.add(mContext.getString(R.string.choose_from_gallery));

        recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), menus, new MenuAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(String Item, int position) {
                mDialog.dismissWithAnimation();
                switch (position) {
                    case 0:
                        if (hasView) {
                            viewImage();
                        } else {
                            cameraIntent();
                        }
                        break;
                    case 1:
                        if (hasView) {
                            cameraIntent();
                        } else {
                            galleryIntent();
                        }
                        break;
                    case 2:
                        galleryIntent();
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

    private void cameraIntent() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void galleryIntent() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(readStoragePermissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void viewImage() {
        Intent intent = new Intent(mContext, ViewMediaActivity.class);
        List<Media> medias = new ArrayList<>();
        if (isUploadBackground) {
            medias.add(new Media("", userModel.getBackgroundPath(), ""));
        } else {
            medias.add(new Media("", userModel.getAvatarPath(), ""));
        }
        intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) medias);
        intent.putExtra("position", 0);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_GALLERY) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA) {
            imageFile = new File(fileUri.getPath());
            if (isUploadBackground) {
                presenter.uploadUserBackground(imageFile);
            } else {
                Glide.with(this)
                        .load(fileUri)
                        .into(avatarImage);
            }

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getPath(this, selectedImageUri);
            if (imagePath != null) {
                imageFile = new File(imagePath);
                if (isUploadBackground) {
                    presenter.uploadUserBackground(imageFile);
                } else {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(avatarImage);
                }
            }
        }
    }


    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new SettingProfilePermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();

        readStoragePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
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
