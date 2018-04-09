package in.ninos.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import in.ninos.R;
import in.ninos.activities.ChallengeActivity;
import in.ninos.activities.CommentActivity;
import in.ninos.activities.EditPostActivity;
import in.ninos.activities.MainActivity;
import in.ninos.activities.ProfileActivity;
import in.ninos.activities.ShowPostActivity;
import in.ninos.firebase.Database;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.listeners.RetrofitService;
import in.ninos.models.PostClapResponse;
import in.ninos.models.PostInfo;
import in.ninos.models.PostReport;
import in.ninos.reterofit.RetrofitInstance;
import in.ninos.utils.AWSClient;
import in.ninos.utils.AWSUrls;
import in.ninos.utils.CrashUtil;
import in.ninos.utils.DateUtil;
import in.ninos.utils.PreferenceUtil;
import in.ninos.views.CircleImageView;
import in.ninos.views.PagerIndicatorDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by smeesala on 6/30/2017.
 */

public class AllChallengeAdapter extends CommonRecyclerAdapter<PostInfo> {

    private Context context;
    private TypedArray typedArray;
    private DateUtil dateUtil;
    private AWSClient awsClient;
    private RequestOptions requestOptions;
    private int color_accent, color_dark_grey;
    private Activity activity;
    private Type type;

    public AllChallengeAdapter(Context context, Activity activity, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener, Type type) {
        super(recyclerView, onLoadMoreListener);
        this.context = context;
        this.activity = activity;
        typedArray = context.getResources().obtainTypedArray(R.array.patterns);
        dateUtil = new DateUtil();
        awsClient = new AWSClient(context);
        awsClient.awsInit();

        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        color_accent = ContextCompat.getColor(context, R.color.colorAccent);
        color_dark_grey = ContextCompat.getColor(context, R.color.dark_grey);

        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_all, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) genericHolder;

        challengeViewHolder.bindData(position);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void addClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.addPostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<PostClapResponse>() {
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
                    setClapListener(postInfo, iv_clap, tv_clap);
                }
            });
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    private void setClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_clap);
            iv_clap.setImageDrawable(drawable);
            int color;

            if (postInfo.isMyRating()) {
                tv_clap.setTextColor(color_accent);
                color = color_accent;
            } else {
                tv_clap.setTextColor(color_dark_grey);
                color = color_dark_grey;
            }

            setClapListener(postInfo, iv_clap, tv_clap);

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

            tv_clap.setText(String.format(context.getString(clapStringId), postInfo.getTotalClapsCount()));
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    private void setClapListener(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        iv_clap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_clap.setOnClickListener(null);

                if (postInfo.isMyRating() && postInfo.getTotalClapsCount() > 0) {
                    removeClap(postInfo, iv_clap, tv_clap);
                } else {
                    addClap(postInfo, iv_clap, tv_clap);
                }
            }
        });
    }

    private void removeClap(final PostInfo postInfo, final ImageView iv_clap, final TextView tv_clap) {
        try {
            RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
            service.removePostClaps(postInfo.get_id(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<PostClapResponse>() {
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
                    setClapListener(postInfo, iv_clap, tv_clap);
                }
            });
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public void updateClap(RecyclerView.ViewHolder viewHolder, PostInfo postInfo) {
        try {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;

            if (challengeViewHolder != null) {
                setClap(postInfo, challengeViewHolder.iv_clap, challengeViewHolder.tv_clap);
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public void updateComment(RecyclerView.ViewHolder viewHolder, int commentCount) {
        try {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;

            if (challengeViewHolder != null) {
                challengeViewHolder.tv_comment.setText(String.format(context.getString(R.string.s_comments), commentCount));
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public void updateTitle(RecyclerView.ViewHolder viewHolder, String title) {
        try {
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) viewHolder;

            if (challengeViewHolder != null) {
                challengeViewHolder.tv_title.setText(title);
                challengeViewHolder.tv_title.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CrashUtil.report(e);
        }
    }

    public enum Type {
        POST,
        CHALLENGE
    }

    private class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_created_time, tv_title, tv_clap, tv_comment, tv_msg;
        ImageView ic_clap_anim, iv_clap, iv_menu;
        CircleImageView iv_profile;
        RecyclerView recyclerView;
        LinearLayout ll_comment, ll_options, ll_clap, ll_share;
        View itemView;
        RelativeLayout rl_challenge;
        JZVideoPlayerStandard video_view;

        ChallengeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_clap = itemView.findViewById(R.id.tv_clap);
            ic_clap_anim = itemView.findViewById(R.id.ic_clap_anim);
            ll_clap = itemView.findViewById(R.id.ll_clap);
            iv_clap = itemView.findViewById(R.id.iv_clap);
            iv_menu = itemView.findViewById(R.id.iv_menu);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_created_time = itemView.findViewById(R.id.tv_created_time);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            ll_comment = itemView.findViewById(R.id.ll_comment);
            ll_share = itemView.findViewById(R.id.ll_share);
            ll_options = itemView.findViewById(R.id.ll_options);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            iv_menu.setOnClickListener(this);
            ll_comment.setOnClickListener(this);
            iv_profile.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            ll_clap.setOnClickListener(this);
            ll_share.setOnClickListener(this);
            video_view = itemView.findViewById(R.id.video_view);
            video_view.batteryLevel.setVisibility(View.GONE);
            video_view.mRetryLayout.setVisibility(View.GONE);
            video_view.backButton.setVisibility(View.GONE);
            video_view.tinyBackImageView.setVisibility(View.GONE);

            rl_challenge = itemView.findViewById(R.id.rl_challenge);
            recyclerView = itemView.findViewById(R.id.image_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        private void bindData(final int position) {
            try {
                final PostInfo postInfo = getItem(position);

                tv_name.setText(postInfo.getUserName());

                if (TextUtils.isEmpty(postInfo.getTitle())) {
                    tv_title.setVisibility(View.GONE);
                } else {
                    tv_title.setText(postInfo.getTitle().trim());
                    tv_title.setVisibility(View.VISIBLE);
                }

                if (type.equals(Type.POST) && postInfo.getIsChallenge() && postInfo.getChallengeTitle() != null && postInfo.getUserName() != null) {
                    String msg = String.format("<b>%s</b> posted in <b style=\"color:#f76707\"><font color='#f76707'>%s</font></b> challenge", postInfo.getUserName(), postInfo.getChallengeTitle());
                    tv_msg.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);
                    tv_msg.setVisibility(View.VISIBLE);
                    tv_msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ChallengeActivity.class);
                            intent.putExtra(ChallengeActivity.CHALLENGE_ID, postInfo.getChallengeId());
                            intent.putExtra(ChallengeActivity.CHALLENGE_TITLE, postInfo.getChallengeTitle());
                            context.startActivity(intent);
                        }
                    });
                } else {
                    tv_msg.setVisibility(View.GONE);
                    tv_msg.setOnClickListener(null);
                }

                if (postInfo.getCreatedAt() != null) {
                    String date = dateUtil.formatDateToString(postInfo.getCreatedAt(), DateUtil.FULL_DATE);
                    tv_created_time.setText(date);
                }

                int commentStringId;

                if (postInfo.getTotalCommentCount() > 0) {
                    commentStringId = R.string.s_comments;
                } else {
                    commentStringId = R.string.s_comment;
                }

                tv_comment.setText(String.format(context.getString(commentStringId), postInfo.getTotalCommentCount()));

                int index = position % 10;
                int resId = typedArray.getResourceId(index, 0);
                String path = String.format("%s/%s", postInfo.getUserId(), postInfo.get_id());

                if (postInfo.isVideo()) {
                    List<String> links = awsClient.getBucket(path);
                    recyclerView.setVisibility(View.GONE);
                    video_view.setVisibility(View.VISIBLE);

                    if (postInfo.getLinks() == null) {
                        new LoadVideo(video_view, position).execute(path);
                    } else {
                        if (links.size() > 0) {
                            String link = links.get(0);
                            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                            Glide.with(context).setDefaultRequestOptions(requestOptions).load(link).into(video_view.thumbImageView);

                            video_view.setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                        }
                    }
                } else {
                    video_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    ImageAdapter imageAdapter = new ImageAdapter(activity, resId, postInfo.get_id());
                    recyclerView.setAdapter(imageAdapter);

                    if (postInfo.getLinks() == null) {
                        new LoadImage(imageAdapter, position).execute(path);
                    } else {
                        for (String link : postInfo.getLinks()) {
                            imageAdapter.addItem(link);
                        }

                        if (postInfo.getLinks().size() > 1) {
                            recyclerView.addItemDecoration(new PagerIndicatorDecoration());
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                                if (postInfo.getLinks().size() > 0) {
                                    param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                    itemView.setVisibility(View.VISIBLE);
                                } else {
                                    itemView.setVisibility(View.GONE);
                                    param.height = 0;
                                    param.width = 0;
                                }
                                itemView.setLayoutParams(param);
                            }
                        });
                    }
                }

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(AWSUrls.GetPI64(context, postInfo.getUserId()))
                        .into(iv_profile);

                setClap(postInfo, iv_clap, tv_clap);
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        private void clapAnimation(final PostInfo postInfo) {
            Animation pulse_fade = AnimationUtils.loadAnimation(context, R.anim.pulse_fade_in);
            pulse_fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                    if (postInfo.isMyRating()) {
                        ic_clap_anim.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clap_full_anim));
                    } else {
                        ic_clap_anim.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clap_anim));
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
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            final int position = getAdapterPosition();
            final PostInfo postInfo = getItem(position);

            switch (id) {
                case R.id.rl_challenge:
                default:
                    Intent showPostIntent = new Intent(context, ShowPostActivity.class);
                    showPostIntent.putExtra(ShowPostActivity.POST_PROFILE_ID, postInfo.get_id());
                    activity.startActivityForResult(showPostIntent, MainActivity.POST_UPDATE);
                    break;

                case R.id.tv_name:
                case R.id.iv_profile:
                    Intent intent = new Intent(context, ProfileActivity.class);
                    int resId = typedArray.getResourceId(position % 10, 0);
                    intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
                    intent.putExtra(ProfileActivity.PROFILE_ID, postInfo.getUserId());
                    context.startActivity(intent);
                    break;

                case R.id.ll_comment:
                    Intent commentIntent = new Intent(context, CommentActivity.class);
                    commentIntent.putExtra(CommentActivity.POST_ID, postInfo.get_id());
                    activity.startActivityForResult(commentIntent, MainActivity.COMMENT_ADDED);
                    break;

                case R.id.ll_clap:
                case R.id.iv_clap:
                    clapAnimation(postInfo);

                    if (postInfo.isMyRating() && postInfo.getTotalClapsCount() > 0) {
                        removeClap(postInfo, iv_clap, tv_clap);
                    } else {
                        addClap(postInfo, iv_clap, tv_clap);
                    }
                    break;

                case R.id.iv_menu:
                    MenuBuilder menuBuilder = new MenuBuilder(context);
                    menuBuilder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                            service.deletePost(postInfo.get_id(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<in.ninos.models.Response>() {
                                                @Override
                                                public void onResponse(Call<in.ninos.models.Response> call, Response<in.ninos.models.Response> response) {
                                                    if (response.isSuccessful() && response.body() != null) {

                                                        if (postInfo.getLinks().size() > 1) {
                                                            awsClient.removeImage(postInfo.get_id(), postInfo.getLinks());
                                                        }

                                                        removeItem(position);
                                                    } else {
                                                        Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<in.ninos.models.Response> call, Throwable t) {
                                                    Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                    builder.create().show();

                                    return true;

                                case R.id.action_edit:

                                    Intent editPostIntent = new Intent(context, EditPostActivity.class);
                                    editPostIntent.putStringArrayListExtra(EditPostActivity.PATHS, new ArrayList<>(postInfo.getLinks()));
                                    editPostIntent.putExtra(EditPostActivity.POST_ID, postInfo.get_id());
                                    editPostIntent.putExtra(EditPostActivity.DESCRIPTION, postInfo.getTitle());
                                    activity.startActivityForResult(editPostIntent, MainActivity.POST_EDIT);

                                    return true;

                                case R.id.action_report:

                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_report);
                                    final EditText et_report = dialog.findViewById(R.id.et_report);

                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

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
                                                Toast.makeText(context, R.string.provide_reason_for_report, Toast.LENGTH_SHORT).show();
                                            } else {
                                                PostReport postReport = new PostReport();
                                                postReport.setPostId(postInfo.get_id());
                                                postReport.setUserReport(text);

                                                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                                                service.reportPost(postReport, PreferenceUtil.getAccessToken(context)).enqueue(new Callback<in.ninos.models.Response>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<in.ninos.models.Response> call, @NonNull Response<in.ninos.models.Response> response) {
                                                        if (response.body() != null && response.body().isSuccess()) {
                                                            removeItem(position);
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

                    if (postInfo.getUserId().equals(Database.getUserId())) {
                        menuBuilder.findItem(R.id.action_edit).setVisible(true);
                        menuBuilder.findItem(R.id.action_report).setVisible(false);
                        menuBuilder.findItem(R.id.action_delete).setVisible(true);
                    } else {
                        menuBuilder.findItem(R.id.action_edit).setVisible(false);
                        menuBuilder.findItem(R.id.action_report).setVisible(true);
                        menuBuilder.findItem(R.id.action_delete).setVisible(false);
                    }

                    MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
                    optionsMenu.setForceShowIcon(true);
                    optionsMenu.show();
                    break;

                case R.id.ll_share:
                    String text = PreferenceUtil.getUserName(context) + " " + context.getString(R.string.share_post) + postInfo.get_id() + context.getString(R.string.encorage);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_to)));
                    break;
            }
        }

        private void hideKeyboard(EditText et_report) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(et_report.getWindowToken(), 0);
            }
        }

        public class LoadImage extends AsyncTask<String, Void, List<String>> {

            ImageAdapter imageAdapter;
            int position;

            LoadImage(ImageAdapter imageAdapter, int position) {
                this.imageAdapter = imageAdapter;
                this.position = position;
            }

            @Override
            protected List<String> doInBackground(String... strings) {
                String path = strings[0];

                final List<String> links = new ArrayList<>();

                try {
                    links.addAll(awsClient.getBucket(path));

                    if (getItemCount() > position) {
                        PostInfo postInfo = getItem(position);

                        if (postInfo != null) {
                            postInfo.setLinks(links);
                        }
                    }


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                            if (links.size() > 0) {
                                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                itemView.setVisibility(View.VISIBLE);
                            } else {
                                itemView.setVisibility(View.GONE);
                                param.height = 0;
                                param.width = 0;
                            }
                            itemView.setLayoutParams(param);
                        }
                    });
                } catch (Exception e) {
                    CrashUtil.report(e);
                }

                return links;
            }

            @Override
            protected void onPostExecute(List<String> links) {

                for (String link : links) {
                    imageAdapter.addItem(link);
                }

                if (getItemCount() > position) {
                    PostInfo postInfo = getItem(position);

                    if (postInfo != null) {
                        postInfo.setLinks(links);
                    }
                }

                if (links.size() > 1) {
                    recyclerView.addItemDecoration(new PagerIndicatorDecoration());
                }
            }
        }

        public class LoadVideo extends AsyncTask<String, Void, List<String>> {

            WeakReference<JZVideoPlayerStandard> video_view;
            int position;

            LoadVideo(JZVideoPlayerStandard video_view, int position) {
                this.video_view = new WeakReference<>(video_view);
                this.position = position;
            }

            @Override
            protected List<String> doInBackground(String... strings) {
                String path = strings[0];

                final List<String> links=new ArrayList<>();

                try {
                    links.addAll(awsClient.getBucket(path));

                    PostInfo postInfo = getItem(position);

                    if (postInfo != null) {
                        postInfo.setLinks(links);
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                            if (links.size() > 0) {
                                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                                itemView.setVisibility(View.VISIBLE);
                            } else {
                                itemView.setVisibility(View.GONE);
                                param.height = 0;
                                param.width = 0;
                            }
                            itemView.setLayoutParams(param);
                        }
                    });
                } catch (Exception e) {
                    CrashUtil.report(e);
                }

                return links;
            }

            @Override
            protected void onPostExecute(List<String> links) {

                if (links.size() > 0) {
                    String link = links.get(0);
                    video_view.get().setUp(link, JZVideoPlayer.SCREEN_WINDOW_LIST);
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    if (activity != null && !activity.isFinishing()) {
                        Glide.with(activity).setDefaultRequestOptions(requestOptions).load(link).into(video_view.get().thumbImageView);
                    }
                }

                if (getItemCount() > position) {
                    PostInfo postInfo = getItem(position);

                    if (postInfo != null) {
                        postInfo.setLinks(links);
                    }
                }
            }
        }
    }
}
