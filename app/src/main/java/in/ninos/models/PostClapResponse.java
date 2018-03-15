package in.ninos.models;

/**
 * Created by FAMILY on 06-01-2018.
 */

public class PostClapResponse {
    private boolean success;
    private String message;
    private int clapsCount;

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

    public int getClapsCount() {
        return clapsCount;
    }

    public void setClapsCount(int clapsCount) {
        this.clapsCount = clapsCount;
    }
}
