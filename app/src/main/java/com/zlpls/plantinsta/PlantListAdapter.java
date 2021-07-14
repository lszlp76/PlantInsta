package com.zlpls.plantinsta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantListHolder> implements Filterable {
    private List<PlantModel> plants;
    private List<PlantModel> plantsFiltered;
    private OnItemClickListener listener;
    private OnItemLongClickListener  longListener;


    public PlantListAdapter (List<PlantModel> plants) {
        this.plants = plants;
        plantsFiltered = new ArrayList<>(plants);

    }


    @NonNull
    @Override
    public PlantListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.plant_item,viewGroup,false);
        return new PlantListHolder(itemView,listener,longListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(@NonNull PlantListHolder plantListHolder, int i) {

        PlantModel currentPlant = plants.get(i);
      ;
        plantListHolder.mplantNameText.setText(currentPlant.getPlantName());
        plantListHolder.mplantFirstDate.setText(currentPlant.getplantFirstDate());
        plantListHolder.mplantPostCount.setText(currentPlant.getPlantPostCount()+" adet günceniz var");
        Picasso.get().load(String.valueOf(currentPlant.getPlantAvatar()))
                .into(plantListHolder.mplantAvatarPicture);
    }

    @Override
    public int getItemCount() {
        System.out.println("PlantsFiltered size " + plantsFiltered.size());
        System.out.println("Plants size " + plants.size());
        return plants.size();
    }

    @Override
    public Filter getFilter() {
        return plantFilter;
    }



    private Filter plantFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PlantModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length()== 0){
                filteredList.addAll(plantsFiltered);//plantsFiltered yerine burayı plants olarak değiştirdim
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( PlantModel item : plants){ //burayı plants olarak değiştirdim
                    if (item.getPlantName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
                    results.values = filteredList;
                    return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        plants.clear();
        plants.addAll((List) results.values);
        notifyDataSetChanged();
        }
    };

    class PlantListHolder extends RecyclerView.ViewHolder {
        TextView mplantNameText;
        TextView mplantFirstDate;
        ImageView mplantAvatarPicture;
        TextView mplantPostCount;
        ImageView mGarbage;
        //TextView mplantPostNumber;

        //TextView plantPostNumber;
        public PlantListHolder (View itemView, OnItemClickListener listener, OnItemLongClickListener longListener) {
            super(itemView);
            mplantNameText = itemView.findViewById(R.id.plantnametext);
            mplantFirstDate = itemView.findViewById(R.id.plantfirstdate);
            mplantAvatarPicture = itemView.findViewById(R.id.plantavatarpicture);
            mplantPostCount = itemView.findViewById(R.id.plantpostnumber);
            // mGarbage = itemView.findViewById(R.id.garbageicon);
            // mplantPostNumber= itemView.findViewById(R.id.plantpostnumber);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   int position = getAdapterPosition();

                   if (position != RecyclerView.NO_POSITION && longListener != null) {
                       longListener.onItemLongClick(position);

                   }
                   return false;
               }
           });
        }

        }

    public interface OnItemClickListener {
        void onItemClick(int position);

    } public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;

    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener longListener){
        this.longListener = longListener;
    }
   }


