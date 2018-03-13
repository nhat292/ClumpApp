package com.ck.clump.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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

import com.ck.clump.App;
import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.PickFriendAdapter;
import com.ck.clump.ui.activity.adapter.PickFriendSelectedAdapter;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.presenter.PickFriendPresenter;
import com.ck.clump.ui.view.PickFriendView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;

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

public class PickFriendActivity extends BaseActivity implements PickFriendView {

    public static final int REQUEST_CREATE_GROUP = 1;

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
    private PickFriendAdapter mAdapter;
    private PickFriendSelectedAdapter mAdapterSelected;
    private List<Friend> mPickFriends = new ArrayList<>();
    private List<Friend> mPickFriendsSelected = new ArrayList<>();
    private List<Friend> mOriginalPickFriends = new ArrayList<>();
    private PickFriendPresenter presenter;
    private String channelId;
    private String action;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeps().injectPickFriendActivity(this);
        mContext = this;
        renderView();
        presenter = new PickFriendPresenter(service, this);
        presenter.getContactsList("CONTACT");
        Intent intent = getIntent();
        action = intent.getStringExtra("ACTION");
        if (action.equals("NEW_CHAT")) {
            tvHeader.setText("New Chat");
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CREATE_GROUP) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void renderView() {
        setContentView(R.layout.activity_pick_friend);
        ButterKnife.bind(this);
        setupRecyclerView();

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtSearch.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtSearch.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
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
            if (action.equals("PICK")) {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("DATA", (ArrayList<? extends Parcelable>) mPickFriendsSelected);
                setResult(RESULT_OK, newIntent);
                finish();
            } else {
                if (mPickFriendsSelected.size() == 1) {
                    presenter.getChannelID(new ChannelRequest(mPickFriendsSelected.get(0).getId()));
                } else {
                    Intent groupIntent = new Intent(mContext, CreatedGroupActivity.class);
                    groupIntent.putParcelableArrayListExtra("DATA", (ArrayList<? extends Parcelable>) mPickFriendsSelected);
                    startActivityForResult(groupIntent, REQUEST_CREATE_GROUP);
                }
            }
        }
    }

    private void setupRecyclerView() {
        mAdapter = new PickFriendAdapter(mContext, mPickFriends, new PickFriendAdapter.OnItemClickListener() {
            @Override
            public void onClick(Friend Item) {
                if (Item.isChecked()) {
                    Item.setChecked(false);
                    mPickFriendsSelected.remove(Item);
                } else {
                    Item.setChecked(true);
                    mPickFriendsSelected.add(Item);
                }
                mAdapter.notifyDataSetChanged();
                mAdapterSelected.notifyDataSetChanged();
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
                mPickFriends.clear();
                for (int i = 0; i < mOriginalPickFriends.size(); i++) {
                    Friend friend = mOriginalPickFriends.get(i);
                    if (friend.getDisplayName().toLowerCase().contains(editable.toString().toLowerCase())) {
                        mPickFriends.add(friend);
                    }
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        // Selected
        mAdapterSelected = new PickFriendSelectedAdapter(mContext, mPickFriendsSelected, new PickFriendSelectedAdapter.OnItemClickListener() {
            @Override
            public void onClick(Friend Item) {
                mPickFriendsSelected.remove(Item);
                for (int i = 0; i < mPickFriends.size(); i++) {
                    if (mPickFriends.get(i).getId().equals(Item.getId())) {
                        mPickFriends.get(i).setChecked(false);
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

    @Override
    public void showRefresh() {
        swipeContainer.setRefreshing(true);
    }

    @Override
    public void hideRefresh() {
        swipeContainer.setRefreshing(false);
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
        ContactResponse response = (ContactResponse) o;
        mPickFriends.clear();
        mOriginalPickFriends.clear();
        final List<GroupModel> lstGroup = response.getDATA().getGroups();
        final List<UserModel> lstUser = response.getDATA().getUsers();
        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(GroupModel.class, new Gson().toJson(lstGroup));
                realm.createOrUpdateAllFromJson(UserModel.class, new Gson().toJson(lstUser));
            }
        });
        if (lstUser.size() > 0) {
            for (UserModel userModel : lstUser) {
                mOriginalPickFriends.add(new Friend(userModel.getId(), userModel.getAvatarPath(), userModel.getDisplayName(), false));
            }
        }
        mPickFriends.addAll(mOriginalPickFriends);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @Override
    public void onGetChannelIDSuccess(ChannelResponse response) {
        channelId = response.getdATA().getChannelId();
        UserModel userModel = Realm.getInstance(App.getRealmConfig()).where(UserModel.class)
                .equalTo("id", mPickFriendsSelected.get(0).getId())
                .findFirst();
        if (userModel != null) {
            openChat(channelId, userModel);
        } else {
            presenter.getUserDetail(new FriendInfoRequest(mPickFriendsSelected.get(0).getId()));
        }
    }

    @Override
    public void onGetUserDetailSuccess(UserModel response) {
        openChat(channelId, response);
    }

    private void openChat(String channelID, UserModel userModel) {
        Intent intent = new Intent(mContext, ChatActivity.class);
        Chat chat = new Chat(userModel.getId(), channelID, userModel.getAvatarPath(), userModel.getDisplayName(), Chat.TYPE_PERSONAL, userModel.getStatus(), userModel.getLastMessage(), Common.convertTimestampToDate(userModel.getLastMessageTime()));
        intent.putExtra("CHAT_ITEM", chat);
        startActivity(intent);
        finish();
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
