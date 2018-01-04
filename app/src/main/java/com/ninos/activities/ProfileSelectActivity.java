package com.ninos.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.ImagePickFragment;
import com.ninos.fragments.ProfileBucketFragment;
import com.ninos.utils.AWSClient;

public class ProfileSelectActivity extends BaseActivity {

    private ProfileBucketFragment profileBucketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);

        Toolbar toolbar_file_picker = findViewById(R.id.toolbar_profile_select);
        toolbar_file_picker.setTitle(R.string.app_name);
        toolbar_file_picker.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar_file_picker);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        }

        profileBucketFragment = new ProfileBucketFragment();

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.fl_profile_select, profileBucketFragment);
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

        if (manager.findFragmentById(R.id.fl_profile_select) instanceof ImagePickFragment) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.fl_profile_select, profileBucketFragment);
            fts.commit();
        } else {
            super.onBackPressed();
        }
    }

    public void setSelectedImage(String selectedImage) {
        if (selectedImage != null) {
            AWSClient awsClient = new AWSClient(this, selectedImage);
            awsClient.awsInit();
            awsClient.upload512Image();
        }
    }
}