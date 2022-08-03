package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HealthRecord {
    private int id;
    private LocalDate date;
    private User user;
    private double userWeight;
    private double userHeight;
    private double calIntake;
    private double waterIntake;


    public HealthRecord(@JsonProperty("id") int id,@JsonProperty("date") LocalDate date,@JsonProperty("user") User user, @JsonProperty("userWeight")double userWeight, @JsonProperty("userHeight") double userHeight,@JsonProperty("calIntake") double calIntake, @JsonProperty("waterIntake") double waterIntake) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.calIntake = calIntake;
        this.waterIntake = waterIntake;
    }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "id=" + id +
                ", date=" + date +
                ", user=" + user +
                ", userWeight=" + userWeight +
                ", userHeight=" + userHeight +
                ", calIntake=" + calIntake +
                ", waterIntake=" + waterIntake +
                '}';
    }

    public HealthRecord(double userWeight, double userHeight, double calIntake, double waterIntake) {
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.calIntake = calIntake;
        this.waterIntake = waterIntake;
    }

    public double getUserHeight() {
        return userHeight;
    }

    public double getCalIntake() {
        return calIntake;
    }

    public double getWaterIntake() {
        return waterIntake;
    }

    public double getUserWeight() {
        return userWeight;
    }

    public LocalDate getDate(){ return date; }

    public void setCalIntake(double calIntake) {
        this.calIntake = calIntake;
    }

    public void setUserHeight(double userHeight) {
        this.userHeight = userHeight;
    }

    public void setUserWeight(double userWeight) {
        this.userWeight = userWeight;
    }

    public void setWaterIntake(double waterIntake) {
        this.waterIntake = waterIntake;
    }
}
