package com.ck.clump.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.BlockingResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.BlockingAdapter;
import com.ck.clump.ui.presenter.BlockingPresenter;
import com.ck.clump.ui.view.BlockingView;
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

/**
 * Created by Nhat on 7/19/17.
 */

public class BlockingActivity extends BaseActivity implements BlockingView {

    @Inject
    Service service;

    @Bind(R.id.rvBlocking)
    RecyclerView rvBlocking;
    @Bind(R.id.textBlockUsers)
    TextView textBlockUsers;
    @Bind(R.id.tvHeader)
    TextView tvHeader;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    private Context mContext;
    private BlockingPresenter presenter;
    private List<BlockingResponse.BlockingUser> mBlockingUsers = new ArrayList<>();
    private BlockingAdapter adapter;
    private BlockingResponse.BlockingUser unBlockObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectBlockingActivity(this);
        renderView();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_blocking);
        ButterKnife.bind(this);

        adapter = new BlockingAdapter(mContext, mBlockingUsers, new BlockingAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(BlockingResponse.BlockingUser Item) {
                unBlockObject = Item;
                presenter.unBlockUser(unBlockObject.getId());
            }
        });
        rvBlocking.setAdapter(adapter);

        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        textBlockUsers.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        presenter = new BlockingPresenter(service, this);
        presenter.getBlockingUsers();

    }

    @OnClick(R.id.imvLeft)
    public void onLeftClick() {
        finish();
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
        BlockingResponse response = (BlockingResponse) o;
        mBlockingUsers.clear();
        mBlockingUsers.addAll(response.getdATA().getUsers());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUnBlockSuccess(SimpleResponse response) {
        if (response.getcODE() == 200) {
            mBlockingUsers.remove(unBlockObject);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void logout() {
        clearDataAndLogout();
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
