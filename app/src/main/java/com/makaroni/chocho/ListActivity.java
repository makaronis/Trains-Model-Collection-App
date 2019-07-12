package com.makaroni.chocho;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.makaroni.chocho.data.TrainsListCursorAdapter;
import com.makaroni.chocho.data.TrainsContract;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TRAIN_LOADER = 1 ;
    private static final int LOCOMOTIVE_LOADER = 2 ;
    private static final int WAGON_LOADER = 3 ;
    TrainsListCursorAdapter trainsAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        Intent intent = getIntent();
        Uri intentData = intent.getData();
        trainsAdapter = new TrainsListCursorAdapter(this,null);

        RecyclerView recyclerView = findViewById(R.id.list_view_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trainsAdapter);

        if (intentData!=null){
            if (intentData.equals(TrainsContract.LOCOMOTIVE_URI)) {
                getLoaderManager().initLoader(LOCOMOTIVE_LOADER, null, ListActivity.this);
                }
            else if (intentData.equals(TrainsContract.WAGONS_URI)){
                getLoaderManager().initLoader(WAGON_LOADER, null, ListActivity.this);
            }
        } else
            getLoaderManager().initLoader(TRAIN_LOADER, null, ListActivity.this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                "i._id as " +
                        TrainsContract.TrainsEntry.COLUMN_ID,
                TrainsContract.TrainsEntry.COLUMN_TYPE,
                TrainsContract.TrainsEntry.COLUMN_SUBTYPE,
                TrainsContract.TrainsEntry.COLUMN_MODEL,
                TrainsContract.TrainsEntry.COLUMN_COMPANY,
                TrainsContract.TrainsEntry.COLUMN_MANUFACTURER,
                TrainsContract.TrainsEntry.COLUMN_ARTICLE,
                TrainsContract.TrainsEntry.COLUMN_IMAGE
        };
        switch (id) {
            case TRAIN_LOADER:
                return new CursorLoader(this, TrainsContract.TRAINS_CONTENT_URI, projection, null, null, null);
            case LOCOMOTIVE_LOADER:
                return new CursorLoader(this, TrainsContract.LOCOMOTIVE_URI, projection, null, null, null);
            case WAGON_LOADER :
                return new CursorLoader(this,TrainsContract.WAGONS_URI,projection,null,null,null);

        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
         trainsAdapter.swapCursor(data);
        }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       trainsAdapter.swapCursor(null);
        }
    }







