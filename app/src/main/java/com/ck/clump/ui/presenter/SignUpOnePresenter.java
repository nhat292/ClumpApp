package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.RegisterRequest;
import com.ck.clump.model.request.UpdateTokenRequest;
import com.ck.clump.model.response.RegisterResponse;
import com.ck.clump.model.response.UpdateTokenResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.SignUpOneView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SignUpOnePresenter {
    private final Service service;
    private final WeakReference<SignUpOneView> view;
    private CompositeSubscription subscriptions;

    public SignUpOnePresenter(Service service, SignUpOneView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void postRegister(RegisterRequest body) {
        view.get().showWait();

        Subscription subscription = service.postRegister(new Service.APICallBack<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse response) {
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

    public void postUpdateDeviceToken(UpdateTokenRequest body) {
        view.get().showWait();
        Subscription subscription = service.postUpdateToken(new Service.APICallBack<UpdateTokenResponse>() {
            @Override
            public void onSuccess(UpdateTokenResponse response) {
                view.get().removeWait();
                view.get().onSuccessUpdateToken(response);
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
