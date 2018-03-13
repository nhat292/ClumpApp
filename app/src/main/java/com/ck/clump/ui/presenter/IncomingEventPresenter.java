package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.IncomingEventResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.IncomingEventView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class IncomingEventPresenter {
    private final Service service;
    private final WeakReference<IncomingEventView> view;
    private CompositeSubscription subscriptions;

    public IncomingEventPresenter(Service service, IncomingEventView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getIncomingEvent(String userId, String groupId) {
        view.get().showWait();
        Subscription subscription = service.getIncomingEvent(new Service.APICallBack<IncomingEventResponse>() {
            @Override
            public void onSuccess(IncomingEventResponse response) {
                view.get().removeWait();
                view.get().onSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                if(networkError.isAccessForbidden()) {
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
