package com.ck.clump.model;

import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by anthony on 4/6/17.
 */

public class UserGroupModel {
    @SerializedName("users")
    @Expose
    private List<UserModel> users;
    @SerializedName("groups")
    @Expose
    private List<GroupModel> groups;

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public List<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupModel> groups) {
        this.groups = groups;
    }

}
