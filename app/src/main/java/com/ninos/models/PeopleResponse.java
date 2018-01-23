package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class PeopleResponse {
    private String message;

    private List<UserInfo> users;

    private String success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "PeopleResponse{" +
                "message='" + message + '\'' +
                ", users=" + users +
                ", success='" + success + '\'' +
                '}';
    }
}

