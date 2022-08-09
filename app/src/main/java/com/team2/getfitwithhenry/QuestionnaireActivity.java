package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QuestionnaireActivity extends AppCompatActivity implements View.OnClickListener {

    private final OkHttpClient client = new OkHttpClient();
    private TextView mtxtName, mtxtSomethingWentWrong;
    private EditText mtxtUserWeight, mtxtUserHeight;
    private Spinner mGoalSelect;
    private Button mbtnSaveHealthDetails;
    private String[] goalmatch = {"WEIGHTLOSS", "WEIGHTGAIN", "WEIGHTMAINTAIN", "MUSCLE"};
    private String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    private User user;
    private User updatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        mtxtName = findViewById(R.id.txtNameQuestionnaire);
        mtxtUserWeight = findViewById(R.id.txtUserWeight);
        mtxtUserHeight = findViewById(R.id.txtUserHeight);
        mGoalSelect = findViewById(R.id.goalSelectQuestionnaire);
        mbtnSaveHealthDetails = findViewById(R.id.btnSaveHealthDetails);
        mtxtSomethingWentWrong = findViewById(R.id.txtSomethingWentWrong);

        mbtnSaveHealthDetails.setOnClickListener(this);

        getUserFromSharedPreference();
        onInitialDataBind();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSaveHealthDetails) {
            try {
                if (!validateFields()) {
                    setDetailstoUpdate();
                    createUserJsonObj();
                }

            } catch (JSONException e) {
                e.printStackTrace();
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

        int position = Arrays.asList(goalmatch).indexOf(user.getGoal().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGoalSelect.setAdapter(adapter);
        mGoalSelect.setSelection(position);
        mGoalSelect.setEnabled(false);

    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void setDetailstoUpdate() {
        String gender = "M";

        updatedUser.setId(user.getId());
        updatedUser.setName(mtxtName.getText().toString());
        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setDateofbirth(user.getDateofbirth());
        updatedUser.setGender(user.getGender());
        updatedUser.setGoal(user.getGoal());
        updatedUser.setCalorieintake_limit_inkcal(Double.parseDouble(performCalculation(updatedUser.getGender(), mtxtUserWeight.getText().toString(), mtxtUserHeight.getText().toString())));
        updatedUser.setWaterintake_limit_inml(updatedUser.getGender().equals(gender) ? 3700.0 : 2700.0);
    }

    private String performCalculation(String gender, String userWeight, String userHeight) {
        Double calculatedCalorie = 0.0;

        if (gender.equals("M")) {
            //13.397W + 4.799H - 5.677A + 88.362
            calculatedCalorie = Math.ceil((13.397 * Double.parseDouble(userWeight)) + (4.799 * Double.parseDouble(userHeight)) - (5.667 * calculateAge()) + 88.362);
        } else if (gender.equals("F")) {
            //9.247W + 3.098H - 4.330A + 447.593
            calculatedCalorie = Math.ceil((9.247 * Double.parseDouble(userWeight)) + (3.098 * Double.parseDouble(userHeight)) - (4.330 * calculateAge()) + 447.593);
        }

        return calculatedCalorie.toString();
    }

    private Integer calculateAge() {
        LocalDate currDate = LocalDate.now();
        return Period.between(updatedUser.getDateofbirth(), currDate).getYears();
    }

    private void createUserJsonObj() throws JSONException {
        JSONObject userHealthObj = new JSONObject();

        userHealthObj.put("id", updatedUser.getId());
        userHealthObj.put("name", updatedUser.getName());
        userHealthObj.put("username", updatedUser.getUsername());
        userHealthObj.put("password", updatedUser.getPassword());
        userHealthObj.put("dateofbirth", updatedUser.getDateofbirth());
        userHealthObj.put("gender", updatedUser.getGender());
        userHealthObj.put("goal", updatedUser.getGoal());
        userHealthObj.put("calorieintake_limit_inkcal", updatedUser.getCalorieintake_limit_inkcal());
        userHealthObj.put("waterintake_limit_inml", updatedUser.getWaterintake_limit_inml());
        userHealthObj.put("user_height", Double.parseDouble(mtxtUserHeight.getText().toString()));
        userHealthObj.put("user_weight", Double.parseDouble(mtxtUserWeight.getText().toString()));
        userHealthObj.put("date", LocalDate.now());

        inserHealthRecordonRegistration(userHealthObj);
    }

    private void inserHealthRecordonRegistration(JSONObject userObj) {
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());
        Request request = new Request.Builder().url(Constants.javaURL + "/questionnaire/insertHealthRecordonRegistration").post(requestBody).build();

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
                    somethingWentWrongError(getApplicationContext(), user);
                }

                if (user != null) {
                    updateUserinSharedPreference(user);
                    startHomeActivity();
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

    private boolean validateFields()
    {
        if(mtxtUserWeight.getText().toString().trim().equals("") || Double.parseDouble(mtxtUserWeight.getText().toString()) <= 0)
        {
            mtxtUserWeight.setError("Weight should be greater than 0");
            return true;
        }

        if(mtxtUserHeight.getText().toString().trim().equals("") || Double.parseDouble(mtxtUserHeight.getText().toString()) <= 0)
        {
            mtxtUserHeight.setError("Height should be greater than 0");
            return true;
        }

        return false;
    }

    private void somethingWentWrongError(Context context, User user) {

        if (context != null && user != null) {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show());
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {

                mtxtSomethingWentWrong.setText("Something Went Wrong. Please try again!");
                mtxtSomethingWentWrong.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            });
        }

    }
}