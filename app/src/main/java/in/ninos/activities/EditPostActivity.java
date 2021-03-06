package in.ninos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ninos.R;
import in.ninos.adapters.EditPostAdapter;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.AddPostResponse;
import in.ninos.models.PostInfo;
import in.ninos.models.PostResponse;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSClient;
import in.ninos.utils.BadWordUtil;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_ID = "POST_ID";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String LINKS = "LINKS";
    public static final String PATHS = "PATHS";
    private TextView tv_description;
    private PostInfo postInfo;
    private EditPostAdapter uploadAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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

                        if (uploadAdapter != null) {
                            uploadAdapter.setPostId(postInfo);
                        }
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

            uploadAdapter = new EditPostAdapter(this, postInfo, this);

            recyclerView.setAdapter(uploadAdapter);

            uploadAdapter.addItems(links);
            tv_description = findViewById(R.id.tv_description);
            tv_description.setText(description);

            findViewById(R.id.btn_upload).setOnClickListener(this);
        }  catch (Exception e) {
            logError(e);
        }
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
            case R.id.btn_upload:
                try {
                    String desc = tv_description.getText().toString().trim();
                    boolean hasBadWords = false;

                    if (!desc.isEmpty()) {
                        List<String> badWords = BadWordUtil.getBardWords();

                        for (String word : desc.toLowerCase().split(" ")) {
                            if (badWords.contains(word)) {
                                hasBadWords = true;
                                break;
                            }
                        }
                    }

                    if (hasBadWords) {
                        showToast(R.string.offensive_words);
                    } else {
                        postInfo.setTitle(tv_description.getText().toString().trim());

                        final RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                        service.updatePost(postInfo.get_id(), postInfo, PreferenceUtil.getAccessToken(this)).enqueue(new Callback<AddPostResponse>() {
                            @Override
                            public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                                if (response.body() != null && response.isSuccessful()) {
                                    AWSClient awsClient = new AWSClient(EditPostActivity.this);
                                    awsClient.awsInit();

                                    if (uploadAdapter.getDeletedLinks().size() > 1) {
                                        awsClient.removeImage(postInfo.get_id(), uploadAdapter.getDeletedLinks());
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra(FilePickerActivity.POST_ID, postInfo.get_id());
                                    intent.putExtra(EditPostActivity.DESCRIPTION, postInfo.getTitle());
                                    intent.putExtra(EditPostActivity.LINKS, new ArrayList<>(uploadAdapter.getDeletedLinks()));
                                    setResult(MainActivity.POST_EDIT, intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<AddPostResponse> call, Throwable t) {
                                finish();
                            }
                        });
                    }
                }  catch (Exception e) {
                    logError(e);
                }

                break;
        }
    }
}
