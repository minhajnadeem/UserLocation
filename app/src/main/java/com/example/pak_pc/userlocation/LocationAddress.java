package com.example.pak_pc.userlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pak_pc.userlocation.services.FetchAddressIntentService;
import com.example.pak_pc.userlocation.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationAddress extends AppCompatActivity {

    private static final String TAG = "LocationAddress";
    private final int RC_PERMISSIONS = 1;
    private String mAddressOutput;

    private Activity mActivity;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Address mAddress;

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);

        mTextView = findViewById(R.id.textView2);

        mActivity = this;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        getLastKnowLocation();
    }

    private void getLastKnowLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,RC_PERMISSIONS);
            return;
        }
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation = location;
                startIntentService();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
                Toast.makeText(mActivity, "could not found location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastKnowLocation();
            }else {
                Toast.makeText(this, "permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * intent service result receiver
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }
            //address object
            mAddress = resultData.getParcelable(Constants.RESULT_DATA_KEY);
            if (mAddress == null) {
                return;
            }

            mTextView.setText(mAddress.getAddressLine(0));
            // Show a toast message if an address was found.
            Log.d(TAG, "onReceiveResult: "+mAddress.getAddressLine(0));
        }
    }
}
