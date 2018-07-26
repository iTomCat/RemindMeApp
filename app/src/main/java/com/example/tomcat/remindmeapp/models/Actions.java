package com.example.tomcat.remindmeapp.models;

/**
 * ActionsModel
 */

public class Actions {
    private int actionIDinDB;
    private String smsContact;
    private String smsNumber;
    private String smsMessage;

    public int getActionIDinDB() {
        return actionIDinDB;
    }

    public void setActionIDinDB(int placeIDinDB) {
        this.actionIDinDB = actionIDinDB;
    }

    public String getSmsContact() {
        return smsContact;
    }
    public void setSmsContact(String smsContact) {
        this.smsContact = smsContact;
    }

    public String getSmsNumber() {
        return smsNumber;
    }
    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getSmsMessage() {
        return smsMessage;
    }
    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }
}
