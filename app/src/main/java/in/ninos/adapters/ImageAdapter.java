package in.ninos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.MainActivity;
import in.ninos.activities.ShowPostActivity;
import in.ninos.utils.CrashUtil;

/**
 * Created by FAMILY on 04-01-2018.
 */

public class ImageAdapter extends CommonRecyclerAdapter<String> {

    private Activity activity;
    private int resId;
    private String postId;

    public ImageAdapter(Activity activity, int resId, String postId) {
        this.activity = activity;
        this.resId = resId;
        this.postId = postId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ImageViewHolder sampleViewHolder = (ImageViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_challenge;

        ImageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iv_challenge = itemView.findViewById(R.id.iv_challenge);
        }

        private void bindData(int position) {
            try {
                String path = getItem(position);
                iv_challenge.setImageDrawable(ContextCompat.getDrawable(activity, resId));

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(resId)
                        .error(resId)
                        .fallback(resId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(activity)
                        .setDefaultRequestOptions(requestOptions)
                        .load(path).into(iv_challenge);
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onClick(View v) {
            Intent showPostIntent = new Intent(activity, ShowPostActivity.class);
            showPostIntent.putExtra(ShowPostActivity.POST_PROFILE_ID, postId);
            activity.startActivityForResult(showPostIntent, MainActivity.POST_UPDATE);
        }
    }
}