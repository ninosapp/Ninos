package com.ninos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ninos.R;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class EditPostAdapter extends CommonRecyclerAdapter<String> {

    private Context mContext;

    public EditPostAdapter(Context context) {
        mContext = context;
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

    private class EditPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_image, iv_delete;

        EditPostViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_delete.setOnClickListener(this);
        }

        private void bindData(int position) {
            String path = getItem(position);
            Glide.with(mContext).load(path).into(iv_image);
        }

        @Override
        public void onClick(View view) {

        }
    }
}