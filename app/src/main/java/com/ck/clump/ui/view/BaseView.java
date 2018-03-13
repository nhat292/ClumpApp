package com.ck.clump.ui.view;

/**
 * Created by Nhat on 9/14/17.
 */

public interface BaseView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage, boolean finishActivity);

    void onSuccess(Object o);

    void logout();
}
