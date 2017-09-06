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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
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



public class Maps extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    // Declare private global instance variable mMap of class GoogleMap
    private GoogleMap mMap;


    private Location dLocation;

    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocation;

    private LocationRequest mLocationRequest;
    private LatLng mFriendLocation;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;


    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    //private DatabaseReference mChildLocation;
    private DatabaseReference mLocNodeRef;

    private DatabaseReference mLocNode;

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


        //mChildLocation = mFirebaseDatabaseReference.child("Convo's").child(childNode);


        //User Location node
        mLocNode = mFirebaseDatabaseReference.child("UserLocation");

        // Equals the unique ID node of that message, under the UserLocation node.
        // UserLocation { UniqueID
        mLocNodeRef = mLocNode.child(childNode);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();


        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        initializeGoogleApiClient();


        mLocationCallback = new LocationCallback();

        uLocation = new UserLocation();
        mLocationRequest = new LocationRequest();



    }


    // Method that creates a map and places a pin over london.
    // Calls the request location method.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        permissionCheck();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);




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

            }
        });

        mGoogleApiClient.disconnect();

        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onConnected(Bundle bundle) {

        createLocationRequest();

        // Google API DOCMENTS https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
        LocationSettingsRequest.Builder lBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, lBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
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

        locationUpdates();


        // mLoNodeRef is the UserLocation Node.
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Callback method in response to LocaionSettings builder.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Intent i = new Intent(Maps.this, ReplyRecycler.class);
                        i.putExtra("uniqueIdMaps", childNode);
                        startActivity(i);
                }
        }

    }



    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    // Callback method.
    // If application fails to connect to Google Maps API, application presents toast.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast toast = Toast.makeText(this, "Connection to google maps location services failed at this time", Toast.LENGTH_LONG);
        toast.show();
    }

    // Method to initialise Google API Client.
    // Detailed in Google Maps API documentation.
    private void initializeGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
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


    // Method too request location updates.
    // Uses Fused Location provider.
    // Detailed in Google Maps API documentation for FusedLocationProvider Class.
    public void locationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    // Callback method called when the device location changes.
    // When the device location changes, the information is stored in a UserLocation object.
    // This object is then sent to Firebase where it is parsed to JSON.
    public void onLocationChanged(Location location) {
        dLocation = location;
        if (dLocation != null) {
            double latitude = dLocation.getLatitude();
            double longitude = dLocation.getLongitude();


            uLocation.setName(mUser.getDisplayName());
            uLocation.setLatitude(latitude);
            uLocation.setLongitude(longitude);

            // This was mLocationDataRef!!!
            mLocNodeRef.child(uLocation.getName()).setValue(uLocation);

            Toast toast = Toast.makeText(Maps.this, Double.toString(latitude) + Double.toString(longitude), Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // Method that checks the user has given the application permission to use location services.
    // If permission has already been requested, then location services is enabled.
    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }


}






