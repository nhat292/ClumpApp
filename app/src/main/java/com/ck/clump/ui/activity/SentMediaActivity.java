package com.ck.clump.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.SentMediaAdapter;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.ui.presenter.SentMediaPresenter;
import com.ck.clump.ui.view.SentMediaView;
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

public class SentMediaActivity extends BaseActivity implements SentMediaView {

    @Inject
    public Service service;

    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.tvMedias)
    TextView tvMedias;
    @Bind(R.id.rvMedias)
    RecyclerView rvMedias;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private SentMediaAdapter mAdapterMedia;
    private List<Media> mMedias = new ArrayList<>();
    private SentMediaPresenter presenter;
    private String id;
    private boolean isGroup = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectSentMediaActivity(this);
        renderView();
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvMedias.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        } else {
            rvMedias.setLayoutManager(new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false));
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_sent_media);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra("displayName")) {
            tvHeader.setText(intent.getStringExtra("displayName"));
            tvMedias.setText(String.format(getString(R.string.medias), intent.getIntExtra("totalImage", 0)));
            id = intent.getStringExtra("id");
        }

        /*Medias*/
        mAdapterMedia = new SentMediaAdapter(mContext, mMedias, new SentMediaAdapter.OnItemClickListener() {
            @Override
            public void onClick(Media Item, int position) {
                Intent intent = new Intent(mContext, ViewMediaActivity.class);
                intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) mMedias);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rvMedias.setAdapter(mAdapterMedia);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvMedias.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));

        presenter = new SentMediaPresenter(service, this);
        if (intent.hasExtra("isGroup")) {
            isGroup = true;
        }
        presenter.loadMedia(isGroup ? null : id, isGroup ? id : null);
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
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
        MediaResponse response = (MediaResponse) o;
        mMedias.addAll(response.getdATA().getMedias());
        mAdapterMedia.notifyDataSetChanged();
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
