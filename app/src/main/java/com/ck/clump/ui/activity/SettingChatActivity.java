package com.ck.clump.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.enums.ChatDisplayType;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhat on 7/19/17.
 */

public class SettingChatActivity extends BaseActivity {

    private int type;
    @Bind(R.id.btnFullMessage)
    Button btnFullMessage;
    @Bind(R.id.btnSecret)
    Button btnSecret;
    @Bind(R.id.tvHeader)
    TextView tvHeader;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderView();
    }


    private void renderView() {
        setContentView(R.layout.activity_chat_setting);
        ButterKnife.bind(this);

        type = SharedPreference.getInt(SharedPreference.KEY_CHAT_SHOW, ChatDisplayType.FULL.getValue());
        if (type == ChatDisplayType.FULL.getValue()) {
            btnFullMessage.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        } else {
            btnSecret.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        }

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnFullMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnSecret.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    @OnClick(R.id.btnFullMessage)
    public void onClickFull() {
        btnFullMessage.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        btnSecret.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        type = ChatDisplayType.FULL.getValue();
    }

    @OnClick(R.id.btnSecret)
    public void onClickSecret() {
        btnFullMessage.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnSecret.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        type = ChatDisplayType.SECRET.getValue();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
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
        SharedPreference.saveInt(SharedPreference.KEY_CHAT_SHOW, type);
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
