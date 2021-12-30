package com.zlpls.plantinsta.visualselection;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FileOperations {
    String fileName;
    String filePath;
    Context context;
    String folderName;
   public String STORAGE_ROOT = (Environment.getExternalStorageDirectory().getAbsolutePath());

   public String CAMERA_IMAGES = STORAGE_ROOT + "/DCIM/Camera";
   public String PICTURES_ROOT = STORAGE_ROOT + "/DCIM";
    public FileOperations(String filePath) {
        this.filePath = filePath;
    }

    public FileOperations() {

    }

    public FileOperations(Context context) {
        this.context = context;
    }

    public FileOperations(String fileName, String filePath, Context context, String folderName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.context = context;
        this.folderName = folderName;
    }

    public FileOperations(String fileName, String folderName) {
        this.fileName = fileName;
        this.folderName = folderName;
    }

    public  Bitmap getStreamByteFromImage(final File imageFile) {

        Bitmap photoBitmap = BitmapFactory.decodeFile(imageFile.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int imageRotation = getImageRotation(imageFile);

        if (imageRotation != 0)
            System.out.println("Rotation was " + imageRotation);
        photoBitmap = getBitmapRotatedByDegree(photoBitmap, imageRotation);
        System.out.println("Rotation is " + imageRotation);
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);


        return photoBitmap;
    }

    public  int getImageRotation(final File imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile.getPath());
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    public  int getImageRotationbyPath(final String imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile);
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }



    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    public  Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public String getFilePath(File image) {

        filePath = image.getAbsolutePath();
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public File createImageFile(Context contextf) throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PlantInsta";//"JPEG_" + timeStamp + "_";
        File storageDir = contextf.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        System.out.println("dosya yeri " + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        image.getAbsolutePath();

        return image;
    }

    public void deleteFiles(Context konteks) {
        File storageDir = konteks.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir.getAbsolutePath());
        if (file.exists()) {
            file.delete();
        }
        System.out.println(storageDir);

    }

    //bitmap oluşturup geri gönderen metod
    public void setUpPicture(File image, Context contextg) {
        setPic(image, contextg);
    }

    public Bitmap setPic(File image, Context contextg) {
        // Get the dimensions of the View


        // Get the dimensions of the bitmap
        Bitmap selectedImage;
        String currentPhotoPath = image.getAbsolutePath();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = 1;//Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        File f = new File(currentPhotoPath);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        selectedImage = bitmap;
        // encode ettikten sonra yönü değişir.
        //https://www.impulseadventure.com/photo/exif-orientation.html
        // bu  nedenle bitmap encode ettikten sonra döndüreceksin
        int imageRotation = getImageRotation(f);
        if (imageRotation != 0) {
            selectedImage = getBitmapRotatedByDegree(bitmap, imageRotation);

        }
        if (selectedImage != null)
            MediaStore.Images.Media.insertImage(contextg.getContentResolver(), selectedImage, "PlantInsta", "PlantInsta");

        //TODO BURADAKİ SİLME OLMAZ İSE TELEFONA DOSYA KAYIT EDİYOR
        //deleteImageFileInStorage(image);

        return selectedImage;
    }

    public void deleteImageFileInStorage(File image) {
        File f = new File(image.getAbsolutePath());

        Uri contentUri = Uri.fromFile(f);
        File filetodelete = new File(Objects.requireNonNull(contentUri.getPath()));
        if (filetodelete.exists()) {
            if (filetodelete.delete()) {
                System.out.println("file Deleted :" + contentUri.getPath());
            } else {
                System.out.println("file not Deleted :" + contentUri.getPath());
            }
        }
    }

    public static ArrayList<String> getFilePath(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File [] listFiles = file.listFiles();
        for ( int i=0;i<listFiles.length;i++){

            if (listFiles[i].isFile()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;




    }
    public static ArrayList<String> getDirectoryPath(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File [] listFiles = file.listFiles();
        for ( int i=0;i<listFiles.length;i++){

            if (listFiles[i].isDirectory()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
