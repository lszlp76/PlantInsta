package com.zlpls.plantinsta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zlpls.plantinsta.menulist.UserMenuList;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.zlpls.plantinsta.visualselection.VisualMainActivity;

public class PlantList extends AppCompatActivity {
    UserActions userActions = new UserActions();
    FragmentTransaction fragmentTransaction;
    //BottomAppBar bottomAppBar;


    BottomNavigationView bottomAppBar;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference plantReference ;
    String plantinstauser,data;
    private AddPlantAdapter adapter;
    FirestoreRecyclerOptions<PlantModel> options ;
RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        mAuth = FirebaseAuth.getInstance();
        plantinstauser = mAuth.getCurrentUser().getEmail();
        String path = plantinstauser;
        plantReference = db.collection(path);


        ActionBar actionBar = getSupportActionBar();
        String title = "My PlantInsta Diaries";

        getSupportActionBar().setTitle(title);
        BottomNavigationView bottomAppBar =  findViewById(R.id.bottom_navigation);

        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);

       //recycleriew set adaptser
        recyclerView = findViewById(R.id.plantlistrecyclerview);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));

        data =""; // açılışta full gelmesi için data = "" olarak yazılıyor
        setUpRecyclerView(data);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();

        System.out.println("resume" );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search my diary");

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                data = (newText).toLowerCase().trim();

                System.out.println("data : " + data);

                setUpRecyclerView(data);


                return false;
            }
        });


        return true;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.home:
                onResume();
                break;
            case R.id.menu:
                Intent intentu = new Intent(PlantList.this, UserMenuList.class);

                startActivity(intentu);
                break;
            case R.id.photo:
                Intent intent = new Intent(PlantList.this, VisualMainActivity.class);
                intent.putExtra("fromList",0);

                startActivity(intent);

                break;
        }
        return false;
    };

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setUpRecyclerView(String data) {


        //original query
        ;
       Query query = plantReference.orderBy("plantName", Query.Direction.ASCENDING)
        .whereEqualTo("plantUserMail", mAuth.getCurrentUser().getEmail())

               .startAt(data).endAt(data + "\uf8ff");;



        //original options
        options = new FirestoreRecyclerOptions.Builder<PlantModel>()
                .setQuery(query, PlantModel.class)
                 .setLifecycleOwner(this)
                .build();
        System.out.println("Size :" + options.getSnapshots().size());

       // adapter = new AddPlantAdapter(options);

       // adapter.updateOptions(options);

       //recycler view i yeniden çağırman lazım.Yoksa filtre çalışmaz



//.startAt(searchText).endAt(searchText + "\uf8ff");




         // data ,search yerine yazılan arama sözcüğü
// filtreli query
        if (data == "q"){

            Query filteredQueryFavo = plantReference.orderBy("plantName", Query.Direction.ASCENDING)
                    .whereEqualTo("plantFavorite", true);

            // filtreli options
            FirestoreRecyclerOptions<PlantModel> filteredListOptions = new FirestoreRecyclerOptions.Builder<PlantModel>()
                    .setQuery(filteredQueryFavo, PlantModel.class)
                    .setLifecycleOwner(this)
                    .build();

            adapter = new com.zlpls.plantinsta.AddPlantAdapter(filteredListOptions);
            System.out.println("query size " + filteredListOptions.getSnapshots().size());
            adapter.updateOptions(filteredListOptions);
            adapter.notifyDataSetChanged();
        }
        if ( data == null || data !=""){
            Query filteredQuery = plantReference.orderBy("plantName", Query.Direction.ASCENDING)
                    .whereEqualTo("plantUserMail", mAuth.getCurrentUser().getEmail())
                    .startAt(data).endAt(data + "\uf8ff");;
            // filtreli options
            FirestoreRecyclerOptions<PlantModel> filteredListOptions = new FirestoreRecyclerOptions.Builder<PlantModel>()
                    .setQuery(filteredQuery, PlantModel.class)
                    .setLifecycleOwner(this)
                    .build();

            adapter = new com.zlpls.plantinsta.AddPlantAdapter(filteredListOptions);
            System.out.println("query size " + filteredListOptions.getSnapshots().size());
            adapter.updateOptions(filteredListOptions);
            adapter.notifyDataSetChanged();
        }else {
            adapter = new com.zlpls.plantinsta.AddPlantAdapter(options);

            adapter.updateOptions(options);
        }
        recyclerView.setAdapter(adapter);








        //????????SWIPE İLE SİLME*********************



/*
 new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
 @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
 */

        adapter.setOnItemClickListener(new AddPlantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //PlantMOdel plant documentsnapshot ile modelin detaylarını alabilirsin
                PlantModel plant = documentSnapshot.toObject(PlantModel.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // Toast.makeText(PlantList.this,
                //       "Position " + position + " ID " + id + " İsim " + plant.getPlantName()
                //     , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PlantList.this, FeedActivity.class);
                intent.putExtra("mark", plant.getPlantName());
                intent.putExtra("postCounterValue", plant.getPlantPostCount());
                startActivity(intent);
            }

            @Override
            public void onAddPlantToFavorite(int position) {
                System.out.println("add to fav + "+ position );

            db.collection(plantinstauser).document(options.getSnapshots().get(position).getPlantName())

                        .update("plantFavorite", true)
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
                        });
            }

            @Override
            public void onDelPlantFromFavorite(int position) {
                System.out.println("del from fav + "+ position );
                db.collection(plantinstauser).document(options.getSnapshots().get(position).getPlantName())

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
                        });
            }

            @Override

            public void onDelete(DocumentSnapshot documentSnapshot, int position) {
                //adapter.deleteItem(position);
                // Toast.makeText(PlantList.this,
                //       "Position " + position, Toast.LENGTH_SHORT).show();
            }
        });


        //uzun basma durumu
        adapter.setOnItemLongClickListener(new AddPlantAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {
                PlantModel plant = documentSnapshot.toObject(PlantModel.class);

                AlertDialog.Builder builder = new AlertDialog.Builder(PlantList.this);
                builder.setMessage( " Your " + plant.getPlantName() +" plant diary will be deleted permanently!")
                        .setTitle("Diary Deleting")
                        .setIcon(R.drawable.alert);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        remove(position);
                        userActions.deleteDocFromFirebase(plant.getPlantName(),"plant",null,plantinstauser);
                        adapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("vazgeçti");
                    }
                });
                builder.show();
            }

            @Override
            public void onDelete(DocumentSnapshot documentSnapshot, int position) {

            }
        });

        adapter.notifyDataSetChanged();
    }
    public void remove(int position){
       adapter.notifyItemRemoved(position);
    }
}