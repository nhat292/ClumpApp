package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.MainAdapter;
import com.ck.clump.ui.listener.permission.MainPermissionListener;
import com.ck.clump.ui.presenter.MainPresenter;
import com.ck.clump.ui.view.MainView;
import com.ck.clump.ui.widget.CustomSwipeViewPager;
import com.ck.clump.ui.widget.navigationbar.NavigationTabBar;
import com.ck.clump.util.Constant;
import com.ck.clump.util.County2Phone;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainView {

    public static final int REQUEST_CREATE_CHAT = 1;

    @Inject
    public Service service;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.navigateBar)
    NavigationTabBar navigationTabBar;
    @Bind(R.id.vpHorizontalContent)
    CustomSwipeViewPager viewPager;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvLeft)
    ImageView imvLeft;
    @Bind(R.id.imvRight)
    ImageView imvRight;
    @Bind(R.id.contentSearch)
    RelativeLayout contentSearch;
    @Bind(R.id.edtSearch)
    EditText edtSearch;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private MainPresenter presenter;
    public static final int ITEMS = 4;
    private MainAdapter mainAdapter;
    private Context mContext;
    private int mSelectedTab = 1;
    private PermissionListener contactPermissionsListener;

    public String channelIDFromIntent;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contains(Constant.REFRESH_EVENT_LIST_BROADCAST)) {
                mainAdapter.getEventsFragment().getEventList();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("channelID")) {
            channelIDFromIntent = getIntent().getStringExtra("channelID");
        }
        mContext = this;
        getDeps().injectMainActivity(this);
        renderView();
        setupNavigateBar();
        setupSearch();

        presenter = new MainPresenter(service, this);

        /*Permissions*/
        createPermissionListeners();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constant.REFRESH_EVENT_LIST_BROADCAST));
        checkContactPermission();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtSearch.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        navigationTabBar.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        navigationTabBar.setTitleSize(25.0f);

        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtSearch.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, Constant.OTHER_ACTIVITIES);
        boolean refreshChat = SharedPreference.getBool(SharedPreference.KEY_REFRESH_CHAT_LIST_WHEN_RESUME, false);
        try {
            if (refreshChat) {
                SharedPreference.deleteKeyAndValue(SharedPreference.KEY_REFRESH_CHAT_LIST_WHEN_RESUME);
                mainAdapter.getChatsFragment().getChatList();
            }
            boolean refreshContact = SharedPreference.getBool(SharedPreference.KEY_REFRESH_CONTACT_LIST_WHEN_RESUME, false);
            if (refreshContact) {
                SharedPreference.deleteKeyAndValue(SharedPreference.KEY_REFRESH_CONTACT_LIST_WHEN_RESUME);
                mainAdapter.getContactsFragment().getListContact();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainAdapter.getChatsFragment().getLocalData();
            }
        }, 500);
    }


    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (mSelectedTab) {
                    case 0:
                        if (mainAdapter.getContactsFragment() != null) {
                            mainAdapter.getContactsFragment().searchContent(editable.toString());
                        }
                        break;
                    case 1:
                        if (mainAdapter.getChatsFragment() != null) {
                            mainAdapter.getChatsFragment().searchContent(editable.toString());
                        }
                        break;
                }
            }
        });
    }

    private void setupNavigateBar() {
        mainAdapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainAdapter);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.tapbar_contacts_inactive),
                        Color.TRANSPARENT)
                        .selectedIcon(getResources().getDrawable(R.drawable.tapbar_contacts_active))
                        .title(getResources().getString(R.string.contacts))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.tapbar_chats_inactive),
                        Color.TRANSPARENT)
                        .selectedIcon(getResources().getDrawable(R.drawable.tapbar_chats_active))
                        .title(getResources().getString(R.string.chats))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.tapbar_events_inactive),
                        Color.TRANSPARENT)
                        .selectedIcon(getResources().getDrawable(R.drawable.tapbar_events_active))
                        .title(getResources().getString(R.string.events))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.tapbar_settings_inactive),
                        Color.TRANSPARENT)
                        .selectedIcon(getResources().getDrawable(R.drawable.tapbar_settings_active))
                        .title(getResources().getString(R.string.contacts))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 1);
        setupChats();
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                mSelectedTab = position;
                switch (position) {
                    case 0:
                        // Contacts
                        setupContacts();
                        break;
                    case 1:
                        // Chats
                        setupChats();
                        break;
                    case 2:
                        // Events
                        setupEvents();
                        break;
                    case 3:
                        // Settings
                        setupSettings();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    private void setupContacts() {
        tvHeader.setText(mContext.getString(R.string.contacts));
        contentSearch.setVisibility(View.VISIBLE);
        imvLeft.setVisibility(View.GONE);
        imvRight.setVisibility(View.VISIBLE);
        imvRight.setImageResource(R.drawable.toolbar_add_white);
    }

    private void setupChats() {
        tvHeader.setText(mContext.getString(R.string.chats));
        contentSearch.setVisibility(View.VISIBLE);
        imvLeft.setVisibility(View.GONE);
        imvRight.setVisibility(View.VISIBLE);
        imvRight.setImageResource(R.drawable.toolbar_create_white);
    }

    private void setupEvents() {
        tvHeader.setText(mContext.getString(R.string.events));
        contentSearch.setVisibility(View.GONE);
        imvLeft.setVisibility(View.VISIBLE);
        imvRight.setVisibility(View.VISIBLE);
        imvLeft.setImageResource(R.drawable.toolbar_feed_white);
        imvRight.setImageResource(R.drawable.toolbar_create_white);
    }

    private void setupSettings() {
        tvHeader.setText(mContext.getString(R.string.settings));
        contentSearch.setVisibility(View.GONE);
        imvLeft.setVisibility(View.GONE);
        imvRight.setVisibility(View.GONE);
    }

    @OnClick(R.id.imvLeft)
    public void onLeftIconClick() {
        switch (mSelectedTab) {
            case 0:
                // Contacts
                break;
            case 1:
                // Chats
                break;
            case 2:
                // Events
                Intent intentAudits = new Intent(this, AuditActivity.class);
                startActivity(intentAudits);
                break;
            case 3:
                // Settings
                break;
        }
    }

    @OnClick(R.id.imvRight)
    public void onRightIconClick() {
        switch (mSelectedTab) {
            case 0:
                // Contacts
                Intent intentInviteFriends = new Intent(this, InviteFriendActivity.class);
                startActivity(intentInviteFriends);
                break;
            case 1:
                // Chats
                Intent intentPickFriends = new Intent(this, PickFriendActivity.class);
                intentPickFriends.putExtra("ACTION", "NEW_CHAT");
                startActivityForResult(intentPickFriends, REQUEST_CREATE_CHAT);
                break;
            case 2:
                // Events
                Intent intentInviteFriendsEvent = new Intent(this, InviteFriendEventActivity.class);
                startActivity(intentInviteFriendsEvent);
                break;
            case 3:
                // Settings
                break;
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
        synchronized (this) {
            try {
                mainAdapter.getContactsFragment().getListContact();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }


    public void checkContactPermission() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(contactPermissionsListener, Manifest.permission.READ_CONTACTS);
    }

    /*Permission listener*/
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
        if (permission.equals(Manifest.permission.READ_CONTACTS)) {
            new AsyncReadContacts().execute();
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {

    }

    private void createPermissionListeners() {
        MainPermissionListener feedbackViewPermissionListener =
                new MainPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();

        contactPermissionsListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.contacts_permission_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
    }


    private class AsyncReadContacts extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> contacts = new ArrayList<>();
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            County2Phone county2Phone = new County2Phone();
            String country = county2Phone.prefixFor(tm.getSimCountryIso());
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    //String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
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
                            contacts.add(phone);
                        }
                        pCur.close();
                    }
                }
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            presenter.verifyContact(result);
        }

        @Override
        protected void onPreExecute() {
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CREATE_CHAT) {
                mainAdapter.getChatsFragment().getChatList();
            }
        }
    }

}
