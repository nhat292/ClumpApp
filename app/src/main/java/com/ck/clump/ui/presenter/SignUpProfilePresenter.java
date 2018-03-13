package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.UserRequest;
import com.ck.clump.model.response.UserResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.SignUpProfileView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SignUpProfilePresenter {
    private final Service service;
    private final WeakReference<SignUpProfileView> view;
    private CompositeSubscription subscriptions;

    public SignUpProfilePresenter(Service service, SignUpProfileView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void postUpdateProfile(UserRequest body) {
        view.get().showWait();

        Subscription subscription = service.postUpdateProfile(new Service.APICallBack<UserResponse>() {
            @Override
            public void onSuccess(UserResponse response) {
                view.get().removeWait();
                view.get().onSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                view.get().onFailure(networkError.getAppErrorMessage(), false);
            }

        }, body);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
