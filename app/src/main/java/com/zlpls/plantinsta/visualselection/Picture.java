package com.zlpls.plantinsta.visualselection;

import android.graphics.Bitmap;

public class Picture {
    String imageName;
    String imagePath;
    Bitmap bitmapGallery;

    public Picture(String imageName, String imagePath, Bitmap bitmapGallery) {
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.bitmapGallery = bitmapGallery;
    }

    public Bitmap getBitmapGallery() {

        return bitmapGallery;
    }

    public Picture(Bitmap bitmapGallery) {
        this.bitmapGallery = bitmapGallery;
    }



    public Picture(String imageName, String imagePath) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
