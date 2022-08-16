package com.team2.getfitwithhenry;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ActivityLevelFragment extends DialogFragment implements View.OnClickListener {

    Button rightBtn;
    Button selectBtn;
    Button leftBtn;
    TextView activityLvl;
    TextView activityDesc;
    ImageView activityImage;
    int selectedActivity = 0;

    private String[] activityLevels = {"Lightly Active", "Moderately Active", "Very Active", "Extra Active"};
    private String[] leveldescriptions =
            {"(Do not or Occasionally exercise and work a desk job)",
            "(Exercises Moderately 2-4 Days a week and lives an active lifestyle)",
            "(Exercises regularly 5 to 7 Days a week and lives an active lifestyle)",
            "(Exercises intensely everyday sometimes working out more than once a day or has a hard labour job)"};

    private int[] drawableList = new int[] {R.drawable.henry_sedentary, R.drawable.henry_level2, R.drawable.henry_level3, R.drawable.henry_level5};


    public ActivityLevelFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_activity_level, container, false);

        rightBtn = myView.findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(this);
        leftBtn = myView.findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(this);
        selectBtn = myView.findViewById(R.id.select_btn);
        selectBtn.setOnClickListener(this);

        activityLvl = myView.findViewById(R.id.activityLevel);
        activityDesc = myView.findViewById(R.id.activity_desc);
        activityImage = myView.findViewById(R.id.activityImage);

        return myView;
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        if(id == R.id.right_btn){
            selectedActivity += 1;
            selectedActivity = selectedActivity > 3 ? 0 : selectedActivity;
            String currActivity = activityLevels[selectedActivity];
            String currDesc = leveldescriptions[selectedActivity];
            activityLvl.setText(currActivity);
            activityDesc.setText(currDesc);
            activityImage.setImageResource( drawableList[selectedActivity]);
        }

        else if (id == R.id.left_btn){
            selectedActivity -= 1;
            selectedActivity = selectedActivity < 0 ? 3 : selectedActivity;
            String currActivity = activityLevels[selectedActivity];
            String currDesc = leveldescriptions[selectedActivity];
            activityLvl.setText(currActivity);
            activityDesc.setText(currDesc);
            activityImage.setImageResource( drawableList[selectedActivity]);
        }

        else if (id == R.id.select_btn){
            String currActivity = activityLevels[selectedActivity];
            ((QuestionnaireActivity) getActivity()).setactivityLvl(currActivity);
            dismiss();
        }
    }


}