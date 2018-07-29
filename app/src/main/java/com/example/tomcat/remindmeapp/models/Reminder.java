package com.example.tomcat.remindmeapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Reminder Model
 */

public class Reminder implements Parcelable {

    private int reminderIDinDB;
    private int inOut;
    private String placeId;
    private String name;
    private int active;
    private int settings;
    private int action;
    private int smsID;
    private String notes;

    public Reminder(){
    }

    private Reminder(Parcel in) {
        reminderIDinDB = in.readInt();
        inOut = in.readInt();
        placeId = in.readString();
        name = in.readString();
        active = in.readInt();
        settings = in.readInt();
        action = in.readInt();
        smsID = in.readInt();
        notes = in.readString();
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    public int getRemIDinDB() {
        return reminderIDinDB;
    }
    public void setRemIDinDB(int reminderIDinDB) {
        this.reminderIDinDB = reminderIDinDB;
    }

    //@SuppressWarnings("unused")
    public int getInOut() {
        return inOut;
    }
    public void setInOut(int inOut) {
        this.inOut = inOut;
    }

    public String getPlaceID() {
        return placeId;
    }
    public void setPlaceID(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }

    public int getSettings() {
        return settings;
    }
    public void setSettings(int settings) {
        this.settings = settings;
    }

    public int getAction() {
        return action;
    }
    public void setAction(int action) {
        this.action = action;
    }

    public int getSmsID() {
        return smsID;
    }
    public void setSmsID(int smsID) {
        this.smsID = smsID;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(reminderIDinDB);
        parcel.writeInt(inOut);
        parcel.writeString(placeId);
        parcel.writeString(name);
        parcel.writeInt(active);
        parcel.writeInt(settings);
        parcel.writeInt(action);
        parcel.writeInt(smsID);
        parcel.writeString(notes);
    }
}
