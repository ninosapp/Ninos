package com.ninos.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
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

public class CommentActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener, TextView.OnEditorActionListener {

    public static final String POST_ID = "POST_ID";
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;
    private CardView cv_comment;
    private EditText et_leave_comment;
    private String postId, accessToken;
    private RetrofitService service;
    private CommentAdapter commentAdapter;
    private boolean isCommentAdded;
    private RecyclerView list_comment;
    private NestedScrollView ns_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ns_comment = findViewById(R.id.ns_comment);
        et_leave_comment = findViewById(R.id.et_leave_comment);
        et_leave_comment.setOnEditorActionListener(this);
        findViewById(R.id.iv_add_comment).setOnClickListener(this);
        cv_comment = findViewById(R.id.cv_comment);
        cv_comment.setOnTouchListener(this);

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
    public boolean onTouch(View view, MotionEvent event) {
        try {
            // Get finger position on screen
            final int Y = (int) event.getRawY();

            // Switch on motion event type
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    // save default base layout height
                    defaultViewHeight = cv_comment.getHeight();

                    // Init finger and view position
                    previousFingerPosition = Y;
                    baseLayoutPosition = (int) cv_comment.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    // If user was doing a scroll up
                    if (isScrollingUp) {
                        // Reset baselayout position
                        cv_comment.setY(0);
                        // We are not in scrolling up mode anymore
                        isScrollingUp = false;
                    }

                    // If user was doing a scroll down
                    if (isScrollingDown) {
                        // Reset baselayout position
                        cv_comment.setY(0);
                        // Reset base layout size
                        cv_comment.getLayoutParams().height = defaultViewHeight;
                        cv_comment.requestLayout();
                        // We are not in scrolling down mode anymore
                        isScrollingDown = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isClosing) {
                        int currentYPosition = (int) cv_comment.getY();

                        // If we scroll up
                        if (previousFingerPosition > Y) {
                            // First time android rise an event for "up" move
                            if (!isScrollingUp) {
                                isScrollingUp = true;
                            }

                            // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
                            if (cv_comment.getHeight() < defaultViewHeight) {
                                cv_comment.getLayoutParams().height = cv_comment.getHeight() - (Y - previousFingerPosition);
                                cv_comment.requestLayout();
                            } else {
                                // Has user scroll enough to "auto close" popup ?
                                //if (mIsReachedBottom && (baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                    closeUpAndDismissDialog(currentYPosition);
                                    return true;
                                }

                                //
                            }
                            cv_comment.setY(cv_comment.getY() + (Y - previousFingerPosition));

                        }
                        // If we scroll down
                        else {

                            // First time android rise an event for "down" move
                            if (!isScrollingDown) {
                                isScrollingDown = true;
                            }

                            // Has user scroll enough to "auto close" popup ?
                            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                                closeDownAndDismissDialog(currentYPosition);
                                return true;
                            }

                            // Change base layout size and position (must change position because view anchor is top left corner)
                            cv_comment.setY(cv_comment.getY() + (Y - previousFingerPosition));
                            cv_comment.getLayoutParams().height = cv_comment.getHeight() - (Y - previousFingerPosition);
                            cv_comment.requestLayout();
                        }

                        // Update position
                        previousFingerPosition = Y;
                    }
                    break;
            }
        } catch (Exception e) {
            showToast(R.string.error_message);
        }
        return true;
    }

    public void closeUpAndDismissDialog(int currentPosition) {
        try {
            isClosing = true;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(cv_comment, "y", currentPosition, -cv_comment.getHeight());
            positionAnimator.setDuration(300);
            positionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onBackPressed();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            positionAnimator.start();
        } catch (Exception e) {
            showToast(R.string.error_message);
        }
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        try {
            isClosing = true;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenHeight = size.y;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(cv_comment, "y", currentPosition, screenHeight + cv_comment.getHeight());
            positionAnimator.setDuration(300);
            positionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    onBackPressed();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            positionAnimator.start();
//            Intent output = new Intent();
//            output.putExtra(COMMENTS_COUNT, mCommentsAdapter.getItemCount());
//            setResult(Activity.RESULT_OK, output);
        } catch (Exception e) {
            showToast(R.string.error_message);
        }
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
