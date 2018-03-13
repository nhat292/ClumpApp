package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.CreateGroupResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.CreateGroupView;

import java.io.File;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class CreateGroupPresenter {
    private final Service service;
    private final WeakReference<CreateGroupView> view;
    private CompositeSubscription subscriptions;

    public CreateGroupPresenter(Service service, CreateGroupView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void createGroup(String name, String memberList, File file) {
        view.get().showWait();
        Subscription subscription = service.createGroup(new Service.APICallBack<CreateGroupResponse>() {
            @Override
            public void onSuccess(CreateGroupResponse response) {
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

        }, name, memberList, file);
        subscriptions.add(subscription);
    }


    public void onStop() {
        subscriptions.clear();
    }
}
