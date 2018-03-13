package com.ck.clump.ui.view;

import com.ck.clump.model.response.CreateEventResponse;

public interface EventSetupView extends BaseView {
    void onUpdateSuccess(CreateEventResponse response);
}
