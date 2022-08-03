package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.navigation.NavigationBarView;
import com.team2.getfitwithhenry.adapter.FoodListAdapter;
import com.team2.getfitwithhenry.model.Ingredient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private NavigationBarView bottomNavView;
    List<Ingredient> iList;
    String query;
    private final OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        setBottomNavBar();

        mSearchBtn = findViewById(R.id.search);
        mEditText = findViewById(R.id.editText);


        Intent intent = getIntent();
        query = intent.getStringExtra("SearchValue");
        if(query != null){
            getSearchResult(query);
        }

        mEditText.setText(query);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = mEditText.getText().toString();
                getSearchResult(query);

                //drop the softkeyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

                    String name = myList.get(0).getName() + " size: " + myList.size();
                    Toast toast = Toast.makeText(context, name, Toast.LENGTH_SHORT);
                    toast.show();

                    mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Ingredient selectedIng = iList.get(position);
                            //maybe change to add meal activity
                            Intent intent = new Intent(context, LoggerActivity.class);
                            intent.putExtra("ingredient", selectedIng);
                            startActivity(intent);

                        }
                    });

                }
            });
        }
    }

    public void setBottomNavBar() {
        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setSelectedItemId(R.id.nav_search);
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch(id){

                    case(R.id.nav_scanner):
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        break;  //or should this be finish?

                    case(R.id.nav_log):
                        intent = new Intent(getApplicationContext(), LoggerActivity.class);
                        startActivity(intent);
                        break;

                    case(R.id.nav_recipe):
                        intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        startActivity(intent);
                        break;

                    case(R.id.nav_home):
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });
    }




}