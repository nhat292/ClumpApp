package com.ck.clump.ui.widget.swiperecyclerview.listener;


import com.ck.clump.ui.widget.swiperecyclerview.SwipeMenuLayout;

public interface SwipeSwitchListener {

    void beginMenuClosed(SwipeMenuLayout swipeMenuLayout);

    void beginMenuOpened(SwipeMenuLayout swipeMenuLayout);

    void endMenuClosed(SwipeMenuLayout swipeMenuLayout);

    void endMenuOpened(SwipeMenuLayout swipeMenuLayout);

}
