package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.request.InviteFriendRequest;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.InviteFriendAdapter;
import com.ck.clump.ui.activity.model.InviteFriend;
import com.ck.clump.ui.listener.permission.InviteFriendPermissionListener;
import com.ck.clump.ui.presenter.InviteFriendPresenter;
import com.ck.clump.ui.view.InviteFriendView;
import com.ck.clump.ui.widget.ProgressWheel;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Constant;
import com.ck.clump.util.County2Phone;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteFriendActivity extends BaseActivity implements InviteFriendView {

    @Inject
    public Service service;

    @Bind(R.id.rvItems)
    RecyclerView rvItems;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    @Bind(R.id.btnInvite)
    Button btnInvite;
    @Bind(R.id.loadingContacts)
    ProgressWheel loadingContacts;
    @Bind(R.id.tvHeader)
    TextView tvHeader;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private InviteFriendAdapter mAdapter;
    private List<InviteFriend> mInviteFriends = new ArrayList<>();
    private List<InviteFriend> mOriginalInviteFriends = new ArrayList<>();
    private int selectedCount = 0;
    private PermissionListener contactPermissionListener;
    private List<String> mPhoneUsers = new ArrayList<>();
    private List<String> mCheckExistedPhoneUsers = new ArrayList<>();
    private InviteFriendPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectInviteFriendActivity(this);
        renderView();
        presenter = new InviteFriendPresenter(service, this);
        /*Permissions*/
        createPermissionListeners();
        checkContactPermission();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);
        setupRecyclerView();

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnInvite.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtSearch.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtSearch.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
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
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getString(R.string.info_title))
                .setContentText(getString(R.string.message_invite_success))
                .setConfirmText(this.getString(R.string.ok))
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        KeyboardUtil.hideKeyboard(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        onClickLeft();
    }

    @OnClick(R.id.btnInvite)
    public void onClickInviteFriends() {
        if (selectedCount == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(this.getString(R.string.error_title))
                    .setContentText(this.getString(R.string.choose_at_lease_friend))
                    .setConfirmText(this.getString(R.string.ok))
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            List<String> phones = new ArrayList<>();
            for (InviteFriend model : mInviteFriends) {
                if (model.getType() == InviteFriend.TYPE_SELECTED) {
                    phones.add(model.getPhone());
                }
            }
            presenter.postInviteFriend(new InviteFriendRequest(phones));
        }
    }

    private void setupRecyclerView() {
        mAdapter = new InviteFriendAdapter(mContext, mInviteFriends, new InviteFriendAdapter.OnItemClickListener() {
            @Override
            public void onClick(InviteFriend Item) {
                if (Item.getType() != InviteFriend.TYPE_CLUMP) {
                    if (Item.getType() == InviteFriend.TYPE_UNSELECTED) {
                        Item.setType(InviteFriend.TYPE_SELECTED);
                        selectedCount++;
                    } else {
                        Item.setType(InviteFriend.TYPE_UNSELECTED);
                        selectedCount--;
                    }
                    mAdapter.notifyDataSetChanged();
                    if (selectedCount > 1) {
                        btnInvite.setText(String.format(mContext.getString(R.string.button_invite_many), String.valueOf(selectedCount)));
                    } else if (selectedCount == 1) {
                        btnInvite.setText(mContext.getString(R.string.button_invite_one));
                    } else {
                        btnInvite.setText(mContext.getString(R.string.button_invite));
                    }
                }
            }
        });
        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(mAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mInviteFriends.clear();
                for (int i = 0; i < mOriginalInviteFriends.size(); i++) {
                    InviteFriend inviteFriend = mOriginalInviteFriends.get(i);
                    if (inviteFriend.getName().toLowerCase().contains(editable.toString().toLowerCase())) {
                        mInviteFriends.add(inviteFriend);
                    }
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new InviteFriendPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        contactPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.contacts_permission_denied_feedback)
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
        readContactsPhone();
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    }

    private void checkContactPermission() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(contactPermissionListener, Manifest.permission.READ_CONTACTS);
    }

    private void readContactsPhone() {
        List<UserModel> userModels = getRealm().where(UserModel.class).findAll();
        for (UserModel user : userModels) {
            mPhoneUsers.add(user.getPhone());
        }
        new AsyncReadContacts().execute();
    }

    private class AsyncReadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            County2Phone county2Phone = new County2Phone();
            String country = county2Phone.prefixFor(tm.getSimCountryIso());
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!phone.startsWith("+")) {
                                phone = phone.replaceAll("[^\\d.+]", "");
                                phone = phone.substring(1, phone.length());
                                phone = country + phone;
                            }
                            if (!TextUtils.isEmpty(phone) && !mCheckExistedPhoneUsers.contains(phone)) {
                                mCheckExistedPhoneUsers.add(phone);
                                int type = InviteFriend.TYPE_UNSELECTED;
                                for (String item : mPhoneUsers) {
                                    if (item.contains(phone)) {
                                        type = InviteFriend.TYPE_CLUMP;
                                        break;
                                    }
                                }
                                mOriginalInviteFriends.add(new InviteFriend(phone, name, type));
                            }
                        }
                        pCur.close();
                    }
                }
            }
            for (int i = 0; i < mOriginalInviteFriends.size(); i++) {
                mInviteFriends.add(mOriginalInviteFriends.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.notifyDataSetChanged();
            loadingContacts.setVisibility(View.GONE);
            rvItems.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            loadingContacts.setVisibility(View.VISIBLE);
            rvItems.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
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
