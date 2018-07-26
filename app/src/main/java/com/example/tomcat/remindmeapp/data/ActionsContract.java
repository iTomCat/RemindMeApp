package com.example.tomcat.remindmeapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database and ContentProvider Contract
 */

public class ActionsContract {
    // -------------------------------------------------------------- Content Provider URI constants
    //static final String AUTHORITY = "com.example.tomcat.remindmeapp";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AppContentProvider.AUTHORITY);
    static final String PATH_ACTIONS = "actions_path";

    public static final class ActionsEntry implements BaseColumns {

        // ----------------------------------------------------------------------- Built Content URI
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIONS).build();

        // -------------------------------------------------------------------------------- Conatsns
        public static final String TABLE_NAME = "actions_table";

        //public static final String COLUMN_ID = "remind_id";
        public static final String COLUMN_SMS_CONTACT = "sms_contact";
        public static final String COLUMN_SMS_NUMBER = "sms_number";
        public static final String COLUMN_SMS_MESSAGE = "sms_message";
    }
}
