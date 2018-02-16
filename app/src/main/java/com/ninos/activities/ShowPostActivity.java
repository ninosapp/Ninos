package com.ninos.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.ninos.adapters.CommentAdapter;
import com.ninos.adapters.ImageAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Comment;
import com.ninos.models.CommentsResponse;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.PagerIndicatorDecoration;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninos.activities.MainActivity.COMMENT_ADDED;

public class ShowPostActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_PROFILE_ID = "POST_PROFILE_ID";
    private static final String POST_ID = "postId";
    private AWSClient awsClient;
    private RecyclerView recyclerView;
    private PostInfo postInfo;
    private RetrofitService service;
    private CommentAdapter commentAdapter;
    private TextView tv_name, tv_created_time, tv_title, tv_clap, tv_comment, tv_comments;
    private ImageView iv_profile;
    private FloatingActionButton fab_back;
    private JZVideoPlayerStandard video_view;
    private String accessToken;

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
        String id = intent.getStringExtra(POST_PROFILE_ID);

        if (id == null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if (uri != null) {
                id = uri.getQueryParameter(POST_ID);
            }
        }

        final String postId = id;
        accessToken = PreferenceUtil.getAccessToken(this);

        if (isNetworkAvailable()) {

            awsClient = new AWSClient(this);
            awsClient.awsInit();

            video_view = findViewById(R.id.video_view);
            recyclerView = findViewById(R.id.image_list);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setItemAnimator(null);

            tv_name = findViewById(R.id.tv_name);
            tv_created_time = findViewById(R.id.tv_created_time);
            tv_title = findViewById(R.id.tv_title);
            tv_clap = findViewById(R.id.tv_clap);
            tv_comment = findViewById(R.id.tv_comment);
            tv_comments = findViewById(R.id.tv_comments);
            tv_comments.setVisibility(View.GONE);
            iv_profile = findViewById(R.id.iv_profile);
            fab_back = findViewById(R.id.fab_back);
            fab_back.setOnClickListener(this);

            if (accessToken != null) {
                tv_clap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfo.isMyRating()) {
                            removeClap(postInfo, tv_clap);
                        } else {
                            addClap(postInfo, tv_clap);
                        }
                    }
                });

                tv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ShowPostActivity.this, CommentActivity.class);
                        commentIntent.putExtra(CommentActivity.POST_ID, postInfo.get_id());
                        startActivityForResult(commentIntent, COMMENT_ADDED);
                    }
                });
            }

            LinearLayoutManager commentLayoutManager = new LinearLayoutManager(this);

            RecyclerView list_comment = findViewById(R.id.list_comment);
            list_comment.setNestedScrollingEnabled(true);
            list_comment.setLayoutManager(commentLayoutManager);

            commentAdapter = new CommentAdapter(this);
            list_comment.setAdapter(commentAdapter);

            service = RetrofitInstance.createService(RetrofitService.class);

            if (accessToken != null) {
                service.getPost(postId, accessToken).enqueue(new Callback<PostResponse>() {
                    @Override
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            setView(response);
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
                service.getPost(postId).enqueue(new Callback<PostResponse>() {
                    @Override
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            setView(response);
                        } else {
                            showToast(R.string.error_message);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<PostResponse> call, Throwable t) {

                    }
                });
            }

            getComments(postId);

        } else {
            showToast(R.string.no_network);
            finish();
        }
    }

    public void setView(Response<PostResponse> response) {
        postInfo = response.body().getPostInfo();

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
            setClap(postInfo, tv_clap);

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

    }

    private void getComments(String postId) {
        service.getPostComments(postId, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentsResponse> call, @NonNull Response<CommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> commentList = response.body().getPostComments();

                    if (commentList != null) {
                        commentAdapter.addItems(commentList);
                        tv_comments.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void addClap(final PostInfo postInfo, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(this)).enqueue(new Callback<PostClapResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostClapResponse> call, @NonNull Response<PostClapResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postInfo.setMyRating(true);
                        int clapCount = postInfo.getTotalClapsCount() + 1;
                        postInfo.setTotalClapsCount(clapCount);
                        setClap(postInfo, tv_clap);
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

    private void setClap(final PostInfo postInfo, final TextView tv_clap) {
        int drawableId;

        if (postInfo.isMyRating()) {
            tv_clap.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            drawableId = R.drawable.ic_clap_tint;
        } else {
            tv_clap.setTextColor(Color.BLACK);
            drawableId = R.drawable.ic_clap;
        }

        tv_clap.setText(String.valueOf(postInfo.getTotalClapsCount()));
        tv_clap.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }

    private void removeClap(final PostInfo postInfo, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.removePostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(this)).enqueue(new Callback<PostClapResponse>() {
                @Override
                public void onResponse(@NonNull Call<PostClapResponse> call, @NonNull Response<PostClapResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postInfo.setMyRating(false);
                        int clapCount = postInfo.getTotalClapsCount() - 1;
                        postInfo.setTotalClapsCount(clapCount);
                        setClap(postInfo, tv_clap);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COMMENT_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    commentAdapter.resetItems();
                    getComments(postId);
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_back:
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

            if (links.size() > 1) {
                recyclerView.addItemDecoration(new PagerIndicatorDecoration());
            }
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
