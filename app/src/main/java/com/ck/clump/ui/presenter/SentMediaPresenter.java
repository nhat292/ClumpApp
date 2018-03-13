package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.SentMediaView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SentMediaPresenter {
    private final Service service;
    private final WeakReference<SentMediaView> view;
    private CompositeSubscription subscriptions;

    public SentMediaPresenter(Service service, SentMediaView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void loadMedia(String userId, String groupId) {
        view.get().showWait();
        Subscription subscription = service.getMedia(new Service.APICallBack<MediaResponse>() {
            @Override
            public void onSuccess(MediaResponse response) {
                view.get().removeWait();
                view.get().onSuccess(response);
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
        }, userId, groupId);
        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
