package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.HealthRecord;
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

public class LoggerActivity extends AppCompatActivity implements LifecycleObserver {

    private User tempUser;
    private final OkHttpClient client = new OkHttpClient();
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private BottomNavigationView bottomNavView;

    //TODO LIST:
    //date
    //refresh page after adding meal
    //get user's calories for the day



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

        // Set up Calendar
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        //set up bottom navbar
        setBottomNavBar();



        //temp user just to add in the logic
        //int id, String name, String username, String password, Role role, Goal goal
        tempUser = new User(1, "Henry","Henry@gmail.com" ,"password", Role.NORMAL, Goal.MUSCLE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        //fix date here
        String currDate = LocalDate.now().format(formatter);
        getDietRecordsFromServer(tempUser, currDate);

        //add meal function
        Button addFoodBtn = findViewById(R.id.add_food);
        addFoodBtn.setOnClickListener((view -> {
            DatePicker datePicker = datePickerDialog.getDatePicker();
            String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("date", dateSelect);
            intent.putExtra("user", tempUser);

            startActivity(intent);


        }));

    }

    @Override
    public void onResume() {

        super.onResume();
    }



    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
                DateTimeFormatter converter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("dd-MMM-yyyy")
                        .toFormatter(Locale.ENGLISH);

                LocalDate curr = LocalDate.parse(date, converter);
                getDietRecordsFromServer(tempUser, curr.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year)
    {
        return day + "-" + getMonthFormat(month) + "-" + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
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
                    .url("http://192.168.10.127:8080/user/getdietrecords")
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
                        //to do: convert responseBody into list of HealthRecords
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        List <DietRecord> dietRecordList = Arrays.asList(objectMapper.readValue(responseBody.string(), DietRecord[].class));
                        FragmentManager fm = getSupportFragmentManager();
                        MealFragment mealFragment = (MealFragment) fm.findFragmentById(R.id.fragment_meal);
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