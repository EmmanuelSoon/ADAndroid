package com.team2.getfitwithhenry;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.team2.getfitwithhenry.model.DietRecord;

import java.util.List;
import com.team2.getfitwithhenry.model.*;

public class MealButtonsFragment extends Fragment {

    private List<DietRecord> dietRecordList;
    private Button breakfastBtn;
    private Button lunchBtn;
    private Button dinnerBtn;
    private Button extrasBtn;


    public MealButtonsFragment() {
        // Required empty public constructor
    }

    public interface IMealButtonsFragment {
        void itemClicked(String content);
    }

    private IMealButtonsFragment iMealButtonsFragment;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iMealButtonsFragment = (IMealButtonsFragment) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_buttons, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        View view = getView();
        if (view != null){
            breakfastBtn = view.findViewById(R.id.breakfast_btn);
            lunchBtn = view.findViewById(R.id.lunch_btn);
            dinnerBtn = view.findViewById(R.id.dinner_btn);
            extrasBtn = view.findViewById(R.id.extras_btn);

            Button[] btnList = {breakfastBtn, lunchBtn, dinnerBtn, extrasBtn};
            for(Button button : btnList){
                button.setOnClickListener((v) ->{
                    iMealButtonsFragment.itemClicked(button.getText().toString());
                });
            }

        }


    }

    public void setDietRecordList(List<DietRecord> dietRecordList){
        this.dietRecordList = dietRecordList;
        updateDietRecords();
    }

    private void updateDietRecords() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    double bfTotal = 0;
                    double lunchTotal = 0;
                    double dinnerTotal = 0;
                    double extrasTotal = 0;

                    for (DietRecord dietRecord: dietRecordList){
                        switch (dietRecord.getMealType()){
                            case BREAKFAST:
                                bfTotal += dietRecord.getCalorie();
                                break;
                            case LUNCH:
                                lunchTotal += dietRecord.getCalorie();
                                break;
                            case DINNER:
                                dinnerTotal += dietRecord.getCalorie();
                                break;
                            case EXTRA:
                                extrasTotal += dietRecord.getCalorie();
                                break;
                        }
                    }
                    //update the button texts
                    String textTemplate = "meal \n\n TotalCalories: 1000";
                    breakfastBtn.setText("Breakfast: " + String.format("%.2f", bfTotal) + " Kcal");
                    lunchBtn.setText("Lunch: " + String.format("%.2f", lunchTotal)+ " Kcal");
                    dinnerBtn.setText("Dinner: " + String.format("%.2f", dinnerTotal)+ " Kcal");
                    extrasBtn.setText("Extras: " + String.format("%.2f", extrasTotal)+ " Kcal");

                }
            });
        }
    }

}
