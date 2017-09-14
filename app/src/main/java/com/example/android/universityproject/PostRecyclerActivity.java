package com.example.android.universityproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class PostRecyclerActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference dataRef;
    private FloatingActionButton addPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recycler);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Creates reference link to database.
        dataRef = FirebaseDatabase.getInstance().getReference();



        // Creates the RecyclerView. Recent post located at the top.
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_tutorial);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Creation of FAB onClick Listener
        addPost = (FloatingActionButton) findViewById(R.id.add_Post);
        addPost.setOnClickListener(startNewPost);

        hideRevealFAB();


        /**
         * Instantiates the Firebase Recycler Adapter
         * Takes the POJO class, layout file, viewholder class and the datbase refernce
         * as arguments
         */
        mAdapter = new FirebaseRecyclerAdapter<ListItem, ItemViewHolder>(
                ListItem.class,
                R.layout.post_recycler_item,
                ItemViewHolder.class,
                dataRef.child("chat")) {
            /**
             * Calls bind method from ReplyViewHolder class
             * Binds the data from the database to the reply_recyler_item layout file
             * If first oringal post, turn blue to differentiate from replies
             * @param viewHolder: Holder for the Layout file
             * @param post: ListItem class object storing information from database
             * @param position: The position the ViewHolder has in the recyclerView
             */
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, ListItem post, int position) {
                viewHolder.bind(post);

            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * Hides floating action button when scrolling down
     * Reveals floating action burron when scrolling up
     */
    private void hideRevealFAB(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            /**
             * @param recyclerView: The recyclerView
             * @param dx: Horizontal scrolling variable
             * @param dy: Vertical scrolling variable
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0){
                    addPost.hide();}
                else if (dy < 0){
                    addPost.show();}
            }
        });
    }


    /**
     * Starts newPost activity
     * Ends this activity
     */
    private View.OnClickListener startNewPost = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), NewPost.class);
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
                        .signOut(PostRecyclerActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast toast = Toast.makeText(PostRecyclerActivity.this, R.string.successful_logout, Toast.LENGTH_SHORT );
                                toast.show();
                                finish();
                                startActivity(new Intent(PostRecyclerActivity.this, MainActivity.class));
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
     * Starts the Main activity
     * Ends this activity
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(PostRecyclerActivity.this,MainActivity.class));
        finish();
    }
}
