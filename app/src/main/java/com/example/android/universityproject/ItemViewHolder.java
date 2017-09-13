package com.example.android.universityproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jake on 09/08/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTitleFiled;
    private final TextView mBodyField;
    private final TextView mUserNameField;
    private final TextView mTimeStampField;
    private final TextView mCountField;
    private final View Card;
    private String id;





    public ItemViewHolder(View itemView){
        super(itemView);
        mTitleFiled = (TextView) itemView.findViewById(R.id.textViewTitle);
        mBodyField = (TextView) itemView.findViewById(R.id.textViewBody);
        mUserNameField = (TextView) itemView.findViewById(R.id.userNameView);
        mTimeStampField = (TextView) itemView.findViewById(R.id.textViewTime);

        mCountField = (TextView) itemView.findViewById(R.id.textViewCount);


        Card = itemView.findViewById(R.id.cards);
        Card.setOnClickListener(replyToMessage);





    }

    public void bind(ListItem post){
        // Populates the fields of the card in the recycler view.
        setTitle(post.getTitle());
        setBody(post.getBody());
        setUserName(post.getUserName());
        setTimeAndDate(post.getTimeAndDateSent());

        // id is passed to putExtra method so that it can be passed to the ReplyRecycler activity.
        // This allows for the creation of a recycler view for all the replies.
        id = post.getId();

    }


    private void setTitle(String title){
        mTitleFiled.setText(title);}

    private void setBody(String body){
        mBodyField.setText(body);
    }

    private void setUserName(String uName){mUserNameField.setText(uName);}

    private void setTimeAndDate(String mCurrentTime){
        mTimeStampField.setText(mCurrentTime);
    }

    // Adds 'Extras' to the creation of the intent. These extras are available in the
    // the activity that is opened.
    // Passing the unique push id to the reply recycler to show parent and child nodes
    private View.OnClickListener replyToMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ReplyRecycler.class);
            i.putExtra("uniqueID",id);
            v.getContext().startActivity(i);


        }
    };

}
