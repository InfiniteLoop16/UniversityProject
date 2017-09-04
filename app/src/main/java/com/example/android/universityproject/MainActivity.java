package com.example.android.universityproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {


    // Google API Instance variables
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 1;

    // Firebase Instance Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignInButton logIn = (SignInButton) findViewById(R.id.sign_in_button);
        logIn.setOnClickListener(logInSwitch);

        Button logOut = (Button) findViewById(R.id.sign_out_button);
        logOut.setOnClickListener(LogOut);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //startActivity(new Intent(MainActivity.this, RecyclerTutorialActivity.class));
                }
            }
        };


        // Configure Google SignIn as provided by Android Google Documentation
        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast toast = Toast.makeText(MainActivity.this, "Oops!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
                .build();


    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuStateListener);

    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public View.OnClickListener logInSwitch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signIn();
        }


    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult gResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (gResult.isSuccess()) {
                // If the Google sign in is successful, then authenticate account with Firebase
                GoogleSignInAccount account = gResult.getSignInAccount();
                fBaseAuth(account);
            }
        }
    }

    private void fBaseAuth(GoogleSignInAccount accnt) {
        AuthCredential gCredentials = GoogleAuthProvider.getCredential(accnt.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(gCredentials)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, PostRecyclerActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed. Please check your network connection.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    public View.OnClickListener LogOut = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Toast toast = Toast.makeText(MainActivity.this, R.string.successful_logout, Toast.LENGTH_SHORT );
            toast.show();

        }

    };

}






