package com.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class QuizStartResponse {

    private boolean success;
    private String message;
    private QuizStarted quizStarted;

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

    public QuizStarted getQuizStarted() {
        return quizStarted;
    }

    public void setQuizStarted(QuizStarted quizStarted) {
        this.quizStarted = quizStarted;
    }

    @Override
    public String toString() {
        return "QuizStartResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", quizStarted=" + quizStarted +
                '}';
    }
}
