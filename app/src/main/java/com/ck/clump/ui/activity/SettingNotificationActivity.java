package com.ck.clump.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.enums.NotificationType;
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

public class SettingNotificationActivity extends BaseActivity {


    @Bind(R.id.btnSound)
    Button btnSound;
    @Bind(R.id.btnVibrate)
    Button btnVibrate;
    @Bind(R.id.btnMute)
    Button btnMute;
    @Bind(R.id.tvHeader)
    TextView tvHeader;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private int notificationType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderView();
    }

    private void renderView() {
        setContentView(R.layout.activity_setting_notification);
        ButterKnife.bind(this);

        notificationType = SharedPreference.getInt(SharedPreference.KEY_NOTIFICATION_TYPE, NotificationType.SOUND.getValue());
        if (notificationType == NotificationType.SOUND.getValue()) {
            btnSound.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        } else if (notificationType == NotificationType.VIBRATE.getValue()) {
            btnVibrate.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        } else {
            btnMute.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        }

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnSound.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnVibrate.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnMute.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }


    @OnClick(R.id.btnSound)
    public void onClickSound() {
        btnSound.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        btnVibrate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnMute.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        notificationType = NotificationType.SOUND.getValue();
    }

    @OnClick(R.id.btnVibrate)
    public void onClickVibrate() {
        btnSound.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnVibrate.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        btnMute.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        notificationType = NotificationType.VIBRATE.getValue();

    }

    @OnClick(R.id.btnMute)
    public void onClickMute() {
        btnSound.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnVibrate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnMute.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.badge_check), null);
        notificationType = NotificationType.MUTE.getValue();
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
        SharedPreference.saveInt(SharedPreference.KEY_NOTIFICATION_TYPE, notificationType);
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
