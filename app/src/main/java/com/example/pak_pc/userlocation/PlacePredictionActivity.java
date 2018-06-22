package com.example.pak_pc.userlocation;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pak_pc.userlocation.adapters.PlacePredictionAdapter;
import com.example.pak_pc.userlocation.interfaces.ListClickInterface;
import com.example.pak_pc.userlocation.models.PlacePrediction;
import com.example.pak_pc.userlocation.utils.Util;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class PlacePredictionActivity extends AppCompatActivity {

    //variables
    private final String TAG = "PlacePredictionActivity";
    private final int SEARCH_DELAY = 2000;
    private double latitude,longitude;
    private ArrayList<PlacePrediction> mPlacePredictionArrayList;
    private GeoDataClient mGeoDataClient;

    //views
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;


    //interfaces
    ListClickInterface mListClickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_prediction);

        mGeoDataClient = Places.getGeoDataClient(this);

        init();
        setupListeners();
    }

    private void init() {

        mPlacePredictionArrayList = new ArrayList<>();

        mEditText = findViewById(R.id.editText);
        mProgressBar = findViewById(R.id.progressBar);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {

        mListClickInterface = new ListClickInterface() {
            @Override
            public void onClick(int position) {
                geocodeAddress(mPlacePredictionArrayList.get(position));
            }
        };

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPlace(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void geocodeAddress(PlacePrediction placePrediction) {
        mPlacePredictionArrayList.clear();
        setPredictionAdapter();
        Util.showProgressBar(mProgressBar);

        mGeoDataClient.getPlaceById(placePrediction.getPlaceId()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                Util.hideProgressBar(mProgressBar);

                if (task.isSuccessful()){
                    PlaceBufferResponse placeBufferResponse = task.getResult();
                    Place place = placeBufferResponse.get(0);
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    setUi(place);
                }
            }
        });
    }

    private void setUi(Place place) {
        TextView textView = findViewById(R.id.textView);
        textView.setText(place.getName() + "\n" + place.getLatLng().latitude + "\n" + place.getLatLng().longitude);
    }

    private void searchPlace(final String query) {
        if (mEditText.getTag() == null) {
            mEditText.setTag(query);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPredictions(mEditText.getText().toString().trim());
                    mEditText.setTag(null);
                }
            }, SEARCH_DELAY);
        }
    }

    private void getPredictions(String query) {
        Util.showProgressBar(mProgressBar);

        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder();
        //builder.setCountry("PK");
        //builder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS);
        Task<AutocompletePredictionBufferResponse> task = mGeoDataClient.getAutocompletePredictions(query, null, builder.build());

        task.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                Util.hideProgressBar(mProgressBar);
                mPlacePredictionArrayList.clear();

                try {
                    AutocompletePredictionBufferResponse autocompletePredictions = task.getResult();
                    for (AutocompletePrediction prediction : autocompletePredictions) {
                        String placeId = prediction.getPlaceId();
                        String primaryText = prediction.getPrimaryText(null).toString();
                        String secondaryTxt = prediction.getSecondaryText(null).toString();
                        Log.i(TAG, primaryText + "-" + placeId);

                        mPlacePredictionArrayList.add(new PlacePrediction(placeId, primaryText,secondaryTxt));
                    }
                    setPredictionAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onComplete: " + e.getMessage());
                }
            }
        });
    }

    private void setPredictionAdapter() {
        if (mPlacePredictionArrayList.size() == 0)
            mRecyclerView.setVisibility(View.INVISIBLE);
        else
            mRecyclerView.setVisibility(View.VISIBLE);

        PlacePredictionAdapter adapter = new PlacePredictionAdapter(mPlacePredictionArrayList, mListClickInterface);
        mRecyclerView.setAdapter(adapter);
    }

    public void launchMapsActivity(View view) {
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
    }
}
