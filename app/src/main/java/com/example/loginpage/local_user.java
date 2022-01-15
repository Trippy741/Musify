package com.example.loginpage;

public class local_user {
    private String displayName;
    private String uid;
    private int profile_pic_index;

    public local_user(String displayName, String uid, int profile_pic_index) {
        this.displayName = displayName;
        this.uid = uid;
        this.profile_pic_index = profile_pic_index;
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

    public int getProfile_pic_index() {
        return profile_pic_index;
    }

    public void setProfile_pic_index(int profile_pic_index) {
        this.profile_pic_index = profile_pic_index;
    }
}
