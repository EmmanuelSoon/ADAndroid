package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
<<<<<<< HEAD
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RecipeActivity extends AppCompatActivity {

    private WebView mWebView;
    private String mUrl = "http://192.168.10.122:3000";
//    private String mUrl = "http://localhost:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mWebView = findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);
=======

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipe);
>>>>>>> 8968fb2059a702085c947ae3a6d9b7e310df40c3
    }
}