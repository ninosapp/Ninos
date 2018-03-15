package in.ninos.models;

/**
 * Created by FAMILY on 11-02-2018.
 */

public class ProfileResponse {
    private boolean success;
    private String message;
    private Profile userDetails;

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

    public Profile getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(Profile userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public String toString() {
        return "ProfileResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userDetails=" + userDetails +
                '}';
    }
}
