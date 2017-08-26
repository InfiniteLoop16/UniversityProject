package com.example.android.universityproject;


import android.os.Bundle;
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
        DatabaseReference myRef = mDatabaseChat.push();
        keyId = myRef.getKey();
        newPost.setId(keyId);
        myRef.setValue(newPost);
        mDatabaseConvo.child(keyId).push().setValue(newPost);
        finish();
    }


}

