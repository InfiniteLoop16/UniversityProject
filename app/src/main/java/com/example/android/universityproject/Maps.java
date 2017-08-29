package com.example.android.universityproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class Maps extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    // Declare private global instance variable mMap of class GoogleMap
    private GoogleMap mMap;


    private Location dLocation;


    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocation;

    private LocationRequest mLocationRequest;
    private LatLng mfriendLocation;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mLocationDataRef;
    private DatabaseReference mChildLocation;

    private String childNode;

    private UserLocation uLocation;

    private LocationCallback mLocationCallback;






    // Id to identify a location permission request.
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Google maps connection
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Gets the putExtra from the ReplyRecycler view. This is placed into childNode.
        Intent i = getIntent();
        childNode = i.getStringExtra("ChildNode");

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mChildLocation = mFirebaseDatabaseReference.child("Convo's").child(childNode);
        mLocationDataRef = mChildLocation.child("UserLocation");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();



        mLocationRequest = new LocationRequest();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);



        initializeGoogleApiClient();

        mLocationCallback = new LocationCallback();

        mLocationRequest = new LocationRequest();
        uLocation = new UserLocation();
        mLocationRequest = new LocationRequest();


        mLocationDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserLocation UserLoc;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserLoc = child.getValue(UserLocation.class);

                    double friendLatitude = UserLoc.getLatitude();
                    double friendLongitude = UserLoc.getLongitude();
                    mfriendLocation = new LatLng(friendLatitude, friendLongitude);
                    String friendName = UserLoc.getName();
                    mMap.addMarker(new MarkerOptions().position(mfriendLocation).title(friendName));

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    // Method that creates a map and places a pin over london.
    // Calls the request location method.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        requestLocationPermission();
        if(!gpsCheck()){
            locDiaFragSwitch();}

        lastKnownLocation();
    }



    @Override
    protected void onStart(){
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();}
    }

    @Override
    protected void onStop(){
        mLocationDataRef.child(uLocation.getName()).removeValue();
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle){

        createLocationRequest();
        locationUpdates();

    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    // WHAT AM I GOING TO DO WITH THIS METHOD!!
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result){
        Toast toast = Toast.makeText(this, "Connection to google maps location services failed at this time", Toast.LENGTH_LONG);
        toast.show();
    }

    // Method to initialise Google API Client.
    private void initializeGoogleApiClient(){

        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    }

    // High priority appropriate for apps that display in real time.
    // Details the interval in which updates to location should be made.
    protected void createLocationRequest() {

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Finds the last known location of the device.
    // Check to ensure that location permission has been granted.
    // If location is not null then creates a GeoLocation object containing the last known location.
    // WOOOOOHOOOOHOHOHOHOH
    public void lastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            dLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            if (dLocation != null) {
                double latitude = dLocation.getLatitude();
                double longitude = dLocation.getLongitude();

                UserLocation uLocation = new UserLocation();
                uLocation.setName(mUser.getDisplayName());
                uLocation.setLatitude(latitude);
                uLocation.setLongitude(longitude);
                mLocationDataRef.push().setValue(uLocation);
            }
        }
    }

    public void locationUpdates(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }



    public void onLocationChanged(Location location){
        dLocation = location;
        if(dLocation!= null) {
            double latitude = dLocation.getLatitude();
            double longitude = dLocation.getLongitude();



            uLocation.setName(mUser.getDisplayName());
            uLocation.setLatitude(latitude);
            uLocation.setLongitude(longitude);
            mLocationDataRef.child(uLocation.getName()).setValue(uLocation);

            Toast toast = Toast.makeText(Maps.this, Double.toString(latitude)+ Double.toString(longitude), Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    // This method starts intent to open activity advising that location services have not been enabled.
    public void locDiaFragSwitch(){
        // Need to write body.
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






