package com.makaroni.chocho.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TrainsProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int TRAINS = 100;

    private static final int TRAINS_ID = 101;

    private static final int IMAGE = 200;

    private static final int IMAGE_ID = 201;

    private static final int LOCOMOTIVE = 300;

    private static final int WAGONS = 400 ;

    private static final int SEARCH = 333 ;
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY, TrainsContract.PATH_TRAINS, TRAINS);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY, TrainsContract.PATH_TRAINS + "/#", TRAINS_ID);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY, TrainsContract.PATH_IMAGE, IMAGE);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY, TrainsContract.PATH_IMAGE + "#", IMAGE_ID);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY, TrainsContract.PATH_LOCOMOTIVE, LOCOMOTIVE);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY,TrainsContract.PATH_WAGONS, WAGONS);
        sUriMatcher.addURI(TrainsContract.CONTENT_AUTHORITY,TrainsContract.PATH_SEARCH,SEARCH);
    }
    private TrainsSQL trainsSQL;

    @Override
    public boolean onCreate() {
        trainsSQL = new TrainsSQL(getContext());

        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,  String sortOrder) {
        // Get readable database
        SQLiteDatabase trainsDatabase = trainsSQL.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(
                "trains AS t LEFT JOIN image as i "
                        + " ON ("
                        + "t._id"
                        + " = "
                        + "i._id)"

        );
        // This cursor will hold the result of the query
        Cursor cursorTrain = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAINS:
               cursorTrain = qb.query(
                        trainsDatabase,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                cursorTrain.setNotificationUri(getContext().getContentResolver(),uri);
                return cursorTrain;
            case TRAINS_ID:
                selection = "i._id" + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursorTrain = qb.query(
                        trainsDatabase,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                cursorTrain.setNotificationUri(getContext().getContentResolver(),uri);
                return cursorTrain;
            case LOCOMOTIVE :
                selection = "type =?";
                selectionArgs = new String[]{"Locomotive"};
                cursorTrain = qb.query(
                        trainsDatabase,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                cursorTrain.setNotificationUri(getContext().getContentResolver(),uri);
                return cursorTrain;
            case WAGONS :
                selection = "type =?";
                selectionArgs = new String[]{"Wagon"};
                cursorTrain = qb.query(
                        trainsDatabase,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                cursorTrain.setNotificationUri(getContext().getContentResolver(),uri);
                return cursorTrain;
            case SEARCH :
                cursorTrain = qb.query(
                        trainsDatabase,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                cursorTrain.setNotificationUri(getContext().getContentResolver(),uri);
                return  cursorTrain;

                default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAINS:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertTrain(uri, values);
            case IMAGE:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertImage(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertTrain(Uri uri, ContentValues values) {
        SQLiteDatabase database = trainsSQL.getWritableDatabase();
        long id = database.insert(TrainsContract.TRAINS_TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("ERROR INSERT", "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }
    private Uri insertImage(Uri uri, ContentValues values) {
        SQLiteDatabase database = trainsSQL.getWritableDatabase();
        long id = database.insert(TrainsContract.IMAGE_TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("ERROR INSERT", "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri,  String selection,  String[] selectionArgs) {
        SQLiteDatabase database = trainsSQL.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAINS:
                rowsDeleted = database.delete(TrainsContract.TRAINS_TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case TRAINS_ID:
                selection = TrainsContract.TrainsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TrainsContract.TRAINS_TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,  String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRAINS:
                return updateTrain(uri, values, selection, selectionArgs);
            case TRAINS_ID:
                selection = TrainsContract.TrainsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTrain(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateTrain(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(TrainsContract.TrainsEntry.COLUMN_MANUFACTURER)) {
            String manufacturer = values.getAsString(TrainsContract.TrainsEntry.COLUMN_MANUFACTURER);
            if (manufacturer == null) {
                throw new IllegalArgumentException();
            }
        }

        if (values.containsKey(TrainsContract.TrainsEntry.COLUMN_MODEL)) {

            String model = values.getAsString(TrainsContract.TrainsEntry.COLUMN_MODEL);
            if (model == null) {
                throw new IllegalArgumentException();
            }
        }
        if (values.containsKey(TrainsContract.TrainsEntry.COLUMN_COMPANY)) {

            String company = values.getAsString(TrainsContract.TrainsEntry.COLUMN_COMPANY);
            if (company == null) {
                throw new IllegalArgumentException();
            }
        }

        SQLiteDatabase database = trainsSQL.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        // If values contains image , put in in another content values and update image table
        if (values.containsKey(TrainsContract.TrainsEntry.COLUMN_IMAGE)){
            String selectionImage = TrainsContract.TrainsEntry.COLUMN_ID + "=?";
            ContentValues imageValue = new ContentValues();
            imageValue.put(TrainsContract.TrainsEntry.COLUMN_IMAGE,values.getAsByteArray(TrainsContract.TrainsEntry.COLUMN_IMAGE));
            values.remove(TrainsContract.TrainsEntry.COLUMN_IMAGE);
            int imageRowUpdated = database.update(TrainsContract.IMAGE_TABLE_NAME,imageValue,selectionImage,selectionArgs);
            if (imageRowUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        int rowsUpdated = database.update(TrainsContract.TRAINS_TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
