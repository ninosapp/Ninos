package in.ninos.models;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class PostReport {
    private String postId;
    private String userReport;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserReport() {
        return userReport;
    }

    public void setUserReport(String userReport) {
        this.userReport = userReport;
    }

    @Override
    public String toString() {
        return "PostReport{" +
                "postId='" + postId + '\'' +
                ", userReport='" + userReport + '\'' +
                '}';
    }
}
