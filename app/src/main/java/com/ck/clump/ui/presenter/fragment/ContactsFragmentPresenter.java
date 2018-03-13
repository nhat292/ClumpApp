package com.ck.clump.ui.presenter.fragment;

import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.fragment.ContactsFragmentView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ContactsFragmentPresenter {
    private final Service service;
    private final WeakReference<ContactsFragmentView> view;
    private CompositeSubscription subscriptions;

    public ContactsFragmentPresenter(Service service, ContactsFragmentView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getContactsList(String type, final boolean showError, boolean showLoading) {
        if (showLoading)
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
                if (showError) {
                    if (networkError.isAccessForbidden()) {
                        view.get().logout();
                    } else {
                        view.get().onFailure(networkError.getAppErrorMessage(), false);
                    }
                }
            }

        }, type);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
