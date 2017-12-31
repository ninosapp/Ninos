package com.ninos.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ninos.R;
import com.ninos.fragments.ImageBucketFragment;
import com.ninos.fragments.VideoBucketFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 29-12-2017.
 */

public class FilePickerAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;

    public FilePickerAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
        titles.add(context.getString(R.string.images));
        titles.add(context.getString(R.string.videos));
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        if (position == 0) {
            fragment = new ImageBucketFragment();
        } else {
            fragment = new VideoBucketFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}