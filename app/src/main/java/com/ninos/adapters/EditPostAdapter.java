package com.ninos.adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ninos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAMILY on 23-01-2018.
 */

public class EditPostAdapter extends CommonRecyclerAdapter<String> {

    private Context mContext;
    private List<String> deletedLinks;

    public EditPostAdapter(Context context) {
        mContext = context;
        deletedLinks = new ArrayList<>();
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
            Glide.with(mContext).load(path).into(iv_image);

            if (getItemCount() == 1) {
                fab_delete.setVisibility(View.GONE);
            } else {
                fab_delete.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            String item = getItem(getAdapterPosition());
            if (getItemCount() > 1) {
                deletedLinks.add(item);
                removeItem(item);
            } else {
                Toast.makeText(mContext, R.string.cannot_delete_image, Toast.LENGTH_SHORT).show();
            }
        }
    }
}