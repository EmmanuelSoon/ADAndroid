package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeightedIngredient {
    private int id;
    private Ingredient ingredient;
    private double weight;

    public WeightedIngredient(@JsonProperty("id") int id, @JsonProperty("ingredient") Ingredient ingredient, @JsonProperty("weight") double weight) {
        this.id = id;
        this.ingredient = ingredient;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
