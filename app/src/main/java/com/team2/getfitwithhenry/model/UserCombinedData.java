package com.team2.getfitwithhenry.model;

import java.util.List;

public class UserCombinedData {

    private List<HealthRecord> myHrList;
    private List<DietRecord> myDietRecord;

    public UserCombinedData() {
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
