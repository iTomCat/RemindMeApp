package com.example.tomcat.remindmeapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database and ContentProvider Contract
 */

public class RemindersContract {

    // -------------------------------------------------------------- Content Provider URI constants
    static final String AUTHORITY = "com.example.tomcat.remindmeapp";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_REMINDERS = "reminders_path";

    public static final class RemindersEntry implements BaseColumns {

        // ----------------------------------------------------------------------- Built Content URI
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDERS).build();

        // -------------------------------------------------------------------------------- Conatsns
        public static final String TABLE_NAME = "reminderds_table";

        //public static final String COLUMN_ID = "remind_id";
        public static final String COLUMN_IN_OR_OUT = "in_or_out";
        public static final String COLUMN_PLACES_ID = "places";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMNM_ACTIVE = "active";
        public static final String COLUMN_REMIND_SETTINGS = "settings";
        public static final String COLUMN_REMIND_ACTION = "action";
        public static final String COLUMN_NOTES = "notes";


    }
}
