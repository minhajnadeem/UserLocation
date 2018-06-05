package com.example.pak_pc.userlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class PlacePickerActivity extends AppCompatActivity {

    private final String TAG = "PlacePickerActivity";
    private final int RC_PLACE_PICKER = 1;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        mTextView = findViewById(R.id.textView);

        mGeoDataClient = Places.getGeoDataClient(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

    }

    private void startPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this),RC_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Log.e(TAG, "startPlacePicker: "+e.getMessage() );

        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.e(TAG, "startPlacePicker: "+e.getMessage() );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PLACE_PICKER){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                setUI(place);
            }else {
                Toast.makeText(this, "No place selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUI(Place place) {
        mTextView.setText(
                place.getAddress() +"\n"
                        +place.getLocale() + "\n"
                        +place.getName() + "\n"
                        +place.getPhoneNumber() + "\n"
                        +place.getRating() + "\n");
    }

    public void launchPlacePicker(View view) {
        startPlacePicker();
    }
}
