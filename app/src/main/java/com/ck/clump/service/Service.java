package com.ck.clump.service;


import android.text.TextUtils;

import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.request.InviteFriendRequest;
import com.ck.clump.model.request.RegisterRequest;
import com.ck.clump.model.request.UpdateTokenRequest;
import com.ck.clump.model.request.UserRequest;
import com.ck.clump.model.request.VerifyCodeRequest;
import com.ck.clump.model.response.BlockingResponse;
import com.ck.clump.model.response.ChannelResponse;
import com.ck.clump.model.response.ChatResponse;
import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.model.response.CreateEventResponse;
import com.ck.clump.model.response.CreateGroupResponse;
import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.EventResponse;
import com.ck.clump.model.response.GetActivitiesResponse;
import com.ck.clump.model.response.GetExitInfoResponse;
import com.ck.clump.model.response.GroupInfoResponse;
import com.ck.clump.model.response.IncomingEventResponse;
import com.ck.clump.model.response.InviteFriendResponse;
import com.ck.clump.model.response.MediaResponse;
import com.ck.clump.model.response.RegisterResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.model.response.UpdateProfileResponse;
import com.ck.clump.model.response.UpdateTokenResponse;
import com.ck.clump.model.response.UploadChatImageResponse;
import com.ck.clump.model.response.UploadUserBackgroundResponse;
import com.ck.clump.model.response.UserResponse;
import com.ck.clump.model.response.VerifyCodeResponse;
import com.ck.clump.ui.activity.model.UserModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Service {

    public interface APICallBack<T> {
        void onSuccess(T t);

        void onError(NetworkError networkError);
    }

    private final NetworkService networkService;

    public Service(NetworkService networkService) {
        this.networkService = networkService;
    }


    /**
     * Register
     *
     * @param callback
     * @param body
     * @return
     */
    public Subscription postRegister(final APICallBack callback, RegisterRequest body) {

        return networkService.postRegister(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends RegisterResponse>>() {
                    @Override
                    public Observable<? extends RegisterResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<RegisterResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(RegisterResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * Register
     *
     * @param callback
     * @param body
     * @return
     */
    public Subscription postUpdateToken(final APICallBack callback, UpdateTokenRequest body) {

        return networkService.postUpdateToken(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UpdateTokenResponse>>() {
                    @Override
                    public Observable<? extends UpdateTokenResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UpdateTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UpdateTokenResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * Verify Code
     *
     * @param callback
     * @param body
     * @return
     */
    public Subscription postVerifyCode(final APICallBack callback, VerifyCodeRequest body) {

        return networkService.postVerifyCode(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends VerifyCodeResponse>>() {
                    @Override
                    public Observable<? extends VerifyCodeResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<VerifyCodeResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(VerifyCodeResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * Update profile
     *
     * @param callback
     * @param body
     * @return
     */
    public Subscription postUpdateProfile(final APICallBack callback, UserRequest body) {

        Map<String, RequestBody> params = new HashMap<>();
        params.put("displayName", RequestBody.create(MediaType.parse("text/plain"), body.getDisplayName()));
        if (!TextUtils.isEmpty(body.getStatus())) {
            params.put("status", RequestBody.create(MediaType.parse("text/plain"), body.getStatus()));
        }
        File file = body.getAvatar();
        if (file != null) {
            params.put("avatar", RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        return networkService.postUpdateProfile(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UserResponse>>() {
                    @Override
                    public Observable<? extends UserResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UserResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @return
     */
    public Subscription getContactsList(final APICallBack callback, String type) {

        return networkService.getContactsList(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ContactResponse>>() {
                    @Override
                    public Observable<? extends ContactResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ContactResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(ContactResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param body
     * @return
     */
    public Subscription postInviteFriend(final APICallBack callback, InviteFriendRequest body) {

        return networkService.postInviteFriend(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends InviteFriendResponse>>() {
                    @Override
                    public Observable<? extends InviteFriendResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<InviteFriendResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(InviteFriendResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @return
     */
    public Subscription getChatsList(final APICallBack callback) {

        return networkService.getChatsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ChatResponse>>() {
                    @Override
                    public Observable<? extends ChatResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ChatResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(ChatResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @param body
     * @return
     */
    public Subscription getUserDetail(final APICallBack callback, FriendInfoRequest body) {

        return networkService.getUserDetail(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UserResponse>>() {
                    @Override
                    public Observable<? extends UserResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UserResponse response) {
                        callback.onSuccess(response.getDATA());
                    }
                });
    }

    /**
     * @param callback
     * @param userId
     * @return
     */
    public Subscription loadFriendMedias(final APICallBack callback, String userId) {

        return networkService.getFriendMedias(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends MediaResponse>>() {
                    @Override
                    public Observable<? extends MediaResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<MediaResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(MediaResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param groupId
     * @return
     */
    public Subscription getGroupInfo(final APICallBack callback, String groupId) {

        return networkService.getGroupInfo(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends GroupInfoResponse>>() {
                    @Override
                    public Observable<? extends GroupInfoResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<GroupInfoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(GroupInfoResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @return
     */
    public Subscription getActivities(final APICallBack callback) {
        return networkService.getActivities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends GetActivitiesResponse>>() {
                    @Override
                    public Observable<? extends GetActivitiesResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<GetActivitiesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(GetActivitiesResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription addMemberToGroup(final APICallBack callback, Map<String, String> params) {
        return networkService.postAddMemberToGroup(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription createEvent(final APICallBack callback, Map<String, RequestBody> params) {
        return networkService.createEvent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends CreateEventResponse>>() {
                    @Override
                    public Observable<? extends CreateEventResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<CreateEventResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(CreateEventResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param eventId
     * @return
     */
    public Subscription getEventDetail(final APICallBack callback, String eventId) {
        return networkService.getEventDetail(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends EventDetailResponse>>() {
                    @Override
                    public Observable<? extends EventDetailResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<EventDetailResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(EventDetailResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription eventCount(final APICallBack callback, Map<String, String> params) {
        return networkService.eventCount(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @return
     */
    public Subscription getEventsList(final APICallBack callback) {
        return networkService.getEventsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends EventResponse>>() {
                    @Override
                    public Observable<? extends EventResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<EventResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(EventResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription updateEvent(final APICallBack callback, Map<String, RequestBody> params) {
        return networkService.updateEvent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends CreateEventResponse>>() {
                    @Override
                    public Observable<? extends CreateEventResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<CreateEventResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(CreateEventResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription reportUser(final APICallBack callback, Map<String, String> params) {
        return networkService.reportUser(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription blockUser(final APICallBack callback, Map<String, String> params) {
        return networkService.blocktUser(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @param params
     * @param imageFile
     * @return
     */
    public Subscription updateGroup(final APICallBack callback, Map<String, RequestBody> params, File imageFile) {
        MultipartBody.Part avatarPart = null;
        if (imageFile != null) {
            avatarPart = MultipartBody.Part.createFormData("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
        }
        return networkService.updateGroup(params, avatarPart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param contacts
     * @return
     */
    public Subscription vefifyContact(final APICallBack callback, List<String> contacts) {
        Map<String, String> params = new HashMap<>();
        String contactStr = "[";
        for (int i = 0; i < contacts.size(); i++) {
            contactStr += "\"" + contacts.get(i) + "\"";
            if (i < contacts.size() - 1) {
                contactStr += ",";
            }
        }
        contactStr += "]";
        params.put("contact_list", contactStr);
        return networkService.verifyContact(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ContactResponse>>() {
                    @Override
                    public Observable<? extends ContactResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ContactResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(ContactResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param body
     * @return
     */
    public Subscription getChannelID(final APICallBack callback, ChannelRequest body) {
        return networkService.getChannelID(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ChannelResponse>>() {
                    @Override
                    public Observable<? extends ChannelResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ChannelResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(ChannelResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param userId
     * @param groupId
     * @param file
     * @return
     */
    public Subscription uploadImageChat(final APICallBack callback, String userId, String groupId, File file) {
        Map<String, RequestBody> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", RequestBody.create(MediaType.parse("text/plain"), userId));
        }
        if (groupId != null) {
            params.put("groupId", RequestBody.create(MediaType.parse("text/plain"), groupId));
        }
        MultipartBody.Part imagePart = null;
        if (file != null) {
            imagePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return networkService.uploadImageChat(params, imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UploadChatImageResponse>>() {
                    @Override
                    public Observable<? extends UploadChatImageResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UploadChatImageResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UploadChatImageResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @param userId
     * @param groupId
     * @return
     */
    public Subscription getNextEvent(final APICallBack callback, String userId, String groupId) {
        Map<String, String> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", userId);
        }
        if (groupId != null) {
            params.put("groupId", groupId);
        }
        return networkService.getNextEvent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends EventDetailResponse>>() {
                    @Override
                    public Observable<? extends EventDetailResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<EventDetailResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(EventDetailResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @param userId
     * @param groupId
     * @return
     */
    public Subscription getMedia(final APICallBack callback, String userId, String groupId) {
        Map<String, String> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", userId);
        }
        if (groupId != null) {
            params.put("groupId", groupId);
        }
        return networkService.getMedia(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends MediaResponse>>() {
                    @Override
                    public Observable<? extends MediaResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<MediaResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(MediaResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param name
     * @param memberList
     * @param file
     * @return
     */
    public Subscription createGroup(final APICallBack callback, String name, String memberList, File file) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        params.put("memberList", RequestBody.create(MediaType.parse("text/plain"), memberList));
        MultipartBody.Part imagePart = null;
        if (file != null) {
            imagePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return networkService.createGroup(params, imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends CreateGroupResponse>>() {
                    @Override
                    public Observable<? extends CreateGroupResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<CreateGroupResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(CreateGroupResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param userId
     * @param groupId
     * @return
     */
    public Subscription getIncomingEvent(final APICallBack callback, String userId, String groupId) {
        Map<String, String> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", userId);
        }
        if (groupId != null) {
            params.put("groupId", groupId);
        }
        return networkService.getIncomingEvent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends IncomingEventResponse>>() {
                    @Override
                    public Observable<? extends IncomingEventResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<IncomingEventResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(IncomingEventResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param file
     * @return
     */
    public Subscription uploadUserBackground(final APICallBack callback, File file) {
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        return networkService.updateUserBackground(imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UploadUserBackgroundResponse>>() {
                    @Override
                    public Observable<? extends UploadUserBackgroundResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UploadUserBackgroundResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UploadUserBackgroundResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param displayName
     * @param status
     * @param file
     * @return
     */
    public Subscription updateUserProfile(final APICallBack callback, String displayName, String status, File file) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("displayName", RequestBody.create(MediaType.parse("text/plain"), displayName));
        params.put("status", RequestBody.create(MediaType.parse("text/plain"), status));
        MultipartBody.Part imagePart = null;
        if (file != null) {
            imagePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return networkService.updateUserProfile(params, imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UpdateProfileResponse>>() {
                    @Override
                    public Observable<? extends UpdateProfileResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<UpdateProfileResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(UpdateProfileResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @return
     */
    public Subscription getBlockingUsers(final APICallBack callback) {
        return networkService.getBlockingUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends BlockingResponse>>() {
                    @Override
                    public Observable<? extends BlockingResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<BlockingResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(BlockingResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param userId
     * @return
     */
    public Subscription unBlockUser(final APICallBack callback, String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        return networkService.unBlockUser(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }


    /**
     * @param callback
     * @param eventId
     * @return
     */
    public Subscription deleteEvent(final APICallBack callback, String eventId) {
        return networkService.deleteEvent(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param params
     * @return
     */
    public Subscription addMemberToEvent(final APICallBack callback, Map<String, String> params) {
        return networkService.postAddMemberToEvent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                    @Override
                    public Observable<? extends SimpleResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<SimpleResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(SimpleResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

    /**
     * @param callback
     * @param channelId
     * @param userId
     * @param isJoin
     * @return
     */
    public Subscription leaveOrJoinChat(final APICallBack callback, String channelId, String userId, boolean isJoin) {
        final Map<String, String> params = new HashMap<>();
        params.put("channelId", channelId);
        params.put("userId", userId);
        long time = System.currentTimeMillis();
        if (isJoin) {
            params.put("rejoinDate", String.valueOf(time));
            return networkService.joinChannel(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                        @Override
                        public Observable<? extends SimpleResponse> call(Throwable throwable) {
                            return Observable.error(throwable);
                        }
                    })
                    .subscribe(new Subscriber<SimpleResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            callback.onError(new NetworkError(e));
                        }

                        @Override
                        public void onNext(SimpleResponse response) {
                            callback.onSuccess(response);
                        }
                    });
        } else {
            params.put("leaveDate", String.valueOf(time));
            return networkService.leaveChannel(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends SimpleResponse>>() {
                        @Override
                        public Observable<? extends SimpleResponse> call(Throwable throwable) {
                            return Observable.error(throwable);
                        }
                    })
                    .subscribe(new Subscriber<SimpleResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            callback.onError(new NetworkError(e));
                        }

                        @Override
                        public void onNext(SimpleResponse response) {
                            callback.onSuccess(response);
                        }
                    });
        }
    }

    /**
     * @param callback
     * @return
     */
    public Subscription getExitInfo(final APICallBack callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", UserModel.currentUser().getId());
        return networkService.getExitInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends GetExitInfoResponse>>() {
                    @Override
                    public Observable<? extends GetExitInfoResponse> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<GetExitInfoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(new NetworkError(e));
                    }

                    @Override
                    public void onNext(GetExitInfoResponse response) {
                        callback.onSuccess(response);
                    }
                });
    }

}
