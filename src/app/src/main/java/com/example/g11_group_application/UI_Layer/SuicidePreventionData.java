package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Saksham Gupta (u7726995)
 * @Edited: Divyesh Srivastava (u7726856)
 * Created: 15-May-2024
 * Comments: This is the class that represents the data for the form that collects information.
 */
import java.io.Serializable;

public class SuicidePreventionData implements Serializable {
    private String individualId;
    private int age;
    private String gender;
    private String region;
    private String totalValue;
    private String lastCrisisEvent;

    public SuicidePreventionData(){}

    public SuicidePreventionData(String individualId, int age, String gender, String region,
                                 String totalValue, String lastCrisisEvent) {
        this.individualId = individualId;
        this.age = age;
        this.gender = gender;
        this.region = region;
        this.lastCrisisEvent = lastCrisisEvent;
        this.totalValue = totalValue;
    }

    // Getters
    public String getIndividualId() {
        return individualId;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getRegion() {
        return region;
    }

    public String gettotalValue() {
        return totalValue;
    }

    public String getLastCrisisEvent() {
        return lastCrisisEvent;
    }

    // Setters
    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setMentalHealthHistory(boolean hasMentalHealthHistory) {
        this.totalValue = totalValue;
    }

    public void setLastCrisisEvent(String lastCrisisEvent) {
        this.lastCrisisEvent = lastCrisisEvent;
    }

    @Override
    public String toString() {
        return "ID: " + individualId +
                "\nAge: " + age +
                "\nGender: " + gender +
                "\nRegion: " + region +
                "\nTotal Value: " + totalValue +
                "\nLast Crisis Event: " + lastCrisisEvent;
    }
}
