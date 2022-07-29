package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    Button searchBtn;
    Button scanBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchBtn = (Button)findViewById(R.id.searchBtn);
        scanBtn = findViewById(R.id.scanBtn);
//
//        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
//        searchBtn.setTypeface(font);
//        searchBtn.setText("\f002");

        searchBtn.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        Intent intent;
        int id=view.getId();
        if(id==R.id.searchBtn){
            intent = new Intent(this,SearchFoodActivity.class);
            startActivity(intent);
        }
        if(id==R.id.scanBtn){
            intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
}