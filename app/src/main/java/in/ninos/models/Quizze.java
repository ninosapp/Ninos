package in.ninos.models;

/**
 * Created by FAMILY on 09-01-2018.
 */

public class Quizze {

    private String _id;
    private String agegroup;
    private int duration;
    private int maxage;
    private int minage;
    private String title;
    private boolean isQuizTaken;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public boolean isQuizTaken() {
        return isQuizTaken;
    }

    public void setQuizTaken(boolean quizTaken) {
        isQuizTaken = quizTaken;
    }

    @Override
    public String toString() {
        return "Quizze{" +
                "_id='" + _id + '\'' +
                ", agegroup='" + agegroup + '\'' +
                ", duration=" + duration +
                ", maxage=" + maxage +
                ", minage=" + minage +
                ", title='" + title + '\'' +
                ", isQuizTaken=" + isQuizTaken +
                '}';
    }
}
