package com.ck.clump.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.PickFriendSelectedAdapter;
import com.ck.clump.ui.activity.adapter.StickyContractHeadersAdapter;
import com.ck.clump.ui.activity.model.Contact;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.presenter.InviteFriendEventPresenter;
import com.ck.clump.ui.view.InviteFriendEventView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class InviteFriendEventActivity extends BaseActivity implements InviteFriendEventView {

    @Inject
    public Service service;

    @Bind(R.id.rvItems)
    RecyclerView rvItems;
    @Bind(R.id.rvSelectedItems)
    RecyclerView rvSelectedItems;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvRight)
    TextView imvRight;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private List<Contact> mContacts;
    private List<Contact> mOriginContacts;
    private StickyContractHeadersAdapter mAdapter;
    private PickFriendSelectedAdapter mAdapterSelected;
    private List<Friend> mPickFriendsSelected = new ArrayList<>();
    private InviteFriendEventPresenter presenter;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.FINISH_ACTIVITIES_AFTER_CREATE_EVENT_BROADCAST)) {
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectInviteFriendEventActivity(this);
        renderView();
        presenter = new InviteFriendEventPresenter(service, this);
        presenter.getContactsList("CONTACT");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constant.FINISH_ACTIVITIES_AFTER_CREATE_EVENT_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_invite_friend_event);
        ButterKnife.bind(this);
        setupRecyclerView();

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtSearch.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtSearch.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    @Override
    public void showWait() {
        swipeContainer.setRefreshing(true);
    }

    @Override
    public void removeWait() {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onFailure(String appErrorMessage, boolean finishActivity) {
        showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        ContactResponse response = (ContactResponse) o;
        mContacts.clear();
        mOriginContacts.clear();
        final List<GroupModel> lstGroup = response.getDATA().getGroups();
        final List<UserModel> lstUser = response.getDATA().getUsers();
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(GroupModel.class, new Gson().toJson(lstGroup));
                realm.createOrUpdateAllFromJson(UserModel.class, new Gson().toJson(lstUser));
            }
        });
        if (lstGroup.size() > 0) {
            for (GroupModel model : lstGroup) {
                mContacts.add(new Contact(model.getId(), model.getImagePath(), model.getName(), Contact.TYPE_GROUP));
            }
        }

        if (lstUser.size() > 0) {
            for (UserModel model : lstUser) {
                mContacts.add(new Contact(model.getId(), model.getAvatarPath(), model.getDisplayName(), Contact.TYPE_PERSONAL));
            }
        }
        mOriginContacts.addAll(mContacts);
        mAdapter.notifyDataSetChanged();
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

    @OnClick(R.id.imvRight)
    public void onClickOK() {
        if (mPickFriendsSelected.size() == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(this.getString(R.string.error_title))
                    .setContentText(this.getString(R.string.choose_friends_or_group))
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
            Intent intent = new Intent(mContext, EventSetupActivity.class);
            ArrayList<String> friendsIDArr = new ArrayList<>();
            for (Friend friend : mPickFriendsSelected) {
                friendsIDArr.add(friend.getId());
            }
            intent.putStringArrayListExtra("friendsID", friendsIDArr);
            startActivity(intent);
        }
    }

    private void setupRecyclerView() {
        mContacts = new ArrayList<>();
        mOriginContacts = new ArrayList<>();
        // Groups

        mAdapter = new StickyContractHeadersAdapter(mContext, mContacts, new StickyContractHeadersAdapter.OnItemClickListener() {
            @Override
            public void onClick(Contact Item, boolean isInfoClick) {
                if (Item.isPick()) {
                    if (Item.getType() == Contact.TYPE_GROUP) {
                        Item.setPick(false);
                        Intent intent = new Intent(mContext, EventSetupActivity.class);
                        intent.putExtra("groupID", Item.getId());
                        startActivity(intent);
                    } else {
                        Item.setPick(false);
                        if (mPickFriendsSelected.size() > 0) {
                            for (int i = 0; i < mPickFriendsSelected.size(); i++) {
                                if (mPickFriendsSelected.get(i).getId().equals(Item.getId())) {
                                    mPickFriendsSelected.remove(mPickFriendsSelected.get(i));
                                }
                            }
                        }
                    }
                } else {
                    if (Item.getType() == Contact.TYPE_GROUP) {
                        Item.setPick(false);
                        Intent intent = new Intent(mContext, EventSetupActivity.class);
                        intent.putExtra("groupID", Item.getId());
                        startActivity(intent);
                    } else {
                        Item.setPick(true);
                        mPickFriendsSelected.add(new Friend(Item.getId(), Item.getAvatar(), Item.getName(), false));
                    }
                }

                mAdapter.notifyDataSetChanged();
                mAdapterSelected.notifyDataSetChanged();
            }
        }, true);
        mAdapter.setType(StickyContractHeadersAdapter.TYPE_INVITE_FRIENDS);
        rvItems.setAdapter(mAdapter);

        // Set layout manager
        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        rvItems.addItemDecoration(headersDecor);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        // Add touch listeners
        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(rvItems, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {

                    }
                });
        rvItems.addOnItemTouchListener(touchListener);

        // Search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content = charSequence.toString();
                searchContent(content);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Selected
        mAdapterSelected = new PickFriendSelectedAdapter(mContext, mPickFriendsSelected, new PickFriendSelectedAdapter.OnItemClickListener() {
            @Override
            public void onClick(Friend Item) {
                mPickFriendsSelected.remove(Item);
                for (int i = 0; i < mContacts.size(); i++) {
                    if (mContacts.get(i).getId().equals(Item.getId())) {
                        mContacts.get(i).setPick(false);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mAdapterSelected.notifyDataSetChanged();
            }
        });
        rvSelectedItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedItems.setAdapter(mAdapterSelected);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getContactsList("CONTACT");
            }
        });
    }

    public void searchContent(String content) {
        mContacts.clear();
        for (Contact contact : mOriginContacts) {
            if (contact.getName().toLowerCase().contains(content.toLowerCase())) {
                mContacts.add(contact);
            }
        }
        mAdapter.notifyDataSetChanged();
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
