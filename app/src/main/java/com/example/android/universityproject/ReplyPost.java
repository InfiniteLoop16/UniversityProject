package com.example.android.universityproject;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplyPost extends AppCompatActivity {

    private ListItem newPost;
    private EditText eTitle;
    private EditText eBody;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mDatabaseChild;
    private DatabaseReference mDatabaseConvo;
    private String keyId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private String mTimeAndDate;
    private String childNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Intent i = getIntent();
        childNode = i.getStringExtra("ChildNode");

        FloatingActionButton sendPost = (FloatingActionButton) findViewById(R.id.Send_Post);
        sendPost.setOnClickListener(mSendPost);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseConvo = mFirebaseDatabaseReference.child("Convo's");
        mDatabaseChild = mDatabaseConvo.child(childNode);

        eTitle = (EditText)findViewById(R.id.TitleBox);
        eBody = (EditText)findViewById(R.id.BodyBox);

        newPost = new ListItem();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();


    }

    public View.OnClickListener mSendPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = eTitle.getText().toString();
            String body = eBody.getText().toString();


            newPost.setTitle(title);
            newPost.setBody(body);
            mTimeAndDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(new Date());
            newPost.setTimeAndDateSent(mTimeAndDate);
            newPost.setUserName(mUser.getDisplayName());
            newPost.setUniqueKey(mTimeAndDate+" "+ newPost.getUserName());



            DatabaseReference myRef = mDatabaseChild.push();
            keyId = myRef.getKey();
            newPost.setId(keyId);
            myRef.setValue(newPost);

            finish();
        }
    };


}

