package com.example.loginpage.CustomDataTypes;


import java.util.ArrayList;

public class local_user {
    public String displayName;

    public String uid = "";

    public String profile_pic_index = "";

    public String email = "";

    public ArrayList<local_user> friends = new ArrayList<local_user>();

    public boolean isUsingGoogle = false;

    public local_user(String uid,String displayName,String email , String profile_pic_index) {
        this.displayName = displayName;
        this.uid = uid;
        this.email = email;
        this.profile_pic_index = profile_pic_index;
    }
    public void addFriend(local_user friend)
    {
        friends.add(friend);
    }

    public void addFriends(ArrayList<local_user> friends)
    {
        friends.addAll(friends);
    }

    public void setFriends(ArrayList<local_user> friends)
    {
        friends = friends;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile_pic_index() {
        return profile_pic_index;
    }

    public void setProfile_pic_index(String profile_pic_index) {
        this.profile_pic_index = profile_pic_index;
    }
}
