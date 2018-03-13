package com.ck.clump.ui.presenter.fragment;

import com.ck.clump.model.response.EventResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.fragment.EventsFragmentView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class EventsFragmentPresenter {
    private final Service service;
    private final WeakReference<EventsFragmentView> view;
    private CompositeSubscription subscriptions;

    public EventsFragmentPresenter(Service service, EventsFragmentView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getEventsList(final boolean showError) {
        view.get().showWait();
        Subscription subscription = service.getEventsList(new Service.APICallBack<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                view.get().removeWait();
                view.get().onSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                if(showError) {
                    if (networkError.isAccessForbidden()) {
                        view.get().logout();
                    } else {
                        view.get().onFailure(networkError.getAppErrorMessage(), false);
                    }
                }
            }

        });
        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
