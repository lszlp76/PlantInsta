package com.zlpls.plantinsta;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    ImageView imageView;
    EditText emailText, passwordText;
    TextView forgottenPass, newMember;
    Button signInButton, signInButtonGoogle;
    UserActions userActions = new UserActions();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView2);
        emailText = findViewById(R.id.editTextTextPersonName);
        passwordText = findViewById(R.id.editTextTextPersonName2);
        forgottenPass = findViewById(R.id.textView2);
        newMember = findViewById(R.id.textView3);
        mAuth = FirebaseAuth.getInstance();

       /* new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});
                })
                .start(); */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.

        super.onStart();
        //connectionCheck();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();

        }


    }
public void gotoSignUp (View view ){

        Intent intent = new Intent(MainActivity.this,NewMemberSignUp.class);
        startActivity(intent);
}
    public void signInWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println("HATA :- > "+e.getMessage());
                Toast.makeText(getApplicationContext(),


                        e.getMessage(), Toast.LENGTH_LONG).show();
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(getApplicationContext(), "Hoşgeldin " + user.getDisplayName(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            //
                        }

                        // ...
                    }
                });
    }

    private void signOut() {


        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });


    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //;
                    }
                });
    }


    /*FİREBAS AUTH İLE GİRİŞ *******************************/
    public void signIn(View view) {
        if (!emailText.getText().toString().equals("") && !passwordText.getText().toString().equals("")) {

            String email = emailText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                //Toast.makeText(getApplicationContext(), "Hoşgeldin " + user.getEmail(), Toast.LENGTH_LONG).show();
                                // to do
                                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(getApplicationContext(), "Wrong ID / password",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "Email or Password should be filled",
                    Toast.LENGTH_SHORT).show();

        }
    }


    public void showAlert(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder
                .setTitle(title)
                .setMessage(message)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MainActivity.this, PlantList.class);
                        startActivity(intent);
                    }
                })

                .show();


    }

    public void forgotPass(View view) {

        if (!emailText.getText().toString().equals("")) {
            mAuth.sendPasswordResetEmail(emailText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "E-mail has been sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong e-mail ",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Enter your e-mail adress used for registration "
                            ,
                    Toast.LENGTH_SHORT).show();
        }


    }


    public void connectionCheck() {
        if (userActions.checkMyConnection(getApplication()) == false) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet Bağlantısı")
                    .setMessage("Lütfen İnternet Bağlantınızı kontrol ediniz..")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert);
            builder.show();


        }

    }


}
