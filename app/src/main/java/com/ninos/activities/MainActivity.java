package com.ninos.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.fragments.AllChallengesFragment;
import com.ninos.fragments.ChallengesFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private final int RC_STORAGE_PERM = 4531;
    private ImageView iv_home, iv_challenges;
    private Fragment allChallengeFragment, challengeFragment;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);

        findViewById(R.id.fl_home).setOnClickListener(this);
        findViewById(R.id.fl_add).setOnClickListener(this);
        findViewById(R.id.fl_challenges).setOnClickListener(this);

        iv_home = findViewById(R.id.iv_home);
        iv_challenges = findViewById(R.id.iv_challenges);

        allChallengeFragment = new AllChallengesFragment();
        challengeFragment = new ChallengesFragment();

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, allChallengeFragment);
        fts.commit();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.fl_home:
                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                displayAllChallengeFragment();
                break;
            case R.id.fl_add:
                addFile();
                break;
            case R.id.fl_challenges:
                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                displayChallengeFragment();
                break;
        }
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private void addFile() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), RC_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void displayAllChallengeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (allChallengeFragment.isAdded()) { // if the fragment is already in container
            ft.show(allChallengeFragment);
        } else {
            ft.add(R.id.frame_layout, allChallengeFragment);
        }

        if (challengeFragment.isAdded()) {
            ft.hide(challengeFragment);
        }
        ft.commit();
    }

    private void displayChallengeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (challengeFragment.isAdded()) {
            ft.show(challengeFragment);
        } else {
            ft.add(R.id.frame_layout, challengeFragment);
        }

        if (allChallengeFragment.isAdded()) {
            ft.hide(allChallengeFragment);
        }
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setRationale(R.string.rationale_storage_ask_again).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_STORAGE_PERM:
                addFile();
                break;
        }
    }
}
