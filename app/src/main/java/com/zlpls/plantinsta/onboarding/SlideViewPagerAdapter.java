package com.zlpls.plantinsta.onboarding;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zlpls.plantinsta.PlantList;
import com.zlpls.plantinsta.R;

public class SlideViewPagerAdapter extends PagerAdapter {
    public SlideViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    Context ctx;

    @Override
    public int getCount() {
        return 3; // kaç sayfa olacaksa o kadar
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater =(LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_screen,container,false);

        ImageView imageView = view.findViewById(R.id.pageoneimage);
        ImageView imageView4 = view.findViewById(R.id.imageView4);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);

        TextView textView2 = view.findViewById(R.id.pageonetext);
        TextView textView = view.findViewById(R.id.pagetitle);

        ImageView next  = view.findViewById(R.id.pagenext);
        ImageView back = view.findViewById(R.id.pageback);
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, PlantList.class);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK |intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideActivity.viewPager.setCurrentItem(position+1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( position!= 0)
                    SlideActivity.viewPager.setCurrentItem(position-1);
            }
        });
        switch (position){
            case 0:
                imageView.setImageResource(R.drawable.ic_pageone);
                imageView4.setImageResource(R.drawable.unselected);
                imageView3.setImageResource(R.drawable.selected);
                imageView2.setImageResource(R.drawable.unselected);
                textView.setText("Keep a diary for your plants");
                textView2.setText("Observe the growth of your seedlings in your garden,the flowers on your balcony and the plants" +
                        "in your field, take some photos of them and some notes.");
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                button.setVisibility(view.GONE);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_pagetwo);
                imageView4.setImageResource(R.drawable.unselected);
                imageView3.setImageResource(R.drawable.unselected);
                imageView2.setImageResource(R.drawable.selected);
                textView.setText("Keep it up to date");
                textView2.setText("Add pages to your diaries. Follow their growths using your plants diaries");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                button.setVisibility(view.GONE);

                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_pagethree);
                imageView4.setImageResource(R.drawable.selected);
                imageView3.setImageResource(R.drawable.unselected);
                imageView2.setImageResource(R.drawable.unselected);
                textView.setText("Share it !");
                textView2.setText("Share your pages with your friends.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                button.setEnabled(true);

                break;




        }



        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) (object));
    }
}

