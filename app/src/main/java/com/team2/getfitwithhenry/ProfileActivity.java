package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.OkHttpClient;

public class ProfileActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mtxtName = findViewById(R.id.txtName);
        mtxtUsername = findViewById(R.id.txtUsername);
        mbtnDob = findViewById(R.id.btnDob);
        mrBtnMale = findViewById(R.id.rBtnMale);
        mrBtnFemale = findViewById(R.id.rBtnFemale);
        mtxtCalorieIntake = findViewById(R.id.txtCalorieIntake);
        mtxtWaterIntake = findViewById(R.id.txtWaterIntake);
        mGoalSelect = findViewById(R.id.goalSelect);
        mbtnSaveChanges = findViewById(R.id.btnSaveChanges);

        getUserFromSharedPreference();
        initDatePicker();
        onInitialDataBind();
    }

    private void getUserFromSharedPreference() {

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);

        if (pref.contains("userDetails")) {
            Gson gson = new Gson();
            String json = pref.getString("userDetails", "");
            user = gson.fromJson(json, User.class);
            user.setDateofbirth(LocalDate.parse(user.getDobStringFormat(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
    }

    private String setDate(LocalDate date) {
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(format1);
    }

    public void openDatePicker(View view) {
        mdobDatePicker.show();
    }
}