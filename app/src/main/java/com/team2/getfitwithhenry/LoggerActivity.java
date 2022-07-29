package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

public class LoggerActivity extends AppCompatActivity {

    private User tempUser;
    private final OkHttpClient client = new OkHttpClient();
    List<HealthRecord> healthRecordList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

        //temp user just to add in the logic
        tempUser = new User("Emmanuel", "password", 172, 65);


        Button setCaloriesBtn = findViewById(R.id.set_limit);
        setCaloriesBtn.setOnClickListener((view)->{
            getRecordsFromServer(tempUser);
        });
        Button addCaloriesBtn = findViewById(R.id.add_calories);
        addCaloriesBtn.setOnClickListener((view -> {
            System.out.println(healthRecordList.get(0));
        }));

    }


    private List<HealthRecord> getRecordsFromServer(User user){
        List<HealthRecord> hList = new ArrayList<>();

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);


            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url("http://192.168.10.122:8080/user/gethealthrecords")
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
                        //to do: convert responseBody into list of HealthRecords
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        healthRecordList = Arrays.asList(objectMapper.readValue(responseBody.string(), HealthRecord[].class));
//                        Log.i("data", responseBody.string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return hList;
    }




}