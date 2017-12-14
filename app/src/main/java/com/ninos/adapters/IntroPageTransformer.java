package com.ninos.adapters;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.ninos.R;

/**
 * Created by smeesala on 6/30/2017.
 */

public class IntroPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {

        int pagePosition = (int) page.getTag();
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);

        if (position <= -1.0f || position >= 1.0f) {
        } else if (position == 0.0f) {
        } else {

            View tv_title = page.findViewById(R.id.tv_title);
            tv_title.setAlpha(1.0f - absPosition);

            View tv_description = page.findViewById(R.id.tv_description);
            tv_description.setTranslationY(-pageWidthTimesPosition / 2f);
            tv_description.setAlpha(1.0f - absPosition);

            View iv_image = page.findViewById(R.id.iv_image);

            if (iv_image != null) {
                iv_image.setAlpha(1.0f - absPosition);
                iv_image.setTranslationX(-pageWidthTimesPosition * 1.5f);
            }

            if (position < 0) {
            } else {
            }
        }
    }

}
