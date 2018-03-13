package com.ck.clump.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.ui.activity.adapter.MediaPagerAdapter;
import com.ck.clump.ui.activity.model.Media;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewMediaActivity extends BaseActivity {

    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @Bind(R.id.llRoot)
    LinearLayout llRoot;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tvCount)
    TextView tvCount;
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
    private String imageURL;
    private boolean isFullScreen = false;
    private MediaPagerAdapter adapter;
    private List<Media> medias;
    private int scrollState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        renderView();
    }

    private void renderView() {
        setContentView(R.layout.activity_view_media);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        medias = intent.getParcelableArrayListExtra("medias");
        imageURL = medias.get(0).getPath();
        changeTextCount(0);
        if (medias.size() == 1) {
            tvCount.setVisibility(View.GONE);
        }
        adapter = new MediaPagerAdapter(mContext, medias, new MediaPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick() {
                if (scrollState == 0) {
                    isFullScreen = !isFullScreen;
                    if (isFullScreen) {
                        openFullScreen();
                    } else {
                        closeFullScreen();
                    }
                }
            }

            private void openFullScreen() {
                appBarLayout.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                llRoot.setBackgroundColor(getResources().getColor(android.R.color.black));
            }

            private void closeFullScreen() {
                appBarLayout.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                llRoot.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                imageURL = BuildConfig.BASEURL + medias.get(position).getPath();
                changeTextCount(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollState = state;
            }
        });
        viewPager.setCurrentItem(position, false);

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvCount.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
    }

    @OnClick(R.id.imvRight)
    public void onClickRight() {
        finish();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        if (imageURL == null) return;

        Glide.with(this)
                .load(imageURL)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        if (drawable != null) {
                            Bitmap bitmap = Common.drawableToBitmap(drawable);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                            startActivity(Intent.createChooser(shareIntent, "Share to:"));
                        }
                    }
                });

    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "clump" + System.currentTimeMillis() + ".png");
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void changeTextCount(int position) {
        tvCount.setText(String.valueOf(position + 1) + " of " + medias.size());
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
