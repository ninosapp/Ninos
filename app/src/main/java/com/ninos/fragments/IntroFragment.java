package com.ninos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninos.R;
import com.ninos.activities.BaseActivity;

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

        BaseActivity baseActivity = (BaseActivity) getActivity();
//        int titleId;
//        int descriptionId;
//        int imageId;
//
//        switch (mPage) {
//            default:
//            case 0:
//                titleId = R.string.app_name;
//                break;
//            case 1:
//                imageId = R.drawable.ic_intro_trophy;
//                titleId = R.string.win_a_movie_ticket;
//                descriptionId = R.string.desc_intro_1;
//                break;
//            case 2:
//                imageId = R.drawable.ic_intro_hourglass;
//                titleId = R.string.win_a_movie_ticket;
//                descriptionId = R.string.desc_intro_1;
//                break;
//            case 3:
//                imageId = R.drawable.ic_intro_music;
//                titleId = R.string.win_a_movie_ticket;
//                descriptionId = R.string.desc_intro_1;
//                break;
//        }
//
//        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
//        TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
//        ImageView iv_songs = (ImageView) view.findViewById(R.id.iv_image);
//
//        tv_title.setText(titleId);
//        tv_description.setText(descriptionId);
//        iv_songs.setImageDrawable(ContextCompat.getDrawable(baseActivity, imageId));
    }

}