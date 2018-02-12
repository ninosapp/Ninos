package com.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.adapters.CommentAdapter;
import com.ninos.firebase.Database;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.Comment;
import com.ninos.models.CommentResponse;
import com.ninos.models.CommentsResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static final String POST_ID = "POST_ID";
    private EditText et_leave_comment;
    private String postId, accessToken;
    private RetrofitService service;
    private CommentAdapter commentAdapter;
    private boolean isCommentAdded;
    private RecyclerView list_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        commentAdapter = new CommentAdapter(this);
        list_comment.setAdapter(commentAdapter);

        service = RetrofitInstance.createService(RetrofitService.class);
        service.getPostComments(postId, accessToken).enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentsResponse> call, @NonNull Response<CommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> commentList = response.body().getPostComments();

                    if (commentList != null) {
                        commentAdapter.addItems(commentList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.iv_add_comment:
                addComment();
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
        String commentValue = et_leave_comment.getText().toString().trim();

        if (commentValue.isEmpty()) {
            showToast(R.string.comment_validation);
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

    @Override
    public void onBackPressed() {
        if (isCommentAdded) {
            Intent intent = new Intent();
            intent.putExtra(FilePickerActivity.POST_ID, postId);
            setResult(MainActivity.COMMENT_ADDED, intent);
        }
        finish();
    }
}
