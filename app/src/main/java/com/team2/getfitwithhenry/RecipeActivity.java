package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.User;

public class RecipeActivity extends AppCompatActivity {

    private WebView mWebView;
    private String mUrl;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("UserDetailsObj", "");
        user = gson.fromJson(json, User.class);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("192.168.10.122:3000")
                .appendQueryParameter("userId", Integer.toString(user.getId()));
        mUrl = builder.build().toString();

        mWebView = findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);
    }
}