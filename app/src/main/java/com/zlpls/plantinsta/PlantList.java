package com.zlpls.plantinsta;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
    String plantinstauser;
    private com.zlpls.plantinsta.AddPlantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);
        // Toolbar toolbar = findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();
        plantinstauser = mAuth.getCurrentUser().getEmail();
        String path = plantinstauser;
        plantReference = db.collection(path);

        /* FRAGMAN HAZIR
        FragmentManager fragmentManager = (getSupportFragmentManager());
        fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.replace(R.id.frame_layout,firstFragment).commit();
        //frame_layout, activity.main.xml içinde fragmentlerin gösterileceği layout isimi
        */
        //setupBottomAppBar();
        setUpRecyclerView();
        ActionBar actionBar = getSupportActionBar();
        String title = "PlantInsta Günlüklerim";

        getSupportActionBar().setTitle(title);
        BottomNavigationView bottomAppBar =  findViewById(R.id.bottom_navigation);

        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);



        adapter.notifyDataSetChanged();

       //kamera izinleri


    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void setUpRecyclerView() {

        Query query = plantReference.orderBy("plantFirstDate", Query.Direction.DESCENDING);
              //  .whereEqualTo("plantUserMail", mAuth.getCurrentUser().getEmail());
        FirestoreRecyclerOptions<PlantModel> options = new FirestoreRecyclerOptions.Builder<PlantModel>()
                .setQuery(query, PlantModel.class)
                .build();

        adapter = new com.zlpls.plantinsta.AddPlantAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.plantlistrecyclerview);
        recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));


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



