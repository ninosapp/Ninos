package com.ninos.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.adapters.CommentAdapter;
import com.ninos.adapters.ImageAdapter;
import com.ninos.firebase.Database;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Comment;
import com.ninos.models.CommentsResponse;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostReport;
import com.ninos.models.PostResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.AWSClient;
import com.ninos.utils.AWSUrls;
import com.ninos.utils.DateUtil;
import com.ninos.utils.PreferenceUtil;
import com.ninos.views.PagerIndicatorDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
    private ImageView iv_profile, iv_clap, iv_menu;
    private JZVideoPlayerStandard video_view;
    private String accessToken;
    private int color_accent, color_dark_grey;
    private boolean isUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        Intent intent = getIntent();
        String id = intent.getStringExtra(POST_PROFILE_ID);

        if (id == null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if (uri != null) {
                id = uri.getQueryParameter(POST_ID);
            }
        }

        color_accent = ContextCompat.getColor(this, R.color.colorAccent);
        color_dark_grey = ContextCompat.getColor(this, R.color.dark_grey);

        final String postId = id;
        accessToken = PreferenceUtil.getAccessToken(this);

        if (isNetworkAvailable()) {

            awsClient = new AWSClient(this);
            awsClient.awsInit();

            video_view = findViewById(R.id.video_view);
            video_view.batteryLevel.setVisibility(View.GONE);
            video_view.mRetryLayout.setVisibility(View.GONE);
            video_view.tinyBackImageView.setVisibility(View.GONE);
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
            iv_clap = findViewById(R.id.iv_clap);
            iv_menu = findViewById(R.id.iv_menu);
            findViewById(R.id.iv_back).setOnClickListener(this);
            findViewById(R.id.ll_share).setOnClickListener(this);

            if (accessToken != null) {
                tv_clap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postInfo.isMyRating()) {
                            removeClap(postInfo, iv_clap, tv_clap);
                        } else {
                            addClap(postInfo, iv_clap, tv_clap);
                        }

                        isUpdated = true;
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

                iv_menu.setOnClickListener(this);
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

            tv_comment.setText(String.format(getString(R.string.s_comments), postInfo.getTotalCommentCount()));
            setClap(postInfo, iv_clap, tv_clap);

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

                ImageAdapter imageAdapter = new ImageAdapter(ShowPostActivity.this, this, R.drawable.pattern_11, postInfo.get_id());
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

                    tv_comment.setText(String.format(getString(R.string.s_comments), commentList.size()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void addClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(this)).enqueue(new Callback<PostClapResponse>() {
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
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_clap);
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

        tv_clap.setText(String.format(getString(clapStringId), postInfo.getTotalClapsCount()));
    }

    private void removeClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.removePostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(this)).enqueue(new Callback<PostClapResponse>() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COMMENT_ADDED:
                if (data != null) {
                    String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                    commentAdapter.resetItems();
                    getComments(postId);
                    isUpdated = true;
                }

                break;
        }
    }

    private void hideKeyboard(EditText et_report) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(et_report.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_menu:
                MenuBuilder menuBuilder = new MenuBuilder(this);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShowPostActivity.this);
                                builder.setTitle(R.string.delete);
                                builder.setMessage(R.string.are_you_sure);
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                        service.deletePost(postInfo.get_id(), PreferenceUtil.getAccessToken(ShowPostActivity.this)).enqueue(new Callback<com.ninos.models.Response>() {
                                            @Override
                                            public void onResponse(Call<com.ninos.models.Response> call, Response<com.ninos.models.Response> response) {
                                                if (response.isSuccessful() && response.body() != null) {

                                                    if (postInfo.getLinks().size() > 1) {
                                                        awsClient.removeImage(postInfo.get_id(), postInfo.getLinks());
                                                    }

                                                    finish();
                                                } else {
                                                    Toast.makeText(ShowPostActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<com.ninos.models.Response> call, Throwable t) {
                                                Toast.makeText(ShowPostActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                builder.create().show();

                                break;
                            case R.id.action_edit:

                                Intent editPostIntent = new Intent(ShowPostActivity.this, EditPostActivity.class);
                                editPostIntent.putStringArrayListExtra(EditPostActivity.PATHS, new ArrayList<>(postInfo.getLinks()));
                                editPostIntent.putExtra(EditPostActivity.POST_ID, postInfo.get_id());
                                editPostIntent.putExtra(EditPostActivity.DESCRIPTION, postInfo.getTitle());
                                startActivityForResult(editPostIntent, MainActivity.POST_EDIT);

                                break;
                            case R.id.action_report:

                                final Dialog dialog = new Dialog(ShowPostActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_report);
                                final EditText et_report = dialog.findViewById(R.id.et_report);

                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
                                            Toast.makeText(ShowPostActivity.this, R.string.provide_reason_for_report, Toast.LENGTH_SHORT).show();
                                        } else {
                                            PostReport postReport = new PostReport();
                                            postReport.setPostId(postInfo.get_id());
                                            postReport.setUserReport(text);

                                            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                            service.reportPost(postReport, PreferenceUtil.getAccessToken(ShowPostActivity.this)).enqueue(new Callback<com.ninos.models.Response>() {
                                                @Override
                                                public void onResponse(@NonNull Call<com.ninos.models.Response> call, @NonNull Response<com.ninos.models.Response> response) {
                                                    if (response.body() != null && response.body().isSuccess()) {
                                                        finish();
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

                MenuInflater inflater = new MenuInflater(ShowPostActivity.this);
                inflater.inflate(R.menu.menu_post, menuBuilder);

                if (postInfo.getUserId().equals(Database.getUserId())) {
                    menuBuilder.findItem(R.id.action_edit).setVisible(true);
                    menuBuilder.findItem(R.id.action_report).setVisible(false);
                    menuBuilder.findItem(R.id.action_delete).setVisible(true);
                } else {
                    menuBuilder.findItem(R.id.action_edit).setVisible(false);
                    menuBuilder.findItem(R.id.action_report).setVisible(true);
                    menuBuilder.findItem(R.id.action_delete).setVisible(false);
                }

                MenuPopupHelper optionsMenu = new MenuPopupHelper(ShowPostActivity.this, menuBuilder, iv_menu);
                optionsMenu.setForceShowIcon(true);
                optionsMenu.show();
                break;
            case R.id.ll_share:
                String text = PreferenceUtil.getUserName(this) + " " + getString(R.string.share_post) + postInfo.get_id() + getString(R.string.encorage);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isUpdated) {
            Intent intent = new Intent();
            intent.putExtra(FilePickerActivity.POST_ID, postInfo.get_id());
            setResult(MainActivity.POST_UPDATE, intent);
        }

        finish();
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
