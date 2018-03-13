package com.ck.clump.ui.presenter;

import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.view.FriendInfoView;

import java.lang.ref.WeakReference;
import java.util.Map;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class FriendInfoPresenter {
    private final Service service;
    private final WeakReference<FriendInfoView> view;
    private CompositeSubscription subscriptions;

    public FriendInfoPresenter(Service service, FriendInfoView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
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
                    view.get().onFailure(networkError.getAppErrorMessage(), true);
                }
            }
        }, body);

        subscriptions.add(subscription);
    }

    public void loadFourImagesOfUser(String userId) {
        Subscription subscription = service.loadFriendMedias(new Service.APICallBack<MediaResponse>() {
            @Override
            public void onSuccess(MediaResponse response) {
                view.get().onLoadingMediaSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {

            }
        }, userId);
        subscriptions.add(subscription);
    }

    public void reportUser(Map<String, String> params) {
        view.get().showWait();
        Subscription subscription = service.reportUser(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onReportSuccess(response);
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
        }, params);
        subscriptions.add(subscription);
    }

    public void blockUser(Map<String, String> params) {
        view.get().showWait();
        Subscription subscription = service.blockUser(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onBlockUserSuccess(response);
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
        }, params);
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


    public void onStop() {
        subscriptions.clear();
    }
}
