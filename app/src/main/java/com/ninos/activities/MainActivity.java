package com.ninos.activities;

import android.os.Bundle;
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

public class MainActivity extends BaseActivity implements View.OnClickListener {

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
                break;
            case R.id.fl_challenges:
                iv_challenges.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                iv_home.setColorFilter(ContextCompat.getColor(this, R.color.grey));

                displayChallengeFragment();
                break;
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
}
