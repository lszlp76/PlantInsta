package com.zlpls.plantinsta;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class fullimage extends AppCompatActivity {
    String imageUrl;
    ImageView fullImageView;
    TextView comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);


        // zoom yapmak için Touchview kütüphanesini kullan

        // https://github.com/MikeOrtiz/TouchImageView

        fullImageView = findViewById(R.id.fullImageView);
        comments = findViewById(R.id.comments);
        Intent intent = getIntent();


        String plantName = intent.getStringExtra("plantname");
        String date = intent.getStringExtra("date");
        String comment = intent.getStringExtra("comments");
        imageUrl = intent.getStringExtra("imageUrl");
        Picasso.get().load(imageUrl)
                .into(fullImageView);

        //comments.setText(comment);
        ActionBar actionBar = getSupportActionBar();
        String title = plantName+ " 'page @"+date;
        actionBar.setTitle(title);

        getSupportActionBar().setTitle(title);

        //zoom
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        //mAttacher = new PhotoViewAttacher(mImageView);


    }

}