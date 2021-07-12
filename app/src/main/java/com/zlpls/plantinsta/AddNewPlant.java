package com.zlpls.plantinsta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zlpls.plantinsta.menulist.UserMenuList;
import com.zlpls.plantinsta.visualselection.FileOperations;
import com.zlpls.plantinsta.visualselection.VisualMainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddNewPlant extends AppCompatActivity {
    static ImageView imageView;
    private final Handler handler = new Handler();
    public ArrayList<PlantModel> mplantModel;
    public FileOperations fileOperations = new FileOperations();
    EditText comment, newplantname;
    ProgressBar progressBar;
    FloatingActionButton fab;
    Bitmap selectedImage, selectedImageAsIcon;
    Uri imageData;
    String currentPhotoPath, selectedImageFromUser;
    byte[] byteAvatar;
    BottomNavigationView bottomAppBar;
    UserActions userActions = new UserActions();
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebasedb;
    private FirebaseAuth firebaseAuth;
    private int progressStatus = 0;
    private Bitmap selectedImageasIcon;
    private StorageTask mLoadNewPlant;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.home:
                Intent intentq = new Intent(AddNewPlant.this, PlantList.class);
                startActivity(intentq);
                finish();
                break;
            case R.id.menu:
                Intent intentu = new Intent(AddNewPlant.this, UserMenuList.class);

                startActivity(intentu);

                break;
            case R.id.photo:

                checkNewName();

                break;
        }
        return false;
    };


    private static int getImageRotation(final File imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile.getPath());
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    private static Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_plant);
        //setupBottomAppBar();
        newplantname = findViewById(R.id.editTextTextPersonName3);
        comment = findViewById(R.id.editTextTextPersonName4);
        //fab = findViewById(R.id.floating_action_button);
        imageView = findViewById(R.id.newplantimageview);
        progressBar = findViewById(R.id.progressBar);
        firebasedb = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        /* Galerifragmane veya TakePhtofragmandan gelen selectedİmagefromuser bilgisi */
        Intent intent = getIntent();
        selectedImageFromUser = intent.getStringExtra("selectedimagefromuser");
        // gelen resmi imageivew ekle
        Picasso.get()
                .load("file:" + selectedImageFromUser)
                .into(imageView);

        // önce dösyaya çevir
        File bitmapFile = new File(selectedImageFromUser);
        //sonra bitmap e çevir

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        selectedImage = fileOperations.getStreamByteFromImage(bitmapFile);
        // selectedImage =    BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), bmOptions);

        ;


        /********BOTTOM NAV BAR OLUŞTURMA ******************/
        bottomAppBar = findViewById(R.id.bottom_navigation);
        bottomAppBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomAppBar.setItemIconTintList(null);
        Menu menu = bottomAppBar.getMenu();
        menu.findItem(R.id.photo).setIcon(R.drawable.ic__send);
        menu.findItem(R.id.photo).setTitle("Add New Diary");
/*
 fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storageTask == null) {
                    checkNewName();
                }
            }
        });
 */

//********PROGRESS BAR******************************************
        progressBar = findViewById(R.id.progressBar);
        //  progressTextView=findViewById(R.id.progress_textview);
        progressBar.setVisibility(android.view.View.INVISIBLE);
        // progressTextView.setVisibility(android.view.View.INVISIBLE);
        // progressTextView.setText(progressStatus )+ "/" + progressBar.getMax());
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//*****************************************************************************

    }

    public void chooseImage(View view) {

        Intent intent = new Intent(AddNewPlant.this, VisualMainActivity.class);

        startActivity(intent);
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        File f = new File(currentPhotoPath);
        int imageRotation = getImageRotation(f);
        if (imageRotation != 0)
            selectedImage = getBitmapRotatedByDegree(bitmap, imageRotation);
        selectedImage = bitmap;
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 10, stream);

        imageView.setImageBitmap(selectedImage);


        //bunu açarsan GALERİ'de PİCTURES adlı bir klasör yaratır ve oraya kayıt yapar
        //kayıt yapacağın klasör adını flie_paths xml'de veriyorsun

        MediaStore.Images.Media.insertImage(getContentResolver(), selectedImage, "PlantInsta", "PlantInsta");


        deleteImageFileInStorage();
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void savePlant() {
       /*
        newplantname = findViewById(R.id.editTextTextPersonName3);
        comment = findViewById(R.id.editTextTextPersonName4);
        fab = findViewById(R.id.floating_action_button);
        imageView = findViewById(R.id.newplantimageview);
        progressBar = findViewById(R.id.progressBar);
        */


    }


    public void sendPicture() {
        //önce
        if (!newplantname.getText().toString().equals("") && selectedImage != null) {
            //yeni isimi al
            String newPlantMarkerName = newplantname.getText().toString();

            //selectedImage ı küçült
            selectedImage = makeSmallerImage(selectedImage, 800);
            //burada bitmap yaratılmış oluyor!!

            if (selectedImageAsIcon == null) {

            }

            //**** önce image byte a çevir***********

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            //***************************************
            /* ID ver****************************************/


            UUID uuid = UUID.randomUUID();
            final String imagename = "images/" + uuid + ".jpg";

            mLoadNewPlant = storageReference.child("images").child(uuid + ".jpg").putBytes(bytes).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // storage kayıt başarılı olduğunda yapılacak şey veriyolunu almak
                            StorageReference restorageReference = FirebaseStorage.getInstance().
                                    getReference(imagename);// kayıt edilen dosyanın yeri
                            //download URL yi almak
                            restorageReference.getDownloadUrl().addOnSuccessListener
                                    (new OnSuccessListener<Uri>() {


                                        @Override
                                        //veritabanına yazma buradan olacak
                                        public void onSuccess(Uri uri)
                                        {

                                            //storage a yazma başarılı olursa resimin url sini
                                            // alarak , cloud firestore a gönderilecek
                                            //paketi hazırlayıp gönderiyor.
                                            // postData paketin adı
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                            String usermail = firebaseUser.getEmail();
                                            //resimin yolu
                                            String downloadUrl = uri.toString();
                                            String explication = comment.getText().toString();

                                            /********   bu kısım ile irestore bir kere boş koyuyorsun ***/
                                            HashMap<String, Object> dummyMap = new HashMap<>();
                                            String plantinstauser = firebaseAuth.getCurrentUser().getEmail();
                                            String path = plantinstauser;


                                            DocumentReference df = firebasedb.collection(path)
                                                    .document(newPlantMarkerName);
                                            df.set(dummyMap);  // add empty field, wont shown in console
                                            df.collection("history");


                                            //kullanıcı kimliğini ayrı bir HashMap'e atıyor
                                            HashMap<String, Object> user = new HashMap<>();
                                            //user.put("plantUserMail", usermail);
                                            //user.put("plantAvatar","");
                                            //user.put("plantFirstDate","");

                                            //Plants altındaki documents lara alan ekliyoruz.
                                            // böylece her dökumanı kimin eklediği
                                            //belli oluyor.
                                            /*****Gonderilecek hashmap**********************/

                                            HashMap<String, Object> postData = new HashMap<>();
                                            postData.put("image", downloadUrl);
                                            postData.put("comment", explication);
                                            postData.put("date", FieldValue.serverTimestamp());
                                            postData.put("storageID", uuid);

                                            makeAvatar();


                                            // veriyi history nin altına yazıyorsun

                                            firebasedb.collection(path)
                                                    .document(newPlantMarkerName)

                                                    .collection("history")
                                                    .add(postData).addOnSuccessListener
                                                    (new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(AddNewPlant.this,
                                                                    "Oluşturuldu", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(AddNewPlant.this,
                                                                    PlantList.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddNewPlant.this,
                                                            e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }


                                    });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");

                    progressBar.setProgress((int) progress);
                    progressBar.setVisibility(View.VISIBLE);
                    //progressTextView.setVisibility(android.view.View.VISIBLE);
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewPlant.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            });


        } else {
            Toast.makeText(getApplicationContext(), "Hatalı İşlem", Toast.LENGTH_LONG).show();
        }

    }

    public void checkNewName() {

        if (!newplantname.getText().toString().equals("") && selectedImage != null) {
            final String[] newPlantMarkerName = {newplantname.getText().toString()};
            String plantinstauser = firebaseAuth.getCurrentUser().getEmail();
            String path = plantinstauser;

            final ArrayList<String> list = new ArrayList<>();
            list.clear();
            firebasedb.collection(path)
                   // .whereEqualTo("plantUserMail", firebaseAuth.getCurrentUser().getEmail())
                    .get().addOnCompleteListener
                    (new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String name = document.getId();
                                    if (newplantname.getText().toString().equals(name)) {
                                        list.add(name);
                                    }
                                }
                                if (list.size() > 0) {
                                    Toast.makeText(getApplicationContext(), newPlantMarkerName[0] + " ismi kullanılmaktadır", Toast.LENGTH_LONG).show();
                                    newPlantMarkerName[0] = "";

                                    newplantname.setText("");
                                    newplantname.setHint("Farklı bir isim giriniz");
                                    newplantname.setActivated(true);

                                } else {
                                    {
                                        if (mLoadNewPlant != null && mLoadNewPlant.isInProgress()) {
                                            Toast.makeText(AddNewPlant.this, "Yükleme devam ediyor", Toast.LENGTH_SHORT).show();

                                        } else {
                                            sendPicture();
                                        }


                                    }


                                }
                            }

                        }

                    });


        } else {
            Toast.makeText(getApplicationContext(), "Günlük adı girmelisiniz !", Toast.LENGTH_LONG).show();
        }

    }

    /*

     */
    public void deleteImageFileInStorage() {
        File f = new File(currentPhotoPath);

        Uri contentUri = Uri.fromFile(f);
        File fdelete = new File(Objects.requireNonNull(contentUri.getPath()));
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + contentUri.getPath());
            } else {
                System.out.println("file not Deleted :" + contentUri.getPath());
            }
        }
    }

    public void makeAvatar() {
        String newPlantMarkerName = newplantname.getText().toString();
        selectedImageAsIcon = makeSmallerImage(selectedImage, 300);
        ByteArrayOutputStream byteArrayOutputStreamAvatar = new ByteArrayOutputStream();
        selectedImageAsIcon.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamAvatar);
        byteAvatar = byteArrayOutputStreamAvatar.toByteArray();

        /* ID ver****************************************/

        UUID uuid = UUID.randomUUID();
        final String imagename = "images/" + uuid + ".jpg";
        storageReference.child("images").child(uuid + ".jpg").putBytes(byteAvatar)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // storage kayıt başarılı olduğunda yapılacak şey veriyolunu almak
                        StorageReference restorageReference = FirebaseStorage.getInstance().getReference(imagename);// kayıt edilen dosyanın yeri
                        //download URL yi almak
                        restorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            //veritabanına yazma buradan olacak
                            public void onSuccess(Uri uri) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String plantinstauser = firebaseAuth.getCurrentUser().getEmail();
                                String path = plantinstauser;
                                DocumentReference df = db.collection(path)
                                        .document(newPlantMarkerName);
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String usermail = firebaseUser.getEmail();
                                String downloadUrl = uri.toString();

                                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                                Map<String, Object> plant_avatar = new HashMap<>();
                                plant_avatar.put("plantAvatar", downloadUrl);
                                plant_avatar.put("plantFirstDate", timeStamp);
                                plant_avatar.put("plantUserMail", usermail);
                                plant_avatar.put("plantPostCount", "1"); //ilk defa eklediği için 1
                                plant_avatar.put("plantName", newPlantMarkerName);

                                df.update(plant_avatar)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //  Log.d("sonuc", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Avatar kayıt hatası " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                //Log.w("sonuc", "Error writing document", e);
                                            }
                                        });


                            }
                        });
                    }
                });
    }
/*
    private void setupBottomAppBar() {

        bottomAppBar = findViewById(R.id.bottomAppBar);


        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:

                    Intent intent = new Intent(AddNewPlant.this, PlantList.class);
                    startActivity(intent);
                    finish();
                    break;


            }
            return false;
        });

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActions.signOutUser();
                Intent intent = new Intent(AddNewPlant.this, MainActivity.class);
                startActivity(intent);
                finish();
                //
            }
        });
        //click event over FAB
        // findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
        //   @Override
        // public void onClick(View view) {
        //   Toast.makeText(MainActivity.this, "FAB Clicked.", Toast.LENGTH_SHORT).show();
        // }
        //});
    }
*/
}