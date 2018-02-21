package com.ninos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninos.R;
import com.ninos.models.Question;

/**
 * Created by FAMILY on 21-02-2018.
 */

public class SummaryAdapter extends CommonRecyclerAdapter<Question> {

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_summary, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder genericHolder, int position) {
        SummaryViewHolder sampleViewHolder = (SummaryViewHolder) genericHolder;

        sampleViewHolder.bindData(position);
    }

    private class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question_count, tv_question, tv_answer;

        SummaryViewHolder(View itemView) {
            super(itemView);
            tv_question_count = itemView.findViewById(R.id.tv_question_count);
            tv_question = itemView.findViewById(R.id.tv_question);
            tv_answer = itemView.findViewById(R.id.tv_answer);
        }

        private void bindData(int position) {
            Question question = getItem(position);

            int qNo = position + 1;
            tv_question_count.setText(String.format("%s.", String.valueOf(qNo)));
            tv_question.setText(question.getQuestion());
            tv_answer.setText(question.getSolution());
        }
    }
}