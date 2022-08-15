package com.team2.getfitwithhenry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserWithWeightHeight {

    private User user;
    private Double userWeight;
    private Double userHeight;

    public UserWithWeightHeight() {
    }

    public UserWithWeightHeight(@JsonProperty("user") User user, @JsonProperty("userWeight") Double userWeight, @JsonProperty("userHeight") Double userHeight) {
        this.user = user;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(Double userWeight) {
        this.userWeight = userWeight;
    }

    public Double getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(Double userHeight) {
        this.userHeight = userHeight;
    }
}
