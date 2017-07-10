package com.example.android.universityproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private GoogleApiClient apiClientGoogle;
    private SignInButton signInButton;
    private FirebaseAuth anFirebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure Google Sign In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and
        // the options specified by options.
        apiClientGoogle = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        anFirebaseAuth = FirebaseAuth.getInstance();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(apiClientGoogle);
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult gResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(gResult.isSuccess()){
                GoogleSignInAccount account = gResult.getSignInAccount();
                fBaseAuth(account);
            }else{
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    private void fBaseAuth(GoogleSignInAccount accnt){
        Log.d(TAG, "fBaseAuth:" + accnt.getId());
        AuthCredential gCredentials = GoogleAuthProvider.getCredential(accnt.getIdToken(), null);
        anFirebaseAuth.signInWithCredential(gCredentials)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(new Intent(MainActivity.this, Maps.class));
                            finish();
                        }
                    }
                });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play WServices error.", Toast.LENGTH_SHORT).show();
    }








}