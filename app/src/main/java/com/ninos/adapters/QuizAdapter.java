package com.ninos.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.activities.MainActivity;
import com.ninos.activities.QuizActivity;
import com.ninos.activities.QuizViewActivity;
import com.ninos.activities.ScoreActivity;
import com.ninos.models.Quizze;

/**
 * Created by FAMILY on 08-12-2017.
 */

public class QuizAdapter extends CommonRecyclerAdapter<Quizze> {

    private Context context;
    private Activity activity;

    public QuizAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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

            if (quizze.isQuizTaken()) {
                tv_quiz_name.setTextColor(Color.GRAY);
            } else {
                tv_quiz_name.setTextColor(Color.BLACK);
            }

            int drawableId;

            switch (quizze.getTitle().toLowerCase()) {
                default:
                case "general knowledge":
                    drawableId = R.drawable.ic_gk;
                    break;
                case "science":
                    drawableId = R.drawable.ic_science;
                    break;
                case "technology":
                    drawableId = R.drawable.ic_technology;
                    break;
                case "sports":
                    drawableId = R.drawable.ic_sports;
                    break;
                case "english":
                    drawableId = R.drawable.ic_english;
                    break;
                case "entertainment":
                    drawableId = R.drawable.ic_entertainment;
                    break;
                case "india":
                    drawableId = R.drawable.ic_india;
                    break;
                case "numbers":
                    drawableId = R.drawable.ic_numbers;
                    break;
                case "puzzles":
                    drawableId = R.drawable.ic_puzzles;
                    break;
                case "social science":
                    drawableId = R.drawable.ic_social_science;
                    break;
                case "more":
                    drawableId = R.drawable.ic_more;
                    break;
            }

            iv_quiz_background.setImageDrawable(ContextCompat.getDrawable(context, drawableId));
        }

        @Override
        public void onClick(View view) {
            Quizze quizze = getItem(getAdapterPosition());

            if (quizze.get_id().equals("more")) {
                Intent intent = new Intent(context, QuizViewActivity.class);
                context.startActivity(intent);
            } else {
                if (quizze.isQuizTaken()) {
                    Intent intent = new Intent(context, ScoreActivity.class);
                    intent.putExtra(ScoreActivity.QUIZ_ID, quizze.get_id());
                    context.startActivity(intent);

                } else {
                    Intent intent = new Intent(context, QuizActivity.class);
                    intent.putExtra(QuizActivity.QUIZ_ID, quizze.get_id());
                    intent.putExtra(QuizActivity.QUIZ_DURATION, quizze.getDuration());
                    intent.putExtra(QuizActivity.QUIZ_TITLE, quizze.getTitle());
                    activity.startActivityForResult(intent, MainActivity.QUIZ_COMPLETE);
                }
            }
        }
    }
}