package com.ninos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.ninos.firebase.Database;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.models.PostInfo;
import com.ninos.utils.AWSClient;
import com.ninos.utils.DateUtil;

import java.util.List;

/**
 * Created by smeesala on 6/30/2017.
 */

public class ChallengeAdapter extends CommonRecyclerAdapter<PostInfo> {

    private int lastPosition = -1;
    private Activity mActivity;
    private TypedArray typedArray;
    private DateUtil dateUtil;
    private String userId;
    private AWSClient awsClient;

    public ChallengeAdapter(Activity activity, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        mActivity = activity;
        typedArray = activity.getResources().obtainTypedArray(R.array.patterns);
        dateUtil = new DateUtil();
        userId = Database.getUserId();
        awsClient = new AWSClient(activity);
        awsClient.awsInit();
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

        PostInfo postInfo = getItem(position);
        sampleViewHolder.bindData(postInfo, position);
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
        TextView tv_name, tv_created_time, tv_claps_count, tv_comments_count, tv_title;
        ImageView ic_clap_anim, iv_clap, iv_profile;
        RecyclerView recyclerView;
        ImageAdapter imageAdapter;

        SampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            ic_clap_anim = itemView.findViewById(R.id.ic_clap_anim);
            iv_clap = itemView.findViewById(R.id.iv_clap);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_created_time = itemView.findViewById(R.id.tv_created_time);
            tv_claps_count = itemView.findViewById(R.id.tv_claps_count);
            tv_comments_count = itemView.findViewById(R.id.tv_comments_count);
            iv_profile.setOnClickListener(this);
            tv_name.setOnClickListener(this);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);

            recyclerView = itemView.findViewById(R.id.image_list);
            recyclerView.setLayoutManager(layoutManager);
        }

        private void bindData(PostInfo postInfo, int position) {
            tv_name.setText(postInfo.getUserName());
            tv_title.setText(postInfo.getTitle());

            String date = dateUtil.formatDateToString(postInfo.getCreatedAt(), DateUtil.FULL_DATE);
            tv_created_time.setText(date);

            tv_claps_count.setText(String.format(mActivity.getString(R.string.s_people), postInfo.getTotalClapsCount()));
            tv_comments_count.setText(String.format(mActivity.getString(R.string.s_comments), postInfo.getTotalCommentCount()));

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);
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

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return gd.onTouchEvent(event);
                }
            });

            String path = String.format("%s/%s", userId, postInfo.get_id());


            imageAdapter = new ImageAdapter(mActivity, resId);

            recyclerView.setAdapter(imageAdapter);


            new LoadImage().execute(path);
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

        public class LoadImage extends AsyncTask<String, Void, List<String>> {

            @Override
            protected List<String> doInBackground(String... strings) {
                String path = strings[0];
                List<String> links = awsClient.getBucket(path);
                return links;
            }

            @Override
            protected void onPostExecute(List<String> links) {
                for (String link : links) {
                    imageAdapter.addItem(link);
                }
            }
        }
    }
}
