package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.InviteFriendRequest;
import com.ck.clump.model.response.InviteFriendResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.InviteFriendView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class InviteFriendPresenter {
    private final Service service;
    private final WeakReference<InviteFriendView> view;
    private CompositeSubscription subscriptions;

    public InviteFriendPresenter(Service service, InviteFriendView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void postInviteFriend(InviteFriendRequest body) {
        view.get().showWait();

        Subscription subscription = service.postInviteFriend(new Service.APICallBack<InviteFriendResponse>() {
            @Override
            public void onSuccess(InviteFriendResponse response) {
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

        }, body);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
