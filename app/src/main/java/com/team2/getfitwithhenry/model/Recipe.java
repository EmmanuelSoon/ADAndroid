package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private List<WeightedIngredient> ingredientList = new ArrayList<>();
    private NutritionRecord nutritionRecord;

    public Recipe(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("ingredientList") List<WeightedIngredient> ingredientList, @JsonProperty("nutritionRecord") NutritionRecord nutritionRecord) {
        this.id = id;
        this.name = name;
        this.ingredientList = ingredientList;
        this.nutritionRecord = nutritionRecord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WeightedIngredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<WeightedIngredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public NutritionRecord getNutritionRecord() {
        return nutritionRecord;
    }

    public void setNutritionRecord(NutritionRecord nutritionRecord) {
        this.nutritionRecord = nutritionRecord;
    }
}
