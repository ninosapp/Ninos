package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 09-01-2018.
 */

public class QuizResponse {
    boolean success;
    String message;
    List<Quizze> quizeData;

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

    public List<Quizze> getQuizeData() {
        return quizeData;
    }

    public void setQuizeData(List<Quizze> quizeData) {
        this.quizeData = quizeData;
    }

    @Override
    public String toString() {
        return "QuizResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", quizeData=" + quizeData +
                '}';
    }
}
