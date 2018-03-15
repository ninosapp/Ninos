package in.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class ChallengeSearchResponse {
    private String message;

    private boolean success;

    private List<ChallengeInfo> challenges;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ChallengeInfo> getChallenges() {

        if (challenges == null) {
            challenges = new ArrayList<>();
        }

        return challenges;
    }

    public void setChallenges(List<ChallengeInfo> challenges) {
        this.challenges = challenges;
    }

    @Override
    public String toString() {
        return "ChallengeSearchResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", challenges=" + challenges +
                '}';
    }
}
