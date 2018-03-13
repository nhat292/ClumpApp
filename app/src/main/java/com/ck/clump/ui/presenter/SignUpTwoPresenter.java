package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.VerifyCodeRequest;
import com.ck.clump.model.response.VerifyCodeResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.SignUpTwoView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SignUpTwoPresenter {
    private final Service service;
    private final WeakReference<SignUpTwoView> view;
    private CompositeSubscription subscriptions;

    public SignUpTwoPresenter(Service service, SignUpTwoView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void postVerifyCode(VerifyCodeRequest body) {
        view.get().showWait();
        Subscription subscription = service.postVerifyCode(new Service.APICallBack<VerifyCodeResponse>() {
            @Override
            public void onSuccess(VerifyCodeResponse response) {
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
