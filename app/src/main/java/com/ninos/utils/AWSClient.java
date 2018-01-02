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
import com.ninos.BuildConfig;
import com.ninos.R;
import com.ninos.firebase.Database;

import java.io.File;

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

    /**
     * Constructor to initialize parameters
     * Groups - upload group attachments
     *
     * @param context context of home activity
     * @param file    feedback image to be uploaded
     * @param userId  user id of user for setting the path to aws
     */
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

            mTransfer = mTransferUtility.upload(BuildConfig.ams_challenge_bucket + "/" + userId, fileName, image);
            mTransfer.setTransferListener(new ImageUploadListener());
        } catch (Exception e) {
            Log.e(TAG, "uploadImage() - " + e.getMessage(), e);
        }
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
