package com.ninos.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ninos.R;
import com.ninos.fragments.QuizViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 19-02-2018.
 */

public class QuizViewAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private List<QuizViewFragment> fragments;

    public QuizViewAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
        fragments = new ArrayList<>();
        titles.add(context.getString(R.string.active));
        titles.add(context.getString(R.string.completed));

        fragments.add(QuizViewFragment.newInstance(QuizViewFragment.ACTIVE));
        fragments.add(QuizViewFragment.newInstance(QuizViewFragment.COMPLETED));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public List<QuizViewFragment> getFragments() {
        return fragments;
    }
}
