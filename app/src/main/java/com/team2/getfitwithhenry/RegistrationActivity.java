package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEmailTxt, mPasswordTxt, mConfirmPasswordTxt;
    RadioButton mRadioMale, mRadioFemale;
    Spinner mDobSelection;
    Spinner mGoalSelection;
    Button mRegisterBtn;
    TextView mReturnLogin;
    String[] goals = {"Weight Loss", "Weight Gain", "Weight Maintain", "Muscle"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

//        mDobSelection.setOnItemSelectedListener(this);

        mEmailTxt.setOnClickListener(this);
        mPasswordTxt.setOnClickListener(this);
        mConfirmPasswordTxt.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mReturnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        if(id == R.id.registerBtn){
            startHomeActivity();

        }
        else if(id == R.id.returnLogin){
            startLoginActivity();
        }
    }


    private void init(){
        mEmailTxt = findViewById(R.id.emailTxt);
        mPasswordTxt = findViewById(R.id.passwordTxt);
        mConfirmPasswordTxt = findViewById(R.id.confirmPasswordTxt);
        mRadioMale = findViewById(R.id.radio_male);
        mRadioFemale = findViewById(R.id.radio_female);
        mDobSelection = findViewById(R.id.dobSelection);
        mGoalSelection = findViewById(R.id.goalSelection);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mReturnLogin = findViewById(R.id.returnLogin);

        mGoalSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),goals[i],Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, goals);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGoalSelection.setAdapter(ad);
    }
    private boolean checkValidation(){
        return true;
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