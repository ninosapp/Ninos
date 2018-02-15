package com.ninos.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninos.R;
import com.ninos.activities.MainActivity;
import com.ninos.activities.QuizActivity;
import com.ninos.firebase.Database;
import com.ninos.listeners.RetrofitService;
import com.ninos.models.EvaluateResult;
import com.ninos.models.QuizEvaluateResultResponse;
import com.ninos.models.Quizze;
import com.ninos.reterofit.RetrofitInstance;
import com.ninos.utils.PreferenceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

            int drawableId;

            switch (quizze.getTitle().toLowerCase()) {
                default:
                case "general knowledge":
                    drawableId = R.drawable.gk;
                    break;
                case "science":
                    drawableId = R.drawable.science;
                    break;
                case "technology":
                    drawableId = R.drawable.technology;
                    break;
                case "sports":
                    drawableId = R.drawable.sports;
                    break;
            }

            iv_quiz_background.setImageDrawable(ContextCompat.getDrawable(context, drawableId));
        }

        @Override
        public void onClick(View view) {
            Quizze quizze = getItem(getAdapterPosition());

            if (quizze.isQuizTaken()) {
                RetrofitService service = RetrofitInstance.createService(RetrofitService.class);
                service.getQuizResult(quizze.get_id(), Database.getUserId(), PreferenceUtil.getAccessToken(context)).enqueue(new Callback<QuizEvaluateResultResponse>() {
                    @Override
                    public void onResponse(Call<QuizEvaluateResultResponse> call, Response<QuizEvaluateResultResponse> response) {
                        if (response.body() != null) {
                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            }

                            dialog.setContentView(R.layout.dialog_score);
                            TextView tv_score_one = dialog.findViewById(R.id.tv_score_one);

                            EvaluateResult eInfo = response.body().getEvaluateResult();

                            if (eInfo != null) {
                                tv_score_one.setText(String.format("%02d", Integer.parseInt(eInfo.getAcquiredScore())));
                            }

                            dialog.findViewById(R.id.fab_close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<QuizEvaluateResultResponse> call, Throwable t) {
                        Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                    }
                });
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