package com.example.android.universityproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewPost extends BasePostActivity {

    private DatabaseReference mDatabaseChat;


    /**
     * Overrides parent class oncreate
     * Ensures that its own DatabaseConnect method is used as opossed to the abstract method
     * of its parent SuperClass.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_post);
        DatabaseConnect();
        super.onCreate(savedInstanceState);


    }

    /**
     * Method to connect to correct database nodes
     * Sends post contents to chat and Convo's nodes in database
     * Overrides parent class abstract method
     */
    @Override
    public void DatabaseConnect(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseChat = mFirebaseDatabaseReference.child("chat");
        mDatabaseConvo = mFirebaseDatabaseReference.child("Convo's");

    }


    /**
     * Gets unique PushID from firebase push method
     * and sends post to the specified nodes
     * Starts the PostRescylcler activity once complete
     *
     * If no title is provided, then shows toast requesting title be added.
     */
    @Override
    public void sendPost(){
        if (newPost.getTitle().toString().replaceAll("\\s", "").length() > 0) {
            DatabaseReference myRef = mDatabaseChat.push();
            keyId = myRef.getKey();
            newPost.setId(keyId);
            myRef.setValue(newPost);
            mDatabaseConvo.child(keyId).push().setValue(newPost);
            startActivity(new Intent(NewPost.this,PostRecyclerActivity.class));
            finish();
        } else {
            Toast toast = Toast.makeText(NewPost.this, R.string.post_warning, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /**
     * Starts the PostRecycler activity when back is pressed
     */
    @Override
    public void onBackPressed(){
        startActivity(new Intent(NewPost.this,PostRecyclerActivity.class));
        finish();
    }


}

