package com.ninos.models;

/**
 * Created by FAMILY on 29-12-2017.
 */

public class UserCheckResponse {
    private String message;

    private String token;

    private String tokenExpireDate;

    private UserInfo userInfo;

    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpireDate() {
        return tokenExpireDate;
    }

    public void setTokenExpireDate(String tokenExpireDate) {
        this.tokenExpireDate = tokenExpireDate;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "UserCheckResponse{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", tokenExpireDate='" + tokenExpireDate + '\'' +
                ", userInfo=" + userInfo +
                ", success='" + success + '\'' +
                '}';
    }
}
