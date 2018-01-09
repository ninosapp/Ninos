package com.ninos.models;

import java.util.List;

/**
 * Created by FAMILY on 09-01-2018.
 */

public class Quizze {

    String agegroup;
    int duration;
    int maxage;
    int minage;
    String title;
    List<Question> questions;

    public String getAgegroup() {
        return agegroup;
    }

    public void setAgegroup(String agegroup) {
        this.agegroup = agegroup;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxage() {
        return maxage;
    }

    public void setMaxage(int maxage) {
        this.maxage = maxage;
    }

    public int getMinage() {
        return minage;
    }

    public void setMinage(int minage) {
        this.minage = minage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Quizze{" +
                "agegroup='" + agegroup + '\'' +
                ", duration=" + duration +
                ", maxage=" + maxage +
                ", minage=" + minage +
                ", title='" + title + '\'' +
                ", questions=" + questions +
                '}';
    }
}
