package com.ck.clump.ui.view;

import com.ck.clump.model.response.SimpleResponse;

public interface EventDetailView extends BaseView {
    void onDeleteEventSuccess(SimpleResponse response);

    void onAddMemberSuccess(SimpleResponse response);

    void onCountInSuccess();

    void onCountOutSuccess();
}
