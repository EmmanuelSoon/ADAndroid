package com.team2.getfitwithhenry;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.team2.getfitwithhenry.adapter.MealListAdapter;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MealFragment extends DialogFragment {

    private ListView mMealView;
    private List<DietRecord> dietRecordList;
    private String meal;
    private String date;
    private String username;
    private final OkHttpClient client = new OkHttpClient();
    private MealListAdapter mlAdaptor;


    public MealFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        meal = getArguments().getString("meal");
        date = getArguments().getString("date");
        username = getArguments().getString("username");

        getMealRecordsFromServer(username, date, meal);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_meal, container, false);

        Button editMeal = myView.findViewById(R.id.edit_meal_btn);
        editMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return myView;
    }

    private void updateDietRecords(){
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // update the listViews
                    MealListAdapter mlAdaptor = new MealListAdapter(getContext(), dietRecordList);
                    View view = getView();
                    ListView listView = view.findViewById(R.id.mealView);
                    if(listView != null){
                        listView.setAdapter(mlAdaptor);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                    }
                    TextView empty = view.findViewById(R.id.empty);
                    if(empty != null){
                        if (dietRecordList.size() != 0){
                            empty.setText("");
                        }
                    }
                    TextView title = (TextView) view.findViewById(R.id.mealTitleView);
                    title.setText(meal.toUpperCase());

                    mlAdaptor.setListener(new MealListAdapter.AdapterListener() {
                        @Override
                        public void removeDiet(DietRecord dr) {
                            removeDietRecordFromServer(dr.getId());
                            dietRecordList.remove(dr);
                            updateDietRecords();
                        }
                    });


                }
            });
        }
    }

    private void getMealRecordsFromServer(String username, String date, String meal){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("date", date);
            postData.put("meal", meal);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL +"/user/getmealrecords")
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
                        //convert responseBody into list of HealthRecords
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        dietRecordList = new ArrayList<>(Arrays.asList(objectMapper.readValue(responseBody.string(), DietRecord[].class)));
                        updateDietRecords();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void removeDietRecordFromServer(int drId) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("dietRecordId", drId);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL +"/user/deletedietrecord")
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


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}