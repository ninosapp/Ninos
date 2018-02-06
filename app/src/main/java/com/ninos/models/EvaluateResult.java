package com.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class EvaluateResult {
    private String totalScore;
    private String acquiredScore;

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

    @Override
    public String toString() {
        return "EvaluateResult{" +
                "totalScore='" + totalScore + '\'' +
                ", acquiredScore='" + acquiredScore + '\'' +
                '}';
    }
}
