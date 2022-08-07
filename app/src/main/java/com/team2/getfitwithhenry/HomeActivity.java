package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.team2.getfitwithhenry.helper.ProgressArcDrawable;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Goal;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.Role;
import com.team2.getfitwithhenry.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeActivity extends AppCompatActivity {
    Button mlogoutBtn;

    private Toolbar mToolbar;
    NavigationBarView bottomNavView;
    private User tempUser;
    private final OkHttpClient client = new OkHttpClient();
    List<HealthRecord> healthRecordList;
    TextView caloriesText;
    TextView waterText;
    GraphView graph;
    User user;
    ImageView calsProg;
    ImageView waterProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTopNavBar();
        setBottomNavBar();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        System.out.println(json);
        user = gson.fromJson(json, User.class);

        getUserHealthRecordHistory(user);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        if (!pref.contains("userDetails"))
            startLoginActivity();
    }

    private void showGraphView(List<HealthRecord> healthRecordList) {
        graph = (GraphView) findViewById(R.id.GraphView);
        //plot data(curve) on X and Y
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        ArrayList<String> xAxisLables = new ArrayList<>();
        for (int i = 0; i < healthRecordList.size(); i++) {
            series.appendData(new DataPoint(i, healthRecordList.get(i).getUserWeight()), true, healthRecordList.size());
            xAxisLables.add(healthRecordList.get(i).getDate().toString());
        }

        //set the appearance of the curve
        series.setColor(Color.rgb(0, 80, 100)); //set the color of the curve
        series.setTitle("Weight Curve"); // set the curve name for the legend
        series.setDrawDataPoints(true); // draw points
        series.setDataPointsRadius(5); // the radius of the data point
        series.setThickness(2); //line thickness

        graph.addSeries(series);


        // graph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        // XAxis xAxis = graph.getXAxis();

        //set title for graph
        graph.setTitle("Weight Tracking");
        graph.setTitleTextSize(50);
        graph.setTitleColor(Color.BLUE);
        //legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        //Axis signatures
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Date");
        gridLabel.setVerticalAxisTitle("Weight");
    }

    public void setProgressStats(Context context) {

        caloriesText = findViewById(R.id.caloriesText);
        waterText = findViewById(R.id.waterText);
        calsProg = findViewById(R.id.calories_progress);
        waterProg = findViewById(R.id.water_progress);

        Double calLimit = user.getCalorieintake_limit_inkcal();
        Double waterLimit = user.getWaterintake_limit_inml();

        //already sorted by db
        HealthRecord mostRecent = healthRecordList.get(0);

        float calAngle = Math.round((mostRecent.getCalIntake() / calLimit) * 270) > 270 ? 270f : Math.round((mostRecent.getCalIntake() / calLimit) * 270);
        float waterAngle = Math.round((mostRecent.getWaterIntake() / waterLimit) * 270) > 270f ? 270f : Math.round((mostRecent.getWaterIntake() / waterLimit) * 270);

        if (context != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {

                    if (calAngle < 243f) {
                        calsProg.setImageDrawable(new ProgressArcDrawable(calAngle, "green"));
                    } else {
                        calsProg.setImageDrawable(new ProgressArcDrawable(calAngle, "red"));
                    }

                    waterProg.setImageDrawable(new ProgressArcDrawable(waterAngle, "blue"));

                    //create text strings
                    String calsLabel = "Cals\n" + String.valueOf(Math.round(mostRecent.getCalIntake()));
                    String kcals = "kcals";

                    SpannableString ss1 = new SpannableString(calsLabel);
                    SpannableString ss2 = new SpannableString(kcals);
                    ss2.setSpan(new RelativeSizeSpan(0.6f), 0, 5, 0);
                    ss2.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0,5, 0);
                    CharSequence finalText = TextUtils.concat(ss1,  "\n" , ss2);

                    caloriesText.setText(finalText);

                    String waterLabel = "Water\n" + String.valueOf(Math.round(mostRecent.getWaterIntake()));
                    String mils = "ml";

                    SpannableString ss3 = new SpannableString(waterLabel);
                    SpannableString ss4 = new SpannableString(mils);
                    ss4.setSpan(new RelativeSizeSpan(0.6f), 0, 2, 0);
                    ss4.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0,2, 0);
                    CharSequence finalText2 = TextUtils.concat(ss3,  "\n" , ss4);

                    waterText.setText(finalText2);

                }
            });

        }
    }

    private void getUserHealthRecordHistory(User user) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user.getUsername());
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);


            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL +"/user/gethealthrecords")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Error");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        ResponseBody responseBody = response.body();
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        healthRecordList = Arrays.asList(objectMapper.readValue(responseBody.string(), HealthRecord[].class));
                        if (healthRecordList.size() != 0) {
                            showGraphView(healthRecordList);
                        } else {
                            Toast.makeText(HomeActivity.this, "No weight tracking for this user", Toast.LENGTH_SHORT).show();
                        }
                        setProgressStats(getApplicationContext());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setTopNavBar() {
        mToolbar = findViewById(R.id.top_navbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_nav_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                startProfileActivity();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setBottomNavBar() {
        bottomNavView = findViewById(R.id.bottom_navigation);
        bottomNavView.setSelectedItemId(R.id.nav_home);
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id) {

                    case (R.id.nav_scanner):
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        break;  //or should this be finish?

                    case (R.id.nav_search):
                        intent = new Intent(getApplicationContext(), SearchFoodActivity.class);
                        startActivity(intent);
                        break;

                    case (R.id.nav_log):
                        intent = new Intent(getApplicationContext(), LoggerActivity.class);
                        startActivity(intent);
                        break;

                    case (R.id.nav_recipe):
                        intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        startActivity(intent);
                        break;

//                    case(R.id.nav_home):
//                        intent = new Intent(getApplicationContext(), HomeActivity.class);
//                        startActivity(intent);
//                        break;
                }

                return false;
            }
        });
    }

    private void logout() {
        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();
        startLoginActivity();
    }

    private void startLogoutActivity() {
        Intent intent = new Intent(this, LogoutActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}