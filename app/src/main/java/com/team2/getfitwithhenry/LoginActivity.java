package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginBtn;
    private EditText mUsernameTxt;
    private EditText mPasswordTxt;
    private final OkHttpClient client = new OkHttpClient();
    private User u;
    private String pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mUsernameTxt = findViewById(R.id.txtUsername);
        mPasswordTxt = findViewById(R.id.txtPassword);
        mLoginBtn =  findViewById(R.id.loginBtn);

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        if(pref.contains("username") && pref.contains("password")){
            boolean loginOk = login(pref.getString("username", ""),
                    pref.getString("password",""));
            if(loginOk){
                startHomeActivity();
            }
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                String username = mUsernameTxt.getText().toString();
                String password = mPasswordTxt.getText().toString();
                u = new User(username, password);
                getUserDetailsFromServer(u);

//                if(login(username, password)){
//                    System.out.println("success");
//                    SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString("username", username);
//                    editor.putString("password", password);
//                    editor.commit();
//
//                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
//                    startHomeActivity();
//                }
//                else{
//                    System.out.println("unsuccess");
//                    Toast.makeText(getApplicationContext(), "Invalid login", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private boolean login(String username, String password){
        getUserDetailsFromServer(u);

        if(password.equals(pass)){
            return true;
        }
        return false;
    }
    private void getUserDetailsFromServer(User user){
        JSONObject postData = new JSONObject();
        try{
            postData.put("username", user.getUsername());

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            Request request = new Request.Builder()
                    .url("http://172.29.208.1:8080/login/authentication")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback(){
                @Override
                public void onFailure(Call call, IOException e){
                    System.out.println("failure");
                    e.printStackTrace(); }

                @Override
                public void onResponse(Call call, Response response){
                    try{
                        ResponseBody responseBody = response.body();
                        if(!response.isSuccessful()){
                            throw new IOException("Unexpected code " + response);
                        }

                        String pass1 = String.valueOf(responseBody);
                        displayResponse(getApplicationContext(), pass1);

//                        ObjectMapper objectMapper = new ObjectMapper();
//                        pass = objectMapper.readValue(responseBody.string(), User[].class.toString());
                    }catch(Exception e){
                        System.out.println("in response error");
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(JSONException e){
            System.out.println("in Json error");

            e.printStackTrace();
        }
    }

    public void displayResponse(final Context context, final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    String toastMsg = "Null";
                    if(msg != null){
                        String toastmsg = msg;
                    }
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}