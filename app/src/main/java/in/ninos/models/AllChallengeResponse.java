package in.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class AllChallengeResponse {
    private String message;

    private List<PostInfo> postInfo;

    private String success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PostInfo> getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(List<PostInfo> postInfo) {
        this.postInfo = postInfo;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AllChallengeResponse{" +
                "message='" + message + '\'' +
                ", postInfo=" + postInfo +
                ", success='" + success + '\'' +
                '}';
    }
}
