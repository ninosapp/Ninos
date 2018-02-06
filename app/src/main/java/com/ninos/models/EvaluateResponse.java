package com.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class EvaluateResponse {
    private boolean success;
    private String message;
    private EvaluateInfo evaluateInfo;

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

    public EvaluateInfo getEvaluateInfo() {
        return evaluateInfo;
    }

    public void setEvaluateInfo(EvaluateInfo evaluateInfo) {
        this.evaluateInfo = evaluateInfo;
    }

    @Override
    public String toString() {
        return "EvaluateResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", evaluateInfo=" + evaluateInfo +
                '}';
    }
}
