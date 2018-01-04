package com.ninos.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ninos.BuildConfig;
import com.ninos.R;
import com.ninos.firebase.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smeesala on 23-Jun-16.
 * Class for uploading files to aws for user profile image and send a report
 */
public class AWSClient { // TODO: 04/Nov/2016 refactor whole class, should be method concerned, reorder dialog appearance, can discuss

    private static final String TAG = AWSClient.class.getSimpleName();
    private static final String COMPLETED = "COMPLETED";

    private TransferUtility mTransferUtility;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private TransferObserver mTransfer;
    private String mPostId, mPath;
    private AmazonS3 mAmazonS3;

    public AWSClient(Context context, String postId, String path) {
        try {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_image_upload));
            mProgressDialog.show();
            mPostId = postId;
            mPath = path;
        } catch (Exception e) {
            Log.e(TAG, "AWSClient() - " + e.getMessage(), e);
        }
    }

    public AWSClient(Context context) {
        try {
            mContext = context;
        } catch (Exception e) {
            Log.e(TAG, "AWSClient() - " + e.getMessage(), e);
        }
    }

    /*Settting TransferUtility api for upload and download a file*/
    public void awsInit() {
        try {
            //for caching the session credentials
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(mContext, BuildConfig.AWS_IDENTITY_POOL, Regions.US_EAST_1);

            //creating new aws client
            mAmazonS3 = new AmazonS3Client(credentialsProvider);

            //choose a region for an existing AWS client
            mAmazonS3.setRegion(Region.getRegion(Regions.US_EAST_1));

            //provides api for uploading and downloading content
            mTransferUtility = new TransferUtility(mAmazonS3, mContext);
        } catch (Exception e) {
            Log.e(TAG, "awsInit() - " + e.toString(), e);
        }
    }


    /*Uploading Feed Back attachment*/
    public void uploadImage() {
        try {
            File image = new File(mPath);

            String fileName = image.getName();
            String userId = Database.getUserId();
            String path = BuildConfig.ams_challenge_bucket + "/" + userId + "/" + mPostId;

            mTransfer = mTransferUtility.upload(path, fileName, image, CannedAccessControlList.PublicRead);
            mTransfer.setTransferListener(new ImageUploadListener());
        } catch (Exception e) {
            Log.e(TAG, "uploadImage() - " + e.getMessage(), e);
        }
    }

    public boolean bucketExist(String path) {
//        ObjectListing ol = mAmazonS3.listObjects(path);
//        List<S3ObjectSummary> objects = ol.getObjectSummaries();
//        for (S3ObjectSummary os : objects) {
//            System.out.println("* " + os.getKey());
//        }
        return mAmazonS3.doesBucketExist(path);
    }

    public List<String> getBucket(String prefix) {
        List<String> links = new ArrayList<>();

        String delimiter = "/";
        if (!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        for (S3ObjectSummary summary : S3Objects.withPrefix(mAmazonS3, BuildConfig.ams_challenge_bucket, prefix)) {
            links.add(String.format("%s/%s/%s", "https://s3.amazonaws.com", BuildConfig.ams_challenge_bucket, summary.getKey()));
        }

        return links;
    }

    /**
     * Listener to check the status of feedback image uploading
     */
    private class ImageUploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {
            try {
                if (state.toString().contentEquals(COMPLETED)) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    ((Activity) mContext).finish();
                }
            } catch (Exception e) {
                Log.e(TAG, "ImageUploadListener() - onStateChanged(): " + e.getMessage(), e);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {


        }

        @Override
        public void onError(int id, Exception ex) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Log.e(TAG, ex.toString(), ex);
            Toast.makeText(mContext, "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
