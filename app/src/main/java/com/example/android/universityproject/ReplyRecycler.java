package com.example.android.universityproject;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyRecycler extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFirebaseAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDataRef;
    private DatabaseReference mDataRefChild;
    private String searcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_recycler);

        // Gets the putExtra from the CardView. This is the Unique push ID which forms the node key.
        Intent i = getIntent();
        searcher = i.getStringExtra("uniqueID");

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mDataRefChild = mDataRef.child("Convo's").child(searcher);


        // Create RecyclerView
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_reply_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // Creation of FAB onClick Listener for reply post
        FloatingActionButton addPost = (FloatingActionButton) findViewById(R.id.add_Reply_Post);
        addPost.setOnClickListener(mAddReplyPostListener);

        // Creation of FAB onClick Listener to share location
        FloatingActionButton shareLocation = (FloatingActionButton) findViewById(R.id.share_Location);
        shareLocation.setOnClickListener(mShareLocation);




        // FirebaseRecycler Adapter Subclass.
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ListItem, ReplyItemViewHolder>(
                ListItem.class,
                R.layout.reply_recycler_item,
                ReplyItemViewHolder.class,
                mDataRefChild) {

            @Override
            protected void populateViewHolder(ReplyItemViewHolder viewHolder, ListItem post, int position) {
                viewHolder.bind(post);

            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private View.OnClickListener mAddReplyPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ReplyPost.class);
            i.putExtra("ChildNode", searcher);
            startActivity(i);

        }
    };

    private View.OnClickListener mShareLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), Maps.class);
            i.putExtra("ChildNode", searcher);
            startActivity(i);
        }
    };


}

