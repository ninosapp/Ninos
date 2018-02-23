package com.ninos.models;

/**
 * Created by FAMILY on 23-02-2018.
 */

public class NotificationCount {
    private boolean success;
    private String message;
    private int notificationsCount;

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

    public int getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(int notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    @Override
    public String toString() {
        return "NotificationCount{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", notificationsCount=" + notificationsCount +
                '}';
    }
}
