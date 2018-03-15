package in.ninos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.ProfileActivity;
import in.ninos.firebase.Database;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.Comment;
import in.ninos.models.CommentResponse;
import in.ninos.models.Response;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSUrls;
import in.ninos.utils.DateUtil;
import in.ninos.utils.PreferenceUtil;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by FAMILY on 05-01-2018.
 */

public class CommentAdapter extends CommonRecyclerAdapter<Comment> {

    private Context context;
    private RequestOptions requestOptions;
    private DateUtil dateUtil;
    private String userId;
    private String postId;

    public CommentAdapter(Context context, String postId) {
        this.context = context;
        userId = Database.getUserId();

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop();

        dateUtil = new DateUtil();
        this.postId = postId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        CommentViewHolder sampleViewHolder = (CommentViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image, iv_menu;
        TextView tv_user_name, tv_time, tv_comment;

        CommentViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            iv_menu = itemView.findViewById(R.id.iv_menu);

            iv_image.setOnClickListener(this);
            tv_user_name.setOnClickListener(this);
            iv_menu.setOnClickListener(this);
        }

        private void bindData(int position) {
            Comment comment = getItem(position);

            if (comment.getUserId().equals(userId)) {
                iv_menu.setVisibility(View.VISIBLE);
            } else {
                iv_menu.setVisibility(View.GONE);
            }

            tv_comment.setText(comment.getComment());
            tv_user_name.setText(comment.getUserName());

            String time = dateUtil.formatDateToString(comment.getCreatedAt(), DateUtil.FULL_DATE);
            tv_time.setText(time);

            Glide.with(context).load(AWSUrls.GetPI64(context, comment.getUserId())).apply(requestOptions).into(iv_image);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            final Comment comment = getItem(position);

            switch (v.getId()) {
                case R.id.iv_menu:
                    MenuBuilder menuBuilder = new MenuBuilder(context);
                    menuBuilder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(R.string.delete_comment);
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
                                            service.deleteComment(postId, comment.get_id(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<Response>() {
                                                @Override
                                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        removeItem(position);
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
                                    });
                                    builder.create().show();

                                    return true;
                                case R.id.action_edit:
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                    LayoutInflater inflater = LayoutInflater.from(context);

                                    View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
                                    dialogBuilder.setView(dialogView);

                                    final EditText et_comment = dialogView.findViewById(R.id.et_comment);
                                    et_comment.setText(comment.getComment());

                                    dialogBuilder.setMessage(R.string.comment);
                                    dialogBuilder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String commentValue = et_comment.getText().toString();

                                            if (commentValue.isEmpty()) {
                                                Toast.makeText(context, R.string.comment_is_empty, Toast.LENGTH_SHORT).show();
                                            } else {
                                                comment.setCommentId(comment.get_id());
                                                comment.setComment(commentValue);

                                                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                                service.updatePostComment(postId, PreferenceUtil.getAccessToken(context), comment).enqueue(new Callback<CommentResponse>() {
                                                    @Override
                                                    public void onResponse(Call<CommentResponse> call, retrofit2.Response<CommentResponse> response) {
                                                        if (response.isSuccessful() && response.body() != null) {
                                                            updateItem(position, comment);
                                                        } else {
                                                            Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<CommentResponse> call, Throwable t) {
                                                        Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();

                                    return true;
                            }

                            return false;
                        }

                        @Override
                        public void onMenuModeChange(MenuBuilder menu) {

                        }
                    });

                    MenuInflater inflater = new MenuInflater(context);
                    inflater.inflate(R.menu.menu_post, menuBuilder);

                    menuBuilder.findItem(R.id.action_edit).setVisible(true);
                    menuBuilder.findItem(R.id.action_report).setVisible(false);
                    menuBuilder.findItem(R.id.action_delete).setVisible(true);

                    MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, iv_menu);
                    optionsMenu.setForceShowIcon(true);
                    optionsMenu.show();
                    break;

                default:
                    if (PreferenceUtil.getAccessToken(context) != null) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        int resId = R.drawable.pattern_13;
                        intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
                        intent.putExtra(ProfileActivity.PROFILE_ID, comment.getUserId());
                        context.startActivity(intent);
                    }
            }
        }
    }
}
