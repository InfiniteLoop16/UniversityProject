package com.example.android.universityproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewPost extends BasePostActivity {

    private DatabaseReference mDatabaseChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_post);
        DatabaseConnect();
        super.onCreate(savedInstanceState);

    }

    @Override
    public void DatabaseConnect(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseChat = mFirebaseDatabaseReference.child("chat");
        mDatabaseConvo = mFirebaseDatabaseReference.child("Convo's");

    }

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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(NewPost.this,PostRecyclerActivity.class));
        finish();
    }


}

