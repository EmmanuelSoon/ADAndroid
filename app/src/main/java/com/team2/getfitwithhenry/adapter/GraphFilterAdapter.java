package com.team2.getfitwithhenry.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team2.getfitwithhenry.CaloriesGraphFilterFragment;
import com.team2.getfitwithhenry.WaterIntakeGraphFilterFragment;
import com.team2.getfitwithhenry.WeightGraphFilterFragment;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;
import com.team2.getfitwithhenry.model.WeekMonthData;

import java.util.List;

public class GraphFilterAdapter extends FragmentStateAdapter{
    private final int NUM_TABS = 3;
    private User user;
    private List<HealthRecord> hrList;
    private List<WeekMonthData> weekList;
    private List<WeekMonthData> monthList;

    public GraphFilterAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, User user, List<HealthRecord> hrList, List<WeekMonthData> weekList,List<WeekMonthData> monthList) {
        super(fragmentManager, lifecycle);
        this.user = user;
        this.hrList = hrList;
        this.weekList = weekList;
        this.monthList = monthList;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new WeightGraphFilterFragment(user, hrList,weekList,monthList,"weight");
            case 1:
                return new CaloriesGraphFilterFragment(user, hrList,"calories");
            case 2:
                return new WaterIntakeGraphFilterFragment(user, hrList,"waterIntake");

        }

        return null;


    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
