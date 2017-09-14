package com.example.android.universityproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyRecycler extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFirebaseAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference dataRef;
    private DatabaseReference dataRefChild;
    private String searcher;

    private FloatingActionButton replyPost;
    private FloatingActionButton shareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_recycler);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Gets the putExtra from the CardView. This is the Unique push ID which forms the node key.
        // The if statement is required depending on whether ReplyRecycler is started from PostRecycler,
        // ReplyPostRecycler or Maps.
        Intent i = getIntent();
        if(i.getStringExtra("uniqueID")!=null){
            searcher = i.getStringExtra("uniqueID");
        }else if(i.getStringExtra("uniqueIdMaps")!=null){
        searcher = i.getStringExtra("uniqueIdMaps");
        }else{
            searcher = i.getStringExtra("uniqueIdReplyPost");
        }

        dataRef = FirebaseDatabase.getInstance().getReference();
        dataRefChild = dataRef.child("Convo's").child(searcher);


        // Create RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_reply_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // Creation of FAB onClick Listener for reply post
        replyPost = (FloatingActionButton) findViewById(R.id.add_Reply_Post);
        replyPost.setOnClickListener(startReplyPost);

        // Creation of FAB onClick Listener to share location
        shareLocation = (FloatingActionButton) findViewById(R.id.share_Location);
        shareLocation.setOnClickListener(startShareLocation);


        hideAndRevealFABS();




        /**
         * Instantiates the Firebase Recycler Adapter
         * Takes the POJO class, layout file, viewholder class and the datbase refernce
         * as arguments
         */
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ListItem, ReplyItemViewHolder>(
                ListItem.class,
                R.layout.reply_recycler_item,
                ReplyItemViewHolder.class,
                dataRefChild){

            /**
             * Calls bind method from ReplyViewHolder class
             * Binds the data from the database to the reply_recyler_item layout file
             * If first oringal post, turn blue to differentiate from replies
             * @param viewHolder: Holder for the Layout file
             * @param post: ListItem class object storing information from database
             * @param position: The position the ViewHolder has in the recyclerView
             */
            @Override
            protected void populateViewHolder(ReplyItemViewHolder viewHolder, ListItem post, int position) {
                if(position == 0){
                    viewHolder.bindOriginal(post);
                    viewHolder.setCardColour();
                }else{
                    viewHolder.bindReplies(post);
                }
            }
        };

        mRecyclerView.setAdapter(mFirebaseAdapter);
    }


    /**
     * Hide floating action buttons when scrolling down
     * Revleas floating action buttons when scrolling up
     */
    private void hideAndRevealFABS(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            /**             *
             * @param recyclerView: The recyclerView
             * @param dx: Horizontal scrolling variable
             * @param dy: Vertical scrolling variable
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0){
                    replyPost.hide();
                    shareLocation.hide();}
                else if (dy < 0){
                    shareLocation.show();
                    replyPost.show();}
            }
        });
    }


    /**
     * Starts the ReplyPOst activity
     * Send the ReplyPost activity the pushID
     */
    private View.OnClickListener startReplyPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ReplyPost.class);
            i.putExtra("ChildNode", searcher);
            startActivity(i);
            finish();
        }
    };


    /**
     * Starts the maps activity
     * Sends map activity the pushID reference
     */
    private View.OnClickListener startShareLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), Maps.class);
            i.putExtra("ChildNode", searcher);
            startActivity(i);
            finish();
        }
    };

    /**
     * Creates the toolbar and options menu
     * @param menu: The menu bar
     * @return: Returns the Action tool bar
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Provides functionality to thee overflow.
     * Signout overflow option signs user out and takes them to main activity page
     * @param item: The item within the overflow menu
     * @return: Creates tool bar with overflow menu sign out option
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(ReplyRecycler.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast toast = Toast.makeText(ReplyRecycler.this, R.string.successful_logout, Toast.LENGTH_SHORT );
                                toast.show();
                                finish();
                                startActivity(new Intent(ReplyRecycler.this, MainActivity.class));
                            }
                        });
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Returns the user to the PostRecycler activity
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(ReplyRecycler.this,PostRecyclerActivity.class));
        finish();
    }


}

