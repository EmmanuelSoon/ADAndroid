package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class DietRecord {
    private int id;
    private LocalDate date;
    private String foodName;
    private double calorie;
    private double weight;
    private MealType mealType;
    private User user;

    public DietRecord(@JsonProperty("id") int id, @JsonProperty("date") LocalDate date,@JsonProperty("foodName") String foodName,@JsonProperty("calorie") double calorie,@JsonProperty("weight") double weight, @JsonProperty("mealType") MealType mealType,@JsonProperty("user") User user) {
        this.id = id;
        this.date = date;
        this.foodName = foodName;
        this.calorie = calorie;
        this.weight = weight;
        this.mealType = mealType;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
