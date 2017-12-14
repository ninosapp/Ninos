package com.ninos.models;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class Profile {
    private String parentName;
    private String userId;
    private String childName;
    private long DOB;
    private String email;
    private String school;
    private String gender;
    private String city;
    private String state;
    private String aboutus;
    private boolean isFirstLogin;
    private boolean isEnabled;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public long getDOB() {
        return DOB;
    }

    public void setDOB(long DOB) {
        this.DOB = DOB;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAboutus() {
        return aboutus;
    }

    public void setAboutus(String aboutus) {
        this.aboutus = aboutus;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "parentName='" + parentName + '\'' +
                ", userId='" + userId + '\'' +
                ", childName='" + childName + '\'' +
                ", DOB=" + DOB +
                ", email='" + email + '\'' +
                ", school='" + school + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", aboutus='" + aboutus + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
