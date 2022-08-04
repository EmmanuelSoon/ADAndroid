package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.adapter.FoodListAdapter;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.Ingredient;
import com.team2.getfitwithhenry.model.MealType;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddMealActivity extends AppCompatActivity {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final OkHttpClient client = new OkHttpClient();
    private LinearLayout linlayout;
    Button addView;
    Button submitBtn;
    Spinner mealTypeSpinner;
    ListView mlistView;
    EditText mealName;
    EditText cals;
    EditText weight;

    //TODO if you come from search how to enter date? also what about form validations?
    //TODO fix date


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        mealName = findViewById(R.id.meal_name_text);
        cals = findViewById(R.id.meal_cals_text);
        weight = findViewById(R.id.meal_weight_text);

        Intent intent = getIntent();
        String strDate = intent.getStringExtra("date");
        User user = (User)intent.getSerializableExtra("user");//save preferences instead
        List<Ingredient> myMeal = (List<Ingredient>) intent.getSerializableExtra("ingredients");

        //TODO CHANGE THIS - setting to today's date if coming from search (to give options)
        if (strDate != null) {
            LocalDate date = LocalDate.parse(strDate);
        }
        else{
            LocalDate date = LocalDate.now();
        }

        //if list not null set cals
        if (myMeal != null){
            setMealCals(myMeal);
        }


        FoodListAdapter myAdapter = new FoodListAdapter(getApplicationContext(), myMeal);
        mlistView = findViewById(R.id.listView);
        if(mlistView != null) {
            mlistView.setAdapter(myAdapter);
        }

        mealTypeSpinner = findViewById(R.id.mealtype_spinner);
        mealTypeSpinner.setAdapter(new ArrayAdapter<MealType>(this, android.R.layout.simple_spinner_item, MealType.values()));

        addView = findViewById(R.id.addViewButton);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });



        submitBtn = findViewById(R.id.submitMealButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mealType = mealTypeSpinner.getSelectedItem().toString();

                Map<String, String> myMap = new HashMap<>();
                myMap.put("mealName", mealName.getText().toString());
                myMap.put("mealType", mealType);
                myMap.put("calories", cals.getText().toString());
                myMap.put("weight", weight.getText().toString());
                myMap.put("date", strDate);
                myMap.put("username", user.getUsername());

                postToServer(buildJson(myMap));

            }
        });



    }

//commented out for now, to improve on in future revisions
    public void addIngredient(){
//        LayoutInflater layInf = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ViewGroup parent = (ViewGroup) findViewById(R.id.linearViewAddMeal);
//        layInf.inflate(R.layout.add_meal_row, parent);

    }

    private void postToServer(JSONObject postData){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(postData.toString(), JSON);

        //need to use your own pc's ip address here, cannot use local host.
        Request request = new Request.Builder()
                .url("http://192.168.10.127:8080/user/adddietrecord")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONObject buildJson(Map<String, String> toConvert) {
        JSONObject postData = new JSONObject();
            toConvert.forEach((k,v)->
            {
                try {
                    postData.put(k, v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        return postData;
    }

    public void setMealCals(List<Ingredient> myMeal){
        double mealCals = 0;
        for (Ingredient ing : myMeal){
            mealCals += ing.getCalorie();
        }

        cals.setText(String.valueOf(mealCals));

    }


}

