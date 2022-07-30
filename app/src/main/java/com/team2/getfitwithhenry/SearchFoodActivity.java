package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.adapter.FoodListAdapter;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.Ingredient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class SearchFoodActivity extends AppCompatActivity {

    private ListView mlistView;
    private Button mSearchBtn;
    private EditText mEditText;
    List<Ingredient> iList;
    String query;
    private final OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        mSearchBtn = findViewById(R.id.search);
        mEditText = findViewById(R.id.editText);


        Intent intent = getIntent();
        query = intent.getStringExtra("SearchValue");

        mEditText.setText(query);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = mEditText.getText().toString();
                getSearchResult(query);
            }
        });

    }

    public void getSearchResult(String query){


        JSONObject postData = new JSONObject();
        try {
            postData.put("query", query);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);


            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url("http://192.168.10.127:8080/search/ingredients")
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

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        iList = Arrays.asList(objectMapper.readValue(responseBody.string(), Ingredient[].class));
                        displaySearchResult(getApplicationContext(), iList);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void displaySearchResult(final Context context, List<Ingredient> myList) {
        if (context != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    FoodListAdapter myAdapter = new FoodListAdapter(context, myList);
                    mlistView = findViewById(R.id.listView);
                    if(mlistView != null) {
                        mlistView.setAdapter(myAdapter);
                    }

                    String name = myList.get(0).getName();
                    Toast toast = Toast.makeText(context, name, Toast.LENGTH_SHORT);
                    toast.show();

                }
            });
        }
    }




}