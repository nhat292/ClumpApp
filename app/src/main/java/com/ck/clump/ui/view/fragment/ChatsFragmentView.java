package com.ck.clump.ui.view.fragment;

import com.ck.clump.model.response.GetExitInfoResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.ui.view.BaseView;

public interface ChatsFragmentView extends BaseView {
    void onLeaveChatSuccess(SimpleResponse response);
    void onGetExitInfoSuccess(GetExitInfoResponse response);
}
