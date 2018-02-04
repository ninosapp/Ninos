package com.ninos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.listeners.OnLoadMoreListener;
import com.ninos.models.PostInfo;

/**
 * Created by FAMILY on 04-02-2018.
 */

public class ChallengeAdapter extends CommonRecyclerAdapter<PostInfo> {

    private Context context;
    private String[] mColors;

    public ChallengeAdapter(Context context, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        this.context = context;
        mColors = context.getResources().getStringArray(R.array.colors);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        ChallengeViewHolder bucketHolder = (ChallengeViewHolder) genericHolder;
        bucketHolder.bindData(position);
    }

    private class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_char, tv_title;
        RelativeLayout rl_challenge;

        ChallengeViewHolder(View view) {
            super(view);
            tv_char = view.findViewById(R.id.tv_char);
            tv_title = view.findViewById(R.id.tv_title);
            rl_challenge = view.findViewById(R.id.rl_challenge);
        }

        void bindData(int position) {
            PostInfo postInfo = getItem(position);
            tv_title.setText(postInfo.getTitle());

            String text = postInfo.getTitle().substring(0, 1);
            tv_char.setText(text);

            int index = position % 10;
            rl_challenge.setBackgroundColor(Color.parseColor(mColors[index]));
        }

        @Override
        public void onClick(View view) {

        }
    }
}
