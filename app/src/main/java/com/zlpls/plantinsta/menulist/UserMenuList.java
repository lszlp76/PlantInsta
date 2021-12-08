package com.zlpls.plantinsta.menulist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.zlpls.plantinsta.MainActivity;
import com.zlpls.plantinsta.PlantList;
import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UserActions;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class UserMenuList extends AppCompatActivity {
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.home:
                Intent intente = new Intent(UserMenuList.this, PlantList.class);

                intente.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intente);
                break;
            case R.id.menu:

                break;
            case R.id.photo:


                break;
        }
        return false;
    };
    /**********************************/

    ListView listView;
    ArrayAdapter<String> listAdapter;
    String[] listItem;
    UserActions userActions = new UserActions();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_menu_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Neşeli Bahçe");

        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_navigation);

        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);
        /**
         * 1.önce layout üzerinde listview ekle
         * 2.başka bir xml ile görünülenecekleri yao
         */


        listItem = getResources().getStringArray(R.array.UserMenuList);
        listView = findViewById(R.id.userMenuList);
        //listview yazı karakteri
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItem) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.rgb(106, 27, 154));
                return view;
            }
        };
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int value = listAdapter.getPosition(listItem[position]);

                String link = "";

                switch (value) {


                    case 5: //çıkış
                        mAuth = FirebaseAuth.getInstance();
                        //System.out.println("Kullanıcı cıkıs: " + mAuth.getCurrentUser());

                        Intent plantinstaweblink = new Intent(UserMenuList.this, MainActivity.class);
                        plantinstaweblink.addFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(plantinstaweblink);
                        finishAffinity();
                        //permission denied hatası çözümü için signOut en son operasyon olarak
                        // göstermek yeterli oluyor
                        mAuth.signOut();
                        break;
                    case 4://neselibahce
                        link = "https://www.hepsiburada.com/magaza/neseli-bahce";
                        Intent plantinstaweblink4 = new Intent(UserMenuList.this, PlantInstaWeb.class);
                        plantinstaweblink4.putExtra("browser", link);
                        startActivity(plantinstaweblink4);

                        break;
                    case 1://how to
                        link = "https://www.agromtek.com/plantInsta.html";
                        Intent plantinstaweblink1 = new Intent(UserMenuList.this, PlantInstaWeb.class);
                        plantinstaweblink1.putExtra("browser", link);
                        startActivity(plantinstaweblink1);
                        break;
                    case 3://iletişim
                        link = "https://www.agromtek.com/plantInstailetisim.html";
                        Intent plantinstaweblink3 = new Intent(UserMenuList.this, PlantInstaWeb.class);
                        plantinstaweblink3.putExtra("browser", link);
                        startActivity(plantinstaweblink3);
                        break;
                    case 2://sözleşme
                        link = "https://www.agromtek.com/plantInstasozlesme.html";

                        Intent plantinstaweblink2 = new Intent(UserMenuList.this, PlantInstaWeb.class);
                        plantinstaweblink2.putExtra("browser", link);
                        startActivity(plantinstaweblink2);
                        break;
                }

            }
        });

    }

    private void SignOutPlantinsta() {


    }
}


