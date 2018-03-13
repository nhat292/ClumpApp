package com.ck.clump.ui.presenter.fragment;

import com.ck.clump.model.response.ChatResponse;
import com.ck.clump.model.response.GetExitInfoResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.fragment.ChatsFragmentView;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ChatsFragmentPresenter {
    private final Service service;
    private final WeakReference<ChatsFragmentView> view;
    private CompositeSubscription subscriptions;

    public ChatsFragmentPresenter(Service service, ChatsFragmentView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getChatsList(final boolean showError) {
        view.get().showWait();
        Subscription subscription = service.getChatsList(new Service.APICallBack<ChatResponse>() {
            @Override
            public void onSuccess(ChatResponse response) {
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

        });

        subscriptions.add(subscription);
    }

    public void leaveChat(String channelId, String userId) {
        view.get().showWait();
        Subscription subscription = service.leaveOrJoinChat(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onLeaveChatSuccess(response);
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

        }, channelId, userId, false);

        subscriptions.add(subscription);
    }

    public void getExitInfo() {
        Subscription subscription = service.getExitInfo(new Service.APICallBack<GetExitInfoResponse>() {
            @Override
            public void onSuccess(GetExitInfoResponse response) {
                view.get().onGetExitInfoSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
            }

        });

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
