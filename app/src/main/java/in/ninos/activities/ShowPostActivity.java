package in.ninos.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.ninos.R;
import in.ninos.adapters.CommentAdapter;
import in.ninos.adapters.ShowPostImageAdapter;
import in.ninos.firebase.Database;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.Comment;
import in.ninos.models.CommentResponse;
import in.ninos.models.CommentsResponse;
import in.ninos.models.PostClapResponse;
import in.ninos.models.PostInfo;
import in.ninos.models.PostReport;
import in.ninos.models.PostResponse;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSClient;
import in.ninos.utils.AWSUrls;
import in.ninos.utils.BadWordUtil;
import in.ninos.utils.DateUtil;
import in.ninos.utils.PreferenceUtil;
import in.ninos.views.PagerIndicatorDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPostActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, OnLoadMoreListener {

    public static final String POST_PROFILE_ID = "POST_PROFILE_ID";
    private static final String POST_ID = "postId";
    private AWSClient awsClient;
    private RecyclerView recyclerView;
    private PostInfo postInfo;
    private RetrofitService service;
    private CommentAdapter commentAdapter;
    private TextView tv_name, tv_created_time, tv_title, tv_clap, tv_comment, tv_comments;
    private ImageView iv_profile, iv_clap, iv_menu, ic_clap_anim, iv_send;
    private String accessToken;
    private int color_accent, color_dark_grey;
    private boolean isUpdated;
    private EditText et_comment;
    private RelativeLayout rl_comment, rl_loading;
    private String postId;
    private int from = 0, size = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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

            if (id == null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policy_link)));
                startActivity(browserIntent);
            }

            color_accent = ContextCompat.getColor(this, R.color.colorAccent);
            color_dark_grey = ContextCompat.getColor(this, R.color.dark_grey);

            postId = id;
            accessToken = PreferenceUtil.getAccessToken(this);

            if (isNetworkAvailable()) {

                awsClient = new AWSClient(this);
                awsClient.awsInit();

                ic_clap_anim = findViewById(R.id.ic_clap_anim);
                recyclerView = findViewById(R.id.image_list);
                rl_loading = findViewById(R.id.rl_loading);
                rl_loading.setVisibility(View.VISIBLE);

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
                rl_comment = findViewById(R.id.rl_comment);
                rl_comment.setVisibility(View.GONE);

                et_comment = findViewById(R.id.et_comment);
                iv_send = findViewById(R.id.iv_send);
                iv_send.setOnClickListener(this);
                findViewById(R.id.iv_back).setOnClickListener(this);
                findViewById(R.id.ll_share).setOnClickListener(this);

                if (accessToken != null) {
                    tv_clap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clapAnimation(postInfo);

                            if (postInfo.isMyRating() && postInfo.getTotalClapsCount() > 0) {
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
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    rl_comment.setVisibility(View.VISIBLE);
                    et_comment.setOnEditorActionListener(this);
                    iv_menu.setOnClickListener(this);
                }

                LinearLayoutManager commentLayoutManager = new LinearLayoutManager(this);

                RecyclerView list_comment = findViewById(R.id.list_comment);
                list_comment.setNestedScrollingEnabled(true);
                list_comment.setLayoutManager(commentLayoutManager);

                commentAdapter = new CommentAdapter(this, postId, list_comment, this);
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

                getComments();

            } else {
                showToast(R.string.no_network);
                finish();
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            addComment();
            return true;
        }

        return false;
    }

    public void setView(Response<PostResponse> response) {
        try {
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

                if (!this.isFinishing()) {
                    Glide.with(ShowPostActivity.this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(AWSUrls.GetPI64(ShowPostActivity.this, postInfo.getUserId()))
                            .into(iv_profile);
                }

                String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());

                recyclerView.setVisibility(View.VISIBLE);

                ShowPostImageAdapter imageAdapter = new ShowPostImageAdapter(ShowPostActivity.this, R.drawable.pattern_11, rl_loading);
                recyclerView.setAdapter(imageAdapter);

                if (postInfo.getLinks() == null) {
                    new LoadImage(imageAdapter, postInfo).execute(path);
                } else {
                    for (String link : postInfo.getLinks()) {
                        imageAdapter.addItem(link);
                    }
                }

            } else {
                showToast(R.string.error_message);
                finish();
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    private void getComments() {
        try {
            service.getPostComments(postId, from, size, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<CommentsResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommentsResponse> call, @NonNull Response<CommentsResponse> response) {
                    commentAdapter.removeItem(null);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Comment> commentList = response.body().getPostComments();

                        if (commentList != null) {
                            commentAdapter.addItems(commentList);

                            if (commentList.size() > 0) {
                                tv_comments.setVisibility(View.VISIBLE);
                            }
                        }

                        from = from + size;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {
                    commentAdapter.removeItem(null);
                }
            });
        } catch (Exception e) {
            logError(e);
        }
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
            logError(e);
        }
    }

    private void setClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
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
                    if (postInfo.isMyRating() && postInfo.getTotalClapsCount() > 0) {
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
        } catch (Exception e) {
            logError(e);
        }
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
            logError(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case MainActivity.COMMENT_ADDED:
                    if (data != null) {
                        String postId = data.getStringExtra(FilePickerActivity.POST_ID);

                        commentAdapter.resetItems();
                        getComments();
                        isUpdated = true;
                    }

                    break;
            }
        } catch (Exception e) {
            logError(e);
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
        try {
            switch (v.getId()) {
                case R.id.iv_send:
                    addComment();
                    break;
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
                                            service.deletePost(postInfo.get_id(), PreferenceUtil.getAccessToken(ShowPostActivity.this)).enqueue(new Callback<in.ninos.models.Response>() {
                                                @Override
                                                public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
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
                                                public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {
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
                                                service.reportPost(postReport, PreferenceUtil.getAccessToken(ShowPostActivity.this)).enqueue(new Callback<in.ninos.models.Response>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<in.ninos.models.Response> call, @NonNull Response<in.ninos.models.Response> response) {
                                                        if (response.body() != null && response.body().isSuccess()) {
                                                            finish();
                                                            hideKeyboard(et_report);
                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {
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
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage(getString(R.string.loading));
                    progressDialog.show();
                    Glide.with(this).asBitmap()
                            .load(postInfo.getLinks().get(0))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    progressDialog.dismiss();
                                    String text = PreferenceUtil.getUserName(ShowPostActivity.this) + " " + getString(R.string.share_post) + postInfo.get_id() + getString(R.string.encorage);
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                                    sendIntent.setType("text/plain");
                                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)));
                                }
                            });
                    break;
            }
        } catch (Exception e) {
            logError(e);
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

    private void clapAnimation(final PostInfo postInfo) {
        try {
            Animation pulse_fade = AnimationUtils.loadAnimation(this, R.anim.pulse_fade_in);
            pulse_fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                    if (postInfo.isMyRating()) {
                        ic_clap_anim.setImageDrawable(ContextCompat.getDrawable(ShowPostActivity.this, R.drawable.ic_clap_full_anim));
                    } else {
                        ic_clap_anim.setImageDrawable(ContextCompat.getDrawable(ShowPostActivity.this, R.drawable.ic_clap_anim));
                    }

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
        } catch (Exception e) {
            logError(e);
        }
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

    public void addComment() {
        try {
            String commentValue = et_comment.getText().toString().trim();

            if (commentValue.isEmpty()) {
                showToast(R.string.comment_validation);
            } else {
                boolean hasBadWords = false;

                List<String> badWords = BadWordUtil.getBardWords();

                for (String word : commentValue.toLowerCase().split(" ")) {
                    if (badWords.contains(word)) {
                        hasBadWords = true;
                        break;
                    }
                }

                if (hasBadWords) {
                    showToast(R.string.offensive_words);
                } else {
                    if (containsUrl(commentValue)) {
                        showToast(R.string.urls_not_allowed);
                    } else {
                        et_comment.setText("");
                        Comment comment = new Comment();
                        comment.setComment(commentValue);
                        comment.setUserId(Database.getUserId());
                        comment.setPostId(postInfo.get_id());

                        service.addPostComments(postInfo.get_id(), accessToken, comment).enqueue(new Callback<CommentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<CommentResponse> call, @NonNull Response<CommentResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Comment comment = response.body().getPostComment();
                                    commentAdapter.addItem(comment, 0);
                                    et_comment.setText("");

                                    tv_comments.setVisibility(View.VISIBLE);

                                    tv_comment.setText(String.format(getString(R.string.s_comments), commentAdapter.getItemCount()));
                                    isUpdated = true;
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<CommentResponse> call, @NonNull Throwable t) {

                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    public boolean containsUrl(String text) {
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        return urlMatcher.find();
    }

    @Override
    public void onLoadMore() {
        commentAdapter.addItem(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getComments();
            }
        }, 3000);
    }

    public class LoadImage extends AsyncTask<String, Void, List<String>> {

        ShowPostImageAdapter imageAdapter;
        PostInfo postInfo;

        LoadImage(ShowPostImageAdapter imageAdapter, PostInfo postInfo) {
            this.imageAdapter = imageAdapter;
            this.postInfo = postInfo;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            String path = strings[0];

            List<String> links = new ArrayList<>();

            try {
                links = awsClient.getBucket(path);
                postInfo.setLinks(links);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
}
