package com.ck.clump.ui.view;

import com.ck.clump.model.response.UpdateProfileResponse;
import com.ck.clump.model.response.UploadUserBackgroundResponse;

public interface SettingProfileView extends BaseView {
    void onUpdateProfileSuccess(UpdateProfileResponse response);

    void onUploadUserBackgroundSuccess(UploadUserBackgroundResponse response);

}
