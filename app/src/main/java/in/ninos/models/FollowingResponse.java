package in.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 16-02-2018.
 */

public class FollowingResponse {
    private boolean success;
    private String message;
    private List<Follow> followingList;

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

    public List<Follow> getFollowingList() {

        if (followingList == null) {
            followingList = new ArrayList<>();
        }

        return followingList;
    }

    public void setFollowingList(List<Follow> followingList) {
        this.followingList = followingList;
    }

    @Override
    public String toString() {
        return "FollowingResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", followingList=" + followingList +
                '}';
    }
}
