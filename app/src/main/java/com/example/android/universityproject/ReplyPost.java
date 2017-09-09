package com.example.android.universityproject;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyPost extends BasePostActivity{


    private DatabaseReference mDatabaseChild;
    private String childNode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_post);
        Intent i = getIntent();
        childNode = i.getStringExtra("ChildNode");
        DatabaseConnect();
        super.onCreate(savedInstanceState);


    }

    @Override
    public void DatabaseConnect(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseConvo = mFirebaseDatabaseReference.child("Convo's");
        mDatabaseChild = mDatabaseConvo.child(childNode);
    }




    @Override
    public void sendPost(){
        DatabaseReference myRef = mDatabaseChild.push();
        keyId = myRef.getKey();
        newPost.setId(keyId);
        myRef.setValue(newPost);
        Intent i = new Intent(ReplyPost.this, ReplyRecycler.class);
        i.putExtra("uniqueIdReplyPost", childNode);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(ReplyPost.this, ReplyRecycler.class);
        i.putExtra("uniqueIdReplyPost", childNode);
        finish();
    }

}
