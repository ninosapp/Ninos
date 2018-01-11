package com.ninos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 28-12-2017.
 */

public class PostsResponse {
    private String message;

    private String success;

    private List<PostInfo> postInfo;

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

    public List<PostInfo> getPostInfo() {

        if (postInfo == null) {
            postInfo = new ArrayList<>();
        }

        return postInfo;
    }

    public void setPostInfo(List<PostInfo> postInfo) {
        this.postInfo = postInfo;
    }

    @Override
    public String toString() {
        return "PostsResponse{" +
                "message='" + message + '\'' +
                ", success='" + success + '\'' +
                ", postInfo=" + postInfo +
                '}';
    }
}
