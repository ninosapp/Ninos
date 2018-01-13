package com.ninos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.CommentActivity;
import com.ninos.activities.MainActivity;
import com.ninos.activities.ProfileActivity;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smeesala on 6/30/2017.
 */

public class ChallengeAdapter extends CommonRecyclerAdapter<PostInfo> {

    private int lastPosition = -1;
    private Activity mActivity;
    private TypedArray typedArray;
    private DateUtil dateUtil;
    private AWSClient awsClient;
    private RequestOptions requestOptions;
    private int color_accent, color_dark_grey;

    public ChallengeAdapter(Activity activity, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        mActivity = activity;
        typedArray = activity.getResources().obtainTypedArray(R.array.patterns);
        dateUtil = new DateUtil();
        awsClient = new AWSClient(activity);
        awsClient.awsInit();

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop();

        color_accent = ContextCompat.getColor(mActivity, R.color.colorAccent);
        color_dark_grey = ContextCompat.getColor(mActivity, R.color.dark_grey);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) genericHolder;

        challengeViewHolder.bindData(position);
        setAnimation(challengeViewHolder.itemView, position);
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

    private void addClap(final PostInfo postInfo, final int position) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(mActivity)).enqueue(new Callback<PostClapResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostClapResponse> call, @NonNull Response<PostClapResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postInfo.setMyRating(true);
                        int clapCount = Integer.parseInt(postInfo.getTotalClapsCount()) + 1;
                        postInfo.setTotalClapsCount("" + clapCount);
                        updateItem(position, postInfo);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PostClapResponse> call, @NonNull Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_created_time, tv_claps_count, tv_comments_count, tv_title, tv_clap;
        ImageView ic_clap_anim, iv_clap, iv_profile, iv_video;
        LinearLayout ll_clap;
        RecyclerView recyclerView;
        LinearLayout ll_comment;
        View itemView;

        ChallengeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_clap = itemView.findViewById(R.id.tv_clap);
            ic_clap_anim = itemView.findViewById(R.id.ic_clap_anim);
            ll_clap = itemView.findViewById(R.id.ll_clap);
            iv_video = itemView.findViewById(R.id.iv_video);
            iv_clap = itemView.findViewById(R.id.iv_clap);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_created_time = itemView.findViewById(R.id.tv_created_time);
            tv_claps_count = itemView.findViewById(R.id.tv_claps_count);
            tv_comments_count = itemView.findViewById(R.id.tv_comments_count);
            ll_comment = itemView.findViewById(R.id.ll_comment);
            ll_comment.setOnClickListener(this);
            iv_profile.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            ll_clap.setOnClickListener(this);
            recyclerView = itemView.findViewById(R.id.image_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        private void bindData(final int position) {
            final PostInfo postInfo = getItem(position);

            tv_name.setText(postInfo.getUserName());
            tv_title.setText(postInfo.getTitle());

            String date = dateUtil.formatDateToString(postInfo.getCreatedAt(), DateUtil.FULL_DATE);
            tv_created_time.setText(date);

            tv_claps_count.setText(String.format(mActivity.getString(R.string.s_people), postInfo.getTotalClapsCount()));
            tv_comments_count.setText(String.format(mActivity.getString(R.string.s_comments), postInfo.getTotalCommentCount()));

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);


            if (postInfo.isVideo()) {
                iv_video.setVisibility(View.VISIBLE);
                iv_video.setOnClickListener(this);
            } else {
                iv_video.setVisibility(View.GONE);
                iv_video.setOnClickListener(null);
            }

            Glide.with(mActivity)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(mActivity, postInfo.getUserId()))
                    .into(iv_profile);

            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_clap);

            if (postInfo.isMyRating()) {
                tv_clap.setTextColor(color_accent);
                iv_clap.setOnClickListener(null);

                if (drawable != null) {
                    drawable.setColorFilter(color_accent, PorterDuff.Mode.SRC_ATOP);
                }
            } else {
                tv_clap.setTextColor(color_dark_grey);
                iv_clap.setOnClickListener(this);

                if (drawable != null) {
                    drawable.setColorFilter(color_dark_grey, PorterDuff.Mode.SRC_ATOP);
                }
            }

            iv_clap.setImageDrawable(drawable);

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
                            addClap(postInfo, position);
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

            String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());
            ImageAdapter imageAdapter = new ImageAdapter(mActivity, resId);
            recyclerView.setAdapter(imageAdapter);

            if (postInfo.getLinks() == null) {
                new LoadImage(imageAdapter, position).execute(path);
            } else {
                for (String link : postInfo.getLinks()) {
                    imageAdapter.addItem(link);
                }
            }
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            PostInfo postInfo = getItem(position);

            switch (id) {
                case R.id.tv_name:
                case R.id.iv_profile:
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    int resId = typedArray.getResourceId(position, 0);
                    intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
                    intent.putExtra(ProfileActivity.PROFILE_ID, postInfo.getUserId());
                    mActivity.startActivity(intent);
                    break;
                case R.id.ll_comment:
                    Intent commentIntent = new Intent(mActivity, CommentActivity.class);
                    commentIntent.putExtra(CommentActivity.POST_ID, postInfo.get_id());
                    mActivity.startActivityForResult(commentIntent, MainActivity.COMMENT_ADDED);
                    break;
                case R.id.ll_clap:
                case R.id.iv_clap:
                    addClap(postInfo, position);
                    break;
                case R.id.iv_video:
                    List<String> links = postInfo.getLinks();

                    if (links.size() > 0) {
                        String videoLink = links.get(0);
                        Intent videoIntent = new Intent();
                        videoIntent.setAction(Intent.ACTION_VIEW);
                        videoIntent.setDataAndType(Uri.parse(videoLink), "video/mp4");
                        mActivity.startActivity(Intent.createChooser(videoIntent, mActivity.getString(R.string.complete_action_using)));
                    }
                    break;
            }
        }

        public class LoadImage extends AsyncTask<String, Void, List<String>> {

            ImageAdapter imageAdapter;
            int position;

            LoadImage(ImageAdapter imageAdapter, int position) {
                this.imageAdapter = imageAdapter;
                this.position = position;
            }

            @Override
            protected List<String> doInBackground(String... strings) {
                String path = strings[0];

                List<String> links = awsClient.getBucket(path);
                getItem(position).setLinks(links);
                return links;
            }

            @Override
            protected void onPostExecute(List<String> links) {

                for (String link : links) {
                    imageAdapter.addItem(link);
                }

                getItem(position).setLinks(links);
            }
        }
    }
}
