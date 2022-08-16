package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Recipe {
    private int id;
    private String name;
    private List<WeightedIngredient> ingredientList = new ArrayList<>();
    private NutritionRecord nutritionRecord;
    private int portion;
    private String image;

    public Recipe(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("ingredientList") List<WeightedIngredient> ingredientList,
                  @JsonProperty("nutritionRecord") NutritionRecord nutritionRecord, @JsonProperty("portion")int portion, @JsonProperty("image") String image) {
        this.id = id;
        this.name = name;
        this.ingredientList = ingredientList;
        this.nutritionRecord = nutritionRecord;
        this.portion = portion;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id;
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

    public String getImage(){
        return image;
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

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }
}

