package in.ninos.models;

/**
 * Created by FAMILY on 11-01-2018.
 */

public class PostResponse {
    private String message;

    private String success;

    private PostInfo postInfo;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public PostInfo getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(PostInfo postInfo) {
        this.postInfo = postInfo;
    }

    @Override
    public String toString() {
        return "PostResponse{" +
                "message='" + message + '\'' +
                ", success='" + success + '\'' +
                ", postInfo=" + postInfo +
                '}';
    }
}
