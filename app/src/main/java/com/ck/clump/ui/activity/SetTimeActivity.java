package com.ck.clump.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhat on 5/22/17.
 */

public class SetTimeActivity extends BaseActivity {
    private static final String TAG = SetTimeActivity.class.getSimpleName();

    @Bind(R.id.timePicker)
    TimePicker timePicker;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvLeft)
    TextView imvLeft;
    @Bind(R.id.btnDone)
    Button btnDone;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    int mHour = 0, mMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        });

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvLeft.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnDone.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
    }


    @OnClick(R.id.imvLeft)
    public void imvLeftClick() {
        finish();
    }

    @OnClick(R.id.btnDone)
    public void done() {
        Intent intent = new Intent();
        intent.putExtra("hour", mHour);
        intent.putExtra("minute", mMinute);
        setResult(RESULT_OK, intent);
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
