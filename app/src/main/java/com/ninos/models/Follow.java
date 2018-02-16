package com.ninos.models;

/**
 * Created by FAMILY on 16-02-2018.
 */

public class Follow {
    private String userName;
    private String userId;
    private boolean isFollowing;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", isFollowing=" + isFollowing +
                '}';
    }
}
