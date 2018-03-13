package com.ck.clump.ui.view;

import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.model.response.SimpleResponse;

public interface FriendInfoView extends BaseView {
    void onGetUserDetailSuccess(UserModel response);

    void onLoadingMediaSuccess(MediaResponse response);

    void onReportSuccess(SimpleResponse response);

    void onBlockUserSuccess(SimpleResponse response);

    void onGetChannelIDSuccess(ChannelResponse response);
}
