package com.example.pak_pc.userlocation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pak_pc.userlocation.R;
import com.example.pak_pc.userlocation.interfaces.ListClickInterface;
import com.example.pak_pc.userlocation.models.PlacePrediction;

import java.util.ArrayList;

public class PlacePredictionAdapter extends RecyclerView.Adapter<PlacePredictionAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<PlacePrediction> mArrayList;
    private ListClickInterface mListClickInterface;

    public PlacePredictionAdapter(ArrayList<PlacePrediction> placePredictions, ListClickInterface listClickInterface){
        mArrayList = placePredictions;
        mListClickInterface = listClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_prediction_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PlacePrediction placePrediction = mArrayList.get(position);
        holder.tvPrimaryTxt.setText(placePrediction.getPrimaryText());
        holder.tvSecondaryTxt.setText(placePrediction.getSecondaryTxt());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvPrimaryTxt,tvSecondaryTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPrimaryTxt = itemView.findViewById(R.id.tvPrimaryTxt);
            tvSecondaryTxt = itemView.findViewById(R.id.tvSecondaryTxt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListClickInterface.onClick(getAdapterPosition());
        }
    }
}
