package com.example.android.universityproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

import static android.R.attr.data;
import static com.example.android.universityproject.R.id.map;


public class Maps extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    // Declare private global instance variable mMap of class GoogleMap
    private GoogleMap mMap;
    private Location dLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mFriendLocation;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    // Firebase instance variables
    private DatabaseReference mLocNodeRef;
    private DatabaseReference mLocNode;
    private String childNode;
    private UserLocation uLocation;
    // Id to identify a location permission request.
    private static final int REQUEST_LOCATION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Google maps connection
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // Gets the putExtra from the ReplyRecycler view. This is placed into childNode.
        Intent i = getIntent();
        childNode = i.getStringExtra("ChildNode");


        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //User Location node
        mLocNode = mFirebaseDatabaseReference.child("UserLocation");
        // Equals the unique ID node of that message, under the UserLocation node.
        // UserLocation { UniqueID
        mLocNodeRef = mLocNode.child(childNode);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        uLocation = new UserLocation();
        mLocationRequest = new LocationRequest();


        initializeGoogleApiClient();

    }

    /**
     * Method to initialise Google API Client to facilitate all google api usage.
     * Detailed in Google API documentation: https://developers.google.com/android/guides/api-client
     * Once OnConnected callback
     */
    private void initializeGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    /**
     * Method callback for when the map is ready.
     * Calls permission check methodd, checks for permission, If permission previously given
     * Then if GPS enabled, location of device is monitored.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        permissionCheck();
    }

    /**
     * Method that checks if the user has given application permission to use location services
     * If permission has been granted, then location trackings is enabled if gps is on
     */
    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Connects to Google Api
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    /**
     * Stops location updates
     * Deletes devices location data from database
     * Disconnects from Google API
     */
    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        mLocNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot SnapShot : dataSnapshot.getChildren()) {
                    UserLocation uLoc = SnapShot.getValue(UserLocation.class);
                    if (dataSnapshot.hasChild(uLoc.getName())) {
                        mLocNodeRef.child(uLoc.getName()).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                    // Un-required method
            }
        });
        googleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Callback method called when Google API has connected
     * Once API connected requests location updates from fused location provider
     * checksuserlocation and then
     * updates user locations on the map
     */
    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        checkUserLocationSettings();
        updateUserLocations();

    }


    /**
     * Reconnects to Google Api if connection is suspended
     * @param cause: integer representing the cause of suspension
     */
    @Override
    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
    }

    /**
     * Callback method.
     * If application fails to connect to Google Maps API, application presents toast.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast toast = Toast.makeText(this, "Connection to google maps location services failed at this time", Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Creates location settings builder
     * API boiler plate found at: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
     * If location services off, creates popup requesting to turn services on or cancel
     * Send result to On ActivityResponse method
     */
    public void checkUserLocationSettings(){
        LocationSettingsRequest.Builder lBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        lBuilder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, lBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        locationUpdatesRequest();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    Maps.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    /**
     * Callback method in response to LocaionSettings builder.
     * If user clicks cancel they are taken to replyrecycler
     * If user clicks ok, their location services are turned on and location requests begin     *
     * @param requestCode: Code for REQUEST_LOCATION constant variable.
     * @param resultCode: Code generated in response to location settings (RESULT_OK, RESULT_CANCELLED)
     * @param data: Intent that carries result data, from locationsettings
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationUpdatesRequest();
                        break;
                    case Activity.RESULT_CANCELED:
                        Intent i = new Intent(Maps.this, ReplyRecycler.class);
                        i.putExtra("uniqueIdMaps", childNode);
                        startActivity(i);
                        finish();
                }
        }

    }

    /**
     * mLoNodeRef is the UserLocation Node in teh database
     * Takes snapshot of database and instantiates UserLocation object with latitude/longitude
     * Adds marker to map for each user currently sharing location in that conversation thread
     */
    public void updateUserLocations(){
        mLocNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserLocation userLoc;
                mMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userLoc = snapshot.getValue(UserLocation.class);
                    double friendLatitude = userLoc.getLatitude();
                    double friendLongitude = userLoc.getLongitude();
                    String friendName = userLoc.getName();
                    mFriendLocation = new LatLng(friendLatitude, friendLongitude);
                    mMap.addMarker(new MarkerOptions().position(mFriendLocation).title(friendName));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {           }
        });

    }


    /**
     * Starts location requests, using LocationRequest object
     * Sets the interval speed and priority of the location updates
     */
    protected void createLocationRequest() {
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Method to request location updates using FusedLocationProviderAPI
     * If permission has been given by the user, the requests begin
     */
    public void locationUpdatesRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,this);
        }
    }


    /**
     * Callback method responding to locationUpdatesRequest method
     * It deals with the device location
     * Location object is instantiated when the device location changes
     * UserLocation object is instantiated and sent to the database
     */
    public void onLocationChanged(Location location) {
        dLocation = location;
        if (dLocation != null) {
            double latitude = dLocation.getLatitude();
            double longitude = dLocation.getLongitude();
            uLocation.setName(mUser.getDisplayName());
            uLocation.setLatitude(latitude);
            uLocation.setLongitude(longitude);
            mLocNodeRef.child(uLocation.getName()).setValue(uLocation);

        }

    }



    /**
     * When back button pressed, returns to recycler view activity.
     * provides required putExtra() variable of the child node for reply recycler.
     */

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Maps.this, ReplyRecycler.class);
        i.putExtra("uniqueIdMaps", childNode);
        startActivity(i);
        finish();
    }


}






