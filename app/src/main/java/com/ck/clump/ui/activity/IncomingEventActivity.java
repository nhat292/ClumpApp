package com.ck.clump.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.IncomingEventResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.IncomingEventAdapter;
import com.ck.clump.ui.activity.model.EventModel;
import com.ck.clump.ui.presenter.IncomingEventPresenter;
import com.ck.clump.ui.view.IncomingEventView;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

/**
 * Created by Nhat on 7/18/17.
 */

public class IncomingEventActivity extends BaseActivity implements IncomingEventView {

    public static final int REQUEST_EVENT_DETAIL = 1;

    @Inject
    Service service;

    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.rvInComingEvents)
    RecyclerView rvInComingEvents;
    @Bind(R.id.avatarImage)
    CircleImageView avatarImage;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.txtNoEvents)
    TextView txtNoEvents;
    @Bind(R.id.btnPlanEvent)
    Button btnPlanEvent;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    private Context mContext;
    private String id;
    private boolean isGroup;
    private IncomingEventPresenter presenter;
    private List<EventModel> eventModels = new ArrayList<>();
    private IncomingEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectInComingEventActivity(this);
        renderView();
    }

    private void renderView() {
        setContentView(R.layout.activity_incoming_event);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        isGroup = intent.getBooleanExtra("IS_GROUP", false);
        String name = intent.getStringExtra("NAME");
        String avatarPath = intent.getStringExtra("AVATAR");

        tvName.setText(name);
        int placeholder = isGroup ? R.drawable.default_profile_group : R.drawable.default_profile_user;

        Glide.with(this)
                .load(BuildConfig.BASEURL + avatarPath)
                .apply(new RequestOptions()
                        .centerCrop()
                        .error(placeholder)
                        .placeholder(placeholder))
                .apply(overrideOf(100, 100))
                .into(avatarImage);

        adapter = new IncomingEventAdapter(mContext, eventModels, new IncomingEventAdapter.OnItemClickListener() {
            @Override
            public void onClick(EventModel Item) {
                Intent intent = new Intent(mContext, EventDetailActivity.class);
                intent.putExtra("eventID", Item.getId());
                intent.putExtra("eventStatus", Item.getStatus());
                startActivityForResult(intent, REQUEST_EVENT_DETAIL);
            }
        });
        rvInComingEvents.setAdapter(adapter);
        setupSwipeRefreshLayout();

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnPlanEvent.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        txtNoEvents.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        presenter = new IncomingEventPresenter(service, this);
        presenter.getIncomingEvent(isGroup ? null : id, isGroup ? id : null);
    }

    private void setupSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getIncomingEvent(isGroup ? null : id, isGroup ? id : null);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary);
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        finish();
    }

    @OnClick(R.id.avatarImage)
    public void onClickAvatar() {
        Intent intent;
        if (!isGroup) {
            intent = new Intent(mContext, FriendInfoActivity.class);
            intent.putExtra("userId", id);
        } else {
            intent = new Intent(mContext, GroupInfoActivity.class);
            intent.putExtra("groupId", id);
        }
        startActivity(intent);
    }

    @OnClick(R.id.btnPlanEvent)
    public void onClickPlanEvent() {
        Intent intent = new Intent(mContext, EventSetupActivity.class);
        if (isGroup) {
            intent.putExtra("groupID", id);
        } else {
            ArrayList<String> friendsIDArr = new ArrayList<>();
            friendsIDArr.add(id);
            intent.putStringArrayListExtra("friendsID", friendsIDArr);
        }
        startActivity(intent);
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
        IncomingEventResponse response = (IncomingEventResponse) o;
        eventModels.clear();
        eventModels.addAll(response.getdATA());
        adapter.notifyDataSetChanged();
        if (eventModels.size() > 0) {
            txtNoEvents.setVisibility(View.GONE);
        } else {
            txtNoEvents.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_EVENT_DETAIL) {
            presenter.getIncomingEvent(isGroup ? null : id, isGroup ? id : null);
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
