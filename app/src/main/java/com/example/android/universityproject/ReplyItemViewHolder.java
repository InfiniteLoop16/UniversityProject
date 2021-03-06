package com.example.android.universityproject;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


/**
 * Created by Jake on 12/08/2017.
 */

public class ReplyItemViewHolder extends RecyclerView.ViewHolder{
    private final TextView mTitleFiled;
    private final TextView mBodyField;
    private final TextView mUserNameField;
    private final TextView mTimeStampField;
    private final CardView mCardView;


    /**
     * ReplyItem constructor
     * @param itemView: View being considered, The CardView within this viewHolder
     */
    public ReplyItemViewHolder(View itemView){
        super(itemView);
        mTitleFiled = (TextView) itemView.findViewById(R.id.textViewReplyTitle);
        mBodyField = (TextView) itemView.findViewById(R.id.textViewReplyBody);
        mUserNameField = (TextView) itemView.findViewById(R.id.userNameReplyView);
        mTimeStampField = (TextView) itemView.findViewById(R.id.textViewReplyTime);
        mCardView = (CardView) itemView.findViewById(R.id.reply_card);
    }


    /**
     * Method that populates the initial message in the reply recycler that created the thread
     * @param post: The ListItem POJO
     */
    public void bindOriginal(ListItem post){
        setTitle(post.getTitle());
        setBody(post.getBody());
        setUserName(post.getUserName());
        setTimeAndDate(post.getTimeAndDateSent());
    }

    /**
     * Method for populating the reply messages in the replyRecycler
     * Removes the title field from the responses as the subject matter of the thread has already
     * been established
     * @param post
     */
    public void bindReplies(ListItem post){
        mTitleFiled.setVisibility(View.GONE);
        setBody(post.getBody());
        setUserName(post.getUserName());
        setTimeAndDate(post.getTimeAndDateSent());
    }

    private void setTitle(String title){ mTitleFiled.setText(title);    }

    private void setBody(String body){
        mBodyField.setText(body);
    }

    private void setUserName(String uName){mUserNameField.setText(uName);}

    private void setTimeAndDate(String mCurrentTime){
        mTimeStampField.setText(mCurrentTime);
    }


    /**
     * Changes the colour of the cardView background
     * Applied to initial message in replyRecycler
     */
    public void setCardColour() {
        mTitleFiled.setBackgroundResource(R.color.original);
        mBodyField.setBackgroundResource(R.color.original);
        mTimeStampField.setBackgroundResource(R.color.original);
        mUserNameField.setBackgroundResource(R.color.original);
        mCardView.setBackgroundResource(R.color.original);
    }
}
