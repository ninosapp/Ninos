package in.ninos.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.ninos.R;
import in.ninos.activities.FilePickerActivity;
import in.ninos.activities.MainActivity;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.PostInfo;
import in.ninos.models.Response;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSClient;
import in.ninos.utils.CrashUtil;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class EditPostAdapter extends CommonRecyclerAdapter<String> {

    private Context context;
    private List<String> deletedLinks;
    private String postId;
    private boolean isChallenge;
    private Activity activity;

    public EditPostAdapter(Context context, PostInfo postInfo, Activity activity) {
        this.context = context;
        this.activity = activity;

        if (postInfo != null) {
            postId = postInfo.get_id();
            isChallenge = postInfo.getIsChallenge();
        }

        deletedLinks = new ArrayList<>();
    }

    public void setPostId(PostInfo postInfo) {
        postId = postInfo.get_id();
        isChallenge = postInfo.getIsChallenge();
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_post, parent, false);
        return new EditPostViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        EditPostViewHolder sampleViewHolder = (EditPostViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    public List<String> getDeletedLinks() {
        return deletedLinks;
    }

    private class EditPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;
        FloatingActionButton fab_delete;

        EditPostViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            fab_delete = itemView.findViewById(R.id.fab_delete);
            fab_delete.setOnClickListener(this);
        }

        private void bindData(int position) {
            String path = getItem(position);
            Glide.with(context).load(path).into(iv_image);
        }

        @Override
        public void onClick(View view) {
            final String item = getItem(getAdapterPosition());

            if (getItemCount() > 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletedLinks.add(item);
                                removeItem(item);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            } else {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setMessage(R.string.delete_image_will_delete_post)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                    service.deletePost(postId, PreferenceUtil.getAccessToken(context)).enqueue(new Callback<Response>() {
                                        @Override
                                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                            if (response.isSuccessful() && response.body() != null) {

                                                try {
                                                    AWSClient awsClient = new AWSClient(context);
                                                    awsClient.awsInit();
                                                    awsClient.removeImage(postId, getDataSet());

                                                    Intent intent = new Intent();
                                                    intent.putExtra(FilePickerActivity.POST_ID, postId);
                                                    activity.setResult(MainActivity.POST_EDIT, intent);
                                                    activity.finish();
                                                } catch (Exception e) {
                                                    CrashUtil.report(e);
                                                }
                                            } else {
                                                Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Response> call, Throwable t) {
                                            Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }catch (Exception e) {
                    CrashUtil.report(e);
                }
            }
        }
    }
}