package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 05-01-2018.
 */

public class CommentsResponse {
    private String message;
    private boolean success;
    private List<Comment> postComments;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Comment> getPostComments() {
        return postComments;
    }

    public void setPostComments(List<Comment> postComments) {
        this.postComments = postComments;
    }

    @Override
    public String toString() {
        return "CommentsResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", postComments=" + postComments +
                '}';
    }
}
