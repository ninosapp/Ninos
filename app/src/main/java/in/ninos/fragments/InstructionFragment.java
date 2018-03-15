package in.ninos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.ninos.R;
import in.ninos.activities.QuizActivity;

/**
 * Created by FAMILY on 03-02-2018.
 */

public class InstructionFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instruction, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ImageView iv_back = view.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(this);
            view.findViewById(R.id.tv_start).setOnClickListener(this);
        } catch (Exception e) {
            logError(e);
            showToast(R.string.error_message);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:

                if (getActivity() != null) {
                    getActivity().finish();
                }

                break;
            case R.id.tv_start:
                if (getActivity() != null) {
                    QuizActivity quizActivity = (QuizActivity) getActivity();
                    quizActivity.startQuiz();
                }
                break;
        }
    }
}