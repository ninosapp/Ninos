package in.ninos.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import in.ninos.BuildConfig;
import in.ninos.R;
import in.ninos.activities.FilePickerActivity;
import in.ninos.activities.MainActivity;
import in.ninos.activities.ProfileActivity;
import in.ninos.firebase.Database;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.AddPostResponse;
import in.ninos.models.PostInfo;
import in.ninos.reterofit.RetrofitInstance;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by smeesala on 23-Jun-16.
 * Class for uploading files to aws for user profile image and send a report
 */
public class AWSClient {
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
    private String fileName;


    public AWSClient(Context context, String postId, String path) {
        try {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_image_upload));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mPostId = postId;
            mPath = path;
            File dir = mContext.getCacheDir();
            mFolder = new File(dir, "images");
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public AWSClient(Context context, String path) {
        try {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading_image_upload));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mPath = path;

            //creating a cache dir
            File dir = mContext.getCacheDir();
            mFolder = new File(dir, "images");
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public AWSClient(Context context) {
        try {
            mContext = context;
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    /*Settting TransferUtility api for upload and download a file*/
    public void awsInit() {
        try {
            if (BuildConfig.FLAVOR.equals("prd")) {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(mContext, BuildConfig.AWS_IDENTITY_POOL, Regions.AP_SOUTH_1);
                mAmazonS3 = new AmazonS3Client(credentialsProvider);
                mAmazonS3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
            } else {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(mContext, BuildConfig.AWS_IDENTITY_POOL, Regions.US_EAST_1);
                mAmazonS3 = new AmazonS3Client(credentialsProvider);
                mAmazonS3.setRegion(Region.getRegion(Regions.US_EAST_1));
            }

            //provides api for uploading and downloading content
            mTransferUtility = new TransferUtility(mAmazonS3, mContext);
        } catch (Exception e) {
            CrashUtil.report(e);
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
            CrashUtil.report(e);
        }
    }

    @SuppressLint("CheckResult")
    public void upload192Image() {
        try {
            new Compressor(mContext)
                    .compressToFileAsFlowable(new File(mPath))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            fileName = file.getName();
                            mBitmap = BitmapFactory.decodeFile(file.getPath());
                            String userId = Database.getUserId();
                            File path192 = resizeImage(192);
                            mTransfer = mTransferUtility.upload(BuildConfig.ams_profile_bucket, userId + mContext.getString(R.string.profile_aws_url_suffix_PI192), path192, CannedAccessControlList.PublicRead);
                            mTransfer.setTransferListener(new UploadListener());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    private File resizeImage(int dimension) {
        File file = null;

        try {
            Bitmap resizedBitmap;
            mFolder.createNewFile();
            file = new File(mFolder, fileName);
            file.createNewFile();

            if (dimension != 0) {
                resizedBitmap = Bitmap.createScaledBitmap(mBitmap, dimension, dimension, true);
            } else {
                resizedBitmap = Bitmap.createBitmap(mBitmap);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            CrashUtil.report(e);
        }

        return file;
    }

    @SuppressLint("CheckResult")
    public void uploadImage(final PostInfo postInfo) {
        try {
            new Compressor(mContext)
                    .compressToFileAsFlowable(new File(mPath))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File image) {
                            String fileName = image.getName();
                            String userId = Database.getUserId();
                            String path = BuildConfig.ams_challenge_bucket + "/" + userId + "/" + mPostId;

                            mTransfer = mTransferUtility.upload(path, fileName, image, CannedAccessControlList.PublicRead);
                            mTransfer.setTransferListener(new ImageUploadListener(postInfo));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            CrashUtil.report(e);
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
                links.add(String.format("%s/%s/%s", BuildConfig.AWS_URL, BuildConfig.ams_challenge_bucket, summary.getKey()));
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }

        return links;
    }

    /*clearing the cache after completion of upload*/
    private void deleteDir() {
        try {
            if (mFolder != null && mFolder.isDirectory()) {
                mFolder.delete();
            }

            if (mPath != null) {
                File imagePath = new File(mPath);
                imagePath.delete();
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public void removeImage(String post, List<String> paths) {
        new DeleteImage(post, paths).execute();
    }

    private void deletePost(final PostInfo postInfo) {
        try {
            if (postInfo != null) {
                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                service.deletePost(postInfo.get_id(), PreferenceUtil.getAccessToken(mContext)).enqueue(new Callback<in.ninos.models.Response>() {
                    @Override
                    public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if (postInfo.getLinks().size() > 1) {
                                removeImage(postInfo.get_id(), postInfo.getLinks());
                            }

                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {
                    }
                });
            }
        } catch (Exception e) {
            CrashUtil.report(e);
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
                            Activity activity = ((Activity) mContext);

                            if (mProgressDialog != null && !activity.isFinishing()) {
                                mProgressDialog.dismiss();
                            }

                            Intent intent = new Intent();
                            intent.putExtra(ProfileActivity.PROFILE_PATH, mPath);
                            activity.setResult(ProfileActivity.IMAGE_UPDATED, intent);
                            activity.finish();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteDir();
                                }
                            }, 3000);
                            break;
                    }
                }
            } catch (Exception e) {
                deleteDir();
                CrashUtil.report(e);
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

            Toast.makeText(mContext, R.string.error_message, Toast.LENGTH_SHORT).show();

            Activity activity = ((Activity) mContext);

            if (mProgressDialog != null && !activity.isFinishing()) {
                mProgressDialog.dismiss();
            }

            CrashUtil.report(ex);
        }
    }

    private class VideoUploadListener implements TransferListener {

        PostInfo postInfo;

        VideoUploadListener(PostInfo postInfo) {
            this.postInfo = postInfo;
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            try {
                if (state.toString().contentEquals(COMPLETED)) {
                    final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                    service.updatePost(postInfo.get_id(), postInfo, PreferenceUtil.getAccessToken(mContext)).enqueue(new Callback<AddPostResponse>() {
                        @Override
                        public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                Activity activity = ((Activity) mContext);

                                if (mProgressDialog != null && !activity.isFinishing()) {
                                    mProgressDialog.dismiss();
                                }

                                Intent intent = new Intent();
                                intent.putExtra(FilePickerActivity.POST_ID, postInfo.get_id());
                                activity.setResult(FilePickerActivity.TRIMMER_RESULT, intent);
                                activity.finish();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteDir();
                                    }
                                }, 3000);
                            }
                        }

                        @Override
                        public void onFailure(Call<AddPostResponse> call, Throwable t) {
                            deletePost(postInfo);

                            Activity activity = ((Activity) mContext);

                            if (mProgressDialog != null && !activity.isFinishing()) {
                                mProgressDialog.dismiss();
                            }

                            activity.finish();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteDir();
                                }
                            }, 3000);
                        }
                    });
                }
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.i(TAG, "Progress changed" + bytesCurrent);
        }

        @Override
        public void onError(int id, Exception ex) {
            deletePost(postInfo);

            Activity activity = ((Activity) mContext);

            if (mProgressDialog != null && !activity.isFinishing()) {
                mProgressDialog.dismiss();
            }

            Log.e(TAG, ex.toString(), ex);
            Toast.makeText(mContext, "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class ImageUploadListener implements TransferListener {

        PostInfo postInfo;

        ImageUploadListener(PostInfo postInfo) {
            this.postInfo = postInfo;
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            try {
                if (state.toString().contentEquals(COMPLETED)) {
                    final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                    service.updatePost(postInfo.get_id(), postInfo, PreferenceUtil.getAccessToken(mContext)).enqueue(new Callback<AddPostResponse>() {
                        @Override
                        public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                Activity activity = ((Activity) mContext);

                                if (mProgressDialog != null && !activity.isFinishing()) {
                                    mProgressDialog.dismiss();
                                }

                                Intent intent = new Intent();
                                intent.putExtra(FilePickerActivity.POST_ID, postInfo.get_id());
                                activity.setResult(MainActivity.POST_ADDED, intent);
                                activity.finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddPostResponse> call, Throwable t) {
                            deletePost(postInfo);

                            Activity activity = ((Activity) mContext);

                            if (mProgressDialog != null && !activity.isFinishing()) {
                                mProgressDialog.dismiss();
                            }

                            activity.finish();
                        }
                    });
                }
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onError(int id, Exception ex) {
            deletePost(postInfo);

            Activity activity = ((Activity) mContext);

            if (mProgressDialog != null && !activity.isFinishing()) {
                mProgressDialog.dismiss();
            }

           CrashUtil.report(ex);
            Toast.makeText(mContext, "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DeleteImage extends AsyncTask<String, Void, Void> {

        List<String> links;
        String postId;

        DeleteImage(String postId, List<String> links) {
            this.links = links;
            this.postId = postId;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String bucket = BuildConfig.ams_challenge_bucket + "/" + Database.getUserId() + "/" + postId;

            for (String link : links) {
                String url = link.substring(link.lastIndexOf('/') + 1);
                try {
                    mAmazonS3.deleteObject(new DeleteObjectRequest(bucket, url));
                } catch (Exception e) {
                    CrashUtil.report(e);
                }
            }

            return null;
        }
    }
}
