package com.example.android.universityproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Maps extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    // Declare private global instance variable mMap of class GoogleMap
    private GoogleMap mMap;

    // Id to identify a location permission request.
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Method that creates a map and places a pin over london.
    // Calls the request location method.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in London, England, and move the camera.
        LatLng london = new LatLng(51.5074, 0.1278);
        mMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(london));

        requestLocationPermission();

        if(gpsCheck() == false){
            locDiaFragSwitch();
        }
    }



    // This method starts intent to open activity advising that location services have not been enabled.
    public void locDiaFragSwitch(){
        Intent locIntent = new Intent(this, LocationWarnningPopUp.class);
        startActivity(locIntent);

    }



    //This method checks to see whether location services is enabled.
    // It returns a true if enabled, or false if not.
    public  boolean gpsCheck(){
        LocationManager location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(location.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            return true;
        }else{
            return false;
        }
    }

    // This method requests permission from the user to allow access to location services.
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            }
        }
    }
}
