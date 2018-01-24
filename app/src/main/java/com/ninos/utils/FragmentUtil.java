package com.ninos.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ninos.R;
import com.ninos.activities.BaseActivity;


/**
 * Created by sumanth on 5/24/2017.
 */

public class FragmentUtil {
    public static void removeFragment(BaseActivity baseActivity, Fragment fragment) {
        try {
            FragmentManager fM = baseActivity.getSupportFragmentManager();
            FragmentTransaction fT = fM.beginTransaction();
            fT.remove(fragment);
            fT.commit();
            fM.popBackStack();
        } catch (Exception e) {
            baseActivity.logError(e);
        }
    }

    private static void detachAndAttachFragment(BaseActivity baseActivity, Fragment fA, Fragment fB) {
        try {
            FragmentTransaction fT = baseActivity.getSupportFragmentManager().beginTransaction();
            fT.detach(fA).attach(fB).commit();
        } catch (Exception e) {
            baseActivity.logError(e);
        }
    }

    static void detachAndAttachFragment(BaseActivity baseActivity, Fragment fragment) {
        detachAndAttachFragment(baseActivity, fragment, fragment);
    }

    public static void replaceFragment(BaseActivity baseActivity, Fragment fragment) {
        try {
            String fragmentName = fragment.getClass().getSimpleName();

            FragmentManager fM = baseActivity.getSupportFragmentManager();
            FragmentTransaction fT = fM.beginTransaction();
            fT.replace(R.id.frame_layout, fragment, fragmentName).addToBackStack(null);
            fT.commit();
        } catch (Exception e) {
            baseActivity.logError(e);
        }
    }

    public static Fragment getFragment(BaseActivity baseActivity) {
        FragmentManager fM = baseActivity.getSupportFragmentManager();
        return fM.findFragmentById(R.id.frame_layout);
    }

    public static Fragment getFragment(BaseActivity baseActivity, String tag) {
        FragmentManager fM = baseActivity.getSupportFragmentManager();
        return fM.findFragmentByTag(tag);
    }
}
