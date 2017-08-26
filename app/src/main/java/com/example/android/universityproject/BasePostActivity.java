package com.example.android.universityproject;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.universityproject.R;
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

        sendPost = (FloatingActionButton) findViewById(R.id.Send_Post);
        sendPost.setOnClickListener(mSendPost);

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


    public View.OnClickListener mSendPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setPostDetails();
            sendPost();

        }
    };

    public void setPostDetails(){
        String title = eTitle.getText().toString();
        String body = eBody.getText().toString();
        newPost.setTitle(title);
        newPost.setBody(body);
        mTimeAndDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(new Date());
        newPost.setTimeAndDateSent(mTimeAndDate);
        newPost.setUserName(mUser.getDisplayName());
        newPost.setUniqueKey(mTimeAndDate+" "+ newPost.getUserName());

    }






}
