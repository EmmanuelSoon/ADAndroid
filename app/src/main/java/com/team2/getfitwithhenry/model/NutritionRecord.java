package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class NutritionRecord implements Serializable {

    private int id;
    private double totalCalories;
    private double carbs;
    private double proteins;
    private double fats;
    private double sugar;
    private double sodium;
    private double calcium;
    private double iron;
    private double vitaminA;
    private double vitaminB12;
    private double vitaminB6;
    private double vitaminC;
    private double vitaminE;
    private double vitaminK;
    private double fiber;
    private double water;
    private double cholesterol;
    private double servingSize;

    public NutritionRecord(@JsonProperty("carbs")double carbs,
                           @JsonProperty("cholesterol")double cholesterol,
                           @JsonProperty("fiber")double fiber,
                           @JsonProperty("totalCalories")double totalCalories,
                           @JsonProperty("proteins")double proteins,
                           @JsonProperty("sugar")double sugar,
                           @JsonProperty("water")double water,
                           @JsonProperty("fats")double fats,
                           @JsonProperty("calcium")double calcium,
                           @JsonProperty("iron")double iron,
                           @JsonProperty("sodium")double sodium,
                           @JsonProperty("vitaminA")double vitaminA,
                           @JsonProperty("vitaminB12")double vitaminB12,
                           @JsonProperty("vitaminB6")double vitaminB6,
                           @JsonProperty("vitaminC")double vitaminC,
                           @JsonProperty("vitaminE")double vitaminE,
                           @JsonProperty("vitaminK")double vitaminK,
                           @JsonProperty("servingSize")double servingSize){

        this.totalCalories = totalCalories;
        this.cholesterol = cholesterol;
        this.fiber = fiber;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fats = fats;
        this.sugar = sugar;
        this.water = water;
        this.vitaminA = vitaminA;
        this.vitaminB12 = vitaminB12;
        this.vitaminB6 = vitaminB6;
        this.vitaminC = vitaminC;
        this.vitaminE = vitaminE;
        this.vitaminK = vitaminK;
        this.sodium = sodium;
        this.calcium = calcium;
        this.iron = iron;
        this.servingSize = servingSize;

    }

    public NutritionRecord(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public String getTruncNutrition(){
        return "Total Calories: " + totalCalories + "\nCarbohydrates: " + carbs + "\nProteins: " + proteins + "\nFats: " + fats;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminB12() {
        return vitaminB12;
    }

    public void setVitaminB12(double vitaminB12) {
        this.vitaminB12 = vitaminB12;
    }

    public double getVitaminB6() {
        return vitaminB6;
    }

    public void setVitaminB6(double vitaminB6) {
        this.vitaminB6 = vitaminB6;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(double vitaminE) {
        this.vitaminE = vitaminE;
    }

    public double getVitaminK() {
        return vitaminK;
    }

    public void setVitaminK(double vitaminK) {
        this.vitaminK = vitaminK;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }
}
