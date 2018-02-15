package com.ninos.models;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class UserInfo {
    private String updatedAt;

    private String _id;

    private String childName;

    private String DOB;

    private String email;

    private String isFirstLogin;

    private String createdAt;

    private String userId;

    private String isEnabled;

    private String parentName;

    private boolean isFollowing;

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(String isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "updatedAt='" + updatedAt + '\'' +
                ", _id='" + _id + '\'' +
                ", childName='" + childName + '\'' +
                ", DOB='" + DOB + '\'' +
                ", email='" + email + '\'' +
                ", isFirstLogin='" + isFirstLogin + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", userId='" + userId + '\'' +
                ", isEnabled='" + isEnabled + '\'' +
                ", parentName='" + parentName + '\'' +
                ", isFollowing='" + isFollowing + '\'' +
                '}';
    }
}