package com.ninos.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ninos.fragments.QuestionFragment;
import com.ninos.models.Question;

import java.util.List;

/**
 * Created by FAMILY on 04-02-2018.
 */

public class QuestionsAdapter extends FragmentPagerAdapter {

    private List<Question> questions;

    public QuestionsAdapter(FragmentManager fm, List<Question> questions) {
        super(fm);
        this.questions = questions;
    }

    @Override
    public Fragment getItem(int position) {
        Question question = questions.get(position);
        return QuestionFragment.newInstance(question);
    }

    @Override
    public int getCount() {
        return questions.size();
    }
}