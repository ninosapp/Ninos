package com.ninos.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.CommentActivity;
import com.ninos.activities.EditPostActivity;
import com.ninos.activities.MainActivity;
import com.ninos.activities.ProfileActivity;
import com.ninos.firebase.Database;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostReport;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.CircleImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smeesala on 6/30/2017.
 */

public class AllChallengeAdapter extends CommonRecyclerAdapter<PostInfo> {

    private Activity mActivity;
    private TypedArray typedArray;
    private DateUtil dateUtil;
    private AWSClient awsClient;
    private RequestOptions requestOptions;
    private int color_accent, color_dark_grey;

    public AllChallengeAdapter(Activity activity, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
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
                .skipMemoryCache(true);

        color_accent = ContextCompat.getColor(mActivity, R.color.colorAccent);
        color_dark_grey = ContextCompat.getColor(mActivity, R.color.dark_grey);

    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_all, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) genericHolder;

        challengeViewHolder.bindData(position);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void addClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(mActivity)).enqueue(new Callback<PostClapResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostClapResponse> call, @NonNull Response<PostClapResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postInfo.setMyRating(true);
                        int clapCount = postInfo.getTotalClapsCount() + 1;
                        postInfo.setTotalClapsCount(clapCount);
                        setClap(postInfo, iv_clap, tv_clap);
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

    private void setClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_clap);
        iv_clap.setImageDrawable(drawable);
        int color;

        if (postInfo.isMyRating()) {
            tv_clap.setTextColor(color_accent);
            color = color_accent;
        } else {
            tv_clap.setTextColor(color_dark_grey);
            color = color_dark_grey;
        }

        iv_clap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postInfo.isMyRating()) {
                    removeClap(postInfo, iv_clap, tv_clap);
                } else {
                    addClap(postInfo, iv_clap, tv_clap);
                }
            }
        });

        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(drawable, color);

            } else {
                drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }

        int clapStringId;

        if (postInfo.getTotalClapsCount() > 1) {
            clapStringId = R.string.s_claps;
        } else {
            clapStringId = R.string.s_clap;
        }

        tv_clap.setText(String.format(mActivity.getString(clapStringId), postInfo.getTotalClapsCount()));
    }

    private void removeClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.removePostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(mActivity)).enqueue(new Callback<PostClapResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostClapResponse> call, @NonNull Response<PostClapResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postInfo.setMyRating(false);
                        int clapCount = postInfo.getTotalClapsCount() - 1;
                        postInfo.setTotalClapsCount(clapCount);
                        setClap(postInfo, iv_clap, tv_clap);
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
        TextView tv_name, tv_created_time, tv_title, tv_clap, tv_comment;
        ImageView ic_clap_anim, iv_clap, iv_menu;
        CircleImageView iv_profile;
        RecyclerView recyclerView;
        LinearLayout ll_comment, ll_options, ll_clap;
        View itemView;
        RelativeLayout rl_challenge;
        JZVideoPlayerStandard video_view;
        GestureDetector gd;

        ChallengeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_clap = itemView.findViewById(R.id.tv_clap);
            ic_clap_anim = itemView.findViewById(R.id.ic_clap_anim);
            ll_clap = itemView.findViewById(R.id.ll_clap);
            iv_clap = itemView.findViewById(R.id.iv_clap);
            iv_menu = itemView.findViewById(R.id.iv_menu);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_created_time = itemView.findViewById(R.id.tv_created_time);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            ll_comment = itemView.findViewById(R.id.ll_comment);
            ll_options = itemView.findViewById(R.id.ll_options);
            iv_menu.setOnClickListener(this);
            ll_comment.setOnClickListener(this);
            iv_profile.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            ll_clap.setOnClickListener(this);
            video_view = itemView.findViewById(R.id.video_view);
            video_view.batteryLevel.setVisibility(View.GONE);
            video_view.mRetryLayout.setVisibility(View.GONE);

            rl_challenge = itemView.findViewById(R.id.rl_challenge);
            recyclerView = itemView.findViewById(R.id.image_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(null);
        }

        private void bindData(final int position) {
            final PostInfo postInfo = getItem(position);

            tv_name.setText(postInfo.getUserName());

            if (TextUtils.isEmpty(postInfo.getTitle())) {
                tv_title.setVisibility(View.GONE);
            } else {
                tv_title.setText(postInfo.getTitle().trim());
                tv_title.setVisibility(View.VISIBLE);
            }

            if (postInfo.getCreatedAt() != null) {
                String date = dateUtil.formatDateToString(postInfo.getCreatedAt(), DateUtil.FULL_DATE);
                tv_created_time.setText(date);
            }

            int commentStringId;

            if (postInfo.getTotalClapsCount() > 1) {
                commentStringId = R.string.s_comments;
            } else {
                commentStringId = R.string.s_comment;
            }

            tv_comment.setText(String.format(mActivity.getString(commentStringId), postInfo.getTotalCommentCount()));

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);
            String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());

            if (postInfo.isVideo()) {
                List<String> links = awsClient.getBucket(path);
                recyclerView.setVisibility(View.GONE);
                video_view.setVisibility(View.VISIBLE);

                if (postInfo.getLinks() == null) {
                    new LoadVideo(video_view, position).execute(path);
                } else {
                    if (links.size() > 0) {
                        String link = links.get(0);
                        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                        Glide.with(mActivity).setDefaultRequestOptions(requestOptions).load(link).into(video_view.thumbImageView);

                        video_view.setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                    }
                }
            } else {
                video_view.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

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

            Glide.with(mActivity)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(mActivity, postInfo.getUserId()))
                    .into(iv_profile);

            setClap(postInfo, iv_clap, tv_clap);


            gd = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    clapAnimation(postInfo);
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
        }

        private void clapAnimation(final PostInfo postInfo) {
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
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            final int position = getAdapterPosition();
            final PostInfo postInfo = getItem(position);

            switch (id) {
                case R.id.tv_name:
                case R.id.iv_profile:
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    int resId = typedArray.getResourceId(position % 10, 0);
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
                    clapAnimation(postInfo);

                    if (postInfo.isMyRating()) {
                        removeClap(postInfo, iv_clap, tv_clap);
                    } else {
                        addClap(postInfo, iv_clap, tv_clap);
                    }
                    break;
                case R.id.iv_menu:
                    MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                    menuBuilder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.action_edit:

                                    Intent editPostIntent = new Intent(mActivity, EditPostActivity.class);
                                    editPostIntent.putStringArrayListExtra(EditPostActivity.PATHS, new ArrayList<>(postInfo.getLinks()));
                                    editPostIntent.putExtra(EditPostActivity.POST_ID, postInfo.get_id());
                                    editPostIntent.putExtra(EditPostActivity.DESCRIPTION, postInfo.getTitle());
                                    mActivity.startActivityForResult(editPostIntent, MainActivity.COMMENT_ADDED);

                                    break;
                                case R.id.action_report:

                                    final Dialog dialog = new Dialog(mActivity);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_report);
                                    final EditText et_report = dialog.findViewById(R.id.et_report);

                                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                                    if (imm != null) {
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    }

                                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            hideKeyboard(et_report);

                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String text = et_report.getText().toString().trim();

                                            if (text.isEmpty()) {
                                                Toast.makeText(mActivity, R.string.provide_reason_for_report, Toast.LENGTH_SHORT).show();
                                            } else {
                                                PostReport postReport = new PostReport();
                                                postReport.setPostId(postInfo.get_id());
                                                postReport.setUserReport(text);

                                                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                                service.reportPost(postReport, PreferenceUtil.getAccessToken(mActivity)).enqueue(new Callback<com.ninos.models.Response>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<com.ninos.models.Response> call, @NonNull Response<com.ninos.models.Response> response) {
                                                        if (response.body() != null && response.body().isSuccess()) {
                                                            removeItem(position);
                                                            hideKeyboard(et_report);
                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {
                                                        hideKeyboard(et_report);
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    dialog.show();
                                    break;
                            }

                            return false;
                        }

                        @Override
                        public void onMenuModeChange(MenuBuilder menu) {

                        }
                    });

                    MenuInflater inflater = new MenuInflater(mActivity);
                    inflater.inflate(R.menu.menu_post, menuBuilder);

                    if (postInfo.getUserId().equals(Database.getUserId())) {
                        menuBuilder.findItem(R.id.action_edit).setVisible(true);
                        menuBuilder.findItem(R.id.action_report).setVisible(true);
                    } else {
                        menuBuilder.findItem(R.id.action_edit).setVisible(false);
                        menuBuilder.findItem(R.id.action_report).setVisible(true);
                    }

                    MenuPopupHelper optionsMenu = new MenuPopupHelper(mActivity, menuBuilder, view);
                    optionsMenu.setForceShowIcon(true);
                    optionsMenu.show();
                    break;
            }
        }

        private void hideKeyboard(EditText et_report) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(et_report.getWindowToken(), 0);
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

                if (getItemCount() > position) {
                    getItem(position).setLinks(links);
                }
                return links;
            }

            @Override
            protected void onPostExecute(List<String> links) {

                for (String link : links) {
                    imageAdapter.addItem(link);
                }

                if (getItemCount() > position) {
                    getItem(position).setLinks(links);
                }
            }
        }

        public class LoadVideo extends AsyncTask<String, Void, List<String>> {

            WeakReference<JZVideoPlayerStandard> video_view;
            int position;

            LoadVideo(JZVideoPlayerStandard video_view, int position) {
                this.video_view = new WeakReference<>(video_view);
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

                if (links.size() > 0) {
                    String link = links.get(0);
                    video_view.get().setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    if (mActivity != null && !mActivity.isDestroyed()) {
                        Glide.with(mActivity).setDefaultRequestOptions(requestOptions).load(link).into(video_view.get().thumbImageView);
                    }
                }

                if (getItemCount() > position) {
                    getItem(position).setLinks(links);
                }
            }
        }
    }
}
