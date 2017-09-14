package com.example.android.universityproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyPost extends BasePostActivity{


    private DatabaseReference mDatabaseChild;
    private String childNode;
    private TextView mTitleField;


    /**
     * Override parent class onCreate
     * Dynamically removes the title field as it is not required for message responses
     * Ensures connection to the correct database node
     * takes the database node in string from replyRecycler activity using getExtra()
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_post);
        Intent i = getIntent();
        childNode = i.getStringExtra("ChildNode");
        DatabaseConnect();
        super.onCreate(savedInstanceState);
        mTitleField = (TextView)findViewById(R.id.TitleBox);
        mTitleField.setVisibility(View.GONE);


    }

    /**
     * Method to connect to correct database nodes
     * Sends post contents to Convo's nodes in database
     * Overrides parent class abstract method
     */
    @Override
    public void DatabaseConnect(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseConvo = mFirebaseDatabaseReference.child("Convo's");
        mDatabaseChild = mDatabaseConvo.child(childNode);
    }



    /**
     * Gets unique PushID from firebase push method
     * and sends post to the specified nodes
     * Starts the PostRescylcler activity once complete
     *
     * If no message body is provided, then shows toast requesting title be added.
     */
    @Override
    public void sendPost() {
        if (newPost.getBody().toString().replaceAll("\\s", "").length() > 0) {
            DatabaseReference myRef = mDatabaseChild.push();
            keyId = myRef.getKey();
            newPost.setId(keyId);
            myRef.setValue(newPost);
            Intent i = new Intent(ReplyPost.this, ReplyRecycler.class);
            i.putExtra("uniqueIdReplyPost", childNode);
            startActivity(i);
        } else {
            Toast toast = Toast.makeText(ReplyPost.this, R.string.reply_post_warning, Toast.LENGTH_SHORT);
            toast.show();

        }
    }


    /**
     * When back pressed starts reply recycler
     * includes the getExtra for the relevant child node to avoid null pointer exception in
     * Reply recycler
     */
    @Override
    public void onBackPressed(){
        Intent i = new Intent(ReplyPost.this, ReplyRecycler.class);
        i.putExtra("uniqueIdReplyPost", childNode);
        startActivity(i);
        finish();
    }

}
