package com.ck.clump.ui.presenter;

import com.ck.clump.model.response.GroupInfoResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.GroupInfoView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class GroupInfoPresenter {
    private final Service service;
    private final WeakReference<GroupInfoView> view;
    private CompositeSubscription subscriptions;

    public GroupInfoPresenter(Service service, GroupInfoView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getGroupInfo(String groupId) {
        view.get().showWait();
        Subscription subscription = service.getGroupInfo(new Service.APICallBack<GroupInfoResponse>() {
            @Override
            public void onSuccess(GroupInfoResponse response) {
                view.get().removeWait();
                view.get().onSuccess(response);
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

        }, groupId);

        subscriptions.add(subscription);
    }

    public void addMember(Map<String, String> params) {
        view.get().showWait();

        Subscription subscription = service.addMemberToGroup(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onAddMemberSuccess(response);
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

    public void updateGroup(Map<String, RequestBody> params, File imageFile) {
        view.get().showWait();
        Subscription subscription = service.updateGroup(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onUpdateGroupSuccess(response);
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

        }, params, imageFile);

        subscriptions.add(subscription);
    }

    public void joneChat(String channelId, String userId) {
        Subscription subscription = service.leaveOrJoinChat(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
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
