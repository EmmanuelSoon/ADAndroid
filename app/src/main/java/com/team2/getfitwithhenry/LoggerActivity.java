package com.team2.getfitwithhenry;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.Constants;
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
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

public class LoggerActivity extends AppCompatActivity implements MealButtonsFragment.IMealButtonsFragment, DefaultLifecycleObserver {


    private User tempUser;
    private final OkHttpClient client = new OkHttpClient();
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Toolbar mToolbar;
    private BottomNavigationView bottomNavView;
    private Button addHeight;
    private Button addWeight;
    private ActivityResultLauncher<Intent> addMealActivityLauncher;
    User user;

    //TODO LIST:
    //UI -> show break down of ingredients on click
    // first change logger ui to show the enum types with the cals
    // then set on click to each row
    // then inflate each row below with listview for each enum


    @Override
    public void itemClicked(String content) {
        DatePicker datePicker = datePickerDialog.getDatePicker();
        String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());

        MealFragment mf = new MealFragment();
        Bundle args = new Bundle();
        args.putString("meal", content.split(" ")[0].toLowerCase().replace(":", ""));
        args.putString("date", dateSelect);
        args.putString("username", user.getUsername());
        mf.setArguments(args);
        mf.show(getSupportFragmentManager(), "Meal Fragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);


        //set up navbars
        setTopNavBar();
        setBottomNavBar();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        user = gson.fromJson(json, User.class);
        user.setDateofbirth(LocalDate.parse(user.getDobStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        user.setDateCreated(LocalDate.parse(user.getDateCreatedStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Set up Calendar
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(setDate(LocalDate.now()));
        //
        addWeight = findViewById(R.id.addWeight);
        addHeight = findViewById(R.id.addHeight);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currDate = LocalDate.now().format(formatter);
        getDietRecordsFromServer(user, currDate);

        //Set up activity result launcher
        addMealActivityLauncher = registerForActivityResult(
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

    public ActivityResultLauncher<Intent> getAddMealActivityLauncher(){
        return this.addMealActivityLauncher;
    }

    public void setRecords(Context context, HealthRecord myHr){
        if(context != null && myHr != null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    TextView totalCals = findViewById(R.id.total_calories);
                    TextView currentCals = findViewById(R.id.current_calories);
                    TextView bmi = findViewById(R.id.BMI);

                    totalCals.setText("Calorie Limit: " + String.valueOf(user.getCalorieintake_limit_inkcal()));
                    currentCals.setText("Calories consumed: " + String.valueOf(Math.round(myHr.getCalIntake())));

                    Double myBmi = 0.0;
                    myBmi = calculateBMI(myHr);
                    setUserBMI(myBmi, bmi);

                    addHeight.setText(String.format("%.1f", myHr.getUserHeight()) + "cm");
                    addHeight.setEnabled(false);

                    addWeight.setText(String.format("%.1f", myHr.getUserWeight()) + "Kg");
                    addWeight.setEnabled(false);

                    if(LocalDate.now().equals(myHr.getDate())){
                        addWeight.setEnabled(true);
                        addHeight.setEnabled(true);
                        addHeight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String[] heightArr = String.valueOf(myHr.getUserHeight()).split("\\.");
                                int intHeight = Integer.parseInt(heightArr[0]);
                                showDialogForInput("height",intHeight > 0 ? intHeight : 170, myHr, bmi);
                            }
                        });

                        addWeight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String[] arr = String.valueOf(myHr.getUserWeight()).split("\\.");
                                int intWeight = Integer.parseInt(arr[0]);
                                showDialogForInput("weight",intWeight > 0 ? intWeight : 50, myHr, bmi);
                            }
                        });
                    }
                }
            });
        }
    }

    public void showDialogForInput(String action, int value, HealthRecord myHr, TextView v){
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.input_dialog_layout);
        TextView title = d.findViewById(R.id.valueText);
        Button save = d.findViewById(R.id.saveValue);
        Button cancel = d.findViewById(R.id.cancelValue);
        NumberPicker npInt = d.findViewById(R.id.valueInt);
        NumberPicker npDouble = d.findViewById(R.id.valueDouble);
        npInt.setMaxValue(300); // max value 100
        npInt.setMinValue(3);
        npInt.setValue(value);
        npInt.setWrapSelectorWheel(false);
        String[] doubleArr = null;
        if(action.equals("weight")){
            title.setText("Update Your Weight");
            doubleArr = new String[]{".0 Kg", ".1 Kg", ".2 Kg", ".3 Kg", ".4 Kg", ".5 Kg", ".6 Kg", ".7 Kg", ".8 Kg", ".9 Kg"};
        }
        else{
            title.setText("Update Your Height");
            doubleArr = new String[]{".0 cm", ".1 cm", ".2 cm", ".3 cm", ".4 cm", ".5 cm", ".6 cm", ".7 cm", ".8 cm", ".9 cm"};
        }
        npDouble.setMaxValue(9);
        npDouble.setMinValue(0);
        npDouble.setDisplayedValues(doubleArr);
        npDouble.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val1 = npInt.getValue();
                int val2 = npDouble.getValue();
                double height = 0.0;
                double weight = 0.0;
                if(action.equals("weight")){
                    height = myHr.getUserHeight();
                    weight = val1 + val2 / 10.0;
                    updateUser(action, weight);
                    addWeight.setText(String.format("%.1f", weight) + "Kg");
                    Toast.makeText(getApplicationContext(), "Weight updated", Toast.LENGTH_SHORT).show();
                }
                else{
                    weight = myHr.getUserWeight();
                    height = val1 + val2 / 10.0;
                    updateUser(action, height);
                    addHeight.setText(String.format("%.1f", height) + "cm");
                    Toast.makeText(getApplicationContext(), "Height updated", Toast.LENGTH_SHORT).show();
                }
                double b = calculateBMI(weight, height);
                setUserBMI(b, v);
                d.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void updateUser(String action, double value){
        JSONObject postData = new JSONObject();
        try{
            postData.put("username", user.getUsername());
            //postData.put("date", setDate(LocalDate.now()));
            DatePicker datePicker = datePickerDialog.getDatePicker();
            String dateSelect = datePicker.getYear() + "-" + String.format("%02d", (datePicker.getMonth() + 1)) + "-" + String.format("%02d", datePicker.getDayOfMonth());
            //postData.put("date", setDate(LocalDate.now()));
            postData.put("date", dateSelect);
            postData.put(action, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(postData.toString(), JSON);
        Request request = new Request.Builder()
                .url(Constants.javaURL + "/user/update" + action)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code" + response);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                HealthRecord myHr = objectMapper.readValue(responseBody.string(), HealthRecord.class);
                setRecords(getApplicationContext(), myHr);
                responseBody.close();
            }
        });
    }

    private double calculateBMI(HealthRecord myHr){

        if (myHr.getUserWeight() != 0 && myHr.getUserHeight() != 0) {
            return myHr.getUserWeight() / Math.pow(myHr.getUserHeight() / 100, 2.0);
        }
        else
            return 0.0;
    }

    private double calculateBMI(double weight, double height){

        if (weight != 0 && height != 0) {
            return weight / Math.pow(height / 100, 2.0);
        }
        else
            return 0.0;
    }

    private void setUserBMI(double myBmi, TextView bmi){
        if (myBmi == 0) {
            bmi.setText("BMI: N.A");
        } else {
            if(myBmi <= 18.5){
                bmi.setText("BMI: " + String.format("%.1f", myBmi));
                bmi.setTextColor(Color.parseColor("#ffcc00"));
            }
            else if(myBmi > 18.5 && myBmi < 25){
                bmi.setText("BMI: " + String.format("%.1f", myBmi));
                bmi.setTextColor(Color.parseColor("#006400"));
            }
            else if(myBmi >= 25 && myBmi < 30){
                bmi.setText("BMI: " + String.format("%.1f", myBmi));
                bmi.setTextColor(Color.parseColor("#ffcc00"));
            }
            else{
                bmi.setText("BMI: " + String.format("%.1f", myBmi));
                bmi.setTextColor(Color.parseColor("#ff0000"));
            }
        }
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedDate = LocalDate.parse(date, format2);
                dateButton.setText(setDate(parsedDate));

                getDietRecordsFromServer(user, date);
                getHealthRecordFromServer(user, date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) +1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

        // reminder to set min date first before max date for same date issue.
        datePickerDialog.getDatePicker().setMinDate(setCalDate(LocalDate.of(2020, 01, 01)).getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String setDate(LocalDate date) {
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return date.format(format1);
    }

    private Calendar setCalDate(LocalDate date) {
        Calendar cal = Calendar.getInstance();
        int year = date.getYear();
        int month = date.getMonthValue() -1;
        int day = date.getDayOfMonth();

        cal.set(year, month, day);
        return cal;
    }

    public void getDietRecordsFromServer(User user, String date) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            postData.put("date", date);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL + "/user/getdietrecords")
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
                        List<DietRecord> dietRecordList = Arrays.asList(objectMapper.readValue(responseBody.string(), DietRecord[].class));

                        //do something with FM here
                        FragmentManager fm = getSupportFragmentManager();
                        MealButtonsFragment mealFragment = (MealButtonsFragment) fm.findFragmentById(R.id.fragment_meal);
                        mealFragment.setDietRecordList(dietRecordList);

                        responseBody.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getHealthRecordFromServer(User user, String date) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            postData.put("date", date);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL + "/user/gethealthrecorddate")
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
                        setRecords(getApplicationContext(), myHr);
                        responseBody.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void setTopNavBar() {
        mToolbar = findViewById(R.id.top_navbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_nav_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                startProfileActivity();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setBottomNavBar() {
        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setSelectedItemId(R.id.nav_log);
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id) {

                    case (R.id.nav_scanner):
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;  //or should this be finish?

                    case (R.id.nav_search):
                        intent = new Intent(getApplicationContext(), SearchFoodActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
                        break;

                    case (R.id.nav_recipe):
                        intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;

                    case (R.id.nav_home):
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;
                }

                return false;
            }
        });
    }

    private void logout() {
        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();
        startLoginActivity();
    }


    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}