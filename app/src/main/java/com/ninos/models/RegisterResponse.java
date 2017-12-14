package com.ninos.models;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class RegisterResponse {
    private String message;

    private String token;

    private String tokenExpireDate;

    private UserInfo userInfo;

    private String success;

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

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", tokenExpireDate='" + tokenExpireDate + '\'' +
                ", userInfo=" + userInfo +
                ", success='" + success + '\'' +
                '}';
    }
}