package com.zlpls.plantinsta;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zlpls.plantinsta.visualselection.FileOperations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PostHolder> {
    FileOperations fileOperations;
    private OnItemClickListener mlistener;

    private ArrayList<PlantModel> plants;
    UserActions userActions;


    public RecyclerAdapter(ArrayList<PlantModel> plants) {
        this.plants = plants;
    }



    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);

        PostHolder ph = new PostHolder(view, mlistener);
        return new PostHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
//bağlanınca ne olacak
        // holder.commentText.setText(commentsList.get(position));
        //  holder.dateText.setText(datesList.get(position));
        holder.commentText.setText(plants.get(position).getPlantComment());
        holder.dateText.setText(plants.get(position).getplantFirstDate());

        // path'i içeren bir file objesi oluştur

      /*
        final File file = new File(Environment.getExternalStorageDirectory(), "http.jpg");
        Uri uri = Uri.fromFile(file);
        File auxFile = new File(uri.getPath());
        System.out.println("Resim galerinde mi geliyor--> "+
       */


        Picasso.get().load(plants.get(position).getPlantImage())
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {

        // kaç tane row olacak buradan
        return plants.size();
        // return imagesList.size();
    }

    public void deleteItem() {


    }


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);

        void onShareClick(int position, Bitmap bmp);
        void onDownloadClick ( int position, Bitmap bmp);


    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        //görünümleri buradan tanımlıyor
        ImageView imageView;
        TextView dateText;
        TextView commentText;
        ImageView shareButton, deleteButton,downloadButton;

        public PostHolder(@NonNull final View itemView, OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerView_row_image);
            dateText = itemView.findViewById(R.id.recyclerView_row_date);
            commentText = itemView.findViewById(R.id.recyclerView_row_comment);
            shareButton = itemView.findViewById(R.id.share);
            deleteButton = itemView.findViewById(R.id.garbage);
            downloadButton = itemView.findViewById(R.id.download);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {

                            listener.onItemClick(position);

                        }
                    }

                }
            });
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!= null){
                        ImageView content = imageView;
                        content.setDrawingCacheEnabled(true);
                        Bitmap bmp= Bitmap.createBitmap(content.getDrawingCache());
                        int position = getAdapterPosition();
                        if (position!= RecyclerView.NO_POSITION){
                            listener.onDownloadClick(position,bmp);
                        }
                    }
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }

                }
            });
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ImageView content = imageView;
                    content.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(content.getDrawingCache());

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onShareClick(position, bitmap);

                    }
                        /*
                         int position = getAdapterPosition();
                        Drawable drawable = imageView.getDrawable();
                        Bitmap bmp = null;
                        if (position != RecyclerView.NO_POSITION) {
                            if (drawable instanceof BitmapDrawable)
                                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                listener.onShareClick(position,bmp);

                        }
                        imageview içindeki resim picasso tarafından formatlandığı için
                        resimi bmp ye çeviremiyor
                        * */


                }
            });
        }
    }

}
//**********https://guides.codepath.com/android/using-the-recyclerview#attaching-click-listeners-with-decorators


/*
holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ImageView  content = holder.imageView ;
                content.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(content.getDrawingCache());
                System.out.println("ok");
                File file = new File( getExternalFilesDir(Environment.DIRECTORY_PICTURES), "plantinsta" + ".jpg");
                Uri photoURI = null;

                    //File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName + ".jpg");
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true,true);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
 */
/*
File file = new File(plants.get(position).getPlantImage()); // path'i içeren bir file objesi oluşturur
        String path = file.getAbsolutePath();
        int degree = fileOperations.getImageRotation(file);
        if (degree == 90) {
            Picasso.get().load("file:" + file.getAbsolutePath())
                    .rotate(270)
                    .fit()
                    .into(holder.imageView);
        } else {
            Picasso.get().load("file:" + file.getAbsolutePath())
                    .fit()
                    .into(holder.imageView);
        }
 */