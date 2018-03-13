package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.GetActivitiesResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.AuditView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class AuditPresenter {
    private final Service service;
    private final WeakReference<AuditView> view;
    private CompositeSubscription subscriptions;

    public AuditPresenter(Service service, AuditView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getActivities() {
        view.get().showWait();
        Subscription subscription = service.getActivities(new Service.APICallBack<GetActivitiesResponse>() {
            @Override
            public void onSuccess(GetActivitiesResponse response) {
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
        });

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
