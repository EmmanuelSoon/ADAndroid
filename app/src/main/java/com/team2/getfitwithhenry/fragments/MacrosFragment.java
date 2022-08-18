package com.team2.getfitwithhenry.fragments;

import android.icu.number.NumberFormatter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.helper.MacrosDrawable;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.NutritionRecord;
import com.team2.getfitwithhenry.model.User;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MacrosFragment extends Fragment {

    private float proteinAngle;
    private float carbAngle;
    private float fatAngle;

    private double calIntake;
    private User user;

    //formula: each gram of a macronutrient is worth this many cals:
    //protein = 4 cals
    //carbs = 4 cals
    //fats = 9 cals

    //Weight loss: 40/40/20 (carbohydrates/protein/fats)
    //Weight gain: 40/30/30
    //Weight maintenance: 40/30/30

    public MacrosFragment() {
        // Required empty public constructor
    }

    public MacrosFragment(User user, double calIntake, List<DietRecord> drList) {
        this.calIntake = calIntake;
        calculateIntake(drList);
        this.user = user;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_macros, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState );
        ImageView macrosChart = view.findViewById(R.id.macros_chart);
        macrosChart.setImageDrawable(new MacrosDrawable(proteinAngle/100*360 == 0 ? 360 : proteinAngle/100*360, carbAngle/100*360, fatAngle/100*360));
        setIdealMacros(user, view);
        setIntake(view);

    }

    public void setIdealMacros(User user, View view){
        TextView tv1 = view.findViewById(R.id.row1_3);
        TextView tv2 = view.findViewById(R.id.row2_3);
        TextView tv3 = view.findViewById(R.id.row3_3);

        switch(user.getGoal()){
            case WEIGHTMAINTAIN: case WEIGHTGAIN:
                tv2.setText("40%");
                tv1.setText("30%");
                tv3.setText("30%");
                break;
            case WEIGHTLOSS: case MUSCLE:
                tv2.setText("40%");
                tv1.setText("40%");
                tv3.setText("20%");
                break;
        }

    }

    public void setIntake(View view){
        TextView tv1 = view.findViewById(R.id.row1_2);
        TextView tv2 = view.findViewById(R.id.row2_2);
        TextView tv3 = view.findViewById(R.id.row3_2);

        tv1.setText(String.valueOf(proteinAngle)+ "%");
        tv2.setText(String.valueOf(carbAngle)+ "%");
        tv3.setText(String.valueOf(fatAngle)+ "%");
    }

    public void calculateIntake(List<DietRecord> drList){
        double proteinIntakeKcal = 0;
        double carbIntakeKcal = 0;
        double fatIntakeKcal = 0;

        for(DietRecord dr : drList){
            NutritionRecord nr = dr.getIngredient().getNutritionRecord();
            carbIntakeKcal += dr.getWeight()/nr.getServingSize() * nr.getCarbs() * 4;
            proteinIntakeKcal += dr.getWeight()/nr.getServingSize() * nr.getProteins() * 4;
            fatIntakeKcal += dr.getWeight()/nr.getServingSize() * nr.getFats() * 9;
        }

        double totalIntake = carbIntakeKcal + proteinIntakeKcal + fatIntakeKcal;
        NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        f.setRoundingMode(RoundingMode.HALF_UP);
        if(totalIntake == 0) totalIntake = 1;

        carbAngle = new Float(f.format(carbIntakeKcal/totalIntake*100));
        proteinAngle = new Float(f.format(proteinIntakeKcal/totalIntake*100));
        fatAngle = new Float(f.format(fatIntakeKcal/totalIntake*100));

    }
}