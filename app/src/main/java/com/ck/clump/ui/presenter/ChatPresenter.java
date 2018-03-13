package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.model.response.UploadChatImageResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.ChatView;
import com.ck.clump.util.SharedPreference;

import java.io.File;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ChatPresenter {
    private final Service service;
    private final WeakReference<ChatView> view;
    private CompositeSubscription subscriptions;

    public ChatPresenter(Service service, ChatView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void uploadImage(String userId, String groupId, File imageFile) {
        Subscription subscription = service.uploadImageChat(new Service.APICallBack<UploadChatImageResponse>() {
            @Override
            public void onSuccess(UploadChatImageResponse response) {
                view.get().onUploadImageSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                if(networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }
        }, userId, groupId, imageFile);
        subscriptions.add(subscription);
    }

    public void getNextEvent(String userId, String groupId) {
        view.get().showWait();
        Subscription subscription = service.getNextEvent(new Service.APICallBack<EventDetailResponse>() {
            @Override
            public void onSuccess(EventDetailResponse response) {
                view.get().removeWait();
                view.get().onNextEventSuccess(response);
            }

            @Override
            public void onError(NetworkError networkError) {
                view.get().removeWait();
                if(networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    //view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }
        }, userId, groupId);
        subscriptions.add(subscription);
    }

    public void joneChat(String channelId, String userId) {
        Subscription subscription = service.leaveOrJoinChat(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                SharedPreference.saveBool(SharedPreference.KEY_REFRESH_CHAT_LIST_WHEN_RESUME, true);
            }

            @Override
            public void onError(NetworkError networkError) {
            }

        }, channelId, userId, true);

        subscriptions.add(subscription);
    }

    public void onStop() {
        subscriptions.clear();
    }
}
