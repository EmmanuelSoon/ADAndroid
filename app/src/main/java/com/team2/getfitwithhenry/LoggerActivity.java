package com.team2.getfitwithhenry;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.Ingredient;
import com.team2.getfitwithhenry.model.Role;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.jvm.internal.TypeReference;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.util.Calendar;
import java.util.Locale;

public class LoggerActivity extends AppCompatActivity implements MealButtonsFragment.IMealButtonsFragment, DefaultLifecycleObserver{


    private User tempUser;
    private final OkHttpClient client = new OkHttpClient();
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private BottomNavigationView bottomNavView;
    User user;

    //TODO LIST:
    //refresh page after adding meal
    //get meal list sorted by meal type (combine meal type)
    //UI -> show break down of ingredients on click
    // first change logger ui to show the enum types with the cals
    // then set on click to each row
    // then inflate each row below with listview for each enum


    @Override
    public void itemClicked(String content){
        DatePicker datePicker = datePickerDialog.getDatePicker();
        String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());

        MealFragment mf = new MealFragment();
        Bundle args = new Bundle();
        args.putString("meal", content.split(" ")[0].toLowerCase());
        args.putString("date", dateSelect);
        args.putString("username", user.getUsername());
        mf.setArguments(args);
        mf.show(getSupportFragmentManager(), "Meal Fragment");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

        //set up bottom navbar
        setBottomNavBar();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        System.out.println(json);
        user = gson.fromJson(json, User.class);

        System.out.println(user.getUsername());

        // Set up Calendar
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(setDate(LocalDate.now()));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currDate = LocalDate.now().format(formatter);
        getDietRecordsFromServer(user, currDate);

        //Set up activity result launcher
        ActivityResultLauncher<Intent> addMealActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();

                            // handle results here
                            DatePicker datePicker = datePickerDialog.getDatePicker();
                            String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());
                            getDietRecordsFromServer(user, dateSelect);
                        }
                    }
                });

        //add meal function
        Button addFoodBtn = findViewById(R.id.add_food);
        addFoodBtn.setOnClickListener((view -> {
            DatePicker datePicker = datePickerDialog.getDatePicker();
            String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("date", dateSelect);

            addMealActivityLauncher.launch(intent);


        }));

        //set My Records
        getHealthRecordFromServer(user, currDate);

    }

    @Override
    //TODO why is this not workikng
    protected void onResume(){
        super.onResume();
        DatePicker datePicker = datePickerDialog.getDatePicker();
        String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());
        getDietRecordsFromServer(user, dateSelect);
    }

    public void getHealthRecordFromServer(User user, String date){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            postData.put("date", date);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url("http://192.168.10.122:8080/user/gethealthrecorddate")
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
                        HealthRecord myHr = objectMapper.readValue(responseBody.string(), HealthRecord.class);
                        setMyRecords(getApplicationContext(), myHr);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMyRecords(final Context context, final HealthRecord myHr){
        if (context != null && myHr != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    TextView totalCals = findViewById(R.id.total_calories);
                    TextView currentCals = findViewById(R.id.current_calories);
                    TextView bmi = findViewById(R.id.BMI);

                    totalCals.setText("Calorie Limit: " + String.valueOf(user.getCalorieintake_limit_inkcal()));
                    currentCals.setText("Calories consumed: " + String.valueOf(Math.round(myHr.getCalIntake())));
                    Double myBmi = myHr.getUserWeight() / Math.pow(myHr.getUserHeight()/100, 2.0);
                    System.out.println(myBmi);
                    bmi.setText("BMI: " + String.valueOf(Math.round(myBmi)));

                }
            });
        }
    }


    public void openDatePicker(View view) {   datePickerDialog.show();    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) ;
                DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedDate = LocalDate.parse(date, format2);
                dateButton.setText(setDate(parsedDate));

                getDietRecordsFromServer(user, date);
                getHealthRecordFromServer(user, date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String setDate(LocalDate date){
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return date.format(format1);
    }


    private void getDietRecordsFromServer(User user, String date){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            postData.put("date", date);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url("http://192.168.10.122:8080/user/getdietrecords")
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

                        String msg = String.valueOf(responseBody);
                        //convert responseBody into list of HealthRecords
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        List <DietRecord> dietRecordList = Arrays.asList(objectMapper.readValue(responseBody.string(), DietRecord[].class));



                        //do something with FM here
                        FragmentManager fm = getSupportFragmentManager();
                        MealButtonsFragment mealFragment = (MealButtonsFragment) fm.findFragmentById(R.id.fragment_meal);
                        mealFragment.setDietRecordList(dietRecordList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setBottomNavBar() {
        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setSelectedItemId(R.id.nav_log);
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

                    case(R.id.nav_search):
                        intent = new Intent(getApplicationContext(), SearchFoodActivity.class);
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