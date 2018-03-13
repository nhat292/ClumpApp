package com.ck.clump.service;


import com.ck.clump.model.request.ChannelRequest;
import com.ck.clump.model.request.FriendInfoRequest;
import com.ck.clump.model.request.InviteFriendRequest;
import com.ck.clump.model.request.RegisterRequest;
import com.ck.clump.model.request.UpdateTokenRequest;
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

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface NetworkService {

    @POST("api/public/m/register")
    Observable<RegisterResponse> postRegister(@Body RegisterRequest body);

    @POST("api/common/m/update_token")
    Observable<UpdateTokenResponse> postUpdateToken(@Body UpdateTokenRequest body);

    @POST("api/public/m/verify_code")
    Observable<VerifyCodeResponse> postVerifyCode(@Body VerifyCodeRequest body);

    @POST("api/common/m/user_update_profile")
    @Multipart
    Observable<UserResponse> postUpdateProfile(@PartMap Map<String, RequestBody> params);

    @GET("api/common/m/get_contact_list")
    Observable<ContactResponse> getContactsList(@Query("type") String type);

    @POST("api/common/m/invite_friend")
    Observable<InviteFriendResponse> postInviteFriend(@Body InviteFriendRequest body);

    @GET("api/m/chat")
    Observable<ChatResponse> getChatsList();

    @POST("api/common/get_user_by_id")
    Observable<UserResponse> getUserDetail(@Body FriendInfoRequest body);

    @GET("api/m/user/images")
    Observable<MediaResponse> getFriendMedias(@Query("userId") String userId);

    @GET("api/m/group/detail/{group_id}")
    Observable<GroupInfoResponse> getGroupInfo(@Path("group_id") String groupId);

    @GET("api/m/event/activities")
    Observable<GetActivitiesResponse> getActivities();

    @FormUrlEncoded
    @POST("api/m/group/addMember")
    Observable<SimpleResponse> postAddMemberToGroup(@FieldMap Map<String, String> params);

    @POST("api/m/event")
    @Multipart
    Observable<CreateEventResponse> createEvent(@PartMap Map<String, RequestBody> params);

    @GET("api/m/event/detail/{id}")
    Observable<EventDetailResponse> getEventDetail(@Path("id") String eventId);

    @FormUrlEncoded
    @POST("api/m/event/user_confirm")
    Observable<SimpleResponse> eventCount(@FieldMap Map<String, String> params);

    @GET("api/m/event")
    Observable<EventResponse> getEventsList();

    @PUT("api/m/event")
    @Multipart
    Observable<CreateEventResponse> updateEvent(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("api/m/user/report")
    Observable<SimpleResponse> reportUser(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/m/user/blockUser")
    Observable<SimpleResponse> blocktUser(@FieldMap Map<String, String> params);

    @PUT("api/m/group")
    @Multipart
    Observable<SimpleResponse> updateGroup(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part avatar);

    @FormUrlEncoded
    @POST("api/common/m/verify_contact_list")
    Observable<ContactResponse> verifyContact(@FieldMap Map<String, String> params);

    @POST("api/m/chat/chat_with_user")
    Observable<ChannelResponse> getChannelID(@Body ChannelRequest body);

    @POST("api/m/chat/image")
    @Multipart
    Observable<UploadChatImageResponse> uploadImageChat(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part avatar);

    @GET("api/m/chat/next_event")
    Observable<EventDetailResponse> getNextEvent(@QueryMap Map<String, String> params);

    @GET("api/m/chat/images")
    Observable<MediaResponse> getMedia(@QueryMap Map<String, String> params);

    @POST("api/m/group")
    @Multipart
    Observable<CreateGroupResponse> createGroup(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part avatar);

    @GET("api/m/chat/event_list")
    Observable<IncomingEventResponse> getIncomingEvent(@QueryMap Map<String, String> params);

    @POST("api/common/m/user_update_background")
    @Multipart
    Observable<UploadUserBackgroundResponse> updateUserBackground(@Part MultipartBody.Part avatar);

    @POST("api/common/m/user_update_profile")
    @Multipart
    Observable<UpdateProfileResponse> updateUserProfile(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part avatar);

    @GET("api/m/user/blockUser")
    Observable<BlockingResponse> getBlockingUsers();

    @FormUrlEncoded
    @POST("api/m/user/unBlockUser")
    Observable<SimpleResponse> unBlockUser(@FieldMap Map<String, String> params);

    @DELETE("api/m/event/delete/{eventId}")
    Observable<SimpleResponse> deleteEvent(@Path("eventId") String eventId);

    @FormUrlEncoded
    @POST("api/m/event/addMember")
    Observable<SimpleResponse> postAddMemberToEvent(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/m/chat/leaveChannel")
    Observable<SimpleResponse> leaveChannel(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/m/chat/rejoinChannel/")
    Observable<SimpleResponse> joinChannel(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/m/chat/getExitInfo")
    Observable<GetExitInfoResponse> getExitInfo(@FieldMap Map<String, String> params);
}
