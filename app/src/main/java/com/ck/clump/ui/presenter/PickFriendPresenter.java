package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.view.PickFriendView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class PickFriendPresenter {
    private final Service service;
    private final WeakReference<PickFriendView> view;
    private CompositeSubscription subscriptions;

    public PickFriendPresenter(Service service, PickFriendView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getContactsList(String type) {
        view.get().showRefresh();

        Subscription subscription = service.getContactsList(new Service.APICallBack<ContactResponse>() {
            @Override
            public void onSuccess(ContactResponse response) {
                view.get().hideRefresh();
                view.get().onSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().hideRefresh();
                if (networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }

        }, type);

        subscriptions.add(subscription);
    }

    public void getChannelID(ChannelRequest channelRequest) {
        view.get().showWait();
        Subscription subscription = service.getChannelID(new Service.APICallBack<ChannelResponse>() {
            @Override
            public void onSuccess(ChannelResponse response) {
                view.get().removeWait();
                view.get().onGetChannelIDSuccess(response);
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
        }, channelRequest);
        subscriptions.add(subscription);
    }

    public void getUserDetail(FriendInfoRequest body) {
        view.get().showWait();
        Subscription subscription = service.getUserDetail(new Service.APICallBack<UserModel>() {
            @Override
            public void onSuccess(UserModel response) {
                view.get().removeWait();
                view.get().onGetUserDetailSuccess(response);
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
        }, body);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
