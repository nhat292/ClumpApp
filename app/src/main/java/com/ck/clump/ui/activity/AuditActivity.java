package com.ck.clump.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.GetActivitiesResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.AuditAdapter;
import com.ck.clump.ui.activity.model.Audit;
import com.ck.clump.ui.presenter.AuditPresenter;
import com.ck.clump.ui.view.AuditView;
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

public class AuditActivity extends BaseActivity implements AuditView {

    @Inject
    public Service service;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.rvItems)
    RecyclerView rvItems;
    @Bind(R.id.textActivityEmpty)
    TextView textActivityEmpty;
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
    private List<Audit> mAudits;
    private AuditAdapter mAdapter;
    private AuditPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectAuditActivity(this);
        renderView();
        presenter = new AuditPresenter(service, this);
        presenter.getActivities();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_audit);
        ButterKnife.bind(this);
        setupSwipeRefreshLayout();

        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        textActivityEmpty.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
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
        GetActivitiesResponse response = (GetActivitiesResponse) o;
        mAudits.clear();
        mAudits.addAll(response.getdATA());
        mAdapter.notifyDataSetChanged();

        if (response.getdATA().size() <= 0) {
            textActivityEmpty.setVisibility(View.VISIBLE);
        } else {
            textActivityEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        finish();
    }

    private void setupSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getActivities();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mAudits = new ArrayList<>();
        mAdapter = new AuditAdapter(mContext, mAudits, new AuditAdapter.OnItemClickListener() {
            @Override
            public void onClick(Audit Item) {

            }
        });
        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(mAdapter);
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
