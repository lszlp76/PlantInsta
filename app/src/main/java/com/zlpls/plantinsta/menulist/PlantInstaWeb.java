package com.zlpls.plantinsta.menulist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.zlpls.plantinsta.R;

public class PlantInstaWeb extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_insta_web);

        Intent sunucu = getIntent();
        String page = sunucu.getStringExtra("browser");
        WebView browser = findViewById(R.id.webbrowser);
        browser.setWebViewClient(new WebViewClient());
        browser.clearCache(true);
        browser.loadUrl(page);
        browser.getSettings().setJavaScriptEnabled(true);




        ;
    }
}
