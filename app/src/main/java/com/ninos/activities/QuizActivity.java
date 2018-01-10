package com.ninos.activities;

import android.os.Bundle;

import com.ninos.BaseActivity;
import com.ninos.R;

public class QuizActivity extends BaseActivity {

    public static String QUIZ_ID = "QUIZ_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
    }
}
