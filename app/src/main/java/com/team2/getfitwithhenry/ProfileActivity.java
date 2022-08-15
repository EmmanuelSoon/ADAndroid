package com.team2.getfitwithhenry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.User;
import com.team2.getfitwithhenry.model.UserWithWeightHeight;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final OkHttpClient client = new OkHttpClient();
    private Toolbar mToolbar;
    private BottomNavigationView bottomNavView;
    TextInputLayout mNameLayout, mEmailLayout, mGenderLayout, mGoalLayout, mDobLayout, mactivityLayout, mweightLayout, mheightLayout, mcalorieLimitLayout, mwaterLimitLayout;
    private EditText mtxtName, mtxtUsername, mtxtCalorieIntake, mtxtWaterIntake, mtxtWeight, mtxtHeight, mbtnDob;
    private AutoCompleteTextView mGoalSelect, mactivityLevelSelector, mgenderSelector;
    private DatePickerDialog mdobDatePicker;
    private ImageButton mbtnSaveChanges;
    private String goalSelction, activitylevelSelction, genderSelection;
    private String[] goalmatch = {"WEIGHTLOSS", "WEIGHTGAIN", "WEIGHTMAINTAIN", "MUSCLE"};
    private String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    private String[] activityLevels = {"Lightly Active", "Moderately Active", "Very Active", "Extra Active"};
    String[] genderArr = {"Male", "Female"};
    private UserWithWeightHeight userWithWeightHeight;
    private User user;
    private User updatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTopNavBar();
        setBottomNavBar();

        mNameLayout = findViewById(R.id.nameLayout);
        mtxtName = findViewById(R.id.txtName);

        mEmailLayout = findViewById(R.id.emailLayout);
        mtxtUsername = findViewById(R.id.txtUsername);

        mDobLayout = findViewById(R.id.dobLayout);
        mbtnDob = findViewById(R.id.btnDob);
        mbtnDob.setInputType(InputType.TYPE_NULL);
        mbtnDob.setKeyListener(null);

        mGoalLayout = findViewById(R.id.goalLayout);
        mGoalSelect = findViewById(R.id.goalSelect);

        mactivityLayout = findViewById(R.id.activityLayout);
        mactivityLevelSelector = findViewById(R.id.continuous_slider);

        mweightLayout = findViewById(R.id.weightLayout);
        mtxtWeight = findViewById(R.id.userWeight);

        mheightLayout = findViewById(R.id.heightLayout);
        mtxtHeight = findViewById(R.id.userHeight);

        mGenderLayout = findViewById(R.id.genderLayout);
        mgenderSelector = findViewById(R.id.genderTxt);

        mcalorieLimitLayout = findViewById(R.id.calorieLimitLayout);
        mtxtCalorieIntake = findViewById(R.id.txtCalorieIntake);

        mwaterLimitLayout = findViewById(R.id.waterLimitLayout);
        mtxtWaterIntake = findViewById(R.id.txtWaterIntake);

        mbtnSaveChanges = findViewById(R.id.btnSaveProfileChanges);

        mtxtName.setOnClickListener(this);
        mtxtUsername.setOnClickListener(this);
        mtxtWeight.setOnClickListener(this);
        mtxtHeight.setOnClickListener(this);
        mbtnDob.setOnClickListener(this);
        mbtnSaveChanges.setOnClickListener(this);

        initEventListeners();

        getUserFromSharedPreference();
        try {
            getUserDetailswithHeightandWeight();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setTopNavBar() {
        mToolbar = findViewById(R.id.top_navbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    public void setBottomNavBar() {
        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setSelected(false);
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id) {

                    case (R.id.nav_scanner):
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        break;  //or should this be finish?

                    case (R.id.nav_search):
                        intent = new Intent(getApplicationContext(), SearchFoodActivity.class);
                        startActivity(intent);
                        break;

                    case (R.id.nav_recipe):
                        intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        startActivity(intent);
                        break;

                    case (R.id.nav_home):
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        break;

                    case (R.id.nav_log):
                        intent = new Intent(getApplicationContext(), LoggerActivity.class);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });
    }

    private void initEventListeners() {
        mbtnDob.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                initDatePicker();
                openDatePicker(mbtnDob);
            }
            return false;
        });

        mgenderSelector.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view == mgenderSelector) {
                    if (hasWindowFocus()) {
                        closeKeyboard(view);
                    }
                }

            }
        });

        mgenderSelector.setOnItemClickListener((adapterView, view, i, l) -> {
            genderSelection = genderArr[i];
            mGenderLayout.setHint("Select Gender");
            //showErrorMsgIfEmpty(mGenderLayout,gender, "Please select One*");
        });

        mGoalSelect.setOnFocusChangeListener((view, b) -> {
            if (view == mGoalSelect) {
                if (hasWindowFocus()) {
                    closeKeyboard(view);
                }
            }
        });
        mGoalSelect.setOnItemClickListener((adapterView, view, i, l) -> {
            goalSelction = goals[i];
            mGoalLayout.setHint("Select Goal");
            //showErrorMsgIfEmpty(mGoalLayout,goal,"Please select One*");
        });

        mactivityLevelSelector.setOnFocusChangeListener((view, b) -> {
            if (view == mactivityLevelSelector) {
                if (hasWindowFocus()) {
                    closeKeyboard(view);
                }
            }
        });
        mactivityLevelSelector.setOnItemClickListener((adapterView, view, i, l) -> {
            activitylevelSelction = activityLevels[i];
            mactivityLayout.setHint("Select Activity");
            //showErrorMsgIfEmpty(mGoalLayout,goal,"Please select One*");
        });

        arrayAddapterSetter();
    }

    private void arrayAddapterSetter() {
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, R.layout.drop_down_goal, goals);
        mGoalSelect.setAdapter(goalAdapter);

        ArrayAdapter<String> activitylevelAdapter = new ArrayAdapter<>(this, R.layout.drop_down_goal, activityLevels);
        mactivityLevelSelector.setAdapter(activitylevelAdapter);

        ArrayAdapter<String> genderAd = new ArrayAdapter(this, R.layout.drop_down_gender, genderArr);
        genderAd.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        mgenderSelector.setAdapter(genderAd);
    }

    private void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void getUserFromSharedPreference() {
        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        user = gson.fromJson(json, User.class);
        user.setDateofbirth(LocalDate.parse(user.getDobStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        user.setDateCreated(LocalDate.parse(user.getDateCreatedStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

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
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSaveProfileChanges) {

            if (validateFormFields()) {
                checkIfDetailsChanged();
                try {
                    createUserJsonObj();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onInitialDataBind() {

        int position = Arrays.asList(goalmatch).indexOf(user.getGoal().name());

        mtxtName.setText(user.getName());
        mtxtUsername.setText(user.getUsername());
        mtxtCalorieIntake.setText(user.getCalorieintake_limit_inkcal().toString());
        mtxtWaterIntake.setText(user.getWaterintake_limit_inml().toString());
        mtxtWeight.setText(userWithWeightHeight.getUserWeight().toString());
        mtxtHeight.setText(userWithWeightHeight.getUserHeight().toString());
        mbtnDob.setText(setDate(user.getDateofbirth()));
        mGoalSelect.setText(goals[position], false);
        mactivityLevelSelector.setText(user.getActivitylevel(), false);
        mgenderSelector.setText((user.getGender().equals("M") ? "Male" : "Female"), false);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = String.format("%02d", day) + "-" + String.format("%02d", month) + "-" + year;
            DateTimeFormatter format2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(date, format2);
            mbtnDob.setText(setDate(parsedDate));
        };

        //Calendar cal = Calendar.getInstance();
        int year = user.getDateofbirth().getYear();//cal.get(Calendar.YEAR);
        int month = user.getDateofbirth().getMonthValue() - 1;//cal.get(Calendar.MONTH);
        int day = user.getDateofbirth().getDayOfMonth();//cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;


        mdobDatePicker = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        mdobDatePicker.getDatePicker().setMaxDate(new Date().getTime());
    }

    private String setDate(LocalDate date) {
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(format1);
    }

    public void openDatePicker(View view) {
        mdobDatePicker.show();
    }

    private void checkIfDetailsChanged() {

        int position = Arrays.asList(goals).indexOf(mGoalSelect.getText().toString());
        String selectedGoal = goalmatch[position];
        genderSelection = mgenderSelector.getText().toString();
        String gender = (genderSelection.equals("Male") ? "M" : "F");
        String selectedDob = mbtnDob.getText().toString();
        String[] dateArray = selectedDob.split("-");
        selectedDob = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
        LocalDate convertedDate = LocalDate.parse(selectedDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        activitylevelSelction = mactivityLevelSelector.getText().toString();


        if (mtxtName.getText().toString().equals(user.getName()) &&
                mtxtUsername.getText().toString().equals(user.getUsername()) &&
                mbtnDob.getText().toString().equals(setDate(user.getDateofbirth())) &&
                gender.equals(user.getGender()) &&
                selectedGoal.equals(user.getGoal().toString()) &&
                mtxtCalorieIntake.getText().toString().equals(user.getCalorieintake_limit_inkcal().toString()) &&
                mtxtWaterIntake.getText().toString().equals(user.getWaterintake_limit_inml().toString()) &&
                activitylevelSelction.equals(user.getActivitylevel()) &&
                mtxtWeight.getText().toString().equals(userWithWeightHeight.getUserWeight().toString()) &&
                mtxtHeight.getText().toString().equals(userWithWeightHeight.getUserHeight().toString())) {
            Toast.makeText(this,
                    "No details changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Details Changed", Toast.LENGTH_SHORT).show();
            //Setting value to be passed over for saving
            updatedUser.setId(user.getId());
            updatedUser.setName(mtxtName.getText().toString());
            updatedUser.setUsername(mtxtUsername.getText().toString());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setDateofbirth(convertedDate);
            updatedUser.setGender(gender);
            updatedUser.setGoal(Goal.valueOf(selectedGoal));
            updatedUser.setActivitylevel(activitylevelSelction);
            updatedUser.setCalorieintake_limit_inkcal(Double.parseDouble(mtxtCalorieIntake.getText().toString()));
            updatedUser.setWaterintake_limit_inml(Double.parseDouble(mtxtWaterIntake.getText().toString()));

        }

    }

    private void createUserJsonObj() throws JSONException {
        JSONObject userObj = new JSONObject();

        userObj.put("id", updatedUser.getId());
        userObj.put("name", updatedUser.getName());
        userObj.put("username", updatedUser.getUsername());
        userObj.put("password", updatedUser.getPassword());
        userObj.put("dateofbirth", updatedUser.getDateofbirth());
        userObj.put("gender", updatedUser.getGender());
        userObj.put("goal", updatedUser.getGoal());
        userObj.put("activitylevel", updatedUser.getActivitylevel());
        userObj.put("calorieintake_limit_inkcal", updatedUser.getCalorieintake_limit_inkcal());
        userObj.put("waterintake_limit_inml", updatedUser.getWaterintake_limit_inml());
        userObj.put("userWeight", mtxtWeight.getText().toString());
        userObj.put("userHeight", mtxtHeight.getText().toString());

        updatUserDetails(userObj);
    }

    @SuppressLint("ResourceType")
    private boolean validateFormFields() {
        if (mtxtName.getText().toString().trim().isEmpty()) {
            mNameLayout.setHelperText("required*");
            return false;
        }

        if (mtxtUsername.getText().toString().trim().isEmpty()) {
            mEmailLayout.setHelperText("required");
            return false;
        }

        String userNameFormat = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(userNameFormat);
        if (!pattern.matcher(mtxtUsername.getText().toString()).matches()) {
            mEmailLayout.setHelperText("Invalid Email Format");
            return false;
        }

        if (mtxtCalorieIntake.getText().toString().trim().isEmpty()) {
            mcalorieLimitLayout.setHelperText("required*");
            return false;
        }

        if (mtxtWaterIntake.getText().toString().trim().isEmpty()) {
            mwaterLimitLayout.setHelperText("required*");
            return false;
        }

        if (mtxtWeight.getText().toString().trim().isEmpty()) {
            mweightLayout.setHelperText("required*");
        }

        if (mtxtHeight.getText().toString().trim().isEmpty()) {
            mheightLayout.setHelperText("required*");
        }

        return true;
    }

    private void updatUserDetails(JSONObject userObj) {
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());
        Request request = new Request.Builder().url(Constants.javaURL + "/userprofile/updateUserDetails").post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                if (responseBody.contentLength() != 0)
                    userWithWeightHeight = objectMapper.readValue(responseBody.string(), UserWithWeightHeight.class);
                else
                    displayprofileInvalidError(getApplicationContext());

                if (userWithWeightHeight != null) {
                    updatedUser = user = userWithWeightHeight.getUser();
                }

                if (user != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUserinSharedPreference(user);
                            onInitialDataBind();
                        }
                    });

                }
            }
        });
    }

    private void getUserDetailswithHeightandWeight() throws JSONException {

        JSONObject getUserObj = new JSONObject();
        getUserObj.put("id", user.getId());

        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, getUserObj.toString());
        Request request = new Request.Builder().url(Constants.javaURL + "/userprofile/getUserDetailswithHeightandWeight").post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                if (responseBody.contentLength() != 0)
                    userWithWeightHeight = objectMapper.readValue(responseBody.string(), UserWithWeightHeight.class);
                else
                    userWithWeightHeight = null;

                if (userWithWeightHeight != null) {
                    updatedUser = user = userWithWeightHeight.getUser();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onInitialDataBind();
                        }
                    });
                }

                if (userWithWeightHeight == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                    });
                }


                response.body().close();
            }
        });

    }

    private void updateUserinSharedPreference(User user) {
        //https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android
        //Check the above url to retrieve the object from shared pref
        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("userDetails", json);
        editor.commit();
    }

    private void displayprofileInvalidError(Context context) {

        new Handler(Looper.getMainLooper()).post(() -> {
            mEmailLayout.setHelperText("Username found. Enter different one!");
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
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

}