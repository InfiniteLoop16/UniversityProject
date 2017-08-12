package com.example.android.universityproject;

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



    public ReplyItemViewHolder(View itemView){
        super(itemView);
        mTitleFiled = (TextView) itemView.findViewById(R.id.textViewReplyTitle);
        mBodyField = (TextView) itemView.findViewById(R.id.textViewReplyBody);
        mUserNameField = (TextView) itemView.findViewById(R.id.userNameReplyView);
        mTimeStampField = (TextView) itemView.findViewById(R.id.textViewReplyTime);

    }

    public void bind(ListItem post){
        setTitle(post.getTitle());
        setBody(post.getBody());
        setUserName(post.getUserName());
        setTimeAndDate(post.getTimeAndDateSent());

    }


    private void setTitle(String title){
        mTitleFiled.setText(title);
    }

    private void setBody(String body){
        mBodyField.setText(body);
    }

    private void setUserName(String uName){mUserNameField.setText(uName);}

    private void setTimeAndDate(String mCurrentTime){
        mTimeStampField.setText(mCurrentTime);
    }


}
