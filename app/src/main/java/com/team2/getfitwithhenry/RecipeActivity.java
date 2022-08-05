package com.team2.getfitwithhenry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.team2.getfitwithhenry.model.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RecipeActivity extends AppCompatActivity {

    private WebView mWebView;
    private String mUrl = "http://192.168.10.122:3000/android";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        SharedPreferences mPrefs = getSharedPreferences("UserDetailsObj", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("userDetails", "");
        user = gson.fromJson(json, User.class);

        String userHash = hashing(user.getUsername());
        String passHash = hashing(user.getPassword());
        String completeUrl = mUrl +"/" + user.getId() + "/" + userHash + "/" + passHash;
//        System.out.println(completeUrl);

        mWebView = findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
//        mWebView.loadUrl("http://192.168.10.122:3000/android" + Integer.toString(user.getId()));
        mWebView.loadUrl(completeUrl);
    }

    private String hashing(String text) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Change this to UTF-16 if needed
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;
    }
}