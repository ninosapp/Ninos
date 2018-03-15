package in.ninos.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.ninos.fragments.IntroFragment;

/**
 * Created by smeesala on 6/30/2017.
 */

public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
