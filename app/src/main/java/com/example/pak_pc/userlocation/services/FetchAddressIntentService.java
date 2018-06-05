package com.example.pak_pc.userlocation.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import com.example.pak_pc.userlocation.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    public FetchAddressIntentService(){
        super("");
        //require default constructor
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        // ...

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            deliverResultToReceiver(Constants.FAILURE_RESULT, null);
        } else {
            Address address = addresses.get(0);
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    address);
        }
    }

    // ...
    private void deliverResultToReceiver(int resultCode, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_DATA_KEY, address);
        mReceiver.send(resultCode, bundle);
    }
}