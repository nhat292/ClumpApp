package com.ck.clump.ui.view;

import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.UploadChatImageResponse;

public interface ChatView extends BaseView {
    void onUploadImageSuccess(UploadChatImageResponse response);

    void onNextEventSuccess(EventDetailResponse response);
}
