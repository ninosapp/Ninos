package com.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class QuizEvaluateResultResponse {
    private boolean success;
    private String message;
    private EvaluateResult evaluateResult;

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

    public EvaluateResult getEvaluateResult() {
        return evaluateResult;
    }

    public void setEvaluateResult(EvaluateResult evaluateResult) {
        this.evaluateResult = evaluateResult;
    }

    @Override
    public String toString() {
        return "QuizEvaluateResultResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", evaluateResult=" + evaluateResult +
                '}';
    }
}
