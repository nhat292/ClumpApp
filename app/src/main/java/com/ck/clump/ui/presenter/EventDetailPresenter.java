package com.ck.clump.ui.presenter;

import com.ck.clump.enums.EventUserStatus;
import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.NetworkError;
import com.ck.clump.service.Service;
import com.ck.clump.ui.view.EventDetailView;

import java.lang.ref.WeakReference;
import java.util.Map;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class EventDetailPresenter {
    private final Service service;
    private final WeakReference<EventDetailView> view;
    private CompositeSubscription subscriptions;

    public EventDetailPresenter(Service service, EventDetailView view) {
        this.service = service;
        this.view = new WeakReference<>(view);
        this.subscriptions = new CompositeSubscription();
    }

    public void getEventDetail(String eventId, boolean showLoading) {
        if(showLoading) {
            view.get().showWait();
        }
        Subscription subscription = service.getEventDetail(new Service.APICallBack<EventDetailResponse>() {
            @Override
            public void onSuccess(EventDetailResponse response) {
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

        }, eventId);

        subscriptions.add(subscription);
    }

    public void eventCount(final Map<String, String> params) {
        Subscription subscription = service.eventCount(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                if (params.get("eventConfirm").toString().equals(EventUserStatus.COUNT_IN.getValue())) {
                    view.get().onCountInSuccess();
                } else {
                    view.get().onCountOutSuccess();
                }
            }

            @Override
            public void onError(NetworkError networkError) {
                if (networkError.isAccessForbidden()) {
                    view.get().logout();
                } else {
                    view.get().onFailure(networkError.getAppErrorMessage(), false);
                }
            }

        }, params);

        subscriptions.add(subscription);
    }

    public void deleteEvent(final String eventId) {
        view.get().showWait();
        Subscription subscription = service.deleteEvent(new Service.APICallBack<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse response) {
                view.get().removeWait();
                view.get().onDeleteEventSuccess(response);
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

        }, eventId);

        subscriptions.add(subscription);
    }

    public void addMember(Map<String, String> params) {
        view.get().showWait();

        Subscription subscription = service.addMemberToEvent(new Service.APICallBack<SimpleResponse>() {
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


    public void onStop() {
        subscriptions.clear();
    }
}
