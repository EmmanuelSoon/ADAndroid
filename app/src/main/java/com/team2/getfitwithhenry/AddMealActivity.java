package com.team2.getfitwithhenry;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.team2.getfitwithhenry.adapter.AddMealAdapter;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Ingredient;
import com.team2.getfitwithhenry.model.MealType;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    TextView mealweight;
    TextView cals;
    User user;
    String strDate;
    List<Ingredient> myMeal = new ArrayList<>();
    Map<Integer, Double> mealMap = new HashMap<>();
    ActivityResultLauncher<Intent> rlSearchActivity;

    //TODO if you come from search how to enter date? also what about form validations?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        cals = findViewById(R.id.meal_cals_text);
        mealweight = findViewById(R.id.meal_weight_text);

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        System.out.println(json);
        user = gson.fromJson(json, User.class);

        Intent intent = getIntent();
        strDate = intent.getStringExtra("date");
        List<Ingredient> listFromActivity = (List<Ingredient>) intent.getSerializableExtra("ingredients");
        if(listFromActivity != null){
            myMeal = listFromActivity;
        }

        registerActivity();

        //TODO CHANGE THIS - setting to today's date if coming from search (to give options)
        if (strDate != null) {
            LocalDate date = LocalDate.parse(strDate);
        }
        else{
            LocalDate date = LocalDate.now();
            strDate = date.toString();
        }

        setListView(myMeal);

        mealTypeSpinner = findViewById(R.id.mealtype_spinner);
        mealTypeSpinner.setAdapter(new ArrayAdapter<MealType>(this, android.R.layout.simple_spinner_item, MealType.values()));

        //TODO change this button name
        addView = findViewById(R.id.search_ingredientsBtn);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addIngredient();
                Intent intent = new Intent(AddMealActivity.this, SearchFoodActivity.class);
                rlSearchActivity.launch(intent);
            }
        });



        submitBtn = findViewById(R.id.submitMealButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mealType = mealTypeSpinner.getSelectedItem().toString();


                postToServer(buildJson(mealMap));

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    public void registerActivity(){
        rlSearchActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == AppCompatActivity.RESULT_OK){
                Intent data = result.getData();
                List<Ingredient> newList = (List<Ingredient>)data.getSerializableExtra("ingredients");
                for (Ingredient ing: newList){
                    System.out.println(ing.getName());
                    if(!myMeal.contains(ing)){
                        myMeal.add(ing);
                    }
                }

                setListView(myMeal);
            }
        });
    }

    private void postToServer(JSONObject postData){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(postData.toString(), JSON);

        //need to use your own pc's ip address here, cannot use local host.
        Request request = new Request.Builder()
                .url(Constants.javaURL +"/user/adddietrecord")
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

    private JSONObject buildJson(Map<Integer, Double> toConvert){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            postData.put("mealType", mealTypeSpinner.getSelectedItem().toString());
            postData.put("date", strDate);
            toConvert.forEach((k,v)->
            {
                try {
                    postData.put(k.toString(), v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }catch (JSONException e) {
            e.printStackTrace();
        }



        return postData;
    }

    public void setListView(List<Ingredient> myMeal){
        if (myMeal != null){
            AddMealAdapter myAdapter = new AddMealAdapter(getApplicationContext(), myMeal, this);
            mlistView = findViewById(R.id.listView);
            if(mlistView != null) {
                mlistView.setAdapter(myAdapter);
                mlistView.setEnabled(true);
            }
            for (Ingredient ing: myMeal){
                mealMap.putIfAbsent(ing.getId(), 0.0);
            }
        }
    }


    public void setCurrentWeight(double inputWeight, int ingredientId){
        double currCal = 0;
        double currWeight = 0;
        mealMap.put(ingredientId, inputWeight);

        for (Map.Entry<Integer, Double> entry : mealMap.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            currWeight += value;
            Ingredient curr = findIngredientById(key);
            if(curr != null){
                currCal += curr.getCalorie() * value/100;
            }
        }

        cals.setText(String.format("%.2f", currCal));
        mealweight.setText(String.format("%.2f", currWeight));
    }

    private Ingredient findIngredientById(int id){
        for (Ingredient ing : myMeal){
            if(ing.getId() == id){
                return ing;
            }
        }
        return null;
    }

    public Map<Integer, Double> getMealMap(){
        return mealMap;
    }
}

