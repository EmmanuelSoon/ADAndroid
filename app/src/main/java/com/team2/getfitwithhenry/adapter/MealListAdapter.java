package com.team2.getfitwithhenry.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.Ingredient;
import com.team2.getfitwithhenry.model.MealType;

import java.util.List;

public class MealListAdapter extends ArrayAdapter<DietRecord> {
    private Context context;
    protected List<DietRecord> dietRecordList;

    public MealListAdapter(Context context, List<DietRecord> dietRecordList)
    {
        super(context, R.layout.meal_list, dietRecordList);
        this.dietRecordList = dietRecordList;
        this.context = context;


    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.meal_list, parent, false);
        }

        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText(dietRecordList.get(pos).getFoodName());

        TextView mealView = view.findViewById(R.id.mealView);
        mealView.setText(dietRecordList.get(pos).getMealType().toString());


        TextView textView = view.findViewById(R.id.caloriesView);
        textView.setText(Double.toString(dietRecordList.get(pos).getCalorie()));

        return view;
    }


}
