package com.ck.clump.ui.view;

import com.ck.clump.model.response.SimpleResponse;

public interface GroupInfoView extends BaseView {
    void onAddMemberSuccess(SimpleResponse response);

    void onUpdateGroupSuccess(SimpleResponse response);
}
