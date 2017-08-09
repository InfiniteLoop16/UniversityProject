package com.example.android.universityproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostRecyclerActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recycler);

        // Creates reference link to database.
        mDataRef = FirebaseDatabase.getInstance().getReference();

        // Creates the RecyclerView. Recent post located at the top.
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_tutorial);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Creation of FAB onClick Listener
        FloatingActionButton addPost = (FloatingActionButton) findViewById(R.id.add_Post);
        addPost.setOnClickListener(mAddPostListener);


        mAdapter = new FirebaseRecyclerAdapter<ListItem, ItemViewHolder>(
                ListItem.class,
                R.layout.post_recycler_item,
                ItemViewHolder.class,
                mDataRef.child("chat")) {

            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, ListItem post, int position) {
                viewHolder.bind(post);

            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private View.OnClickListener mAddPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), NewPost.class);
            startActivity(i);

        }
    };
}
