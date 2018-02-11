package com.ninos.models;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class UserProfileResponse {
    private boolean success;
    private String message;
    private UserProfile userProfile;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userProfile=" + userProfile +
                '}';
    }
}
