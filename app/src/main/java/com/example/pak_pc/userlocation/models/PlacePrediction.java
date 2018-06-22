package com.example.pak_pc.userlocation.models;

public class PlacePrediction {

    private String placeId,primaryText,secondaryTxt;

    public PlacePrediction(String placeId,String primaryText,String secondaryTxt){
        setPlaceId(placeId);
        setPrimaryText(primaryText);
        setSecondaryTxt(secondaryTxt);
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondaryTxt() {
        return secondaryTxt;
    }

    public void setSecondaryTxt(String secondaryTxt) {
        this.secondaryTxt = secondaryTxt;
    }
}
