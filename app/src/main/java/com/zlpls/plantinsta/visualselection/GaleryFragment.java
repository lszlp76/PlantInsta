package com.zlpls.plantinsta.visualselection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zlpls.plantinsta.AddNewPlant;
import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UploadPlantFollow;
import com.zlpls.plantinsta.UserActions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * https://www.youtube.com/watch?v=gQdWkFux4l4&list=PLgCYzUzKIBE9XqkckEJJA0I1wVKbUAOdv&index=49
 * part 48 'de 3:11'de directory adının kısatılması var.
 * <p>
 * firebase'de fotograf eklediğinde bir integer oluştur her bir günlük için
 * sonra bu değeri her eklediğinde bir arttır,her sildiğinde bir azalt ve sürekli olarak
 * data modelin bir parçası gibi tut.
 */
public class GaleryFragment extends Fragment {

    private static final int REQUEST_CODE = 1;
    //private static Uri[] mUrls ;
    private static ArrayList<String> strUrls, mNames;

    private ArrayList<Picture> pictureList = new ArrayList<>();
    public UserActions userActions = new UserActions();
    ImageViewerAdapter imageViewerAdapter;
    RecyclerView recyclerView;
    int THUMBNAIL_SIZE = 1000;
    Set folderName;
    ArrayList<String> folderNameArr ;
    ArrayList<String> directories;
    private Spinner directoriesSpinner;
    ImageView selectedImageView;
    private String arr [];
    FileOperations fileOperations;
    //15
    public static GaleryFragment newInstance() {
        return new GaleryFragment();

    }


    public ArrayList<String> getFile(File dir ) {
        File listFile [] = dir.listFiles();
        if ( listFile != null && listFile.length > 0 )
        {
            for ( File file : listFile){
                if (file.isDirectory()) {

file.listFiles();
                }
                if ( file.getName().endsWith(".png") ||
                        file.getName().endsWith(".jpg")||
                        file.getName().endsWith(".jpeg")||
                        file.getName().endsWith(".gif")
                ){
                    String temp =file.getPath().substring(0,file.getPath().lastIndexOf('/'));
                    fileList.add( temp );
                    System.out.println("Folder name "+ temp);
                }
            }
        }
        return fileList;
    }
 private ArrayList<String> fileList = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        directories = new ArrayList<>();
        setHasOptionsMenu(true); //menu eklemek içim gerekli
        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);



        }
        if (pictureList.size() == 0) {
            try {
                folderName = new HashSet<>();

                String[] projection = {MediaStore.MediaColumns.DATA};

                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "DATE_MODIFIED DESC");
                cursor.moveToFirst();
                //mUrls = new Uri[cursor.getCount()];

                mNames = new ArrayList<>();
                strUrls = new ArrayList<>();
                while (cursor.moveToNext()) {

                    String pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    // mUrls[i] = Uri.parse(pathImage);
                    // burası folder ismi
                    System.out.println("folder name"+pathImage.substring(0,pathImage.lastIndexOf('/')));
//resim içeren tüm dosyaların isimlerini alıyor
                    folderName.add(pathImage.substring(0,pathImage.lastIndexOf('/')));
                    String name = cursor.getString(0);
                    System.out.println("isimler  "+ name);
                    strUrls.add((pathImage));
                    mNames.add(pathImage);
                    pictureList.add(new Picture(pathImage, pathImage));

                }
                cursor.close();
                //bu isimleri arr adlı bir diziye veriyor.
                arr = new String[folderName.size()];
                arr = (String[]) folderName.toArray(arr);
                //System.out.println("Folder name "+ Arrays.toString(arr));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }




    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String TAG = "TAKİP";
        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Galeri izini ");
                if (pictureList.size() == 0) {
                    try {
                        String[] projection = {MediaStore.MediaColumns.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,"DATE_MODIFIED DESC" );
                        cursor.moveToFirst();
                        //mUrls = new Uri[cursor.getCount()];

                        mNames = new ArrayList<>();
                        strUrls = new ArrayList<>();
                        while (cursor.moveToNext()) {

                            String pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                            // mUrls[i] = Uri.parse(pathImage);
                            String name = cursor.getString(0);
                            strUrls.add((pathImage));
                            mNames.add(pathImage);

                         // pictureList.add(new Picture(pathImage, pathImage));
                            folderName.add(pathImage.substring(0,pathImage.lastIndexOf('/')));

                        }
                        cursor.close();
                        arr = new String[folderName.size()];
                        arr = (String[]) folderName.toArray(arr);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                imageViewerAdapter.notifyDataSetChanged();
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {

menu.clear();
        inflater.inflate(R.menu.spinnermenu, menu);


        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayList<String> directoryNames = new ArrayList<String>();
        for (int i = 0; i<arr.length;i++) {
            int index = arr[(i)].lastIndexOf("/");
            String string = (arr[(i)].substring(index).replace("/", "")).trim();
            directoryNames.add(string);
        }

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromRsource(getActivity(),
//               arr.length, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_style_file,directoryNames);
        adapter.setDropDownViewResource(R.layout.spinner_style_file);
        //directoriesSpinner.setAdapter(adapter);
        spinner.setAdapter(adapter);
        spinner.setPopupBackgroundResource(R.color.primaryColor);

         spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 System.out.println("Directory selected : "+ arr[position]);
                 setUpView(arr[position]);
                 recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
                 imageViewerAdapter = new ImageViewerAdapter(getContext(), pictureList);
                 recyclerView.setAdapter(imageViewerAdapter);
                 recyclerView.setHasFixedSize(true);
                 recyclerView.setItemViewCacheSize(10);
                 recyclerView.setDrawingCacheEnabled(true);
                 recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                 imageViewerAdapter.setOnItemClickListener(new ImageViewerAdapter.OnItemClickListener() {
                     @Override
                     public void onItemClick(int position) {
                         Picasso.get()
                                 .load("file:" + pictureList.get(position).getImagePath())
                                 .into(selectedImageView);

                         /*İlgili sayfaya gönderme */
                         switch (userActions.getFromList()) {
                             case 0:
                                 userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                                 Intent intent = new Intent(getContext(), AddNewPlant.class);
                                 intent.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                                 startActivity(intent);
                                 break;
                             case 1:
                                 userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                                 Intent intentf = new Intent(getContext(), UploadPlantFollow.class);
                                 intentf.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                                 startActivity(intentf);
                         }


                     }

                 });

                 imageViewerAdapter.notifyDataSetChanged();
                 ;
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });
        super.onCreateOptionsMenu(menu, inflater);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    // https://stackoverflow.com/questions/27188536/android-recyclerview-scrolling-performance
    //perofrmns issue for view
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_galery, container, false);
        directoriesSpinner = rootView.findViewById(R.id.spinner);

        recyclerView = rootView.findViewById(R.id.pictureselector);
        selectedImageView = rootView.findViewById(R.id.selectedimage);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        imageViewerAdapter = new ImageViewerAdapter(getContext(), pictureList);
        recyclerView.setAdapter(imageViewerAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


;//getPictureFromSelectedFolder();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

       //getPictureFromSelectedFolder();
        //14 recycler view tanıtma

    }
    private ArrayList<String> getFilePath(String directory){
       pictureList.clear();
        ArrayList<String> pathArray = new ArrayList<String>();
        pictureList = new ArrayList<>();
        File file = new File(directory);
        File [] listFiles = file.listFiles();
        for ( int i=0;i<listFiles.length;i++){

            if (listFiles[i].isFile()
            &&
                    (listFiles[i].getName().endsWith(".png") ||
                    listFiles[i].getName().endsWith(".jpg")||
                            listFiles[i].getName().endsWith(".jpeg")||
                                    listFiles[i].getName().endsWith(".gif")

            ))
            {

                pathArray.add(listFiles[i].getAbsolutePath());
                pictureList.add(new Picture(listFiles[i].getAbsolutePath()));
            }
        }
        return pathArray;

    }
    private ArrayList<String> getDirectoryPath(String directory){
        pictureList.clear();
        ArrayList<String> pathArray = new ArrayList<String>();

        File file = new File(directory);
        File [] listFiles = file.listFiles();
        for ( int i=0;i<listFiles.length;i++){

            if (listFiles[i].isDirectory()){
                pathArray.add(listFiles[i].getAbsolutePath());
                pictureList.add(new Picture(listFiles[i].getAbsolutePath()));
            }
        }
        return pathArray;
    }
    private void setUpView (String selectedDirectory){
    //pictureList.clear()
        getFilePath(selectedDirectory);


    //System.out.println("Pciture list "+pictureList.size());
    }
    public void getImagesList() {

    }


        // buradan sonrası kopya https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri

    /**
     * Kullanılmayan kod
     */
    private void getPictureFromSelectedFolder(){
        ;fileOperations = new FileOperations();

        if (getDirectoryPath(fileOperations.PICTURES_ROOT) != null){
            directories = getDirectoryPath(fileOperations.PICTURES_ROOT);
        }
        ArrayList<String> directoryNames = new ArrayList<String>();
        for (int i = 0; i<arr.length;i++) {
            int index = arr[(i)].lastIndexOf("/");
            String string = (arr[(i)].substring(index).replace("/", "")).trim();
            directoryNames.add(string);
        }
        //directories.add(fileOperations.CAMERA_IMAGES);

        // Spinner yükleme
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directoriesSpinner.setAdapter(adapter);

        directoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Directory selected : "+ arr[position]);
                setUpView(arr[position]);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
                imageViewerAdapter = new ImageViewerAdapter(getContext(), pictureList);
                recyclerView.setAdapter(imageViewerAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemViewCacheSize(10);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                imageViewerAdapter.setOnItemClickListener(new ImageViewerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Picasso.get()
                                .load("file:" + pictureList.get(position).getImagePath())
                                .into(selectedImageView);

                        /*İlgili sayfaya gönderme */
                        switch (userActions.getFromList()) {
                            case 0:
                                userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                                Intent intent = new Intent(getContext(), AddNewPlant.class);
                                intent.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                                startActivity(intent);
                                break;
                            case 1:
                                userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                                Intent intentf = new Intent(getContext(), UploadPlantFollow.class);
                                intentf.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                                startActivity(intentf);
                        }


                    }

                });

                imageViewerAdapter.notifyDataSetChanged();
                ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    //*********
}