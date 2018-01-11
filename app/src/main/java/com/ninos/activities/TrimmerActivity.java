package com.ninos.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.firebase.Database;
import com.ninos.models.PostInfo;
import com.ninos.utils.AWSClient;
import com.ninos.videoTrimmer.VideoTrimmer;
import com.ninos.videoTrimmer.interfaces.OnTrimVideoListener;
import com.ninos.videoTrimmer.interfaces.OnVideoListener;
import com.ninos.videoTrimmer.utils.StorageUtils;

import java.util.Date;

public class TrimmerActivity extends BaseActivity implements OnTrimVideoListener, OnVideoListener {

    public static final String VIDEO_PATH = "VIDEO_PATH";
    public static final String POST_ID = "POST_ID";
    private VideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        String path = extraIntent.getStringExtra(VIDEO_PATH);
        postId = extraIntent.getStringExtra(POST_ID);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = findViewById(R.id.timeLine);

        String destinationPath = StorageUtils.getPostPath(this, postId);

        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(60);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setDestinationPath(destinationPath);
            mVideoTrimmer.setOnVideoListener(this);
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri contentUri) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PostInfo postInfo = new PostInfo();
                postInfo.set_id(postId);
                postInfo.setCreatedAt(new Date());
                postInfo.setIsChallenge(false);
                postInfo.setUserId(Database.getUserId());
                postInfo.setType("post");
                postInfo.setVideo(true);

                AWSClient awsClient = new AWSClient(TrimmerActivity.this, postInfo.get_id(), contentUri.getPath());
                awsClient.awsInit();
                awsClient.uploadVideo(postInfo);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void sizeExceeded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.file_size_is_too_large);
                finish();
            }
        });
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}