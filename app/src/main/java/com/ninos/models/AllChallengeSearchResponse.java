package com.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 12-02-2018.
 */

public class AllChallengeSearchResponse {
    private String message;

    private boolean success;

    private List<PostInfo> challenges;

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

    public List<PostInfo> getChallenges() {

        if (challenges == null) {
            challenges = new ArrayList<>();
        }

        return challenges;
    }

    public void setChallenges(List<PostInfo> challenges) {
        this.challenges = challenges;
    }

    @Override
    public String toString() {
        return "ChallengeSearchResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", challenges=" + challenges +
                '}';
    }
}
