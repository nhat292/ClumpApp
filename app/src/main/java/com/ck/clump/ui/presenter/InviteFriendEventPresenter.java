package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.InviteFriendEventView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class InviteFriendEventPresenter {
    private final Service service;
    private final WeakReference<InviteFriendEventView> view;
    private CompositeSubscription subscriptions;

    public InviteFriendEventPresenter(Service service, InviteFriendEventView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getContactsList(String type) {
        view.get().showWait();

        Subscription subscription = service.getContactsList(new Service.APICallBack<ContactResponse>() {
            @Override
            public void onSuccess(ContactResponse response) {
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

        }, type);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
