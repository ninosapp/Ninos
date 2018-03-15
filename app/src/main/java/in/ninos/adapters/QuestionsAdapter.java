package in.ninos.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import in.ninos.fragments.QuestionFragment;
import in.ninos.models.MCQSolution;
import in.ninos.models.Question;

/**
 * Created by FAMILY on 04-02-2018.
 */

public class QuestionsAdapter extends FragmentPagerAdapter {
    public static final String correct = "correct";
    public static final String incorrect = "incorrect";
    private List<Question> questions;
    private SparseArray<QuestionFragment> questionFragments;
    private String quizId;

    public QuestionsAdapter(FragmentManager fm, List<Question> questions, String quizId) {
        super(fm);
        this.questions = questions;
        this.quizId = quizId;
        questionFragments = new SparseArray<>();
    }

    @Override
    public Fragment getItem(int position) {
        Question question = questions.get(position);

        if (questionFragments.indexOfKey(position) < 0) {
            questionFragments.put(position, QuestionFragment.newInstance(question, quizId));
        }

        return questionFragments.get(position);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    public List<MCQSolution> getAnswers() {
        List<MCQSolution> mcqSolutions = new ArrayList<>();


        for (Question question : questions) {
            MCQSolution mcqSolution = new MCQSolution();
            mcqSolution.setQuestionId(quizId);
            mcqSolution.setAnswer(question.getSolution());
            mcqSolution.setStatus(incorrect);

            for (int i = 0; i < questionFragments.size(); i++) {
                QuestionFragment questionFragment = questionFragments.get(i);
                String questionValue = questionFragment.getQuestion();
                MCQSolution mcq = questionFragment.getMCQSolution();

                if (questionValue.equals(question.getQuestion())) {
                    mcqSolution = mcq;
                }
            }

            mcqSolutions.add(mcqSolution);
        }

        return mcqSolutions;
    }

}