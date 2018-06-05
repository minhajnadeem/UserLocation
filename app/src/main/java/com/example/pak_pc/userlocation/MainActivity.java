package com.example.pak_pc.userlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void lastLocation(View view) {
        Intent intent = new Intent(this,LastLocation.class);
        startActivity(intent);
    }

    public void locationSettings(View view) {
        Intent intent = new Intent(this,LocationSettings.class);
        startActivity(intent);
    }

    public void locationUpdates(View view) {
        Intent intent = new Intent(this,LocationUpdates.class);
        startActivity(intent);
    }

    public void locationAddress(View view) {
        Intent intent = new Intent(this,LocationAddress.class);
        startActivity(intent);
    }

    public void placePicker(View view) {
        Intent intent = new Intent(this,PlacePickerActivity.class);
        startActivity(intent);
    }
}
