package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.UpdateProfileResponse;
import com.ck.clump.model.response.UploadUserBackgroundResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.SettingProfileView;

import java.io.File;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SettingProfilePresenter {
    private final Service service;
    private final WeakReference<SettingProfileView> view;
    private CompositeSubscription subscriptions;

    public SettingProfilePresenter(Service service, SettingProfileView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void uploadUserBackground(File file) {
        view.get().showWait();

        Subscription subscription = service.uploadUserBackground(new Service.APICallBack<UploadUserBackgroundResponse>() {
            @Override
            public void onSuccess(UploadUserBackgroundResponse response) {
                view.get().removeWait();
                view.get().onUploadUserBackgroundSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                if (networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }

        }, file);

        subscriptions.add(subscription);
    }

    public void updateProfile(String displayName, String status, File file) {
        view.get().showWait();

        Subscription subscription = service.updateUserProfile(new Service.APICallBack<UpdateProfileResponse>() {
            @Override
            public void onSuccess(UpdateProfileResponse response) {
                view.get().removeWait();
                view.get().onUpdateProfileSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                if (networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }

        }, displayName, status, file);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
