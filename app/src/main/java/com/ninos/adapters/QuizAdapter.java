package com.ninos.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.activities.QuizActivity;
import com.ninos.models.Quizze;

/**
 * Created by FAMILY on 08-12-2017.
 */

public class QuizAdapter extends CommonRecyclerAdapter<Quizze> {

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

        sampleViewHolder.bindData(position);
    }

    private class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_quiz_name;
        ImageView iv_quiz_background;

        QuizViewHolder(View itemView) {
            super(itemView);
            tv_quiz_name = itemView.findViewById(R.id.tv_quiz_name);
            iv_quiz_background = itemView.findViewById(R.id.iv_quiz_background);
            itemView.setOnClickListener(this);
        }

        private void bindData(int position) {
            Quizze quizze = getItem(position);
            tv_quiz_name.setText(quizze.getTitle());
            int index = position % 10;

            Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(mContext, R.drawable.ic_circle));
            iv_quiz_background.setImageDrawable(drawable);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(drawable, Color.parseColor(mColors[index]));

            } else {
                drawable.mutate().setColorFilter(Color.parseColor(mColors[index]), PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        public void onClick(View view) {
            Quizze quizze = getItem(getAdapterPosition());
            Intent intent = new Intent(mContext, QuizActivity.class);
            intent.putExtra(QuizActivity.QUIZ_ID, quizze.get_id());
            intent.putExtra(QuizActivity.QUIZ_DURATION, quizze.getDuration());
            intent.putExtra(QuizActivity.QUIZ_TITLE, quizze.getTitle());
            mContext.startActivity(intent);
        }
    }
}