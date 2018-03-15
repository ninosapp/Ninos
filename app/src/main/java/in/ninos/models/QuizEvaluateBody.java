package in.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class QuizEvaluateBody {
    private String evalutionId;
    private List<MCQSolution> mcqSolution;

    public String getEvalutionId() {
        return evalutionId;
    }

    public void setEvalutionId(String evalutionId) {
        this.evalutionId = evalutionId;
    }

    public List<MCQSolution> getMcqSolution() {
        return mcqSolution;
    }

    public void setMcqSolution(List<MCQSolution> mcqSolution) {
        this.mcqSolution = mcqSolution;
    }

    @Override
    public String toString() {
        return "QuizEvaluateBody{" +
                "evalutionId='" + evalutionId + '\'' +
                ", mcqSolution=" + mcqSolution +
                '}';
    }
}
