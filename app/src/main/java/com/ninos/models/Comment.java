package com.ninos.models;

import java.util.Date;

/**
 * Created by FAMILY on 05-01-2018.
 */

public class Comment {
    private String comment;
    private String commentId;
    private String userId;
    private String postId;
    private String userName;
    private Date createdAt;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", commentId='" + commentId + '\'' +
                ", userId='" + userId + '\'' +
                ", postId='" + postId + '\'' +
                ", userName='" + userName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
