package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.MainView;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter {
    private final Service service;
    private final WeakReference<MainView> view;
    private CompositeSubscription subscriptions;

    public MainPresenter(Service service, MainView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void verifyContact(List<String> contacts) {
        Subscription subscription = service.vefifyContact(new Service.APICallBack<ContactResponse>() {
            @Override
            public void onSuccess(ContactResponse response) {
                view.get().onSuccess(null);
            }

            @Override
            public void onError(NetworkError networkError) {
                if (networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }
        }, contacts);
        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
