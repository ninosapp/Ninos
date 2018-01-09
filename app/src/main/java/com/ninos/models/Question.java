package com.ninos.models;

/**
 * Created by FAMILY on 09-01-2018.
 */

public class Question {
    String question;
    String options;
    String solution;
    String type;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", options='" + options + '\'' +
                ", solution='" + solution + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
