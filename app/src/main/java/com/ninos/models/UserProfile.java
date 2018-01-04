package com.ninos.models;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class UserProfile {
    String childName;
    String userId;
    String aboutYou;
    String postCount;
    String followersCount;
    String followingCount;
    String city;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getPostCount() {
        return postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "childName='" + childName + '\'' +
                ", userId='" + userId + '\'' +
                ", aboutYou='" + aboutYou + '\'' +
                ", postCount='" + postCount + '\'' +
                ", followersCount='" + followersCount + '\'' +
                ", followingCount='" + followingCount + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
