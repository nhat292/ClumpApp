package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.request.RegisterRequest;
import com.ck.clump.model.request.UpdateTokenRequest;
import com.ck.clump.model.response.RegisterResponse;
import com.ck.clump.model.response.UpdateTokenResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.CountryAdapter;
import com.ck.clump.ui.activity.model.Country;
import com.ck.clump.ui.listener.permission.SignUpOnePermissionListener;
import com.ck.clump.ui.presenter.SignUpOnePresenter;
import com.ck.clump.ui.view.SignUpOneView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpOneActivity extends BaseActivity implements SignUpOneView {

    public static final int REQUEST_ACTIVE_CODE = 100;
    public static final int REQUEST_UPDATE_PROFILE = 200;

    @Inject
    public Service service;

    @Bind(R.id.asuoTvCountry)
    TextView asuoTvCountry;
    @Bind(R.id.asuoBtnCountry)
    Button asuoBtnCountry;
    @Bind(R.id.edtPhone)
    EditText edtPhone;
    @Bind(R.id.tvTutorial)
    TextView tvTutorial;
    @Bind(R.id.asuoBtnDone)
    Button asuoBtnDone;

    /*Others*/
    private Context mContext;
    private SweetAlertDialog mDialog;
    private List<Country> mCountryList = new ArrayList<>();
    private List<Country> mOriginalCountryList = new ArrayList<>();
    private CountryAdapter mAdapter;
    private String mCountryCode = "+65", mCountryName;
    private SignUpOnePresenter presenter;
    private PermissionListener smsPermissionListener;

    private GoogleCloudMessaging gcm;
    private String gcmRegId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectSignUpOneActivity(this);
        renderView();
        presenter = new SignUpOnePresenter(service, this);
        gcmRegister();
        createPermissionListeners();
        checkSmsPermission();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_sign_up_one);
        ButterKnife.bind(this);

        tvTutorial.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asuoBtnCountry.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        edtPhone.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asuoBtnDone.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        asuoTvCountry.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    @OnClick(R.id.asuoBtnDone)
    public void onClickDone() {
        String phoneNumber = edtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            showErrorMessage(getString(R.string.message_please_input_phone_number), false);
        } else {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = phoneNumber.substring(1, phoneNumber.length());
            }
            presenter.postRegister(new RegisterRequest(mCountryCode + phoneNumber, mCountryCode));
        }
    }

    @OnClick(R.id.asuoBtnCountry)
    public void onClickSelectCountry() {
        mDialog = new SweetAlertDialog(mContext, SweetAlertDialog.RECYCLER_VIEW_TYPE_COUNTRY);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        mDialog.hideButtonControl();
        mDialog.getTxtTitle().setText(getString(R.string.asuo_title_country));
        addCountryList();
        mCountryList.clear();
        for (int i = 0; i < mOriginalCountryList.size(); i++) {
            mCountryList.add(mOriginalCountryList.get(i));
        }
        updateCheckedCountry(mCountryList, mCountryCode);

        // Setup RecyclerView
        RecyclerView recyclerView = mDialog.getRecyclerView();
        EditText edtSearch = mDialog.getEdtSearch();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new CountryAdapter(getApplicationContext(), mCountryList,
                new CountryAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Country Item) {
                        updateCheckedCountry(mCountryList, Item.getCode());
                        mCountryCode = Item.getCode();
                        mCountryName = Item.getName();
                        asuoTvCountry.setText(mCountryCode);
                        asuoBtnCountry.setText(mCountryName);
                        mAdapter.notifyDataSetChanged();
                        mDialog.dismissWithAnimation();
                    }
                });
        recyclerView.setAdapter(mAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mCountryList.clear();
                for (int i = 0; i < mOriginalCountryList.size(); i++) {
                    Country country = mOriginalCountryList.get(i);
                    if (country.getName().toLowerCase().contains(editable.toString().toLowerCase())
                            || country.getCode().toLowerCase().contains(editable.toString().toLowerCase())) {
                        mCountryList.add(country);
                    }
                }
                if (mAdapter != null) {
                    updateCheckedCountry(mCountryList, mCountryCode);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addCountryList() {
        if (mOriginalCountryList.size() == 0) {
            String[] array = mContext.getResources().getStringArray(R.array.country_codes);
            for (String str : array) {
                String[] arrItem = str.split(";;;");
                Country country = new Country(arrItem[1], arrItem[0]);
                mOriginalCountryList.add(country);
            }
        }
    }

    private void updateCheckedCountry(List<Country> countries, String code) {
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            if (code != null && code.equalsIgnoreCase(country.getCode())) {
                country.setChecked(true);
            } else {
                country.setChecked(false);
            }
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
        RegisterResponse response = (RegisterResponse) o;
        Intent intent = new Intent(this, SignUpTwoActivity.class);
        intent.putExtra("active_code", response.getDATA().getActiveCode());
        intent.putExtra("phone", response.getDATA().getPhone());
        startActivityForResult(intent, REQUEST_ACTIVE_CODE);
    }

    @Override
    public void logout() {

    }

    @Override
    public void onSuccessUpdateToken(UpdateTokenResponse response) {
        goToMainScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_ACTIVE_CODE) {
            UserModel userModel = UserModel.currentUser();
            if (userModel.getDisplayName() == null) {
                Intent intent = new Intent(this, SignUpProfileActivity.class);
                startActivityForResult(intent, REQUEST_UPDATE_PROFILE);
            } else {
                updateDeviceToken();
            }
        } else if (resultCode == REQUEST_UPDATE_PROFILE) {
            updateDeviceToken();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateDeviceToken() {
        presenter.postUpdateDeviceToken(new UpdateTokenRequest(gcmRegId, "android"));
    }

    private void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkSmsPermission() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(smsPermissionListener, Manifest.permission.READ_SMS);
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

    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new SignUpOnePermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        smsPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.sms_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    }

    public void showPermissionGranted(String permission) {
    }

    /**
     * GCM Functionality.
     * In order to use GCM Push notifications you need an API key and a Sender ID.
     * Get your key and ID at - https://developers.google.com/cloud-messaging/
     */

    private void gcmRegister() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                gcmRegId = SharedPreference.getString(SharedPreference.GCM_REG_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(gcmRegId)) {
                registerInBackground();
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(Constant.TAG, "Registration ID already exists: " + gcmRegId);
                }
            }
        } else {
            Log.e("GCM-register", "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, Constant.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("GCM-check", "This device is not supported.");
                /*finish();*/
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new SignUpOneActivity.RegisterTask().execute();
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(SignUpOneActivity.this);
                }
                gcmRegId = gcm.register(getString(R.string.gcm_sender_id));
                msg = "Device registered, registration ID: " + gcmRegId;

                SharedPreference.saveString(SharedPreference.GCM_REG_ID, gcmRegId);
                Log.i("GCM-register", msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }
    }
}
