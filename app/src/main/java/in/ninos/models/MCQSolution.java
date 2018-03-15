package in.ninos.models;

/**
 * Created by FAMILY on 06-02-2018.
 */

public class MCQSolution {
    private String questionId;
    private String answer;
    private String status;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MCQSolution{" +
                "questionId='" + questionId + '\'' +
                ", answer='" + answer + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
