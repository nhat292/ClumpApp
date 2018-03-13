package com.ck.clump.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ck.clump.App;
import com.ck.clump.R;
import com.ck.clump.deps.DaggerDeps;
import com.ck.clump.deps.Deps;
import com.ck.clump.service.NetworkModule;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.SharedPreference;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Pubnub;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;

public abstract class BaseActivity extends AppCompatActivity {

    private Deps deps;
    private Dialog mDialog;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getApplicationContext();
        getApplication().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getApplication().getContentResolver().unregisterContentObserver(contactObserver);
        super.onDestroy();
    }

    private ContentObserver contactObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            SharedPreference.saveBool(SharedPreference.KEY_REFRESH_CONTACT_LIST_WHEN_RESUME, true);
        }
    };

    public Deps getDeps() {
        if (deps == null) {
            File cacheFile = new File(getCacheDir(), "responses");
            deps = DaggerDeps.builder().networkModule(new NetworkModule(cacheFile)).build();
        }
        return deps;
    }

    public Realm getRealm() {
        return app.getRealm();
    }

    public Pubnub getmPubNub() {
        return app.getPubNub();
    }

    private GoogleCloudMessaging gcm;

    public void clearDataAndLogout() {
        String gcmRegId = SharedPreference.getString(SharedPreference.GCM_REG_ID);
        if (!TextUtils.isEmpty(gcmRegId)) {
            List<GroupModel> lstGroup = getRealm()
                    .where(GroupModel.class)
                    .findAll();
            List<UserModel> lstUser = getRealm()
                    .where(UserModel.class)
                    .notEqualTo("id", UserModel.currentUser().getId())
                    .findAll();
            for (GroupModel item : lstGroup) {
                if (item.getChannelId() != null) {
                    getmPubNub().disablePushNotificationsOnChannel(item.getChannelId(), gcmRegId);
                }
            }
            for (UserModel item : lstUser) {
                if (item.getChannelId() != null) {
                    getmPubNub().disablePushNotificationsOnChannel(item.getChannelId(), gcmRegId);
                }
            }
        }
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        unregister();
        SharedPreference.clearAll();
        SharedPreference.saveBool(SharedPreference.KEY_FIRST_TIME, false);
        Intent intent = new Intent(this, SignUpOneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void unregister() {
        new BaseActivity.UnRegisterTask().execute();
    }

    private class UnRegisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(BaseActivity.this);
                }
                gcm.unregister();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.progress_dialog);
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            ((ProgressBar) mDialog.findViewById(R.id.dialog_progress_bar)).getIndeterminateDrawable()
                    .setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void showErrorMessage() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.getString(R.string.error_title))
                .setContentText(this.getString(R.string.error_message))
                .show();
    }

    public void showErrorMessage(String message, final boolean finishActivity) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.getString(R.string.error_title))
                .setContentText(message);
        sweetAlertDialog.show();
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                if (finishActivity) {
                    finish();
                }
            }
        });
    }

    public void showInfoMessage(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getString(R.string.info_title))
                .setContentText(message)
                .show();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
