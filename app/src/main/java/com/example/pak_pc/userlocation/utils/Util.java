package com.example.pak_pc.userlocation.utils;

import android.view.View;
import android.widget.ProgressBar;

public class Util {

    public static void showProgressBar(ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(ProgressBar progressBar){
        progressBar.setVisibility(View.INVISIBLE);
    }
}
