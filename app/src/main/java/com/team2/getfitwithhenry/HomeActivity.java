package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationBarView;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeActivity extends AppCompatActivity implements AddWaterFragment.IAddWater {
    Button mlogoutBtn;

    private Toolbar mToolbar;
    private NavigationBarView bottomNavView;
    private final OkHttpClient client = new OkHttpClient();
    private List<HealthRecord> healthRecordList;
    private TextView caloriesText;
    private TextView waterText;
    private User user;
    private ImageView calsProg;
    private ImageView waterProg;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItem;
    private String[] itemLists = {"Weight","Calories","Water Intake"};
    private String dropdownItem = null;
    private LineChart mpLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTopNavBar();
        setBottomNavBar();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("userDetails", "");
        user = gson.fromJson(json, User.class);
        showDropdownList();
        Map<String, String> getData = new HashMap<>();
        getData.put("username", user.getUsername());
        sendToServer(getData,"/user/gethealthrecords");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        if (!pref.contains("userDetails"))
            startLoginActivity();
        else if (pref.contains("userDetails") && (user.getCalorieintake_limit_inkcal() == null || user.getCalorieintake_limit_inkcal() <= 0))
            startQuestionnaireActivity();
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

                    waterProg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showWaterDialog();
                        }
                    });

                }
            });

        }
    }

    private void sendToServer(Map<String, ?> dataMap, String url) {
        JSONObject postData = new JSONObject();
        try {
            for(Map.Entry<String, ?> entry : dataMap.entrySet()){
                postData.put(entry.getKey(), entry.getValue());
            }
            postData.put("username", user.getUsername());



            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);


            //need to use your own pc's ip address here, cannot use local host.
            Request request = new Request.Builder()
                    .url(Constants.javaURL + url)
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
                            showLineGraph(healthRecordList,"Weight");
                           // showGraphView(healthRecordList);
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

    private void showDropdownList() {
        autoCompleteTextView = findViewById(R.id.dropDownList);
        int testing = itemLists.length;
        adapterItem = new ArrayAdapter<String>(this, R.layout.graph_list_item, itemLists);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dropdownItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(HomeActivity.this, "Item: " + dropdownItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLineGraph(List<HealthRecord> healthRecordList,String item){
        mpLineChart = findViewById(R.id.LineChart);
        LineDataSet lineDataSet1= new LineDataSet(dataValuesforChart(healthRecordList,item),item+ " tracking");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        //mpLineChart.setNoDataText("No Data to show! Please Update your information to show graph");
        mpLineChart.setDrawGridBackground(true);
        mpLineChart.setDrawBorders(true);
        mpLineChart.setBorderColor(Color.LTGRAY);
        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
//            final ArrayList<String> xAxisLabel = new ArrayList<>();
//            for(HealthRecord hr: healthRecordList){
//                xAxisLabel.add(hr.getDate().toString());
//            }
        List<String> xAxisLabel = getXAxisLabels(healthRecordList);
        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(xAxisLabel.size(),false); // yes, false. This is intentional
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.mAxisMaximum = 3;
        mpLineChart.invalidate();

    }

    private ArrayList<String> getXAxisLabels(List<HealthRecord> hrList){
        ArrayList<String> xAxisLabel = new ArrayList<>();
        for(HealthRecord hr: healthRecordList){
            xAxisLabel.add(hr.getDate().toString());
        }
        return xAxisLabel;
    }

    private ArrayList<Entry> dataValuesforChart(List<HealthRecord> hrList,String itemName){

        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        switch (itemName){
            case "Weight":
                for(int i=0;i<hrList.size();i++){
                    dataVals.add(new Entry(i,(float) hrList.get(i).getUserWeight()));
                };
                break;
            case "Calories":
                for(int i=0;i<hrList.size();i++){
                    dataVals.add(new Entry(i,(float) hrList.get(i).getCalIntake()));
                };
                break;
            case "Water Intake":
                for(int i=0;i<hrList.size();i++){
                    dataVals.add(new Entry(i,(float) hrList.get(i).getWaterIntake()));
                };
                break;
            default:
                for(int i=0;i<hrList.size();i++){
                    dataVals.add(new Entry(i,(float) hrList.get(i).getUserWeight()));
                };
                break;
        }

//        dataVals.add(new Entry(0,10));
//        dataVals.add(new Entry(1,20));
//        dataVals.add(new Entry(3,30));
//        dataVals.add(new Entry(4,40));
//        dataVals.add(new Entry(5,50));

//        for(int i=0;i<hrList.size();i++){
//            dataVals.add(new Entry(i, (float)hrList.get(i).getUserWeight()));
//        };
        return dataVals;
    }

    public void showWaterDialog(){
        DialogFragment df = new AddWaterFragment();
        df.show(getSupportFragmentManager(), "AddWaterFragment");
    }

    @Override
    public void onSelectedData(Double selectedMils) {
        Map<String, Object> waterData = new HashMap<>();
        waterData.put("hrID", healthRecordList.get(0).getId());
        waterData.put("addMils", selectedMils);
        sendToServer(waterData, "/user/addwater");

        Double newWaterVal = healthRecordList.get(0).getWaterIntake() + selectedMils;


        String waterLabel = "Water\n" + String.valueOf(Math.round(newWaterVal));
        String mils = "ml";
        SpannableString ss3 = new SpannableString(waterLabel);
        SpannableString ss4 = new SpannableString(mils);
        ss4.setSpan(new RelativeSizeSpan(0.6f), 0, 2, 0);
        ss4.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0,2, 0);
        CharSequence finalText2 = TextUtils.concat(ss3,  "\n" , ss4);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                float waterAngle = Math.round((newWaterVal / user.getWaterintake_limit_inml()) * 270) > 270f ? 270f : Math.round((newWaterVal / user.getWaterintake_limit_inml()) * 270);
                waterProg.setImageDrawable(new ProgressArcDrawable(waterAngle, "blue"));

                waterText.setText(finalText2);


            }
        });


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

    private void startQuestionnaireActivity() {
        Intent intent = new Intent(this, QuestionnaireActivity.class);
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