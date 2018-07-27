package com.example.tomcat.remindmeapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Places Model
 */

public class Places implements Parcelable {
    private int placeIDinDB;
    private String placeID;
    private String placeName;

    public Places(){

    }

    private Places(Parcel in) {
        placeIDinDB = in.readInt();
        placeID = in.readString();
        placeName = in.readString();
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(placeIDinDB);
        parcel.writeString(placeID);
        parcel.writeString(placeName);
    }
}
