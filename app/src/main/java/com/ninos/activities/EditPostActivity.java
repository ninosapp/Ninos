package com.ninos.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ninos.BaseActivity;
import com.ninos.R;
import com.ninos.adapters.EditPostAdapter;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_ID = "POST_ID";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PATHS = "PATHS";
    private TextView tv_description;
    private PostInfo postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        String postId = getIntent().getStringExtra(POST_ID);
        String description = getIntent().getStringExtra(DESCRIPTION);

        RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
        service.getPost(postId, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.body() != null) {
                    postInfo = response.body().getPostInfo();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });

        List<String> links = getIntent().getStringArrayListExtra(PATHS);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_post);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        final RecyclerView recyclerView = findViewById(R.id.upload_list);
        recyclerView.setLayoutManager(layoutManager);

        EditPostAdapter uploadAdapter = new EditPostAdapter(this);

        recyclerView.setAdapter(uploadAdapter);

        uploadAdapter.addItems(links);
        tv_description = findViewById(R.id.tv_description);
        tv_description.setText(description);

        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_select_count).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_cancel:
                onBackPressed();
                break;
        }
    }
}
