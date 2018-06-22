package com.example.pak_pc.userlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationUpdates extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "LocationUpdates";
    private double latitude,longitude;
    private final int RC_PERMISSIONS = 1,RC_LOCATIONS_SETTINGS = 2;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Activity mContext;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_updates);

        mContext = this;

        //setup map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null){
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    setPinOnMap();
                    Toast.makeText(mContext, latitude + "-" + longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };
        createLocationRequest();
        getLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},RC_PERMISSIONS);
            return;
        }
        checkLocationSettings();
    }

    private void checkLocationSettings(){
        //Get current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        //check whether the current location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                Toast.makeText(mContext, "All location settings are satisfied", Toast.LENGTH_SHORT).show();
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+e.getMessage() );
                //check if exception is resolveable
                if (e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(mContext,RC_LOCATIONS_SETTINGS);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * set pin on map
     */
    private void setPinOnMap() {
        if (mMap == null)
            return;
        // Add a marker in Sydney, Australia, and move the camera.
        mMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_PERMISSIONS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }else {
                Toast.makeText(this, "permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCATIONS_SETTINGS){
            startLocationUpdates();
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "location set by GPS", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "location not set by GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * google map ready callback
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}
