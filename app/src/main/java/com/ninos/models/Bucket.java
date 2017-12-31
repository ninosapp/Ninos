package com.ninos.models;

/**
 * Created by FAMILY on 30-12-2017.
 */

public class Bucket {
    private long bucketId;
    private String bucketName;
    private String path;

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "bucketId=" + bucketId +
                ", bucketName='" + bucketName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
