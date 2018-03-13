package com.ck.clump.ui.view;

import com.ck.clump.model.response.SimpleResponse;

public interface BlockingView extends BaseView {
    void onUnBlockSuccess(SimpleResponse response);
}
