package com.zlpls.plantinsta.visualselection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zlpls.plantinsta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageViewerAdapter extends RecyclerView.Adapter<ImageViewerAdapter.PictureHolder> {
    private final ArrayList<Picture> mPicture;
    Context mContext;
    private OnItemClickListener mlistener;

    public ImageViewerAdapter(Context mContext, ArrayList<Picture> mPicture) {
        this.mContext = mContext;
        this.mPicture = mPicture;
    }

    @NonNull
    @Override //5
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //9
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.pictureview, parent, false);
        PictureHolder vHolder = new PictureHolder(view, mlistener);
        return vHolder;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    @Override //5
    public void onBindViewHolder(@NonNull PictureHolder holder, int position) {
        //8

        //System.out.println("Resim yolu ----->" + mPicture.get(position).getImagePath());
        Picasso.get().load("file:" + mPicture.get(position).getImagePath())
                .fit()
                .placeholder(R.drawable.cicek)
                .into(holder.mpictureView);

        //Picasso.get().setLoggingEnabled(true); --> hata yakalama için
     /*File bitmapFile = new File(mPicture.get(position).getImagePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(),bmOptions);
        holder.mpictureView.setImageBitmap(bitmap);

      */


        // holder.mpictureView.setImageBitmap(mPicture.get(position).getBitmapGallery());
// Environment.getExternalStorageDirectory()
// bu linki tıklamada kullan
    /*
    https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
     */


    }

    @Override //5
    public int getItemCount() {
        // 12
        return mPicture.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class PictureHolder extends RecyclerView.ViewHolder {
        //6
        ImageView mpictureView;
        //TextView mpictureName;


        //3
        public PictureHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            //7
            mpictureView = itemView.findViewById(R.id.pictureView);
            //mpictureName = itemView.findViewById(R.id.PicName.pictureVie);

            mpictureView.setOnClickListener(new View.OnClickListener() {
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

        }
    }

    //2

}

