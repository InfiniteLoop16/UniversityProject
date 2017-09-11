package com.example.android.universityproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {


    // Google API Instance variables
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 1;
    private int Flag = 1;

    // Firebase Instance Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuStateListener;  @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*SignInButton logIn = (SignInButton) findViewById(R.id.sign_in_button);
        logIn.setOnClickListener(logInSwitch);*/

        /*Button logOutButton = (Button) findViewById(R.id.sign_out_button);
        logOutButton.setOnClickListener(logOut);*/


            mFirebaseAuth = FirebaseAuth.getInstance();

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);

            mAuStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        //startActivity(new Intent(MainActivity.this, RecyclerTutorialActivity.class));
                    } else {

                    }

                }
            };


        /*// Configure Google SignIn as provided by Android Google documentation.
        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Configure Google API Client. Provided by Android Google documentation.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this *//* FragmentActivity *//*, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast toast = Toast.makeText(MainActivity.this, "Connection to Google failed.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
                .build();*/


        }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuStateListener);

    }



    // Sign in method, uses sign in intent provided by Android Google documentation.
    // Sends request to Google API client, with constant RC_SIGN_IN defined in this activity.
    // Response of intent used in onActivityResult method.
    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
        /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);*/
    }


    // onClickListener calls signIn Method.
    public View.OnClickListener logInSwitch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signIn();
        }


    };

    // Verifies request code value, sent from Google API, to RC_SIGN_IN constant.
    // If values match then Users federated account (Google, Gmail, Hotmail) is verified
    // User is signed in, and allowed to use the application.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                startActivity(new Intent(MainActivity.this, PostRecyclerActivity.class));
                finish();
                return;
            }else{
                if(response == null){
                    Toast toast = Toast.makeText(MainActivity.this, "Bye bye", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast toast = Toast.makeText(MainActivity.this, "No internet access. Please check your connection", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast toast = Toast.makeText(MainActivity.this, "Unknown Error", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        }
    }

    // Authenticates users with the Firebase database. Provided by Google Firebase Android documentation.
    // If authentication fails, then the user is presented with a toast.
    // A user receives an authentication token from Firebase. There information is recorded in firebase.
    // If user is successful they are taken to the PostRecycler Activity.
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

    // onClickListener for logging user ot from their email account.
    // When successful a toast will display, confirming.
    public View.OnClickListener logOut = new View.OnClickListener(){
        @Override
        public void onClick(View v) { {
            AuthUI.getInstance()
                    .signOut(MainActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast toast = Toast.makeText(MainActivity.this, R.string.successful_logout, Toast.LENGTH_SHORT );
                            toast.show();
                        }
                    });
        }

        }

    };

}



