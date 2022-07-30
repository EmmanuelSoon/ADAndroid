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
import com.team2.getfitwithhenry.model.Ingredient;

import java.util.List;

public class FoodListAdapter extends ArrayAdapter<Ingredient> {
    private Context context;
    protected List<Ingredient> iList;

    public FoodListAdapter(Context context, List<Ingredient> ingList)
    {
        super(context, R.layout.food_list, ingList);
        this.iList = ingList;
        this.context = context;

    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.food_list, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.bread);
        //to set images

        TextView textView = view.findViewById(R.id.textView);
        textView.setText(iList.get(pos).getName());

        return view;
    }


}
