package com.ck.clump.model.request;

import java.util.List;

/**
 * Created by anthony on 4/9/17.
 */

public class InviteFriendRequest {
    private List<String> contactList = null;

    public InviteFriendRequest(List<String> contactList) {
        this.contactList = contactList;
    }

    public List<String> getContactList() {
        return contactList;
    }

    public void setContactList(List<String> contactList) {
        this.contactList = contactList;
    }
}
