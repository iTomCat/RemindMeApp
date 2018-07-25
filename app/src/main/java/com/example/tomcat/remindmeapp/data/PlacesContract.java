package com.example.tomcat.remindmeapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database and ContentProvider Contract
 */

public class PlacesContract {
    // -------------------------------------------------------------- Content Provider URI constants
    //static final String AUTHORITY = "com.example.tomcat.remindmeapp";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AppContentProvider.AUTHORITY);
    static final String PATH_PLACES = "places_path";

    public static final class PlacesEntry implements BaseColumns {

        // ----------------------------------------------------------------------- Built Content URI
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        // -------------------------------------------------------------------------------- Conatsns
        static final String TABLE_NAME = "places_table";

        public static final String COLUMN_PLACE_GOOGLE_ID = "place_id";
        public static final String COLUMN_PLACE_NAME = "place_name";
    }
}
