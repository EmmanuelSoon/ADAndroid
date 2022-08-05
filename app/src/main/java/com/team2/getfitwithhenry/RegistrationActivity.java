package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEmailTxt, mPasswordTxt, mConfirmPasswordTxt;
    String gender = null;
    String goal; LocalDate dateOfBirth;
    Button mDobBtn;
    Spinner mGoalSelection;
    Button mRegisterBtn;
    TextView mReturnLogin, mValdiationErrorText;
    String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    private DatePickerDialog datePickerDialog;
    private User user;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        mEmailTxt.setOnClickListener(this);
        mPasswordTxt.setOnClickListener(this);
        mConfirmPasswordTxt.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mReturnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        String mEmail = mEmailTxt.getText().toString();
        String mPassword = mPasswordTxt.getText().toString();
        String mConfirmPassword = mConfirmPasswordTxt.getText().toString();
        LocalDate mDob = dateOfBirth;
        Goal mGoal = getGoal(goal);

        if(id == R.id.registerBtn){
            try{
                if(validationAndSeeding(mEmail,mPassword,mConfirmPassword,mDob,gender,mGoal)){
                    Toast.makeText(getApplicationContext(),"Welcome On Board!",Toast.LENGTH_LONG).show();
                    startHomeActivity();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        else if(id == R.id.returnLogin){
            startLoginActivity();
        }
    }

    private void init(){
        mEmailTxt = findViewById(R.id.emailTxt);
        mPasswordTxt = findViewById(R.id.passwordTxt);
        mConfirmPasswordTxt = findViewById(R.id.confirmPasswordTxt);
        mDobBtn = findViewById(R.id.dobBtn);
        mGoalSelection = findViewById(R.id.goalSelection);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mReturnLogin = findViewById(R.id.returnLogin);
        mValdiationErrorText = findViewById(R.id.registerValidationTxt);

        initDatePicker();

        mGoalSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                goal = goals[i];
//                Toast.makeText(getApplicationContext(),goals[i],Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, goals);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGoalSelection.setAdapter(ad);
    }
    private boolean validationAndSeeding(String email, String password, String confirmPassword
        , LocalDate dob, String gender, Goal goal) throws JSONException {

        String msg = "";

        if(email.isEmpty()|| password.isEmpty() || confirmPassword.isEmpty() || gender==null || dob==null){
            msg = "All Fields Should Be Filled";
            showErrorMsg(msg);
            return false;
        }
        else if(!isValidEmail(email)){
            msg = "Must be valid email address";
            showErrorMsg(msg);
            return false;
        }
        else if(!password.equals(confirmPassword)){
            msg = "Password Didn't Match";
            showErrorMsg(msg);
            return false;
        }

        //Creating new user
         user = new User(email, password, dob, gender, goal);

        JSONObject userObj = new JSONObject();
        userObj.put("email", user.getUsername());
        userObj.put("password", user.getPassword());
        userObj.put("dob", user.getDateofbirth());
        userObj.put("gender", user.getGender());
        userObj.put("goal", user.getGoal());

        validateUserFromDetails(userObj);
        if(user == null){
            msg = "Username is already existed";
            showErrorMsg(msg);
            return false;
        }
        else{
            storeUserinSharedPreference(user);
        }

        return true;
    }

    private boolean isValidEmail(String email){
        String mailFormat =  "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(mailFormat);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches())
            return true;
        return false;
    }

    private void showErrorMsg(String msg){
        if(msg != null){
            mValdiationErrorText.setText(msg);
            mValdiationErrorText.setVisibility(View.VISIBLE);
        }
    }

    private void validateUserFromDetails(JSONObject userObj){
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());

        Request request = new Request.Builder().url("http://172.27.64.1:8080/register/validateNewUser").post(requestBody).build();

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
            }
        });
    }
    private void storeUserinSharedPreference(User user) {
        //https://stackoverflow.com/questions/7145606/how-do-you-save-store-objects-in-sharedpreferences-on-android
        //Check the above url to retrieve the object from shared pref
        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("userDetails", json);
        editor.commit();

    }
    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dateOfBirth = LocalDate.of(year, month, day);
                String date = makeDateString(day, month, year);
                mDobBtn.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
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
        return "JAN";
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.radio_male:
                if(checked)
                    gender = "M";
                break;
            case R.id.radio_female:
                if(checked)
                    gender = "F";
                break;
            default: gender = "";
        }
    }
    private Goal getGoal(String goal) {
        switch (goal) {
            case "Weight Loss":
                return Goal.WEIGHTLOSS;
            case "Weight Gain":
                return Goal.WEIGHTGAIN;
            case "Weight Maintain":
                return Goal.WEIGHTMAINTAIN;
            case "Muscle":
                return Goal.MUSCLE;

            // should not come to this default
            default:
                return Goal.WEIGHTMAINTAIN;
        }
    }

    private void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}