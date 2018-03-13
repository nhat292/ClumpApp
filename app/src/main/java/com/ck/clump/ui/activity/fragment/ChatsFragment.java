package com.ck.clump.ui.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ck.clump.App;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.response.ChatResponse;
import com.ck.clump.model.response.GetExitInfoResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.ChatActivity;
import com.ck.clump.ui.activity.MainActivity;
import com.ck.clump.ui.activity.adapter.ChatListAdapter;
import com.ck.clump.ui.activity.adapter.VerticalSpaceItemDecoration;
import com.ck.clump.ui.activity.model.Chat;
import com.ck.clump.ui.activity.model.ChatMessage;
import com.ck.clump.ui.activity.model.Exit;
import com.ck.clump.ui.listener.BasicCallback;
import com.ck.clump.ui.presenter.fragment.ChatsFragmentPresenter;
import com.ck.clump.ui.view.fragment.ChatsFragmentView;
import com.ck.clump.ui.widget.DeleteChatDialog;
import com.ck.clump.util.Common;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatsFragment extends BaseFragment implements ChatsFragmentView {

    public static final int CHECK_REFRESH_REQUEST = 1;

    @Inject
    public Service service;

    private Context mContext;
    private List<Chat> mOriginChats;
    private List<Chat> mChats;
    private ChatListAdapter mAdapter;
    private ChatsFragmentPresenter presenter;
    private String leaveChannelID;
    private String channelsHide = "";
    private int deleteIndex = 0;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.rvItems)
    RecyclerView rvItems;
    @Bind(R.id.imvEmpty)
    ImageView imvEmpty;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        channelsHide = SharedPreference.getString(SharedPreference.KEY_ID_CHANNEL_HIDE);
        if (channelsHide == null) {
            channelsHide = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        mContext = getActivity();
        baseActivity.getDeps().injectChatsFragment(this);
        presenter = new ChatsFragmentPresenter(service, this);
        ButterKnife.bind(this, view);
        setupSwipeRefreshLayout();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == CHECK_REFRESH_REQUEST) {
            getLocalData();
        }
    }

    private void setupSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getChatsList(true);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary);

        setupRecyclerView();
        getLocalData();
        presenter.getChatsList(false);
    }

    private void setupRecyclerView() {
        mChats = new ArrayList<>();
        mOriginChats = new ArrayList<>();

        mAdapter = new ChatListAdapter(mContext, mChats, new ChatListAdapter.OnItemListener() {
            @Override
            public void onClick(Chat Item, int position) {
                chatClick(Item, position);
            }

            @Override
            public void onDelete(final Chat Item, final int position) {
                String message = Item.getType() == Chat.TYPE_PERSONAL ? String.format(getString(R.string.delete_chat_with_format), Item.getName()) : String.format(getString(R.string.delete_chat_group_format), Item.getName());
                new DeleteChatDialog(getActivity(), message, new DeleteChatDialog.DeleteClickListener() {
                    @Override
                    public void onClick() {
                        deleteIndex = position;
                        leaveChannelID = Item.getChanelId();
                        presenter.leaveChat(leaveChannelID, UserModel.currentUser().getId());
                    }
                }).show();
            }
        });
        rvItems.setLayoutManager(new LinearLayoutManager(rvItems.getContext(), LinearLayoutManager.VERTICAL, false));
        rvItems.addItemDecoration(new VerticalSpaceItemDecoration(2));
        rvItems.setAdapter(mAdapter);
    }

    //Call from MainActivity
    public void getChatList() {
        presenter.getChatsList(true);
    }

    public void searchContent(String content) {
        mChats.clear();
        for (Chat chat : mOriginChats) {
            if (chat.getName().toLowerCase().contains(content.toLowerCase())) {
                mChats.add(chat);
            }
        }
        mAdapter.notifyDataSetChanged();
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
        baseActivity.showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        ChatResponse response = (ChatResponse) o;
        final List<GroupModel> lstGroup = response.getDATA().getGroups();
        final List<UserModel> lstUser = response.getDATA().getUsers();
        baseActivity.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(GroupModel.class, new Gson().toJson(lstGroup));
                realm.createOrUpdateAllFromJson(UserModel.class, new Gson().toJson(lstUser));
            }
        });
        showList(lstGroup, lstUser, channelsHide);
        //Get exit info
        presenter.getExitInfo();
    }

    @Override
    public void logout() {
        baseActivity.clearDataAndLogout();
    }

    @Override
    public void onLeaveChatSuccess(SimpleResponse response) {
        mChats.remove(deleteIndex);
        mAdapter.notifyItemRemoved(deleteIndex);
        channelsHide += leaveChannelID + "@@";
        String userIds = SharedPreference.getString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + leaveChannelID);
        if (userIds == null) {
            userIds = "";
        }
        String userId = UserModel.currentUser().getId();
        if (!userIds.contains(userId)) {
            userIds += userId + "@@";
        }
        SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_HIDE, channelsHide);
        SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + leaveChannelID, userIds);
        //Clear local message
        baseActivity.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ChatMessage> rows = realm.where(ChatMessage.class).equalTo("channelId", leaveChannelID).findAll();
                rows.deleteAllFromRealm();
            }
        });
        //Send a delete message
        try {
            baseActivity.getmPubNub().subscribe(leaveChannelID, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    String content = Common.getDeleteMessage();
                    Map<String, Object> payload = new HashMap<>();
                    JSONObject pn_gcm = new JSONObject();
                    JSONObject pn_gcm_data = new JSONObject();
                    JSONObject pn_apns = new JSONObject();
                    JSONObject pn_apns_data = new JSONObject();
                    try {
                        pn_gcm_data.put("channel_id", leaveChannelID);
                        pn_gcm_data.put("content", content);
                        pn_gcm.put("data", pn_gcm_data);

                        pn_apns_data.put("alert", "");
                        pn_apns_data.put("channel_id", leaveChannelID);
                        pn_apns.put("aps", pn_apns_data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    payload.put("pn_gcm", pn_gcm);
                    payload.put("pn_apns", pn_apns);
                    payload.put("pn_other", content);
                    payload.put("pn_debug", true);
                    baseActivity.getmPubNub().publish(leaveChannelID, new JSONObject(payload), true, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            baseActivity.getmPubNub().unsubscribe(leaveChannelID);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            baseActivity.getmPubNub().unsubscribe(leaveChannelID);
                        }
                    });
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetExitInfoSuccess(GetExitInfoResponse response) {
        channelsHide = "";
        for (Chat chat : mChats) {
            SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + chat.getChanelId(), "");
        }
        if (response.getdATA().size() > 0) {
            for (Exit exit : response.getdATA()) {
                if (exit.getLeaveDate() > exit.getRejoinDate()) {
                    channelsHide += exit.getChannelId() + "@@";
                    SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + exit.getChannelId(),
                            SharedPreference.getString(SharedPreference.KEY_ID_CHANNEL_USER_HIDE + "-" + exit.getChannelId()) + exit.getUserId() + "@@");
                }
            }
        }
        SharedPreference.saveString(SharedPreference.KEY_ID_CHANNEL_HIDE, channelsHide);
        getLocalData();
    }

    public void getLocalData() {
        List<GroupModel> lstGroup = baseActivity.getRealm()
                .where(GroupModel.class)
                .findAll();
        List<UserModel> lstUser = baseActivity.getRealm()
                .where(UserModel.class)
                .notEqualTo("id", UserModel.currentUser().getId())
                .findAll();
        showList(lstGroup, lstUser, channelsHide);
    }

    private void showList(List<GroupModel> lstGroup, List<UserModel> lstUser, String channelsHide) {
        mOriginChats.clear();
        mChats.clear();
        String gcmRegId = SharedPreference.getString(SharedPreference.GCM_REG_ID);
        for (GroupModel item : lstGroup) {
            ChatMessage message = null;
            if (item.getChannelId() != null) {
                if (!channelsHide.contains(item.getChannelId())) {
                    SharedPreference.deleteKeyAndValue(SharedPreference.KEY_NUMBER_MESSAGE_CHANNEL + "-" + item.getChannelId());
                    baseActivity.getmPubNub().enablePushNotificationsOnChannel(item.getChannelId(), gcmRegId, new BasicCallback());
                    try {
                        message = baseActivity.getRealm()
                                .where(ChatMessage.class)
                                .equalTo("channelId", item.getChannelId())
                                .findAllSorted("timeSend", Sort.DESCENDING).first();
                    } catch (IndexOutOfBoundsException e) {
                        Log.d(getClass().getSimpleName(), item.getName() + " - No chat recently");
                    }
                    Chat chat = new Chat(item.getId(), item.getChannelId(), item.getImagePath(), item.getName(), Chat.TYPE_GROUP, null, item.getLastMessage(), Common.convertTimestampToDate(item.getLastMessageTime()));
                    chat.setMessage(message);
                    int count = SharedPreference.getInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + item.getChannelId(), 0);
                    chat.setAmount(count);
                    mOriginChats.add(chat);
                } else {
                    baseActivity.getmPubNub().disablePushNotificationsOnChannel(item.getChannelId(), gcmRegId);
                }
            }
        }
        for (UserModel item : lstUser) {
            if (item.getChannelId() != null) {
                ChatMessage message = null;
                if (!channelsHide.contains(item.getChannelId())) {
                    SharedPreference.deleteKeyAndValue(SharedPreference.KEY_NUMBER_MESSAGE_CHANNEL + "-" + item.getChannelId());
                    baseActivity.getmPubNub().enablePushNotificationsOnChannel(item.getChannelId(), gcmRegId, new BasicCallback());
                    try {
                        message = baseActivity.getRealm()
                                .where(ChatMessage.class)
                                .equalTo("channelId", item.getChannelId())
                                .findAllSorted("timeSend", Sort.DESCENDING).first();
                    } catch (IndexOutOfBoundsException e) {
                        Log.d(getClass().getSimpleName(), item.getDisplayName() + " - No chat recently");
                    }
                    Chat chat = new Chat(item.getId(), item.getChannelId(), item.getAvatarPath(), item.getDisplayName(), Chat.TYPE_PERSONAL, item.getStatus(), item.getLastMessage(), Common.convertTimestampToDate(item.getLastMessageTime()));
                    chat.setMessage(message);
                    int count = SharedPreference.getInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + item.getChannelId(), 0);
                    chat.setAmount(count);
                    mOriginChats.add(chat);
                } else {
                    baseActivity.getmPubNub().disablePushNotificationsOnChannel(item.getChannelId(), gcmRegId);
                }
            }
        }

        // Sort
        Collections.sort(mOriginChats, new Comparator<Chat>() {
            public int compare(Chat o1, Chat o2) {
                if (o1.getMessage() == null) {
                    return 1;
                } else if (o2.getMessage() == null) {
                    return -1;
                } else if (o1.getMessage().getTimeSend() > o2.getMessage().getTimeSend()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        mChats.addAll(mOriginChats);
        if (mOriginChats.size() > 0) {
            rvItems.setVisibility(View.VISIBLE);
            imvEmpty.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            rvItems.setVisibility(View.GONE);
            imvEmpty.setVisibility(View.VISIBLE);
        }

        checkAndPerformClick();
    }

    private void chatClick(Chat Item, int position) {
        Common.cancelNotification(App.getInstance(), Common.getNotificationIDFromChannelID(Item.getChanelId()));
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("CHAT_ITEM", Item);
        startActivityForResult(intent, CHECK_REFRESH_REQUEST);
        SharedPreference.saveInt(SharedPreference.KEY_CHAT_MESSAGE_COUNT + "-" + Item.getChanelId(), 0);
        Item.setAmount(0);
        mAdapter.notifyItemChanged(position);
    }

    private void checkAndPerformClick() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.channelIDFromIntent != null) {
            if (mChats.size() > 0) {
                for (int i = 0; i < mChats.size(); i++) {
                    if (mChats.get(i).getChanelId().equals(mainActivity.channelIDFromIntent)) {
                        mainActivity.channelIDFromIntent = null;
                        chatClick(mChats.get(i), i);
                        break;
                    }
                }
            }
        }
    }
}
