package com.ninos.models;

import java.util.Date;
import java.util.List;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class PostInfo {
    private String[] tags;

    private Date updatedAt;

    private boolean isChallenge;

    private String title;

    private String _id;

    private String totalCommentCount;

    private Date createdAt;

    private String userId;

    private String userName;

    private String type;

    private String totalClapsCount;

    private String challengeTitle;

    private String challengeId;

    private boolean myRating;

    private boolean isVideo;

    private List<String> links;

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isChallenge() {
        return isChallenge;
    }

    public void setChallenge(boolean challenge) {
        isChallenge = challenge;
    }

    public boolean isMyRating() {
        return myRating;
    }

    public void setMyRating(boolean myRating) {
        this.myRating = myRating;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean getIsChallenge() {
        return isChallenge;
    }

    public void setIsChallenge(boolean isChallenge) {
        this.isChallenge = isChallenge;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(String totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalClapsCount() {
        return totalClapsCount;
    }

    public void setTotalClapsCount(String totalClapsCount) {
        this.totalClapsCount = totalClapsCount;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    @Override
    public String toString() {
        return "ClassPojo [tags = " + tags + ", updatedAt = " + updatedAt + ", isChallenge = " + isChallenge + ", title = " + title + ", _id = " + _id + ", totalCommentCount = " + totalCommentCount + ", createdAt = " + createdAt + ", userId = " + userId + ", type = " + type + ", totalClapsCount = " + totalClapsCount + ", challengeTitle = " + challengeTitle + ", challengeId = " + challengeId + "]";
    }
}