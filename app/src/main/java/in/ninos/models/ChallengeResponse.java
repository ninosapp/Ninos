package in.ninos.models;

/**
 * Created by FAMILY on 10-02-2018.
 */

public class ChallengeResponse {

    private boolean success;
    private String message;
    private ChallengeInfo challengeInfo;

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

    public ChallengeInfo getChallengeInfo() {
        return challengeInfo;
    }

    public void setChallengeInfo(ChallengeInfo challengeInfo) {
        this.challengeInfo = challengeInfo;
    }

    @Override
    public String toString() {
        return "ChallengeResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", challengeInfo=" + challengeInfo +
                '}';
    }
}
