package com.team2.getfitwithhenry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLoginBtn;
    private EditText mUsernameTxt;
    private EditText mPasswordTxt;
    private TextView mValidationErrorText;
    private final OkHttpClient client = new OkHttpClient();
    private User user;
    private TextView mForgotPasswordTxt;
    private TextView mNewUserTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameTxt = findViewById(R.id.txtUsername);
        mPasswordTxt = findViewById(R.id.txtPassword);
        mLoginBtn = findViewById(R.id.loginBtn);
        mValidationErrorText = findViewById(R.id.validationErrorText);
        mForgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);
        mNewUserTxt = findViewById(R.id.newUserTxt);


        mUsernameTxt.addTextChangedListener(loginWatcher);
        mPasswordTxt.addTextChangedListener(loginWatcher);
        mLoginBtn.setOnClickListener(this);
        mForgotPasswordTxt.setOnClickListener(this);
        mNewUserTxt.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        if (pref.contains("userDetails"))
            startHomeActivity();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loginBtn) {
            try {
                validateUser(mUsernameTxt.getText().toString(), mPasswordTxt.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.forgotPasswordTxt) {
            startRegistrationActivity();
        } else if (id == R.id.newUserTxt) {
            startRegistrationActivity();
        }
    }

    private TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Boolean isUsernameEmpty = (mUsernameTxt.getText().toString().trim().isEmpty());
            Boolean isPasswordEmpty = (mPasswordTxt.getText().toString().trim().isEmpty());

            mValidationErrorText.setText(" ");
            mValidationErrorText.setVisibility(View.INVISIBLE);

            mLoginBtn.setEnabled(!mUsernameTxt.getText().toString().trim().isEmpty() && !mPasswordTxt.getText().toString().trim().isEmpty());

            if (isUsernameEmpty) {
                mUsernameTxt.setError("Username cannot be empty");
            }

            if (isPasswordEmpty) {
                mPasswordTxt.setError("Password cannot be empty");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void validateUser(String username, String password) throws JSONException {
        User validUser = new User(username, password);

        JSONObject userObj = new JSONObject();
        userObj.put("username", validUser.getUsername());
        userObj.put("password", validUser.getPassword());

        validateUserFromDetails(userObj);
    }

    private void validateUserFromDetails(JSONObject userObj) {
        MediaType JsonObj = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JsonObj, userObj.toString());

        Request request = new Request.Builder().url("http://192.168.10.122:8080/login/validateUserDetails").post(requestBody).build();

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
                    displayValidationError(getApplicationContext(), user);
                }

                if (user != null) {
                    storeUserinSharedPreference(user);
                    startHomeActivity();
                }
            }
        });
    }

    private void displayValidationError(Context context, User user) {

        if (context != null && user != null) {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show());
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {

                mValidationErrorText.setText("Invalid Username/Password");
                mValidationErrorText.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            });
        }

    }

    private void startRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void startForgotPasswordActivity() {
        //logic haven't implemented yet
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

}