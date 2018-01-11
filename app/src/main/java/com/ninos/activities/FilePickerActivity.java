package com.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.BucketFragment;
import com.ninos.fragments.ImagePickFragment;
import com.ninos.fragments.UploadFragment;
import com.ninos.fragments.VideoPickFragment;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.AddPostResponse;
import com.ninos.models.PostInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FilePickerActivity extends BaseActivity {

    public static final int TRIMMER_RESULT = 2563;
    private BucketFragment bucketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        bucketFragment = new BucketFragment();

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.fl_file_pick, bucketFragment);
        fts.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();

        if (manager.findFragmentById(R.id.fl_file_pick) instanceof ImagePickFragment || manager.findFragmentById(R.id.fl_file_pick) instanceof VideoPickFragment) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.fl_file_pick, bucketFragment);
            fts.commit();
        } else {
            super.onBackPressed();
        }
    }

    public void setSelectedImages(ArrayList<String> selectedImages) {
        if (selectedImages.size() > 0) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.add(R.id.fl_file_pick, UploadFragment.newInstance(selectedImages));
            fts.commit();
        } else {
            finish();
        }
    }

    public void setSelectedVideo(final String selectedVideo) {
        if (selectedVideo == null) {
            finish();
        } else {
            PostInfo postInfo = new PostInfo();
            final String token = PreferenceUtil.getAccessToken(this);
            final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPost(postInfo, token).enqueue(new Callback<AddPostResponse>() {
                @Override
                public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        PostInfo postInfo = response.body().getPostInfo();

                        Intent intent = new Intent(FilePickerActivity.this, TrimmerActivity.class);
                        intent.putExtra(TrimmerActivity.POST_ID, postInfo.get_id());
                        intent.putExtra(TrimmerActivity.VIDEO_PATH, selectedVideo);
                        startActivityForResult(intent, TRIMMER_RESULT);
                    }
                }

                @Override
                public void onFailure(Call<AddPostResponse> call, Throwable t) {
                    Log.e(UploadFragment.class.getSimpleName(), t.getMessage());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TRIMMER_RESULT:
                finish();
                break;
        }
    }
}
