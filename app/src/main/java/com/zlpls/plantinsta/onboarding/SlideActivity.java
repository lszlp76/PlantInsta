package com.zlpls.plantinsta.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.zlpls.plantinsta.PlantList;
import com.zlpls.plantinsta.R;

public class SlideActivity extends AppCompatActivity {
    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        viewPager = findViewById(R.id.viewpager);
        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        if (isAllOpenRead()) {
            Intent intent = new Intent(SlideActivity.this, PlantList.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            SharedPreferences.Editor editor = getSharedPreferences("slide", MODE_PRIVATE).edit();
            // editor.clear().commit();
           editor.putBoolean("slide", true);
           editor.apply();

        }
    }

    private boolean isAllOpenRead() {
        //test yapacaan zaman return false ı aktif et diğerlerini pasif et
     //return false;

        SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide", false);
        return result;





    }





}