package in.ninos.models;

/**
 * Created by FAMILY on 23-02-2018.
 */

public class NotificationData {
    private String postId;
    private String postTitle;
    private String commentId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "postId='" + postId + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", commentId='" + commentId + '\'' +
                '}';
    }
}
