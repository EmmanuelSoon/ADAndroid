package com.team2.getfitwithhenry.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team2.getfitwithhenry.R;
import com.team2.getfitwithhenry.model.Recipe;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    Context context;
    List<Recipe> recipeList;

    public RecipeListAdapter(Context context, List<Recipe> rList) {
        super(context, R.layout.food_list);
        this.context = context;
        this.recipeList = rList;
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.food_list, parent, false);
        }

//        String recipeName = recipeList.get(pos).getName();
//        ImageView imageView = view.findViewById(R.id.imageView);
//        try {
//            imageView.setImageBitmap(getBitmapFromAssets("seed_images/" + recipeName + ".jpg"));
//        } catch (IOException ex){
//            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
//        }

        TextView nameView = view.findViewById(R.id.queryName);
        nameView.setText(recipeList.get(pos).getName());
        System.out.println(recipeList.get(pos).getName());

        TextView nutriView = view.findViewById(R.id.queryNutrition);
        nutriView.setText(recipeList.get(pos).getNutritionRecord().getTruncNutrition());
        view.setBackgroundColor(Color.WHITE);

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
