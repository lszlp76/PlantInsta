package com.zlpls.plantinsta.menulist;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.zlpls.plantinsta.R;

public class PlantInstaWeb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_insta_web);

        Intent sunucu = getIntent();
        String page = sunucu.getStringExtra("browser");
        WebView browser = findViewById(R.id.agromtekwebsite);
        browser.clearCache(true);
        browser.getSettings().setJavaScriptEnabled(true);

        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl(page);
    }
}
