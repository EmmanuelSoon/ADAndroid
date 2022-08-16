package com.team2.getfitwithhenry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.slider.Slider;
import com.team2.getfitwithhenry.adapter.FoodListAdapter;
import com.team2.getfitwithhenry.adapter.RecipeListAdapter;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.Ingredient;
import com.team2.getfitwithhenry.model.Recipe;
import com.team2.getfitwithhenry.model.WeightedIngredient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
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


public class SearchFoodActivity extends AppCompatActivity {

    private ListView mlistView;
    private Button mSearchBtn;
    private EditText mEditText;
    private Toolbar mToolbar;
    private NavigationBarView bottomNavView;
    private Button mAddMealBtn;
    private Button caloriesFilter;
    private MaterialButtonToggleGroup mToggleBtn;
    List<Recipe> rList;
    List<Ingredient> iList;
    String query;
    private final OkHttpClient client = new OkHttpClient();
    List<Ingredient> mealBuilder = new ArrayList<>();
    List<Recipe> recipeBuilder = new ArrayList<>();
    int prevPos = 0;
    int calSelection = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        setTopNavBar();
        setBottomNavBar();
        caloriesFilter = findViewById(R.id.calFilter);
        mSearchBtn = findViewById(R.id.search);
        mEditText = findViewById(R.id.editText);
        mToggleBtn = (MaterialButtonToggleGroup) findViewById(R.id.toggleButton);

        //TODO check if there are other actiivities using this function
//        Intent intent = getIntent();
//        query = intent.getStringExtra("SearchValue");
//        if (query != null) {
//            getSearchResult(query);
//        }
//        mEditText.setText(query);

        mToggleBtn.setSingleSelection(true);
        mToggleBtn.setSelectionRequired(true);
        mToggleBtn.check(R.id.ingredientBtn);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddMealBtn.setVisibility(View.INVISIBLE);
                query = mEditText.getText().toString();
                if (mToggleBtn.getCheckedButtonId() == R.id.ingredientBtn) {
                    getIngredientResult(query);
                }
                else if (mToggleBtn.getCheckedButtonId() == R.id.recipeBtn) {
                    getRecipeResult(query);
                }
                //drop the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


        mAddMealBtn = findViewById(R.id.add_as_meal);
        mAddMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Integer, Double> mealMap = new HashMap<>();
                if(!recipeBuilder.isEmpty()){
                    for (Recipe recipe : recipeBuilder){
                        for(WeightedIngredient wi : recipe.getIngredientList()){
                            mealBuilder.add(wi.getIngredient());
                            mealMap.put(wi.getIngredient().getId(), (double) Math.round(wi.getWeight()/recipe.getPortion()));
                        }
                    }
                }
                //check if there is an activity that called search
                ComponentName componentName = getCallingActivity();
                if (componentName == null) {
                    Intent intent = new Intent(SearchFoodActivity.this, AddMealActivity.class);
                    intent.putExtra("ingredients", (Serializable) mealBuilder);
                    if (!mealMap.isEmpty()){
                        intent.putExtra("AddRecipe", true);
                        intent.putExtra("mealmap", (Serializable) mealMap);
                        intent.putExtra("meal", "BREAKFAST");
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Intent response = new Intent();
                    response.putExtra("ingredients", (Serializable) mealBuilder);
                    if (!mealMap.isEmpty()){
                        response.putExtra("AddRecipe", true);
                        response.putExtra("mealmap", (Serializable) mealMap);
                        response.putExtra("meal", "BREAKFAST");
                    }
                    setResult(RESULT_OK, response);
                    finish();
                }

            }
        });

        caloriesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new Dialog(SearchFoodActivity.this);
                d.setContentView(R.layout.max_calories_dialog_layout);
                Slider slider = d.findViewById(R.id.maxCaloriesSlider);
                slider.setValue(calSelection);
                TextView selection = d.findViewById(R.id.currentCalSelection);
                selection.setText(String.format("Max. %d kcal", calSelection));
                slider.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                        int current = (int) value;
                        selection.setText(String.format("Max. %d kcal", current));
                    }
                });
                Button applyFilter = d.findViewById(R.id.applyFilter);
                Button cancelFilter = d.findViewById(R.id.cancelFilter);
                applyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calSelection = (int) slider.getValue();
                        d.dismiss();
                    }
                });
                cancelFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

                d.show();
            }
        });

    }

    public void getIngredientResult(String query) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("query", query);
            postData.put("maxCal", calSelection);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postData.toString(), JSON);

            Request request = new Request.Builder()
                    .url(Constants.javaURL + "/search/ingredients")
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

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        iList = Arrays.asList(objectMapper.readValue(responseBody.string(), Ingredient[].class));
                        displayIngredientResult(getApplicationContext(), iList);

                        response.body().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getRecipeResult(String query) {
        String url = Constants.javaURL + "/recipe/search" +
                (query.isEmpty()||query == null ? "" : "/" + query) + "/" + calSelection;
            Request request = new Request.Builder()
                    .url(url)
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
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        objectMapper.registerModule(new JavaTimeModule());
                        rList = Arrays.asList(objectMapper.readValue(responseBody.string(), Recipe[].class));
                        displayRecipeResult(getApplicationContext(), rList);
                        response.body().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    }

    public void displayIngredientResult(final Context context, List<Ingredient> myList) {
        if (context != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    FoodListAdapter myAdapter = new FoodListAdapter(context, myList, mealBuilder);
                    mlistView = findViewById(R.id.listView);
                    if (mlistView != null) {
                        mlistView.setAdapter(myAdapter);
                    }

                    mlistView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    mlistView.setItemsCanFocus(false);

                    mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mAddMealBtn.setText("Add into meal");
                            mAddMealBtn.setVisibility(View.VISIBLE);
                            if (mealBuilder.isEmpty() | !mealBuilder.contains(iList.get(position))) {
                                mealBuilder.add(iList.get(position));
                                myAdapter.notifyDataSetChanged();
                            }
                            else {
                                mealBuilder.remove(iList.get(position));
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                }
            });
        }
    }

    public void displayRecipeResult(final Context context, List<Recipe> recipeList) {
        if (context != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    RecipeListAdapter rAdapter = new RecipeListAdapter(context, recipeList, recipeBuilder);
                    mlistView = findViewById(R.id.listView);
                    if (mlistView != null) {
                        mlistView.setAdapter(rAdapter);
                    }

                    mlistView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    mlistView.setItemsCanFocus(false);

                    mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(!recipeBuilder.isEmpty() && recipeBuilder.contains(recipeList.get(position))){
                                recipeBuilder.clear();
                                rAdapter.notifyDataSetChanged();
                                mAddMealBtn.setVisibility(View.INVISIBLE);
                            }
                            else{
                                recipeBuilder.clear();
                                rAdapter.notifyDataSetChanged();
                                recipeBuilder.add(recipeList.get(position));
                                rAdapter.notifyDataSetChanged();
                                mAddMealBtn.setText("Add 1 Serving");
                                mAddMealBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
            });
        }
    }

    public void setTopNavBar() {
        mToolbar = findViewById(R.id.top_navbar);
        mToolbar.setTitle("");
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
        bottomNavView.setSelectedItemId(R.id.nav_search);
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id) {

                    case (R.id.nav_scanner):
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;  //or should this be finish?

                    case (R.id.nav_log):
                        intent = new Intent(getApplicationContext(), LoggerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;

                    case (R.id.nav_recipe):
                        intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;

                    case (R.id.nav_home):
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                        break;
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

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}