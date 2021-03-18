package com.zlpls.plantinsta.visualselection;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zlpls.plantinsta.AddNewPlant;
import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UploadPlantFollow;
import com.zlpls.plantinsta.UserActions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * https://www.youtube.com/watch?v=gQdWkFux4l4&list=PLgCYzUzKIBE9XqkckEJJA0I1wVKbUAOdv&index=49
 part 48 'de 3:11'de directory adının kısatılması var.

 firebase'de fotograf eklediğinde bir integer oluştur her bir günlük için
 sonra bu değeri her eklediğinde bir arttır,her sildiğinde bir azalt ve sürekli olarak
 data modelin bir parçası gibi tut.



 */
public class GaleryFragment extends Fragment {

    private static final int REQUEST_CODE = 1;
    //private static Uri[] mUrls ;
    private static ArrayList<String> strUrls,mNames;
    private final ArrayList<Picture> pictureList = new ArrayList<>();
    ImageViewerAdapter imageViewerAdapter;
    RecyclerView recyclerView;
   public UserActions userActions = new UserActions();
    int THUMBNAIL_SIZE = 1000;


    //15
    public static GaleryFragment newInstance() {
        return new GaleryFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (pictureList.size() ==0 ){
            try{ String[] projection = {MediaStore.MediaColumns.DATA};
                int i = 0;
                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified DESC");
                cursor.moveToFirst();
                //mUrls = new Uri[cursor.getCount()];

                mNames = new ArrayList<>();
                strUrls = new ArrayList<>();
                while (cursor.moveToNext()) {
                    i++;
                    String pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    // mUrls[i] = Uri.parse(pathImage);
                    String name = cursor.getString(0);
                    strUrls.add((pathImage));
                    mNames.add(pathImage);
                    pictureList.add(new Picture(pathImage,pathImage));

                }
                cursor.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override

    // https://stackoverflow.com/questions/27188536/android-recyclerview-scrolling-performance
    //perofrmns issue for recycler view
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_galery, container, false);

        recyclerView = rootView.findViewById(R.id.pictureselector);
        ImageView selectedImageView = rootView.findViewById(R.id.selectedimage);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5));
        imageViewerAdapter = new ImageViewerAdapter(getContext(),pictureList);
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
                switch (userActions.getFromList()){
                case 0:
                    userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                    Intent intent = new Intent(getContext(), AddNewPlant.class);
                    intent.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                    startActivity(intent);
                    break;
                case 1 :
                    userActions.setImageFromGallery(true); //resim galeriden gidiyor mu?
                    Intent intentf = new Intent(getContext(), UploadPlantFollow.class);
                    intentf.putExtra("selectedimagefromuser", pictureList.get(position).getImagePath());
                    startActivity(intentf);
            }


            }

        });
        imageViewerAdapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(rootView, savedInstanceState);


   
        //14 recycler view tanıtma


    }

    public void getImagesList() {

    }


// buradan sonrası kopya https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri

}