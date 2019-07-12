package com.makaroni.chocho.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrainsContract {
    //PATHS's
    public static final String PATH_LOCOMOTIVE = "locomotive";
    public static final String PATH_WAGONS = "wagons";
    public static final String PATH_TRAINS = "trains";
    public final static String PATH_SEARCH = "search";
    public final static String PATH_IMAGE = "image";

    //TABLE NAME
    public final static String TRAINS_TABLE_NAME = "trains";
    public final static String IMAGE_TABLE_NAME = "image";

    // URI's
    public static final String CONTENT_AUTHORITY = "com.example.chocho";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static Uri TRAINS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TRAINS);
    public final static Uri IMAGE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_IMAGE);
    public final static Uri LOCOMOTIVE_URI = Uri.withAppendedPath(TrainsContract.BASE_CONTENT_URI,"locomotive");
    public final static Uri WAGONS_URI = Uri.withAppendedPath(TrainsContract.BASE_CONTENT_URI, "wagons");
    public final static Uri SEARCH_URI = Uri.withAppendedPath(TrainsContract.BASE_CONTENT_URI,"search");



    public static final class TrainsEntry implements BaseColumns {

        //COLUMN's
        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_ARTICLE ="article";
        public final static String COLUMN_MANUFACTURER = "manufacturer";
        public final static String COLUMN_MODEL = "model";
        public final static String COLUMN_COMPANY = "company";
        public final static String COLUMN_NOTE = "note";
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_SUBTYPE = "subtype";
        public final static String COLUMN_IMAGE = "image";

        //SPINNER SELECTIONS
        public final static String TYPE_LOCOMOTIVES = "Locomotive";
        public final static String TYPE_COACHES_AND_WAGONS = "Wagon";
        public final static String SUBTYPE_STEAM = "Steam";
        public final static String SUBTYPE_ELECTRIC = "Electric";
        public final static String SUBTYPE_DIESEL = "Diesel";
        public final static String SUBTYPE_PASSENGER = "Passenger";
        public final static String SUBTYPE_FREIGHT = "Freight";

    }
}
