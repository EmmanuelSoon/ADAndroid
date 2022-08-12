package com.team2.getfitwithhenry.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.team2.getfitwithhenry.AddMealActivity;
import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.model.Ingredient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AddMealAdapter extends ArrayAdapter<Ingredient> {
    private Context context;
    protected List<Ingredient> iList;
    private AddMealActivity addMealActivity;

    public AddMealAdapter(Context context, List<Ingredient> ingList, AddMealActivity addMealActivity)
    {
        super(context, R.layout.add_meal_row, ingList);
        this.iList = ingList;
        this.context = context;
        this.addMealActivity = addMealActivity;

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
        cals.setText("Calories/100g :" + String.valueOf(iList.get(pos).getCalorie()) + " kcals");

        EditText editText = view.findViewById(R.id.foodWeight);
        editText.setText("");
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if(addMealActivity.getMealMap().get(iList.get(pos).getId())!=0) {
            editText.setText(addMealActivity.getMealMap().get(iList.get(pos).getId()).toString());
            editText.setSelection(editText.length());
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    double weight = Double.parseDouble(v.getText().toString())/100;
                    addMealActivity.setCurrentWeight(Double.parseDouble(v.getText().toString()), iList.get(pos).getId());
                    return false;
                }
                return false;
            }
        });


        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    Double weight = 0.0;
                    try{
                        weight = Double.parseDouble(editText.getText().toString());
                    } catch (Exception ex){
                        weight = 0.0;
                    }
                    addMealActivity.setCurrentWeight(weight , iList.get(pos).getId());

                }
            }
        });

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
