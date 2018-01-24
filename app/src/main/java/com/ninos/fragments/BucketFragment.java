package com.ninos.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.R;
import com.ninos.activities.BaseActivity;
import com.ninos.adapters.FilePickerAdapter;

/**
 * Created by FAMILY on 31-12-2017.
 */

public class BucketFragment extends BaseFragment {

    private BaseActivity mBaseActivity;
    private View cl_home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bucket, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mBaseActivity = (BaseActivity) getActivity();

            cl_home = mBaseActivity.findViewById(R.id.cl_home);

            Toolbar toolbar_file_picker = view.findViewById(R.id.toolbar_file_picker);
            toolbar_file_picker.setTitle(R.string.app_name);
            toolbar_file_picker.setTitleTextColor(ContextCompat.getColor(mBaseActivity, R.color.colorAccent));
            mBaseActivity.setSupportActionBar(toolbar_file_picker);

            ActionBar actionBar = mBaseActivity.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(mBaseActivity, R.drawable.ic_back));
            }

            ViewPager viewPager = view.findViewById(R.id.view_pager);
            viewPager.setAdapter(new FilePickerAdapter(mBaseActivity, getChildFragmentManager()));

            TabLayout tabLayout = view.findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            logError(e);
            showSnackBar(R.string.error_message, cl_home);
        }
    }
}