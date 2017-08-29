package com.example.android.universityproject;

/**
 * Created by Jake on 28/08/2017.
 */

public class UserLocation {

    public double latitude;
    public double longitude;
    public String name;

    public UserLocation(){

    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){ return name;}

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude() {return longitude;}

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude () { return latitude;}

}
