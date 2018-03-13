package com.ck.clump.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSwipeViewPager extends ViewPager {

    public CustomSwipeViewPager(Context context) {
        super(context);
    }

    public CustomSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Always return false to disable user swipes
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Always return false to disable user swipes
        return false;
    }
}
