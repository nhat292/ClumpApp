package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.BlockingResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.BlockingView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BlockingPresenter {
    private final Service service;
    private final WeakReference<BlockingView> view;
    private CompositeSubscription subscriptions;

    public BlockingPresenter(Service service, BlockingView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getBlockingUsers() {
        view.get().showWait();
        Subscription subscription = service.getBlockingUsers(new Service.APICallBack<BlockingResponse>() {
            @Override
            public void onSuccess(BlockingResponse response) {
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

        });
        subscriptions.add(subscription);
    }

    public void unBlockUser(String userId) {
        view.get().showWait();
        Subscription subscription = service.unBlockUser(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onUnBlockSuccess(response);
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

        }, userId);
        subscriptions.add(subscription);
    }


    public void onStop() {
        subscriptions.clear();
    }
}
