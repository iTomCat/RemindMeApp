package com.example.tomcat.remindmeapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ActionsModel
 */

public class Actions implements Parcelable {
    private int actionIDinDB;
    private String smsContact;
    private String smsNumber;
    private String smsMessage;


    public Actions(){

    }

    private Actions(Parcel in) {
        actionIDinDB = in.readInt();
        smsContact = in.readString();
        smsNumber = in.readString();
        smsMessage = in.readString();
    }

    public static final Creator<Actions> CREATOR = new Creator<Actions>() {
        @Override
        public Actions createFromParcel(Parcel in) {
            return new Actions(in);
        }

        @Override
        public Actions[] newArray(int size) {
            return new Actions[size];
        }
    };

    public int getActionIDinDB() {
        return actionIDinDB;
    }

    public void setActionIDinDB(int actionIDinDB) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(actionIDinDB);
        parcel.writeString(smsContact);
        parcel.writeString(smsNumber);
        parcel.writeString(smsMessage);
    }
}
