package in.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.ninos.R;
import in.ninos.adapters.CommentAdapter;
import in.ninos.firebase.Database;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.Comment;
import in.ninos.models.CommentResponse;
import in.ninos.models.CommentsResponse;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.BadWordUtil;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, OnLoadMoreListener {

    public static final String POST_ID = "POST_ID";
    private EditText et_leave_comment;
    private String postId, accessToken;
    private RetrofitService service;
    private CommentAdapter commentAdapter;
    private boolean isCommentAdded;
    private RecyclerView list_comment;
    private int from = 0, size = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_comment);

            et_leave_comment = findViewById(R.id.et_leave_comment);
            et_leave_comment.setOnEditorActionListener(this);
            findViewById(R.id.iv_add_comment).setOnClickListener(this);

            postId = getIntent().getStringExtra(POST_ID);
            accessToken = PreferenceUtil.getAccessToken(this);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            list_comment = findViewById(R.id.list_comment);
            list_comment.setNestedScrollingEnabled(true);
            list_comment.setLayoutManager(layoutManager);

            commentAdapter = new CommentAdapter(this, postId, list_comment, this);
            list_comment.setAdapter(commentAdapter);

            service = RetrofitInstance.createService(RetrofitService.class);

            getComments();

            findViewById(R.id.iv_back).setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
        }
    }

    private void getComments() {
        try {
            service.getPostComments(postId, from, size, accessToken).enqueue(new Callback<CommentsResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommentsResponse> call, @NonNull Response<CommentsResponse> response) {
                    commentAdapter.removeItem(null);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Comment> commentList = response.body().getPostComments();

                        if (commentList != null) {
                            commentAdapter.addItems(commentList);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.iv_add_comment:
                addComment();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
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

    public void addComment() {
        try {
            String commentValue = et_leave_comment.getText().toString().trim();

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
                        et_leave_comment.setText("");
                        Comment comment = new Comment();
                        comment.setComment(commentValue);
                        comment.setUserId(Database.getUserId());
                        comment.setPostId(postId);
                        isCommentAdded = true;
                        service.addPostComments(postId, accessToken, comment).enqueue(new Callback<CommentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<CommentResponse> call, @NonNull Response<CommentResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Comment comment = response.body().getPostComment();
                                    commentAdapter.addItem(comment, 0);
                                    et_leave_comment.setText("");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<CommentResponse> call, @NonNull Throwable t) {

                            }
                        });
                    }
                }
            }
        }  catch (Exception e) {
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
    public void onBackPressed() {
        if (isCommentAdded) {
            Intent intent = new Intent();
            intent.putExtra(FilePickerActivity.POST_ID, postId);
            setResult(MainActivity.COMMENT_ADDED, intent);
        }
        finish();
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
}
