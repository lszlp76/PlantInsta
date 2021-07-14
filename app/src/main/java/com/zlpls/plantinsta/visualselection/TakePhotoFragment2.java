package com.zlpls.plantinsta.visualselection;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.zlpls.plantinsta.AddNewPlant;
import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UploadPlantFollow;
import com.zlpls.plantinsta.UserActions;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
// https://www.youtube.com/watch?v=CLXXXtslPik&list=PLgCYzUzKIBE9XqkckEJJA0I1wVKbUAOdv&index=46

/**
 * part 44 3:45'de hangi fragmenteten geliyorsa ona göre davranma var.
 */
public class TakePhotoFragment2 extends Fragment {
    private static ImageView imageView;
    String currentPhotoPath;
    Bitmap selectedImage;
    int targetW, targetH;
    FileOperations fileOperations;
    File photoFile;
    UserActions userActions = new UserActions();
    private final String TAG = "TAKİP";

    public TakePhotoFragment2() {

    }

    public static TakePhotoFragment2 newInstance() {
        return new TakePhotoFragment2();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_take_photo, container, false);

        Button takePhotoButton = v.findViewById(R.id.takephotobutton);
        imageView = (v.findViewById(R.id.phototaken));
        targetW = imageView.getWidth();
        targetH = imageView.getHeight();
        fileOperations = new FileOperations();
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d(TAG, "Kamera başlatıldı ");
                startCamera();
            }
        });


        return v;
    }


    public void startCamera() {
        if
/**
 * izinleri fragment te uygulandığı görmek için alttaki kodu kullan. Eğer aktivitiye koyacak isen
 * getContext olan yerlere aktivite gelmeli
 * ve aktivite class altında onRequestPerxxx olacak şekilde
 * yazılmalı
 */
        (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = null;
            try {
                photoFile = fileOperations.createImageFile(getContext());
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.zlpls.plantinsta.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                try {


                    startActivityForResult(takePictureIntent, 0);

                    // Gösterimde 90 derece çevirmek


                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    // display error state to the user
                }

            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 1){


            if ( grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0) {


            //selectedImage = fileOperations.setPic(photoFile,getContext());

            int degree = fileOperations.getImageRotation(photoFile);
            if (degree == 90) {
                Picasso.get().load("file:" + photoFile.getAbsolutePath())
                        .rotate(0)
                        .fit()
                        .into(imageView);
            } else {
                Picasso.get().load("file:" + photoFile.getAbsolutePath())
                        .fit()
                        .into(imageView);
            }
            //TODO BURADA SİLME OPERASYONU VAR. DÜZELTİLMELİ
            //fileOperations.deleteImageFileInStorage(photoFile);

            switch (userActions.getFromList()) {
                case 0:
                    userActions.setImageFromGallery(false); //resim galeriden gidiyor mu?
                    Intent intent = new Intent(getContext(), AddNewPlant.class);
                    intent.putExtra("selectedimagefromuser", photoFile.getAbsolutePath());
                    startActivity(intent);

                    break;
                case 1:
                    userActions.setImageFromGallery(false); //resim galeriden gidiyor mu?
                    Intent intentf = new Intent(getContext(), UploadPlantFollow.class);
                    intentf.putExtra("selectedimagefromuser", photoFile.getAbsolutePath());
                    startActivity(intentf);

            }


        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
/*
  Bitmap  photoFile2Bitmap = fileOperations.getStreamByteFromImage(photoFile);
                photoFile2Bitmap = fileOperations.getBitmapRotatedByDegree(photoFile2Bitmap,270);

                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(photoFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

              photoFile2Bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
 */