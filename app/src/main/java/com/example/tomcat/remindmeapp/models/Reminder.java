package com.example.tomcat.remindmeapp.models;

import com.example.tomcat.remindmeapp.data.RemindersContract;

/**
 * Reminder Model
 */

public class Reminder {
    private int inOut;
    private int placeId;
    private String name;
    private int active;
    private int settings;
    private int action;
    private int smsID;
    private String notes;

    public int getInOut() {
        return inOut;
    }
    public void setInOut(int inOut) {
        this.inOut = inOut;
    }

    public int getPlaceID() {
        return placeId;
    }
    public void setPlaceID(int placeId) {
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
}
