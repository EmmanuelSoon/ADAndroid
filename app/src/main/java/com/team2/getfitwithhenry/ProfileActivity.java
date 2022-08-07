package com.team2.getfitwithhenry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

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
    private EditText mtxtName;
    private EditText mtxtUsername;
    private EditText mtxtCalorieIntake;
    private EditText mtxtWaterIntake;
    private RadioButton mrBtnMale, mrBtnFemale;
    private RadioGroup mRGGenderGrp;
    private Spinner mGoalSelect;
    private DatePickerDialog mdobDatePicker;
    private Button mbtnDob;
    private Button mbtnSaveChanges;
    private String[] goalmatch = {"WEIGHTLOSS", "WEIGHTGAIN", "WEIGHTMAINTAIN", "MUSCLE"};
    private String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    private User user;
    private User updatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mtxtName = findViewById(R.id.txtName);
        mtxtUsername = findViewById(R.id.txtUsername);
        mbtnDob = findViewById(R.id.btnDob);
        mrBtnMale = findViewById(R.id.rBtnMale);
        mrBtnFemale = findViewById(R.id.rBtnFemale);
        mRGGenderGrp = findViewById(R.id.rgGenderGrp);
        mtxtCalorieIntake = findViewById(R.id.txtCalorieIntake);
        mtxtWaterIntake = findViewById(R.id.txtWaterIntake);
        mGoalSelect = findViewById(R.id.goalSelect);
        mbtnSaveChanges = findViewById(R.id.btnSaveProfileChanges);

        mbtnSaveChanges.setOnClickListener(this);

        getUserFromSharedPreference();
        initDatePicker();
        onInitialDataBind();
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

    private void getUserFromSharedPreference() {

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);

        if (pref.contains("userDetails")) {
            Gson gson = new Gson();
            String json = pref.getString("userDetails", "");
            user = gson.fromJson(json, User.class);
            user.setDateofbirth(LocalDate.parse(user.getDobStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            updatedUser = user;
        }

    }

    private void onInitialDataBind() {

        mtxtName.setText(user.getName());
        mtxtUsername.setText(user.getUsername());
        mtxtCalorieIntake.setText(user.getCalorieintake_limit_inkcal().toString());
        mtxtWaterIntake.setText(user.getWaterintake_limit_inml().toString());

        if (user.getGender().toUpperCase().equals("M")) {
            mrBtnMale.setChecked(true);
            mrBtnFemale.setChecked(false);
        } else if (user.getGender().toUpperCase().equals("F")) {
            mrBtnMale.setChecked(false);
            mrBtnFemale.setChecked(true);
        }

        mbtnDob.setText(setDate(user.getDateofbirth()));

        int position = Arrays.asList(goalmatch).indexOf(user.getGoal().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGoalSelect.setAdapter(adapter);
        mGoalSelect.setSelection(position);

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
        RadioButton checkBtn = (RadioButton) findViewById(mRGGenderGrp.getCheckedRadioButtonId());
        String selectedGoal = goalmatch[Arrays.asList(goals).indexOf(mGoalSelect.getSelectedItem().toString())];
        String gender = (checkBtn == mrBtnMale ? "M" : "F");
        String selectedDob = mbtnDob.getText().toString();
        String[] dateArray = selectedDob.split("-");
        selectedDob = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
        LocalDate convertedDate = LocalDate.parse(selectedDob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (mtxtName.getText().toString().equals(user.getName()) &&
                mtxtUsername.getText().toString().equals(user.getUsername()) &&
                mbtnDob.getText().toString().equals(setDate(user.getDateofbirth())) &&
                gender.equals(user.getGender()) &&
                selectedGoal.equals(user.getGoal().toString()) &&
                mtxtCalorieIntake.getText().toString().equals(user.getCalorieintake_limit_inkcal().toString()) &&
                mtxtWaterIntake.getText().toString().equals(user.getWaterintake_limit_inml().toString())) {
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
        userObj.put("calorieintake_limit_inkcal", updatedUser.getCalorieintake_limit_inkcal());
        userObj.put("waterintake_limit_inml", updatedUser.getWaterintake_limit_inml());

        updatUserDetails(userObj);
    }

    @SuppressLint("ResourceType")
    private boolean validateFormFields() {
        if (mtxtName.getText().toString().trim().isEmpty()) {
            mtxtName.setError("Name cannot be empty");
            return false;
        }

        if (mtxtUsername.getText().toString().trim().isEmpty()) {
            mtxtUsername.setError("Username cannot be empty");
            return false;
        }

        //RadioButton checkBtn = (RadioButton) findViewById(mRGGenderGrp.getCheckedRadioButtonId());
        if (mRGGenderGrp.getCheckedRadioButtonId() <= 0) {
            mrBtnFemale.setError("Select gender");
        }

        if (mGoalSelect.getSelectedItem() == null || mGoalSelect.getSelectedItem().toString().trim().isEmpty()) {
            TextView errorText = (TextView) mGoalSelect.getSelectedView();
            errorText.setError("Select goal");
            errorText.setTextColor(Color.RED);
        }

        if (mtxtCalorieIntake.getText().toString().trim().isEmpty()) {
            mtxtCalorieIntake.setError("Calorie limit cannot be empty");
            return false;
        }

        if (mtxtWaterIntake.getText().toString().trim().isEmpty()) {
            mtxtWaterIntake.setError("water limit cannot be empty");
            return false;
        }
        return true;
    }

    private void updatUserDetails(JSONObject userObj) {
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());
        Request request = new Request.Builder().url(Constants.javaURL +"/register/updateUserDetails").post(requestBody).build();

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
                    user = objectMapper.readValue(responseBody.string(), User.class);
                else
                    user = null;

                if (user == null) {
                   // displayValidationError(getApplicationContext(), user);
                }

                if (user != null) {
                    updateUserinSharedPreference(user);
                   // startHomeActivity();
                }
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

}