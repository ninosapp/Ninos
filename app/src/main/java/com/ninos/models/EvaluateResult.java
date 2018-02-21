package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class EvaluateResult {
    private String totalScore;
    private String acquiredScore;
    private List<Question> questions;

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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "EvaluateResult{" +
                "totalScore='" + totalScore + '\'' +
                ", acquiredScore='" + acquiredScore + '\'' +
                ", questions=" + questions +
                '}';
    }
}
