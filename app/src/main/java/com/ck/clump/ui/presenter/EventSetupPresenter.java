package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.CreateEventResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.EventSetupView;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class EventSetupPresenter {
    private final Service service;
    private final WeakReference<EventSetupView> view;
    private CompositeSubscription subscriptions;

    public EventSetupPresenter(Service service, EventSetupView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void createEvent(Map<String, RequestBody> params) {
        view.get().showWait();
        Subscription subscription = service.createEvent(new Service.APICallBack<CreateEventResponse>() {
            @Override
            public void onSuccess(CreateEventResponse response) {
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

        }, params);
        subscriptions.add(subscription);
    }

    public void updateEvent(Map<String, RequestBody> params) {
        view.get().showWait();
        Subscription subscription = service.updateEvent(new Service.APICallBack<CreateEventResponse>() {
            @Override
            public void onSuccess(CreateEventResponse response) {
                view.get().removeWait();
                view.get().onUpdateSuccess(response);
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

        }, params);
        subscriptions.add(subscription);
    }


    public void onStop() {
        subscriptions.clear();
    }
}
