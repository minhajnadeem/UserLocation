package com.example.pak_pc.userlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CurrentLocation extends AppCompatActivity {

    //variables
    private final int RC_PERMISSION = 1;
    private final String TAG = "CurrentLocation";
    private double latitude,longitude;
    private PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
    }

    public void getCurrentLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},RC_PERMISSION);
            return;
        }else {
            getLocation();
        }
    }
    @SuppressLint("MissingPermission")
    private void getLocation(){
        Task<PlaceLikelihoodBufferResponse> likelihoodBufferResponseTask =mPlaceDetectionClient.getCurrentPlace(null);
        likelihoodBufferResponseTask.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                try {
                    PlaceLikelihoodBufferResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response){
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    PlaceLikelihood placeLikelihood = response.get(0);
                    Place place = placeLikelihood.getPlace();
                    setUi(place);
                    response.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUi(Place place) {
        TextView textView = findViewById(R.id.textView);
        textView.setText(place.getName() + "\n" + place.getLatLng().latitude + "\n" + place.getLatLng().longitude);
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }else {
            Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchMapsActivity(View view) {
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }
}
