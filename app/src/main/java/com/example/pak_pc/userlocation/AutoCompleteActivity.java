package com.example.pak_pc.userlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class AutoCompleteActivity extends AppCompatActivity {

    //variables
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);

        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setUi(place);
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    private void setUi(Place place) {
        TextView textView = findViewById(R.id.textView3);
        textView.setText(place.getName());

        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
    }

    public void launchMapsActivity(View view) {
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }
}
