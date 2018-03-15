package in.ninos.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.ninos.R;
import in.ninos.fragments.AllChallengeSearchFragment;
import in.ninos.fragments.ChallengesSearchFragment;
import in.ninos.fragments.PeopleSearchFragment;

/**
 * Created by FAMILY on 22-01-2018.
 */

public class SearchAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private List<Fragment> fragments;

    public SearchAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
        fragments = new ArrayList<>();
        titles.add(context.getString(R.string.people));
        titles.add(context.getString(R.string.posts));
        titles.add(context.getString(R.string.challenges));

        fragments.add(new PeopleSearchFragment());
        fragments.add(new AllChallengeSearchFragment());
        fragments.add(new ChallengesSearchFragment());
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
}
