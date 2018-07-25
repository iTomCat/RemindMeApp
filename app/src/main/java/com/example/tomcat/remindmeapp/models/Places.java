package com.example.tomcat.remindmeapp.models;

/**
 * Places Model
 */

public class Places {
    private int placeIDinDB;
    private String placeID;
    private String placeName;

    public int getPlaceIDinDB() {
        return placeIDinDB;
    }

    public void setPlaceIDinDB(int placeIDinDB) {
        this.placeIDinDB = placeIDinDB;
    }


    public String getPlaceGoogleID() {
        return placeID;
    }

    public void setPlaceGoogleID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
