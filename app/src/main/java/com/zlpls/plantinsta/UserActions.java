package com.zlpls.plantinsta;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class UserActions {
    public static String TAG = " sonuçlar";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Context context;
    public static ArrayList<Integer> postCount;
    public static int fromList;
    public static String plantNameToModify;
    private static ArrayList<PlantModel> plantdata;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private int postCountValue;
    public static boolean imageFromGallery ;

    public boolean isImageFromGallery() {

        return imageFromGallery;
    }

    public  void setImageFromGallery(boolean imageFromGallery) {
        UserActions.imageFromGallery = imageFromGallery;
    }
    public UserActions(int postCountValue, int fromList) {
        this.postCountValue = postCountValue;
        UserActions.fromList = fromList;

    }

    public UserActions() {

    }

    public String getPlantNameToModify() {
        return plantNameToModify;
    }

    public void setPlantNameToModify(String plantNameToModify) {
        UserActions.plantNameToModify = plantNameToModify;
    }

    public int getFromList() {
        return fromList;
    }

    public void setFromList(int fromList) {
        UserActions.fromList = fromList;
    }

    public Context getContext() {
        return context;
    }

    public int getPostCountValue() {

        postCountValue = postCount.get(0);

        return postCountValue;
    }

    public void signOutUser(Context context) {




    }

    public void setPostCountFor(Context mContext,String plantname, String type, String position) {
        /**
         * BU fonksiyon yeni bir bitki eklendiğinde veya bir bitki günlüğünden bir günce
         * silindiğinde veritabanındaki sayaç verisini arttırır veya azaltır. Kullanım şekli
         * olarak:
         * Yeni bitki günlüğü eklendiğinde --> setPostCountFor(Bitki Günlük Adı, "add") şeklinde verildiğinde sayacı 1 olarak
         * başlatır ve her günce eklendiğinde sayac verisini okuyarak 1 arttırır.
         * Bir bitki güncesi silindiğinde --> setPostCountFor(bitki günlük adı,"del") şeklinde verildiğinde sayacı
         * 1 azaltır
         */

        postCount = new ArrayList<>();
        try {
            CollectionReference plantReference = db.collection("Plants");

            Query query = plantReference.whereEqualTo("plantName", plantname);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    } else {
                        List<PlantModel> plantList = value.toObjects(PlantModel.class);
                        postCount.add(Integer.parseInt(plantList.get(0).getPlantPostCount()));

                        if (type == "add") {
                            upgradeCounterFor(postCount.get(0), plantname, "add");

                        } else {
                            if (postCount.get(0) != 1)
                            {
                                upgradeCounterFor(postCount.get(0), plantname, "del");
                                deleteDocFromFirebase(plantname, "feed", position,"");

                            }else {
                                Toast.makeText(mContext, "Günlük silme yapmalısınız!", Toast.LENGTH_LONG).show();

                            }


                        }

                    }
                }

            });

        } catch (Exception e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void upgradeCounterFor(int countw, String plantname, String type) {
        //  System.out.println("3 sayaç değeri--> " + postCount.get(0));

        if (type == "add") {
            countw++;//yeni bitki güncesi eklenirse
        } else {
            countw--;//eklenen bir bitki güncesi slinirse
        }
        Map<String, Object> countData = new HashMap<>();
        countData.put("plantPostCount", String.valueOf(countw));
        CollectionReference plantReference = db.collection("Plants");
        plantReference.document(plantname).update(countData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });


    }

    public void revokeAccess() {
        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener((Executor) this,
                task -> {
                    //;
                });
    }


    @SuppressLint("RestrictedApi")
    public void deleteDocFromFirebase(String name, String type, String feedname,String userName) {
        switch (type) {
            case ("plant"):
                try {
                    db.collection(userName).document(name)

                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }

                            });

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case ("feed"):
                try {

                    db.collection(userName).document(name).collection("history")
                            .document(feedname)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }

                            });

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    /**
     * Ağ bağlantısı kontrolü*
     *
     * @param application
     * @return
     */
    public boolean checkMyConnection(Application application) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }


}






