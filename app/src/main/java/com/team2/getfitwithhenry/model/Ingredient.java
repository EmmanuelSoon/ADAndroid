package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {
    private int id;
    private String name;
    private String image;
    private double calorie;

    private NutritionRecord nutritionRecord;

    public Ingredient(@JsonProperty("name")String name, @JsonProperty("image")String image, @JsonProperty("calorie")double calorie, @JsonProperty("nutritionalRecord")NutritionRecord nutritionRecord) {
        this.name = name;
        this.image = image;
        this.calorie = calorie;
        this.nutritionRecord = nutritionRecord;
    }

    public Ingredient(String name, double calorie) {
        this.name = name;
        this.calorie = calorie;
    }

    public Ingredient(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", calorie=" + calorie +
                '}';
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public double getCalorie() { return calorie; }

    public void setCalorie(double calorie) { this.calorie = calorie; }

    public NutritionRecord getNutritionRecord() { return nutritionRecord; }

    public void setNutritionRecord(NutritionRecord nutritionRecord) { this.nutritionRecord = nutritionRecord; }

}
