package com.ck.clump.ui.widget.swiperecyclerview.listener;


import com.ck.clump.ui.widget.swiperecyclerview.SwipeMenuLayout;

public interface SwipeFractionListener {
    void beginMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction);

    void endMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction);
}
