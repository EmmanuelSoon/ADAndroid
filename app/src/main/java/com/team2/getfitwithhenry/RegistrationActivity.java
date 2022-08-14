package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.Constants;
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

    TextInputLayout mNameLayout, mEmailLayout, mPasswordLayout, mGenderLayout, mGoalLayout, mDobLayout;
    EditText mNameTxt, mEmailTxt, mPasswordTxt, mDobBtn;
    String goal, gender;
    LocalDate dateOfBirth;
    Button mRegisterBtn;
    TextView mReturnLogin;
    String [] genderArr = {"Male", "Female"};
    String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    private DatePickerDialog datePickerDialog;
    private User user;
    private final OkHttpClient client = new OkHttpClient();
    AutoCompleteTextView mGenderSelection;
    AutoCompleteTextView mGoalSelection;
    int min = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        mNameTxt.setOnClickListener(this);
        mEmailTxt.setOnClickListener(this);
        mPasswordTxt.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mReturnLogin.setOnClickListener(this);
        mDobBtn.setOnClickListener(this);
        mNameTxt.addTextChangedListener(nameWatcher);
        mPasswordTxt.addTextChangedListener(passwordWatcher);
        mEmailTxt.addTextChangedListener(emailWatcher);
    }
    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        if(pref.contains("userDetails"))
            startHomeActivity();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        String mName = mNameTxt.getText().toString();
        String mEmail = mEmailTxt.getText().toString();
        String mPassword = mPasswordTxt.getText().toString();
        LocalDate mDob = dateOfBirth;
        String mGender = convertGenderFormat(gender);
        Goal mGoal = getGoal(goal);

        if(id == R.id.registerBtn){
            try{
                seedUser(mName,mEmail,mPassword,mDob,mGender,mGoal);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        else if(id == R.id.returnLogin){
            startLoginActivity();
        }
    }

    private void init(){
        mNameTxt = findViewById(R.id.nameTxt);
        mEmailTxt = findViewById(R.id.emailTxt);
        mPasswordTxt = findViewById(R.id.passwordTxt);
        mDobBtn = findViewById(R.id.dobBtn);
        mDobBtn.setInputType(InputType.TYPE_NULL);
        mDobBtn.setKeyListener(null);
        mGoalSelection =(AutoCompleteTextView) findViewById(R.id.goalSelection);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mReturnLogin = findViewById(R.id.returnLogin);
        mGenderSelection = (AutoCompleteTextView)findViewById(R.id.genderTxt);
        mNameLayout = findViewById(R.id.nameLayout);
        mEmailLayout = findViewById(R.id.emailLayout);
        mPasswordLayout = findViewById(R.id.passwordLayout);
        mGenderLayout = findViewById(R.id.genderLayout);
        mGoalLayout = findViewById(R.id.goalLayout);
        mDobLayout = findViewById(R.id.dobLayout);

        mDobBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    initDatePicker();
                    openDatePicker(mDobBtn);

                    // brute force
                    showErrorMsgIfEmpty(mDobLayout,dateOfBirth,"");
                }
                return false;
            }
        });

//        mGenderSelection.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                    closeKeyboard(mGenderSelection);
//                }
//                return false;
//            }
//        });

        mGenderSelection.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view == mGenderSelection){
                    if(hasWindowFocus()){
                        closeKeyboard(view);
                    }
                }
            }
        });
        mGenderSelection.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                gender = genderArr[i];
                mGenderLayout.setHint("Select Gender");
                showErrorMsgIfEmpty(mGenderLayout,gender, "Please select One*");
            }
        });

        mGoalSelection.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view == mGoalSelection){
                    if(hasWindowFocus()){
                        closeKeyboard(view);
                    }
                }
            }
        });
        mGoalSelection.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                goal = goals[i];
                mGoalLayout.setHint("Select Goal");
                showErrorMsgIfEmpty(mGoalLayout,goal,"Please select One*");
            }
        });

//        for spinner selection
//        mGoalSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                goal = goals[i];
//                Toast.makeText(getApplicationContext(),goals[i],Toast.LENGTH_LONG).show();
//                System.out.println(goal);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        ArrayAdapter<String> genderAd = new ArrayAdapter(this, R.layout.drop_down_gender, genderArr);
        genderAd.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        mGenderSelection.setAdapter(genderAd);

        ArrayAdapter ad = new ArrayAdapter(this, R.layout.drop_down_goal, goals);
        mGoalSelection.setAdapter(ad);

    }

    private boolean validation(String name, String email, String password, LocalDate dob, String gender, Goal goal) throws JSONException {

        String countError = "Please enter between "+min+" and 20 characters!";
        String msg = "Please select One*";

        showWordCountError(name, min, mNameLayout, countError );
        showWordCountError(email, min, mEmailLayout, countError );
        showWordCountError(password, min, mPasswordLayout, countError );
        showErrorMsgIfEmpty(mGenderLayout,gender, msg);
        showErrorMsgIfEmpty(mGoalLayout,goal,msg);
        showErrorMsgIfEmpty(mDobLayout,dob,msg);

        if(email.length() >= min ){
            if(!isValidEmail(email)){
                mEmailLayout.setHelperText("Must be valid email address");
            }
            else{
                mEmailLayout.setHelperText("");
            }
        }

        if((name.length() < min)  ||
                (email.length() < min) ||
                (password.length() < min) ||
                gender.isEmpty() || dob == null || goal == null || (!isValidEmail(email))){
            return false;
        }
        return true;
    }

    // remark max length is specified in xml, so there is no need to check for that
    private void wordCountError(EditText sentence, int min, TextInputLayout layout, String error){
        if(sentence.getText().length() < min ){
            layout.setHelperText(error);
//            layout.setCounterEnabled(true);
//            layout.setCounterMaxLength(max);
        }
        else{
            layout.setHelperText("");
//            layout.setCounterEnabled(false);
        }
    }

    private void showWordCountError(String sentence, int min, TextInputLayout layout, String error){
        if(sentence.length() < min){
            layout.setHelperText(error);
        }
        else{
            layout.setHelperText("");
        }
    }

    private void showErrorMsgIfEmpty(TextInputLayout layout, String field, String error){
        if(field.isEmpty())
            layout.setHelperText(error);
        else
            layout.setHelperText("");
    }

    private void showErrorMsgIfEmpty(TextInputLayout layout, LocalDate date, String error){
        if(date==null)
            layout.setHelperText(error);
        else
            layout.setHelperText("");
    }

    private void showErrorMsgIfEmpty(TextInputLayout layout, Goal goal, String error){
        if(goal==null)
            layout.setHelperText(error);
        else
            layout.setHelperText("");
    }

    private void seedUser(String name, String email, String password, LocalDate dob, String gender, Goal goal) throws JSONException{

        if(validation(name, email, password, dob, gender, goal)){
            //Creating new user
            user = new User(name, email, password, dob, gender, goal);

            JSONObject userObj = new JSONObject();
            userObj.put("name", user.getName());
            userObj.put("email", user.getUsername());
            userObj.put("password", user.getPassword());
            userObj.put("role", user.getRole());
            userObj.put("dob", user.getDateofbirth());
            userObj.put("gender", user.getGender());
            userObj.put("goal", user.getGoal());

            validateUserFromDetails(userObj);
        }
    }

    private boolean isValidEmail(String email){
        String mailFormat =  "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(mailFormat);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    private void validateUserFromDetails(JSONObject userObj){
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());

        Request request = new Request.Builder().url(Constants.javaURL +"/register/validateNewUser").post(requestBody).build();

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

                if(user == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEmailLayout.setHelperText("Username is already existed");
                        }
                    });
                }
                else{
                    storeUserinSharedPreference(user);
                    startQuestionnaireActivity();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Welcome On Board!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                response.body().close();
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

//    public void onRadioButtonClicked(View view){
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch (view.getId()){
//            case R.id.radio_male:
//                if(checked)
//                    gender = "M";
//                break;
//            case R.id.radio_female:
//                if(checked)
//                    gender = "F";
//                break;
//
//            default: gender = "";
//        }
//    }
    private Goal getGoal(String goal) {
        if(goal == null)
            return null;
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
                return null;
        }
    }

    private String convertGenderFormat(String gender){
        if(gender == "Male")
            return "M";
        else if(gender == "Female")
            return "F";
        else
            return "";
    }

    private void closeKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startQuestionnaireActivity(){
        Intent intent = new Intent(this, QuestionnaireActivity.class);
        startActivity(intent);
    }

    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String countError = "Please enter between "+min+" and 20 characters!";
            wordCountError(mNameTxt, min, mNameLayout, countError);
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String countError = "Please enter between "+min+" and 20 characters!";
            wordCountError(mEmailTxt,min,mEmailLayout, countError);
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String countError = "Please enter between "+min+" and 20 characters!";
            wordCountError(mPasswordTxt, min, mPasswordLayout, countError);
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}