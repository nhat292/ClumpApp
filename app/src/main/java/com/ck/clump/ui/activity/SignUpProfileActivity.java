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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ck.clump.R;
import com.ck.clump.model.request.UserRequest;
import com.ck.clump.model.response.UserResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.listener.permission.SignUpProfilePermissionListener;
import com.ck.clump.ui.presenter.SignUpProfilePresenter;
import com.ck.clump.ui.view.SignUpProfileView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class SignUpProfileActivity extends BaseActivity implements SignUpProfileView {

    @Inject
    public Service service;

    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.edtDisplayName)
    EditText edtDisplayName;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvRight)
    TextView imvRight;
    @Bind(R.id.tvDisplayName)
    TextView tvDisplayName;
    @Bind(R.id.tvTutorial)
    TextView tvTutorial;

    /*Others*/
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private Context mContext;
    private SweetAlertDialog mDialog;
    private PermissionListener cameraPermissionListener;
    private PermissionListener readstoragePermissionListener;
    private PermissionListener writeStoragePermissionListener;
    private SignUpProfilePresenter presenter;
    private Uri fileUri;
    private File imageFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectSignUpProfileActivity(this);
        renderView();
        /*Permissions*/
        createPermissionListeners();
        presenter = new SignUpProfilePresenter(service, this);
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_sign_up_profile);
        ButterKnife.bind(this);


        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvDisplayName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtDisplayName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvTutorial.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtDisplayName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtDisplayName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
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
        final UserResponse response = (UserResponse) o;
        String apiToken = SharedPreference.getString(SharedPreference.KEY_API_TOKEN);
        response.getDATA().setSessionToken(apiToken);

        this.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateObjectFromJson(UserModel.class, new Gson().toJson(response.getDATA()));
            }
        });

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getString(R.string.info_title))
                .setContentText(getString(R.string.message_profile_updated))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        setResult(SignUpOneActivity.REQUEST_UPDATE_PROFILE);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void logout() {

    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        if (TextUtils.isEmpty(edtDisplayName.getText().toString())) {
            showErrorMessage(getString(R.string.message_please_input_display_name), false);
        } else {
            UserRequest request = new UserRequest(edtDisplayName.getText().toString());
            if (imageFile != null)
                request.setAvatar(imageFile);
            presenter.postUpdateProfile(request);
        }
    }

    @OnClick(R.id.avatarImage)
    public void onClickAvatar() {
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
        menus.add(mContext.getString(R.string.take_a_photo));
        menus.add(mContext.getString(R.string.choose_from_gallery));

        recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), menus, new MenuAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(String Item, int position) {
                mDialog.dismissWithAnimation();
                switch (position) {
                    case 0:
                        cameraIntent();
                        break;
                    case 1:
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
        Dexter.checkPermission(readstoragePermissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_GALLERY) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA) {
            imageFile = new File(fileUri.getPath());
            Glide.with(this)
                    .load(fileUri)
                    .into(avatarImage);

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = getPath(this, selectedImageUri);
            if (imagePath != null) {
                imageFile = new File(imagePath);
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(avatarImage);
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


    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new SignUpProfilePermissionListener(this);
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
}
