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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.team2.getfitwithhenry.AddWaterFragment;
import com.team2.getfitwithhenry.LoggerActivity;
import com.team2.getfitwithhenry.MealFragment;
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
    private AdapterListener mListener;


    public MealListAdapter(Context context, List<DietRecord> dietRecordList)
    {
        super(context, R.layout.meal_list, dietRecordList);
        this.dietRecordList = dietRecordList;
        this.context = context;

    }

    //Creating adapter listener interface here to allow me to pass back the onclick to the fragment
    public interface AdapterListener {
        void removeDiet(DietRecord dr);
    }
    //constructor for AdapterListener
    public void setListener(AdapterListener listener) {
        this.mListener = listener;
    }


    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.meal_list, parent, false);
        }
        String className = dietRecordList.get(pos).getIngredient().getName();

        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText(className);

        ImageView imageView = view.findViewById(R.id.foodView);
        try {
            imageView.setImageBitmap(getBitmapFromAssets("seed_images/" + className.trim() + ".jpg"));
        } catch (IOException ex){
            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }


        TextView textView = view.findViewById(R.id.caloriesView);
        textView.setText(String.format("%.1f", dietRecordList.get(pos).getCalorie()) + " kcal \n (" + Double.toString(dietRecordList.get(pos).getWeight()) + " g)");

        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Meal deleted!", Toast.LENGTH_SHORT).show();
                mListener.removeDiet(dietRecordList.get(pos));
            }
        });

        //String.format("%.2f", 1.23456)
        return view;
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
