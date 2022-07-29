package com.team2.getfitwithhenry.model;

import android.text.LoginFilter;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Integer> dailyCal = new ArrayList<>();
    private double height;
    private double weight;
    private int calorieCap = 2000;

    public User(String username, String password, double height, double weight){
        this.username = username;
        this.password = password;
        this.height = height;
        this.weight = weight;
        dailyCal.add(0);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Integer> getDailyCal() {
        return dailyCal;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }
    public int getCalorieCap() {
        return calorieCap;
    }

    public void addCalories(int cal){
        int todayCal = dailyCal.get(dailyCal.size() - 1);
        todayCal = todayCal + cal;
        dailyCal.set(dailyCal.size()-1, todayCal);
    }

    public int getCurrentCal(){
        return dailyCal.get(dailyCal.size() -1);
    }

    public void setCalorieCap(int cal){
        calorieCap = cal;
    }

    public void setNewDay(){
        dailyCal.add(0);
    }

    public void seeding(){
        dailyCal.add(1900);
        dailyCal.add(2000);
        dailyCal.add(1922);
        dailyCal.add(1800);
        dailyCal.add(0);
    }


}
