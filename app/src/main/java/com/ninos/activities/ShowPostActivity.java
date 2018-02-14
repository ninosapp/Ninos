package com.ninos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.adapters.ImageAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPostActivity extends BaseActivity implements View.OnClickListener {

    private static final String POST_ID = "postId";
    private AWSClient awsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if (uri != null) {
                String postId = uri.getQueryParameter(POST_ID);

                if (isNetworkAvailable()) {

                    awsClient = new AWSClient(this);
                    awsClient.awsInit();

                    final TextView tv_name, tv_created_time, tv_title, tv_clap, tv_comment;
                    final ImageView iv_profile, iv_back;
                    final RecyclerView recyclerView;
                    final JZVideoPlayerStandard video_view;

                    video_view = findViewById(R.id.video_view);
                    recyclerView = findViewById(R.id.image_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(null);


                    tv_name = findViewById(R.id.tv_name);
                    tv_created_time = findViewById(R.id.tv_created_time);
                    tv_title = findViewById(R.id.tv_title);
                    tv_clap = findViewById(R.id.tv_clap);
                    tv_comment = findViewById(R.id.tv_comment);
                    iv_profile = findViewById(R.id.iv_profile);
                    iv_back = findViewById(R.id.iv_back);
                    iv_back.setOnClickListener(this);

                    RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                    service.getPost(postId).enqueue(new Callback<PostResponse>() {
                        @Override
                        public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                PostInfo postInfo = response.body().getPostInfo();

                                if (postInfo != null) {
                                    tv_name.setText(postInfo.getUserName());

                                    if (TextUtils.isEmpty(postInfo.getTitle())) {
                                        tv_title.setVisibility(View.GONE);
                                    } else {
                                        tv_title.setText(postInfo.getTitle().trim());
                                        tv_title.setVisibility(View.VISIBLE);
                                    }

                                    if (postInfo.getCreatedAt() != null) {
                                        String date = new DateUtil().formatDateToString(postInfo.getCreatedAt(), DateUtil.FULL_DATE);
                                        tv_created_time.setText(date);
                                    }

                                    tv_comment.setText(String.valueOf(postInfo.getTotalCommentCount()));
                                    tv_clap.setText(String.valueOf(postInfo.getTotalClapsCount()));

                                    RequestOptions requestOptions = new RequestOptions()
                                            .placeholder(R.drawable.ic_account)
                                            .error(R.drawable.ic_account)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true);

                                    Glide.with(ShowPostActivity.this)
                                            .setDefaultRequestOptions(requestOptions)
                                            .load(AWSUrls.GetPI64(ShowPostActivity.this, postInfo.getUserId()))
                                            .into(iv_profile);

                                    String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());

                                    if (postInfo.isVideo()) {
                                        List<String> links = awsClient.getBucket(path);
                                        recyclerView.setVisibility(View.GONE);
                                        video_view.setVisibility(View.VISIBLE);

                                        if (postInfo.getLinks() == null) {
                                            new LoadVideo(video_view, postInfo).execute(path);
                                        } else {
                                            if (links.size() > 0) {
                                                String link = links.get(0);
                                                RequestOptions rO = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                                                Glide.with(ShowPostActivity.this).setDefaultRequestOptions(rO).load(link).into(video_view.thumbImageView);

                                                video_view.setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                                            }
                                        }
                                    } else {
                                        video_view.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        ImageAdapter imageAdapter = new ImageAdapter(ShowPostActivity.this, R.drawable.pattern_11);
                                        recyclerView.setAdapter(imageAdapter);

                                        if (postInfo.getLinks() == null) {
                                            new LoadImage(imageAdapter, postInfo).execute(path);
                                        } else {
                                            for (String link : postInfo.getLinks()) {
                                                imageAdapter.addItem(link);
                                            }
                                        }
                                    }
                                } else {
                                    showToast(R.string.error_message);
                                    finish();
                                }

                            } else {
                                showToast(R.string.error_message);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<PostResponse> call, Throwable t) {

                        }
                    });

                } else {
                    showToast(R.string.no_network);
                    finish();
                }
            } else {
                showToast(R.string.error_message);
                finish();
            }
        } else {
            showToast(R.string.error_message);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }

    public class LoadImage extends AsyncTask<String, Void, List<String>> {

        ImageAdapter imageAdapter;
        PostInfo postInfo;

        LoadImage(ImageAdapter imageAdapter, PostInfo postInfo) {
            this.imageAdapter = imageAdapter;
            this.postInfo = postInfo;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            String path = strings[0];

            List<String> links = awsClient.getBucket(path);
            postInfo.setLinks(links);

            return links;
        }

        @Override
        protected void onPostExecute(List<String> links) {

            for (String link : links) {
                imageAdapter.addItem(link);
            }

            postInfo.setLinks(links);
        }
    }

    public class LoadVideo extends AsyncTask<String, Void, List<String>> {

        WeakReference<JZVideoPlayerStandard> video_view;
        PostInfo postInfo;

        LoadVideo(JZVideoPlayerStandard video_view, PostInfo postInfo) {
            this.video_view = new WeakReference<>(video_view);
            this.postInfo = postInfo;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            String path = strings[0];

            List<String> links = awsClient.getBucket(path);

            postInfo.setLinks(links);
            return links;
        }

        @Override
        protected void onPostExecute(List<String> links) {

            if (links.size() > 0) {
                String link = links.get(0);
                video_view.get().setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                Glide.with(ShowPostActivity.this).setDefaultRequestOptions(requestOptions).load(link).into(video_view.get().thumbImageView);
            }

            postInfo.setLinks(links);
        }
    }
}
