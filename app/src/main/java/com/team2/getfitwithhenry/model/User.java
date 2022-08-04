package com.team2.getfitwithhenry.model;

import android.text.LoginFilter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private Goal goal;
    private List<Ingredient> dislike = new ArrayList<>();


    public User(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("role") Role role, @JsonProperty("goal") Goal goal, @JsonProperty("dislike") List<Ingredient> dislike) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.goal = goal;
        this.dislike = dislike;
    }

    public User(int id, String name, String username, String password, Role role, Goal goal) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.goal = goal;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
