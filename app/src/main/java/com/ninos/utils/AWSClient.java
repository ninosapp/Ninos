package com.ninos.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;


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
    private int mCount = 0;
    private File mFolder;
    private Bitmap mBitmap;

    public AWSClient(Context context, String postId, String path) {
        try {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_image_upload));
            mProgressDialog.show();
            mPostId = postId;
            mPath = path;
            File dir = mContext.getCacheDir();
            mFolder = new File(dir, "images");
        } catch (Exception e) {
            Log.e(TAG, "AWSClient() - " + e.getMessage(), e);
        }
    }

    public AWSClient(Context context, String path) {
        try {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_image_upload));
            mProgressDialog.show();
            mPath = path;

            //creating a cache dir
            File dir = mContext.getCacheDir();
            mFolder = new File(dir, "images");
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

    private void upload64Image() {
        try {
            String userId = Database.getUserId();
            File path64 = resizeImage(64);

            //setting the path to which bucket file need to be uploaded
            mTransfer = mTransferUtility.upload(BuildConfig.ams_profile_bucket, userId + mContext.getString(R.string.profile_aws_url_suffix_PI64), path64, CannedAccessControlList.PublicRead);
            mTransfer.setTransferListener(new UploadListener());
        } catch (Exception e) {
            Log.e(TAG, "upload64Image() - " + e.getMessage(), e);
        }
    }

    /*Uploading 128X128 resolution image*/
    private void upload128Image() {
        try {
            String userId = Database.getUserId();
            File path128 = resizeImage(128);
            mTransfer = mTransferUtility.upload(BuildConfig.ams_profile_bucket, userId + mContext.getString(R.string.profile_aws_url_suffix_PI128), path128, CannedAccessControlList.PublicRead);
            mTransfer.setTransferListener(new UploadListener());
        } catch (Exception e) {
            Log.e(TAG, "upload128Image() - " + e.getMessage(), e);
        }
    }

    private void upload256Image() {
        try {
            String userId = Database.getUserId();
            File path256 = resizeImage(256);
            mTransfer = mTransferUtility.upload(BuildConfig.ams_profile_bucket, userId + mContext.getString(R.string.profile_aws_url_suffix_PI256), path256, CannedAccessControlList.PublicRead);
            mTransfer.setTransferListener(new UploadListener());
        } catch (Exception e) {
            Log.e(TAG, "upload256Image() - " + e.getMessage(), e);
        }
    }

    /*Uploading 512X512 resolution image*/
    public void upload512Image() {
        try {
            Luban.compress(mContext, new File(mPath))
                    .putGear(Luban.THIRD_GEAR)
                    .launch(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            mBitmap = BitmapFactory.decodeFile(file.getPath());
                            String userId = Database.getUserId();
                            File path512 = resizeImage(512);
                            mTransfer = mTransferUtility.upload(BuildConfig.ams_profile_bucket, userId + mContext.getString(R.string.profile_aws_url_suffix_PI512), path512, CannedAccessControlList.PublicRead);
                            mTransfer.setTransferListener(new UploadListener());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "upload512Image() - " + e.getMessage(), e);
        }
    }

    private File resizeImage(int dimension) {
        try {
            Bitmap resizedBitmap;
            mFolder.createNewFile();
            if (dimension != 0) {
                resizedBitmap = Bitmap.createScaledBitmap(mBitmap, dimension, dimension, true);
            } else {
                resizedBitmap = Bitmap.createBitmap(mBitmap);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            FileOutputStream fos = new FileOutputStream(mFolder);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            Log.e(TAG, "resizeImage() - " + e.toString(), e);
        }
        return mFolder;
    }

    public void uploadImage() {
        try {
            Luban.compress(mContext, new File(mPath))
                    .putGear(Luban.THIRD_GEAR)
                    .launch(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File image) {
                            String fileName = image.getName();
                            String userId = Database.getUserId();
                            String path = BuildConfig.ams_challenge_bucket + "/" + userId + "/" + mPostId;

                            mTransfer = mTransferUtility.upload(path, fileName, image, CannedAccessControlList.PublicRead);
                            mTransfer.setTransferListener(new ImageUploadListener());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

//            if ((image.length() / 1024) > 100) {
//                image = new ImageUtil().getResizedBitmap(mContext, mPath, 400);
//            }


        } catch (Exception e) {
            Log.e(TAG, "uploadImage() - " + e.getMessage(), e);
        }
    }

    public List<String> getBucket(String prefix) {
        List<String> links = new ArrayList<>();

        String delimiter = "/";
        if (!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        try {
            for (S3ObjectSummary summary : S3Objects.withPrefix(mAmazonS3, BuildConfig.ams_challenge_bucket, prefix)) {
                links.add(String.format("%s/%s/%s", "https://s3.amazonaws.com", BuildConfig.ams_challenge_bucket, summary.getKey()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return links;
    }

    /*clearing the cache after completion of upload*/
    private void deleteDir() {
        if (mFolder != null && mFolder.isDirectory()) {
            mFolder.delete();
        }

        if (mPath != null) {
            File imagePath = new File(mPath);
            imagePath.delete();
        }

    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {
            try {
                if (state.toString().contentEquals(COMPLETED)) {
                    mCount = mCount + 1;
                    switch (mCount) {
                        case 1:
                            upload64Image();
                            break;
                        case 2:
                            upload128Image();
                            break;
                        case 3:
                            upload256Image();
                            break;
                        case 4:
                            deleteDir();
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }

                            ((Activity) mContext).finish();
                            break;
                    }
                }
            } catch (Exception e) {
                deleteDir();

                Log.e(TAG, "UploadListener() - onStateChanged(): " + e.getMessage(), e);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            //int percentage = (int) (bytesCurrent / bytesTotal * 100);
        }

        @Override
        public void onError(int id, Exception ex) {
            deleteDir();

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

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
