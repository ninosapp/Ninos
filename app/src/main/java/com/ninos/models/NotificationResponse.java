package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 23-02-2018.
 */

public class NotificationResponse {
    private boolean success;
    private String message;
    private List<Notification> notifications;

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

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "NotificationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", notifications=" + notifications +
                '}';
    }
}
