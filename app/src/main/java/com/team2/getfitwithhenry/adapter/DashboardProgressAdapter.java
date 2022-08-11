package com.team2.getfitwithhenry.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team2.getfitwithhenry.fragments.CalAndWaterFragment;
import com.team2.getfitwithhenry.fragments.MacrosFragment;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;

import java.util.List;

public class DashboardProgressAdapter extends FragmentStateAdapter {

    private final int NUM_TABS = 2;
    private User user;
    private HealthRecord currHr;
    private List<DietRecord> drList;

    public DashboardProgressAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, User user, HealthRecord hr, List<DietRecord> drList) {
        super(fragmentManager, lifecycle);
        this.user = user;
        this.currHr = hr;
        this.drList = drList;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CalAndWaterFragment(user, currHr);
            case 1:
                return new MacrosFragment(user, currHr.getCalIntake(), drList);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
