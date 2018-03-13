package com.ck.clump.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ck.clump.R;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.util.SharedPreference;

import java.lang.ref.WeakReference;

public class LaunchActivity extends BaseActivity {

    private static final String TAG = LaunchActivity.class.getSimpleName();
    private final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new HandlePassLaunchScreen(this), SPLASH_DURATION);
    }

    private static class HandlePassLaunchScreen implements Runnable {
        private WeakReference<LaunchActivity> activityRf;

        public HandlePassLaunchScreen(LaunchActivity activity) {
            activityRf = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            LaunchActivity activity = activityRf.get();
            if (activity == null)
                return;
            if (SharedPreference.getBool(SharedPreference.KEY_FIRST_TIME, true)) {
                Intent intent = new Intent(activity, TermAndConditionActivity.class);
                intent.putExtra("FIRST", true);
                activity.startActivity(intent);
            } else if (UserModel.currentUser() != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(activity, SignUpOneActivity.class);
                activity.startActivity(intent);
            }
            activity.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
