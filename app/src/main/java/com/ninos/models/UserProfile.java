package com.ninos.models;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class UserProfile {
    private String childName;
    private String userId;
    private String aboutYou;
    private String postCount;
    private String followersCount;
    private String followingCount;
    private boolean isFollowing;
    private String city;
    private String pointsCount;

    public String getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(String pointsCount) {
        this.pointsCount = pointsCount;
    }

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

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
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
                ", isFollowing=" + isFollowing +
                ", city='" + city + '\'' +
                ", pointsCount='" + pointsCount + '\'' +
                '}';
    }
}
