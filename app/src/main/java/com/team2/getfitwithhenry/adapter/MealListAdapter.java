package com.team2.getfitwithhenry.adapter;

import static java.util.stream.Collectors.groupingBy;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MealListAdapter extends ArrayAdapter<DietRecord> {
    private Context context;
    protected List<DietRecord> dietRecordList;
    final int THUMBSIZE = 64;

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
        String className = dietRecordList.get(pos).getFoodName();

        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText(className);

        System.out.println(className);
        ImageView imageView = view.findViewById(R.id.foodView);
        try {
            imageView.setImageBitmap(getBitmapFromAssets("seed_images/" + className.trim() + ".jpg"));
        } catch (IOException ex){
            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }


        TextView textView = view.findViewById(R.id.caloriesView);
        textView.setText(String.format("%.1f", dietRecordList.get(pos).getCalorie()) + " kcal \n (" + Double.toString(dietRecordList.get(pos).getWeight()) + " g)");

        //String.format("%.2f", 1.23456)
        return view;
    }

    public void sortListByEnum(List<DietRecord> drList){
       Map<MealType, Double> calsByType = drList.stream()
               .sorted(Comparator.comparing(DietRecord::getMealType))
               .collect(Collectors.groupingBy(DietRecord::getMealType, Collectors.summingDouble(DietRecord::getCalorie)));

    }

    public Bitmap getBitmapFromAssets(String filename) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream ins = assetManager.open(filename);
        Bitmap bitmap = BitmapFactory.decodeStream(ins);
        ins.close();
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);

        return thumbImage;
    }


}
