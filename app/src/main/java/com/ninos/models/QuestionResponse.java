package com.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 30-01-2018.
 */

public class QuestionResponse {
    private boolean success;
    private String message;
    private List<Question> questions;

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

    public List<Question> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<>();
        }

        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuestionResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", questions=" + questions +
                '}';
    }
}
