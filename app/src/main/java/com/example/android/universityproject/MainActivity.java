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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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



    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLogIn();
    }

    /**
     * Boiler plate code provided by Firebase UI Auth library - https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
     * Starts login flow.
     */
    public void requestLogIn(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }


    /**
     * Verifies user has logged in.
     * Shows toast if response null, user pressing back
     * Boilder plat code provided by Firebase UI Auth library - https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
     * @param requestCode: RC_SIGN_IN constant variable value
     * @param resultCode: Response from user (RESULT_OK,NO_NETWORK, UNKNOWN_ERROR)
     * @param data: The intent awaiting result
     */
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
                    Toast toast = Toast.makeText(MainActivity.this, R.string.good_bye, Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.unknown, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        }
    }



}



