package com.team2.getfitwithhenry.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.team2.getfitwithhenry.AddWaterFragment;
import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.helper.ProgressArcDrawable;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;


public class CalAndWaterFragment extends Fragment {

    Double calIntake;
    Double calLimit;
    Double waterIntake;
    Double waterLimit;


    public CalAndWaterFragment() {
        // Required empty public constructor
    }

    public CalAndWaterFragment(User user, HealthRecord hr){
        this.calIntake = hr.getCalIntake();
        this.waterIntake = hr.getWaterIntake();
        this.calLimit = user.getCalorieintake_limit_inkcal();
        this.waterLimit = user.getWaterintake_limit_inml();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cal_and_water, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView caloriesText = view.findViewById(R.id.caloriesText);
        TextView waterText = view.findViewById(R.id.waterText);
        ImageView calsProg = view.findViewById(R.id.calories_progress);
        ImageView waterProg = view.findViewById(R.id.water_progress);

        float calAngle = Math.round((calIntake / calLimit) * 270) > 270 ? 270f : Math.round((calIntake / calLimit) * 270);
        float waterAngle = Math.round((waterIntake / waterLimit) * 270) > 270f ? 270f : Math.round((waterIntake / waterLimit) * 270);

        if (calAngle < 243f) {
            calsProg.setImageDrawable(new ProgressArcDrawable(calAngle, "green"));
        } else {
            calsProg.setImageDrawable(new ProgressArcDrawable(calAngle, "red"));
        }

        waterProg.setImageDrawable(new ProgressArcDrawable(waterAngle, "blue"));

        //create text strings
        String calsLabel = "Cals\n" + String.valueOf(Math.round(calIntake));
        String kcals = "kcals";

        SpannableString ss1 = new SpannableString(calsLabel);
        SpannableString ss2 = new SpannableString(kcals);
        ss2.setSpan(new RelativeSizeSpan(0.6f), 0, 5, 0);
        ss2.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0,5, 0);
        CharSequence finalText = TextUtils.concat(ss1,  "\n" , ss2);

        caloriesText.setText(finalText);

        String waterLabel = "Water\n" + String.valueOf(Math.round(waterIntake));
        String mils = "ml";

        SpannableString ss3 = new SpannableString(waterLabel);
        SpannableString ss4 = new SpannableString(mils);
        ss4.setSpan(new RelativeSizeSpan(0.6f), 0, 2, 0);
        ss4.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0,2, 0);
        CharSequence finalText2 = TextUtils.concat(ss3,  "\n" , ss4);

        waterText.setText(finalText2);

        waterProg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogFragment df = new AddWaterFragment();
                    df.show(getChildFragmentManager(), "AddWaterFragment");

            }
        });
    }

}