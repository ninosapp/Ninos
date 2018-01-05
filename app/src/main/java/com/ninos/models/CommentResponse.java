package com.ninos.models;

/**
 * Created by FAMILY on 05-01-2018.
 */

public class CommentResponse {
    private String message;
    private boolean success;
    private Comment postComment;

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

    public Comment getPostComment() {
        return postComment;
    }

    public void setPostComment(Comment postComment) {
        this.postComment = postComment;
    }

    @Override
    public String toString() {
        return "CommentsResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", postComments=" + postComment +
                '}';
    }
}
