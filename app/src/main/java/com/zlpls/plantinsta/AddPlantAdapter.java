package com.zlpls.plantinsta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.zlpls.plantinsta.visualselection.FileOperations;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

//1
public class AddPlantAdapter extends FirestoreRecyclerAdapter<PlantModel, AddPlantAdapter.PlantHolder>  {

//implements Filterable
    private OnItemClickListener listener;
    private OnItemLongClickListener longlistener;
    Context mContext ;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddPlantAdapter(@NonNull FirestoreRecyclerOptions<PlantModel> options )  {

        super(options);

      // exampleListFull = new ArrayList<>(exampleList);


    }

    /**
     *  @Override
     *     public Filter getFilter() {
     *         return exampleFilter;
     *     }
     *     private Filter exampleFilter = new Filter() {
     *         @Override
     *         protected FilterResults performFiltering(CharSequence constraint) {
     *             List<PlantModel> filteredList = new ArrayList<>();
     *
     *             if (constraint == null || constraint.length() == 0) {
     *                 filteredList.addAll(exampleListFull);
     *             } else {
     *                 String filterPattern = constraint.toString().toLowerCase().trim();
     *
     *                 for (PlantModel item : exampleListFull) {
     *                     if (item.getPlantName().toLowerCase().contains(filterPattern)) {
     *                         filteredList.add(item);
     *                     }
     *                 }
     *             }
     *
     *             FilterResults results = new FilterResults();
     *             results.values = filteredList;
     *
     *             return results;
     *         }
     *
     *         @Override
     *         protected void publishResults(CharSequence constraint, FilterResults results) {
     *             exampleList.clear();
     *             exampleList.addAll((List) results.values);
     *             notifyDataSetChanged();
     *         }
     *     };
     *       protected boolean filterCondition(PlantModel model, String filterPattern) {
     *         return model.getPlantName().toLowerCase().contains(filterPattern) ||
     *                 model.getPlantName().toLowerCase().contains(filterPattern);
     *     }
     * @return
     */

    @SuppressLint("RestrictedApi")
    @Override
    protected void onBindViewHolder(@NonNull PlantHolder holder, int position, @NonNull PlantModel model) {
        holder.mplantNameText.setText(model.getPlantName());
        holder.mplantFirstDate.setText(model.getplantFirstDate());
        if (Integer.parseInt(model.getPlantPostCount()) == 0 ){
            holder.mplantPostCount.setText("No pages !!");
        }else if ( Integer.parseInt(model.getPlantPostCount()) == 1) {
            holder.mplantPostCount.setText("You have "+model.getPlantPostCount()+" page");

        }else {
            holder.mplantPostCount.setText("You have "+ model.getPlantPostCount()+" pages");
        }

            Picasso.get().load(String.valueOf(model.getPlantAvatar()))


                    //.resize(500,500)
                    .into(holder.mplantAvatarPicture);

        ;//mplantAddToFavorite.setChecked(false);

       if (model.getPlantFavorite() == null)
        {

            db.collection(model.getPlantUserMail()).document(model.getPlantName())

                    .update("plantFavorite", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Error updating document");
                        }
                    })  ;
           ;
        }
        else if (model.getPlantFavorite() == true){

            holder.mplantAddToFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favoritefilled));
        }
        else{
            ;
            holder.mplantAddToFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favorite));
        }

        /*
       holder.mplantAddToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked) {
                   holder.mplantAddToFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favoritefilled));

               }else {
                   holder.mplantAddToFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favorite));

               }
           }
       });
*/
    }



    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position)
                .getReference().delete();

    }


    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_item,
                parent, false);
        return new PlantHolder(v);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
    public void setOnItemLongClickListener(OnItemLongClickListener longlistener){
        this.longlistener=longlistener;
    }
    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onAddPlantToFavorite(int position);
        void onDelPlantFromFavorite ( int position);
        void onDelete(DocumentSnapshot documentSnapshot,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(DocumentSnapshot documentSnapshot,int position);
        void onDelete(DocumentSnapshot documentSnapshot,int position);
    }
    //2
    class PlantHolder extends RecyclerView.ViewHolder {
        TextView mplantNameText;
        TextView mplantFirstDate;
        ImageView mplantAvatarPicture ;
        ToggleButton mplantAddToFavorite;
        TextView mplantPostCount;
        ImageView mGarbage;
        //TextView mplantPostNumber;

        //TextView plantPostNumber;
        @SuppressLint("RestrictedApi")
        public PlantHolder(@NonNull View itemView) {
            super(itemView);
            mplantNameText = itemView.findViewById(R.id.plantnametext);
            mplantFirstDate = itemView.findViewById(R.id.plantfirstdate);
            mplantAvatarPicture = itemView.findViewById(R.id.plantavatarpicture);
            mplantPostCount = itemView.findViewById(R.id.plantpostnumber);
            mplantAddToFavorite = itemView.findViewById(R.id.addtofavorite);


            mplantAddToFavorite.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                 if  (mplantAddToFavorite.isChecked()){
                     int position = getAdapterPosition();
                     if (position != RecyclerView.NO_POSITION && listener != null) {
                         listener.onAddPlantToFavorite(position);
                     }
                 }else {
                     int position = getAdapterPosition();
                     if (position != RecyclerView.NO_POSITION && listener != null) {
                         listener.onDelPlantFromFavorite(position);
                     }
                 }
                 };

                                                   });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
           /*
            mGarbage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onDelete(getSnapshots().getSnapshot(position), position);
                    }
                }

            });
            */

            // özellik eklemek için mutlaka izle.
            // https://codinginflow.com/tutorials/android/simple-recyclerview-java/part-5-onclicklistener-single-view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && longlistener != null) {
                        longlistener.onItemLongClick(getSnapshots().getSnapshot(position), position);
                    }

                    return false;
                }
            });

        }
    }
}

/*
 private String plantAvatar;
    private String plantDate;
    private  String plantUserMail;
class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
        }
    }
 */