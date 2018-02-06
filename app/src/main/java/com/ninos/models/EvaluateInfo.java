package com.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class EvaluateInfo {
    private String updatedAt;

    private String completedDate;

    private String totalScore;

    private String _id;

    private String createdAt;

    private String userId;

    private String acquiredScore;

    private String quizId;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAcquiredScore() {
        return acquiredScore;
    }

    public void setAcquiredScore(String acquiredScore) {
        this.acquiredScore = acquiredScore;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    @Override
    public String toString() {
        return "EvaluateInfo{" +
                "updatedAt='" + updatedAt + '\'' +
                ", completedDate='" + completedDate + '\'' +
                ", totalScore='" + totalScore + '\'' +
                ", _id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", userId='" + userId + '\'' +
                ", acquiredScore='" + acquiredScore + '\'' +
                ", quizId='" + quizId + '\'' +
                '}';
    }
}
