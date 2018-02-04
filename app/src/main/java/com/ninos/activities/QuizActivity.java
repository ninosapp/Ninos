package com.ninos.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ninos.R;
import com.ninos.fragments.InstructionFragment;
import com.ninos.fragments.QuizFragment;

public class QuizActivity extends BaseActivity {

    public static String QUIZ_ID = "QUIZ_ID";
    public static String QUIZ_DURATION = "QUIZ_DURATION";
    public static String QUIZ_TITLE = "QUIZ_TITLE";
    private String quizId, title;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizId = getIntent().getStringExtra(QUIZ_ID);
        duration = getIntent().getIntExtra(QUIZ_DURATION, 6000);
        title = getIntent().getStringExtra(QUIZ_TITLE);

        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, new InstructionFragment());
        fts.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.frame_layout);

        if (fragment instanceof QuizFragment) {
            QuizFragment quizFragment = (QuizFragment) fragment;
            quizFragment.onBackPressed();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void startQuiz() {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(R.id.frame_layout, QuizFragment.newInstance(quizId, duration, title));
        fts.commit();
    }
}
