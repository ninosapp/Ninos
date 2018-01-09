package com.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.BucketFragment;
import com.ninos.fragments.ImagePickFragment;
import com.ninos.fragments.UploadFragment;
import com.ninos.fragments.VideoPickFragment;

import java.util.ArrayList;


public class FilePickerActivity extends BaseActivity {

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

    public void setSelectedVideo(String selectedVideo) {
        if (selectedVideo == null) {
            finish();
        } else {
//            ArrayList<String> selectedVideos = new ArrayList<>();
//            selectedVideos.add(selectedVideo);
//
//            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
//            fts.add(R.id.fl_file_pick, UploadFragment.newInstance(selectedVideos));
//            fts.commit();
            Intent intent = new Intent(this, TrimmerActivity.class);
            intent.putExtra(TrimmerActivity.VIDEO_PATH, selectedVideo);
            startActivity(intent);
        }
    }
}
