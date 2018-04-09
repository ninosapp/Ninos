package in.ninos.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import in.ninos.R;

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
        try {
            super.onViewCreated(view, savedInstanceState);

            int imageId;
            String colour;

            switch (mPage) {
                default:
                case 0:
                    imageId = R.drawable.onboarding1;
                    colour = "#EB5333";
                    break;
                case 1:
                    imageId = R.drawable.onboarding2;
                    colour = "#22C0FF";
                    break;
                case 2:
                    imageId = R.drawable.onboarding3;
                    colour = "#387106";
                    break;
                case 3:
                    imageId = R.drawable.onboarding4;
                    colour = "#F19233";
                    break;
            }

            ImageView iv_songs = view.findViewById(R.id.iv_image);
            FrameLayout intro_background = view.findViewById(R.id.intro_background);

            if (getContext() != null) {
                Glide.with(getContext()).load(imageId).into(iv_songs);
                intro_background.setBackgroundColor(Color.parseColor(colour));
            }
        } catch (Exception e) {
            logError(e);
        }
    }

}