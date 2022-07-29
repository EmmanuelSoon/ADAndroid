package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SearchFoodActivity extends AppCompatActivity {

    private ListView mlistView;
    private Button mSearchBtn;
    private EditText mEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        mlistView = findViewById(R.id.listView);
        mSearchBtn = findViewById(R.id.search);
        mEditText = findViewById(R.id.editText);

        Intent intent = getIntent();
        String searchFrom = intent.getStringExtra("SearchValue");

        mEditText.setText(searchFrom);
    }
}