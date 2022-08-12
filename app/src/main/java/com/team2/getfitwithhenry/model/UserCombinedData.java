package com.team2.getfitwithhenry.model;

import java.util.List;

public class UserCombinedData {

    private List<WeekMonthData> weekList;
    private List<WeekMonthData> monthList;
    private List<HealthRecord> myHrList;
    private List<DietRecord> myDietRecord;

    public UserCombinedData() {
    }

    public List<WeekMonthData> getWeekList() {
        return weekList;
    }

    public void setWeekList(List<WeekMonthData> weekList) {
        this.weekList = weekList;
    }

    public List<WeekMonthData> getMonthList() {
        return monthList;
    }

    public void setMonthList(List<WeekMonthData> monthList) {
        this.monthList = monthList;
    }

    public List<HealthRecord> getMyHrList() {
        return myHrList;
    }

    public void setMyHrList(List<HealthRecord> myHrList) {
        this.myHrList = myHrList;
    }

    public List<DietRecord> getMyDietRecord() {
        return myDietRecord;
    }

    public void setMyDietRecord(List<DietRecord> myDietRecord) {
        this.myDietRecord = myDietRecord;
    }
}
