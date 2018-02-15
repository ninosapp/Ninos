package com.ninos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.listeners.OnLoadMoreListener;

/**
 * Created by FAMILY on 15-02-2018.
 */

public class ChallengeImageAdapter extends CommonRecyclerAdapter<String> {

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
            String url = getItem(position);

            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .load(url)
                    .into(iv_image);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
