package com.team2.getfitwithhenry.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.model.Ingredient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AddMealAdapter extends ArrayAdapter<Ingredient> {
    private Context context;
    protected List<Ingredient> iList;

    public AddMealAdapter(Context context, List<Ingredient> ingList)
    {
        super(context, R.layout.add_meal_row, ingList);
        this.iList = ingList;
        this.context = context;

    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.add_meal_row, parent, false);
        }


        String className = iList.get(pos).getName();

        ImageView imageView = view.findViewById(R.id.imageView);
        try {
            imageView.setImageBitmap(getBitmapFromAssets("seed_images/" + className + ".jpg"));
        } catch (IOException ex){
            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }

        TextView nameView = view.findViewById(R.id.foodName);
        nameView.setText(iList.get(pos).getName());

        TextView cals = view.findViewById(R.id.foodCalories);
        cals.setText("Calories per " + iList.get(pos).getNutritionRecord().getServingSize() + "g :" + String.valueOf(iList.get(pos).getCalorie()) + " kcals");

        return view;

    }

    public Bitmap getBitmapFromAssets(String filename) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream ins = assetManager.open(filename);
        Bitmap bitmap = BitmapFactory.decodeStream(ins);
        ins.close();

        return bitmap;
    }
}