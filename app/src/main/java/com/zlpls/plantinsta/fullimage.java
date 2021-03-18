package com.zlpls.plantinsta;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class fullimage extends AppCompatActivity {
    String imageUrl;
    ImageView fullImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);


        // zoom yapmak için Touchview kütüphanesini kullan

        // https://github.com/MikeOrtiz/TouchImageView

        fullImageView = findViewById(R.id.fullImageView);
        Intent intent = getIntent();

        String plantName = intent.getStringExtra("plantname");
        String date = intent.getStringExtra("date");
        imageUrl = intent.getStringExtra("imageUrl");
        Picasso.get().load(imageUrl)
                .into(fullImageView);

        ActionBar actionBar = getSupportActionBar();
        String title = plantName+ " Güncesi@"+date;
        actionBar.setTitle(title);
        getSupportActionBar().setTitle(title);

        //zoom
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        //mAttacher = new PhotoViewAttacher(mImageView);


    }

}