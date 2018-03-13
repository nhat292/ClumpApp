package com.ck.clump.deps;


import com.ck.clump.service.NetworkModule;
import com.ck.clump.ui.activity.AuditActivity;
import com.ck.clump.ui.activity.BlockingActivity;
import com.ck.clump.ui.activity.ChatActivity;
import com.ck.clump.ui.activity.CreatedGroupActivity;
import com.ck.clump.ui.activity.EventDetailActivity;
import com.ck.clump.ui.activity.EventSetupActivity;
import com.ck.clump.ui.activity.FriendInfoActivity;
import com.ck.clump.ui.activity.GroupInfoActivity;
import com.ck.clump.ui.activity.IncomingEventActivity;
import com.ck.clump.ui.activity.InviteFriendActivity;
import com.ck.clump.ui.activity.InviteFriendEventActivity;
import com.ck.clump.ui.activity.MainActivity;
import com.ck.clump.ui.activity.PickFriendActivity;
import com.ck.clump.ui.activity.PinLocationActivity;
import com.ck.clump.ui.activity.SentMediaActivity;
import com.ck.clump.ui.activity.SettingProfileActivity;
import com.ck.clump.ui.activity.SignUpOneActivity;
import com.ck.clump.ui.activity.SignUpProfileActivity;
import com.ck.clump.ui.activity.SignUpTwoActivity;
import com.ck.clump.ui.activity.fragment.ChatsFragment;
import com.ck.clump.ui.activity.fragment.ContactsFragment;
import com.ck.clump.ui.activity.fragment.EventsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class})
public interface Deps {
    void injectSignUpOneActivity(SignUpOneActivity signUpOneActivity);
    void injectSignUpTwoActivity(SignUpTwoActivity signUpTwoActivity);
    void injectSignUpProfileActivity(SignUpProfileActivity signUpProfileActivity);
    void injectMainActivity(MainActivity mainActivity);
    void injectSettingProfileActivity(SettingProfileActivity settingProfileActivity);
    void injectInviteFriendActivity(InviteFriendActivity inviteFriendActivity);
    void injectAuditActivity(AuditActivity auditActivity);
    void injectGroupInfoActivity(GroupInfoActivity groupInfoActivity);
    void injectFriendInfoActivity(FriendInfoActivity friendInfoActivity);
    void injectChatActivity(ChatActivity chatActivity);
    void injectInviteFriendEventActivity(InviteFriendEventActivity inviteFriendEventActivity);
    void injectEventSetupActivity(EventSetupActivity eventSetupActivity);
    void injectEventDetailActivity(EventDetailActivity eventDetailActivity);
    void injectPickFriendActivity(PickFriendActivity pickFriendActivity);
    void injectPinLocationActivity(PinLocationActivity pinLocationActivity);
    void injectSentMediaActivity(SentMediaActivity sentMediaActivity);
    void injectCreateGroupActivity(CreatedGroupActivity createdGroupActivity);
    void injectInComingEventActivity(IncomingEventActivity incomingEventActivity);
    void injectBlockingActivity(BlockingActivity blockingActivity);

    void injectContactsFragment(ContactsFragment fragment);
    void injectChatsFragment(ChatsFragment fragment);
    void injectEventsFragment(EventsFragment fragment);

}
