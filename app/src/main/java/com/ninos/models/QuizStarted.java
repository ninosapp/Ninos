package com.ninos.models;

import java.util.Date;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class QuizStarted {
    private String _id;
    private String userId;
    private String quizId;
    private String totalScore;
    private String acquiredScore;
    private Date updatedAt;
    private Date createdAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getAcquiredScore() {
        return acquiredScore;
    }

    public void setAcquiredScore(String acquiredScore) {
        this.acquiredScore = acquiredScore;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "QuizStarted{" +
                "_id='" + _id + '\'' +
                ", userId='" + userId + '\'' +
                ", quizId='" + quizId + '\'' +
                ", totalScore='" + totalScore + '\'' +
                ", acquiredScore='" + acquiredScore + '\'' +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
