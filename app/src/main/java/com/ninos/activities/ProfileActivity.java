package com.ninos.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ninos.R;

public class ProfileActivity extends AppCompatActivity {

    public static final String PROFILE_PLACE_HOLDER = "PROFILE_PLACE_HOLDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        int placeHolderId = getIntent().getIntExtra(PROFILE_PLACE_HOLDER, 0);

        ImageView iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setImageDrawable(ContextCompat.getDrawable(this, placeHolderId));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
