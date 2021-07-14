package com.zlpls.plantinsta.visualselection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UserActions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class VisualMainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final Uri[] mUrls = null;
    private static final ArrayList<String> strUrls = null;
    UserActions userActions = new UserActions();
    //fromList= intent.getIntExtra("fromList",0);
    public int fromList = UserActions.fromList;
    private final String[] mNames = null;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_visual_main);
        ActionBar actionBar = getSupportActionBar();
        String title = "Take photo / picture";
        actionBar.setTitle(title);
        getSupportActionBar().setTitle(title);
/*
 if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

     ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
             REQUEST_CODE);


 }
*/


        ViewPager viewPager = findViewById(R.id.view_pager);
        //viewPager.setAdapter(sectionsPagerAdapter);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        TabLayout tabs = findViewById(R.id.tabs);


        // Fragmanları eklemek

        viewPageAdapter.AddFragment(new TakePhotoFragment(), "Kamera");
        viewPageAdapter.AddFragment(new GaleryFragment(), "Galeri");
        viewPager.setAdapter(viewPageAdapter);
        tabs.setupWithViewPager(viewPager);

        tabs.getTabAt(1).setIcon(R.drawable.ic_addimage);
        tabs.getTabAt(0).setIcon(R.drawable.ic_takephoto);
        tabs.setBackgroundColor(Color.parseColor("#c8e6c9"));

        tabs.setSelectedTabIndicatorColor(Color.parseColor("#c43e00"));
//#c8e6c9
        Intent intentf = getIntent();
        fromList = intentf.getIntExtra("fromList", 0);
        UserActions.fromList = fromList;
        userActions.setFromList(fromList);
        //FloatingActionButton fab = findViewById(R.id.fab);
/*
fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

 */
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       String TAG = "TAKİP";
        if ( requestCode == 1){
            if ( grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "VisualActivy izini ");
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    */

}
