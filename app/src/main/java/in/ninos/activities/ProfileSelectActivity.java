package in.ninos.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.yalantis.ucrop.UCrop;

import java.io.File;

import in.ninos.R;
import in.ninos.fragments.ImagePickFragment;
import in.ninos.fragments.ProfileBucketFragment;
import in.ninos.fragments.ProfilePickFragment;
import in.ninos.utils.AWSClient;
import in.ninos.utils.StorageUtils;

public class ProfileSelectActivity extends BaseActivity {

    private ProfileBucketFragment profileBucketFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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

    @Override
    public void onBackPressed() {
        try {
            FragmentManager manager = getSupportFragmentManager();

            if (manager.findFragmentById(R.id.fl_profile_select) instanceof ImagePickFragment || manager.findFragmentById(R.id.fl_profile_select) instanceof ProfilePickFragment) {
                FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
                fts.replace(R.id.fl_profile_select, profileBucketFragment);
                fts.commit();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);

                if (resultUri != null && resultUri.getPath() != null) {
                    String path = resultUri.getPath();
                    AWSClient awsClient = new AWSClient(this, path);
                    awsClient.awsInit();
                    awsClient.upload192Image();
                } else {
                    showToast(R.string.error_message);
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                showToast(R.string.error_message);
            }
        }  catch (Exception e) {
            logError(e);
        }
    }

    public void setSelectedImage(String selectedImage) {
        try {
            Uri uri = Uri.fromFile(new File(selectedImage));
            String dPath = StorageUtils.getUserImagePath(this);
            dPath = String.format("%s/%s", dPath, uri.getLastPathSegment());

            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
            options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));
            options.setCompressionQuality(80);

            UCrop.of(uri, Uri.parse(dPath))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(512, 512)
                    .withOptions(options)
                    .start(this);
        }  catch (Exception e) {
            logError(e);
        }
    }
}