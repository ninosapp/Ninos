package com.ninos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.activities.ProfileActivity;

/**
 * Created by smeesala on 6/30/2017.
 */

public class ChallengeAdapter extends CommonRecyclerAdapter<String> {

    private int lastPosition = -1;
    private Activity mActivity;
    private TypedArray typedArray;

    public ChallengeAdapter(Activity activity) {
        mActivity = activity;
        typedArray = activity.getResources().obtainTypedArray(R.array.patterns);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new SampleViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        SampleViewHolder sampleViewHolder = (SampleViewHolder) genericHolder;

        String title = getItem(position);
        sampleViewHolder.bindData(title, position);
        setAnimation(sampleViewHolder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private class SampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name;
        ImageView iv_challenge, ic_clap_anim, iv_clap, iv_profile;

        SampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_challenge = itemView.findViewById(R.id.iv_challenge);
            ic_clap_anim = itemView.findViewById(R.id.ic_clap_anim);
            iv_clap = itemView.findViewById(R.id.iv_clap);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_profile.setOnClickListener(this);
            tv_name.setOnClickListener(this);
        }

        private void bindData(String title, int position) {
            tv_name.setText(title);
            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);
            iv_challenge.setImageDrawable(ContextCompat.getDrawable(mActivity, resId));
            iv_profile.setImageDrawable(ContextCompat.getDrawable(mActivity, resId));

            final GestureDetector gd = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    Animation pulse_fade = AnimationUtils.loadAnimation(mActivity, R.anim.pulse_fade_in);
                    pulse_fade.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ic_clap_anim.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ic_clap_anim.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    ic_clap_anim.startAnimation(pulse_fade);
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return true;
                }
            });

            iv_challenge.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return gd.onTouchEvent(event);
                }
            });
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.tv_name:
                case R.id.iv_profile:
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    int resId = typedArray.getResourceId(getAdapterPosition(), 0);
                    intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
                    mActivity.startActivity(intent);
                    break;
            }
        }
    }
}
