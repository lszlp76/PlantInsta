package com.zlpls.plantinsta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zlpls.plantinsta.menulist.UserMenuList;
import com.zlpls.plantinsta.visualselection.VisualMainActivity;

public class PlantList extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                intent.putExtra("fromList", 0);

                startActivity(intent);

                break;
        }
        return false;
    };

    FirestoreRecyclerOptions<PlantModel> options;

    String plantinstauser, data;
    MenuItem sortFav, desortFav, searchItem;
    private FirebaseAuth mAuth;
    RecyclerView recyclerView

    ;
    private CollectionReference plantReference;
    private AddPlantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        mAuth = FirebaseAuth.getInstance();
        plantinstauser = mAuth.getCurrentUser().getEmail();
        String path = plantinstauser;
        plantReference = db.collection(path);



        String title = "My PlantInsta Diaries";

        getSupportActionBar().setTitle(title);
        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_navigation);

        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);
        data = "";

        setUpRecyclerView(data);

System.out.println("Sıra on Create");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);
        searchItem = menu.findItem(R.id.search_bar);
        sortFav = menu.findItem(R.id.favbutton);
        desortFav = menu.findItem(R.id.defavbutton);
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
                data = (newText).trim();
                setUpRecyclerView(data);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        desortFav.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.favbutton):
                setUpRecyclerView("plantFavorite");
                desortFav.setVisible(true);
                sortFav.setVisible(false);
                break;
            case (R.id.defavbutton):
                setUpRecyclerView(data);
                sortFav.setVisible(true);
                desortFav.setVisible(false);
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ;
       adapter.startListening();
        System.out.println("Sıra on Start");

    }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Sıra on Stop");
        adapter.stopListening();
    }

    @Override
    protected void onResume() { // refresh gibi çalışır
        super.onResume();
        System.out.println("Sıra on resume");
       setUpRecyclerView(data); // resume içine steup çağırırsan refreh olayı ok olur.
        adapter.startListening();
        ;
    }

    private void setUpRecyclerView(String data)  {
        if (data == "plantFavorite") {

            Query mainQuery = plantReference.orderBy("plantName", Query.Direction.ASCENDING)

                    .whereEqualTo("plantFavorite", true);
            options = new FirestoreRecyclerOptions.Builder<PlantModel>()
                    .setQuery(mainQuery, PlantModel.class)
.setLifecycleOwner(this)
                    .build();
        } else if (data == null || data != "" && data != "plantFavorite") {
            Query mainQuery = plantReference.orderBy("plantName", Query.Direction.ASCENDING)
// getMail kullanma yoksa karışıyor

                    .whereGreaterThanOrEqualTo("plantName", data)
                    .whereLessThanOrEqualTo("plantName", data + "\uF8FF");

            options = new FirestoreRecyclerOptions.Builder<PlantModel>()
                    .setQuery(mainQuery, PlantModel.class)
                    .setLifecycleOwner(this)
                    .build();
        } else {
            Query mainQuery = plantReference.orderBy("plantName", Query.Direction.ASCENDING);

            options = new FirestoreRecyclerOptions.Builder<PlantModel>()

                    .setQuery(mainQuery, PlantModel.class)
.setLifecycleOwner(this)
                    .build();
        }

        adapter = new com.zlpls.plantinsta.AddPlantAdapter(options);
        System.out.println("Sıra adapter size "+adapter.getItemCount());
        recyclerView = findViewById(R.id.plantlistrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();


        adapter.setOnItemClickListener(new AddPlantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //PlantMOdel plant documentsnapshot ile modelin detaylarını alabilirsin
                PlantModel plant = documentSnapshot.toObject(PlantModel.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(PlantList.this, FeedActivity.class);
                intent.putExtra("mark", plant.getPlantName());
                intent.putExtra("postCounterValue", plant.getPlantPostCount());
                intent.putExtra("plantFavorite", String.valueOf(plant.getPlantFavorite()));
                startActivity(intent);
            }

            @Override
            public void onAddPlantToFavorite(int position) {


            }

            @Override
            public void onDelPlantFromFavorite(int position) {
                ;

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
                builder.setMessage(" Your " + plant.getPlantName() + " plant diary will be deleted permanently!")
                        .setTitle("Diary Deleting")
                        .setIcon(R.drawable.alert);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.deleteItem(position);

                        // remove(position);
                        // userActions.deleteDocFromFirebase(plant.getPlantName(),"plant",null,plantinstauser);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(adapter);
                        adapter.updateOptions(options);
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


    }

    public void remove(int position) {
        adapter.notifyItemRemoved(position);
    }
}