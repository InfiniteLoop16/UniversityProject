package com.example.android.universityproject;

/**
 * Created by Jake on 08/08/2017.
 */

public class ListItem {

    private String mTitle;
    private String mBody;
    private String messageId;
    private String mUserName;
    private String mCurrentDateAndTime;
    private String mUniqueKey;


    /**
     * Default constructor required for Firebase to serialize Java POJO to JSON
     */
    public ListItem(){
    }


    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getBody(){return mBody;
    }

    public void setBody(String body){
        mBody = body;
    }

    public String getId(){ return messageId;}

    public void setId(String iD){
        messageId = iD;
    }

    public String getUserName(){return mUserName;}

    public void setUserName(String userName){
        mUserName = userName;
    }

    public String getTimeAndDateSent(){return mCurrentDateAndTime;}

    public void setTimeAndDateSent(String currentTime){
        mCurrentDateAndTime = currentTime;
    }

    public String getUniqueKey(){return mUniqueKey;}

    public void setUniqueKey(String uniqueKey){mUniqueKey = uniqueKey;}


}

