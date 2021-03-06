package in.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.ninos.R;
import in.ninos.activities.ShowPostActivity;
import in.ninos.listeners.OnLoadMoreListener;
import in.ninos.models.PostInfo;
import in.ninos.utils.CrashUtil;

/**
 * Created by FAMILY on 15-02-2018.
 */

public class ChallengeImageAdapter extends CommonRecyclerAdapter<PostInfo> {

    private Context context;

    public ChallengeImageAdapter(Context context, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_image, parent, false);
        return new ChallengeImageHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeImageHolder imageHolder = (ChallengeImageHolder) genericHolder;
        imageHolder.bindData(position);
    }

    private class ChallengeImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image;

        ChallengeImageHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
            iv_image.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        void bindData(int position) {
            try {
                PostInfo postInfo = getItem(position);

                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .load(postInfo.getLinks().get(0))
                        .into(iv_image);
            } catch (Exception e) {
                CrashUtil.report(e);
            }
        }

        @Override
        public void onClick(View view) {
            PostInfo postInfo = getItem(getAdapterPosition());

            Intent intent = new Intent(context, ShowPostActivity.class);
            intent.putExtra(ShowPostActivity.POST_PROFILE_ID, postInfo.get_id());
            context.startActivity(intent);
        }
    }
}
