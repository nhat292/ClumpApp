package com.ck.clump.ui.view;

import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.response.ChannelResponse;

public interface PickFriendView extends BaseView {

    void showRefresh();

    void hideRefresh();

    void onGetChannelIDSuccess(ChannelResponse response);

    void onGetUserDetailSuccess(UserModel response);

}
