package com.ninos.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninos.R;

/**
 * Created by smeesala on 6/30/2017.
 */

public class IntroFragment extends BaseFragment {

    private static final String PAGE = "PAGE";
    private int mPage;

    public static IntroFragment newInstance(int page) {
        IntroFragment introFragment = new IntroFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, page);
        introFragment.setArguments(bundle);

        return introFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPage = getArguments().getInt(PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        view.setTag(mPage);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int imageId;

        switch (mPage) {
            default:
            case 0:
                imageId = R.drawable.onboarding1;
                break;
            case 1:
                imageId = R.drawable.onboarding1;
                break;
            case 2:
                imageId = R.drawable.onboarding2;
                break;
            case 3:
                imageId = R.drawable.onboarding3;
                break;
        }

        ImageView iv_songs = view.findViewById(R.id.iv_image);

        if (getContext() != null) {
            iv_songs.setImageDrawable(ContextCompat.getDrawable(getContext(), imageId));
        }
    }

}