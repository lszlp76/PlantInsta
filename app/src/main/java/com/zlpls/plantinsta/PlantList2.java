package com.zlpls.plantinsta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.zlpls.plantinsta.menulist.UserMenuList;
import com.zlpls.plantinsta.visualselection.VisualMainActivity;

import java.util.ArrayList;

/**
 *
 * Plantınsta2 ve PlantAdapter kullanılmıyor.
 * sadece örnek olması amacıyla
 * yapıldı
 */


public class PlantList2 extends AppCompatActivity {
    UserActions userActions = new UserActions();
    FragmentTransaction fragmentTransaction;
    //BottomAppBar bottomAppBar;

    BottomNavigationView bottomAppBar;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference plantReference;
    String plantinstauser;
    RecyclerView recyclerView;
    private PlantListAdapter adapter;
    ArrayList<PlantModel> plant = new ArrayList<PlantModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);
        // Toolbar toolbar = findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();
        plantinstauser = mAuth.getCurrentUser().getEmail();
        String path = plantinstauser;
        plantReference = db.collection(path);


        plant.clear();


        ActionBar actionBar = getSupportActionBar();
        String title = "PlantInsta Günlüklerim";

        getSupportActionBar().setTitle(title);
        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_navigation);

        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);
      getDataFromFirebase();

        System.out.println("oncreate plantsize " + plant.size());

    }

    private void getDataFromFirebase() {
        recyclerView = findViewById(R.id.plantlistrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlantListAdapter(plant);
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        plantReference
                .orderBy("plantName", Query.Direction.ASCENDING)
                .whereEqualTo("plantUserMail", mAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }
                        if (queryDocumentSnapshots != null) {

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                String plantName = (String) snapshot.get("plantName");
                                String plantAvatar = (String) snapshot.get("plantAvatar");
                                String plantFirstDate = (String) snapshot.get("plantFirstDate");
                                String plantPostCount = (String) snapshot.get("plantPostCount");
                                String plantUserMail = (String) snapshot.get("plantUserMail");
                                plant.add(new PlantModel(plantAvatar, plantName, "RAS", plantAvatar, plantFirstDate, plantUserMail, plantPostCount));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


        /**
         * uzun click
         */
        adapter.setOnItemLongClickListener(new PlantListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {


                AlertDialog.Builder builder = new AlertDialog.Builder(PlantList2.this);
                builder.setMessage(plant.get(position).getPlantName() + " isimli bitki günlüğünüz kalıcı olarak silinecektir !")
                        .setTitle("Günlük Silme")
                        .setIcon(R.drawable.alert);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {


                        System.out.println("Plantsize slindikten önce sayı" + plant.size());                      // recyclerView.removeViewAt(position);

                        userActions.deleteDocFromFirebase(plant.get(position).getPlantName(), "plant", null, plantinstauser);

                        plant.remove(position);
                        System.out.println("Plantsize slindikten sonra sayı" + plant.size());                      // recyclerView.removeViewAt(position);
                        adapter.notifyItemRemoved(position);
                        //
                        //
                        adapter.notifyItemRangeChanged(position, plant.size());

                        adapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("vazgeçti");
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();


            }
        });

/******remove from list******
 *
 */

        /**
         * Tek click
         */
        adapter.setOnItemClickListener(new PlantListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(PlantList2.this, FeedActivity.class);
                intent.putExtra("mark", plant.get(position).getPlantName());
                startActivity(intent);
            }
        });
    }

    public void remove(int position) {

        adapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Plantsize : "+ plant.size());
       System.out.println("resume");

        System.out.println("Plantsize : "+ plant.size());
       //adapter.notifyDataSetChanged();

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.home:
                onResume();
                break;
            case R.id.menu:
                Intent intentu = new Intent(PlantList2.this, UserMenuList.class);

                startActivity(intentu);
                break;
            case R.id.photo:
                Intent intent = new Intent(PlantList2.this, VisualMainActivity.class);
                intent.putExtra("fromList", 0);

                startActivity(intent);

                break;
        }
        return false;
    };

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Start plantsize "+ plant.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    private void setUpRecyclerView() {



        /**
         * Firebase read data
         */




    }

}
















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


       // adapter.setOnItemClickListener(new PlantListAdapter.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(int position) {
               // PlantModel plant = documentSnapshot.toObject(PlantModel.class);
              // String id = plant.getId()
                //String path = documentSnapshot.getReference().getPath();

                // Toast.makeText(PlantList.this,
                //       "Position " + position + " ID " + id + " İsim " + plant.getPlantName()
                //     , Toast.LENGTH_SHORT).show();
                       /*
                Intent intent = new Intent(PlantList.this, FeedActivity.class);
                intent.putExtra("mark", plant.getPlantName());
                startActivity(intent);
                */

          //  }
       // });

       // adapter.setOnItemLongClickListener(new PlantListAdapter.OnItemLongClickListener() {
         //   @Override
       //     public void onItemLongClick(int position) {
              /*
                PlantModel plant = documentSnapshot.toObject(PlantModel.class);

                AlertDialog.Builder builder = new AlertDialog.Builder(PlantList.this);
                builder.setMessage(plant.getPlantName() + " isimli bitki günlüğünüz kalıcı olarak silinecektir !")
                        .setTitle("Günlük Silme")
                        .setIcon(R.drawable.alert);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        userActions.deleteDocFromFirebase(plant.getPlantName(), "plant", null, plantinstauser);


                    }
                });
                builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("vazgeçti");
                    }
                });
                builder.show();
          */
         //   }

        //});

//adapter.notifyDataSetChanged();



       /*
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
                startActivity(intent);
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
                builder.setMessage(plant.getPlantName() + " isimli bitki günlüğünüz kalıcı olarak silinecektir !")
                        .setTitle("Günlük Silme")
                        .setIcon(R.drawable.alert);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        userActions.deleteDocFromFirebase(plant.getPlantName(),"plant",null,plantinstauser);


                    }
                });
                builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
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
}

*/
/*
private void setupBottomAppBar() {
        bottomAppBar = findViewById(R.id.bottomAppBar);


        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {


                case R.id.home:

                    break;
            }
            return false;
        });

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActions.signOutUser();

                Intent intent = new Intent(PlantList.this, MainActivity.class);
                startActivity(intent);
                finish();
                //
            }
        });
        /*
         FloatingActionButton fab = findViewById(R.id.floating_action_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(PlantList.this, VisualMainActivity.class);
                 intent.putExtra("fromList",0);

                startActivity(intent);

            }
        });
         */



