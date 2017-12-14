package com.ninos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninos.R;

/**
 * Created by FAMILY on 08-12-2017.
 */

public class QuizAdapter extends CommonRecyclerAdapter<String> {

    private Context mContext;
    private String[] mColors;

    public QuizAdapter(Context activity) {
        mContext = activity;
        mColors = activity.getResources().getStringArray(R.array.colors);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        QuizViewHolder sampleViewHolder = (QuizViewHolder) genericHolder;

        String title = getItem(position);
        sampleViewHolder.bindData(title, position);
    }

    private class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_quiz_name;
        ImageView iv_quiz_background;

        QuizViewHolder(View itemView) {
            super(itemView);
            tv_quiz_name = itemView.findViewById(R.id.tv_quiz_name);
            iv_quiz_background = itemView.findViewById(R.id.iv_quiz_background);
        }

        private void bindData(String title, int position) {
            tv_quiz_name.setText(title);
            int index = position % 10;

            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_circle);
            drawable.setColorFilter(Color.parseColor(mColors[index]), PorterDuff.Mode.SRC_ATOP);
            iv_quiz_background.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {

            }
        }
    }
}