package com.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ninos.R;
import com.ninos.activities.ProfileActivity;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.models.UserInfo;
import com.ninos.utils.AWSUrls;
import com.ninos.views.CircleImageView;

/**
 * Created by FAMILY on 22-01-2018.
 */

public class PeopleAdapter extends CommonRecyclerAdapter<UserInfo> {

    private Context context;
    private TypedArray typedArray;

    public PeopleAdapter(Context context, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);

        this.context = context;
        typedArray = context.getResources().obtainTypedArray(R.array.patterns);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_people, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        PeopleViewHolder sampleViewHolder = (PeopleViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_user;
        CircleImageView iv_user;

        PeopleViewHolder(View itemView) {
            super(itemView);
            tv_user = itemView.findViewById(R.id.tv_user);
            iv_user = itemView.findViewById(R.id.iv_user);
            itemView.setOnClickListener(this);
        }

        private void bindData(int position) {
            UserInfo userInfo = getItem(position);
            tv_user.setText(userInfo.getChildName());

            int index = position % 10;
            int resId = typedArray.getResourceId(index, 0);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(resId)
                    .error(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(AWSUrls.GetPI64(context, userInfo.getUserId()))
                    .into(iv_user);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            UserInfo userInfo = getItem(position);

            Intent intent = new Intent(context, ProfileActivity.class);
            int resId = typedArray.getResourceId(position, 0);
            intent.putExtra(ProfileActivity.PROFILE_PLACE_HOLDER, resId);
            intent.putExtra(ProfileActivity.PROFILE_ID, userInfo.getUserId());
            context.startActivity(intent);
        }
    }
}
