package in.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class PostSearchResponse {
    private String message;

    private boolean success;

    private List<PostInfo> postsInfo;

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

    public List<PostInfo> getPostsInfo() {

        if (postsInfo == null) {
            postsInfo = new ArrayList<>();
        }

        return postsInfo;
    }

    public void setPostsInfo(List<PostInfo> postsInfo) {
        this.postsInfo = postsInfo;
    }

    @Override
    public String toString() {
        return "PostSearchResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", postsInfo=" + postsInfo +
                '}';
    }
}
