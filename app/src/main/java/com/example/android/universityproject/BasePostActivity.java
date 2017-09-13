package com.example.android.universityproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BasePostActivity extends AppCompatActivity {


    protected ListItem newPost;
    protected EditText eTitle;
    protected EditText eBody;
    protected DatabaseReference mFirebaseDatabaseReference;
    protected DatabaseReference mDatabaseConvo;
    protected FirebaseUser mUser;
    protected FirebaseAuth mFirebaseAuth;
    protected String keyId;
    protected String mTimeAndDate;
    protected FloatingActionButton sendPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        sendPost = (FloatingActionButton) findViewById(R.id.Send_Post);
        sendPost.setOnClickListener(despatchPost);

        DatabaseConnect();

        eTitle = (EditText)findViewById(R.id.TitleBox);
        eBody = (EditText)findViewById(R.id.BodyBox);

        newPost = new ListItem();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();


    }


    // Abstract Method
    public void DatabaseConnect(){}

    // Abstract Method
    public void sendPost(){}


    public View.OnClickListener despatchPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setPostDetails();
            sendPost();
        }

        };



    public void setPostDetails(){
        String title = eTitle.getText().toString().trim();
        String body = eBody.getText().toString().trim();
        newPost.setTitle(title);
        newPost.setBody(body);
        mTimeAndDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(new Date());
        newPost.setTimeAndDateSent(mTimeAndDate);
        newPost.setUserName(mUser.getDisplayName());
        newPost.setUniqueKey(mTimeAndDate+" "+ newPost.getUserName());

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(BasePostActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast toast = Toast.makeText(BasePostActivity.this, R.string.successful_logout, Toast.LENGTH_SHORT );
                                toast.show();
                                finish();
                                startActivity(new Intent(BasePostActivity.this, MainActivity.class));
                            }
                        });
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }







}
