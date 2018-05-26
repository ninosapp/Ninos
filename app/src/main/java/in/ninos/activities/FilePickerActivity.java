package in.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import in.ninos.R;
import in.ninos.fragments.BucketFragment;
import in.ninos.fragments.ImageBucketFragment;
import in.ninos.fragments.ImagePickFragment;
import in.ninos.fragments.UploadFragment;
import in.ninos.fragments.VideoPickFragment;


public class FilePickerActivity extends BaseActivity {

    public static final int TRIMMER_RESULT = 2563;
    public static final String POST_ID = "POST_ID";
    private String challengeId, challengeName;
    private ImageBucketFragment imageBucketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_file_picker);

            imageBucketFragment = new ImageBucketFragment();
            challengeId = getIntent().getStringExtra(ChallengeActivity.CHALLENGE_ID);
            challengeName = getIntent().getStringExtra(ChallengeActivity.CHALLENGE_TITLE);

            Toolbar toolbar_file_picker = findViewById(R.id.toolbar_file_picker);
            toolbar_file_picker.setTitle(R.string.select_image);
            setSupportActionBar(toolbar_file_picker);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {

                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.fl_file_pick, imageBucketFragment);
            fts.commit();
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager manager = getSupportFragmentManager();

            if (manager.findFragmentById(R.id.fl_file_pick) instanceof ImagePickFragment) {
                FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
                fts.replace(R.id.fl_file_pick, imageBucketFragment);
                fts.commit();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            logError(e);
        }
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

    public void setSelectedImages(ArrayList<String> selectedImages) {
        try {
            if (selectedImages.size() > 0) {
                FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
                fts.add(R.id.fl_file_pick, UploadFragment.newInstance(selectedImages, challengeId, challengeName));
                fts.commit();
            } else {
                finish();
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TRIMMER_RESULT:
                try {
                    if (data != null) {
                        String postId = data.getStringExtra(POST_ID);
                        Intent intent = new Intent();
                        intent.putExtra(FilePickerActivity.POST_ID, postId);
                        setResult(MainActivity.POST_ADDED, intent);
                        finish();
                    }
                } catch (Exception e) {
                    logError(e);
                }
                break;
        }
    }
}
