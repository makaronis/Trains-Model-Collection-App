package com.makaroni.chocho;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.makaroni.chocho.data.TrainsContract;
import com.makaroni.chocho.data.TrainsListCursorAdapter;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TrainsListCursorAdapter trainsAdapter;
    private EditText inputOne ;
    private Spinner spinnerOne;
    private String selectionSP;
    private Button button;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    boolean infoInpute = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        inputOne = findViewById(R.id.search_input_text);
        spinnerOne = findViewById(R.id.spinner1);
        trainsAdapter = new TrainsListCursorAdapter(this,null);
        button = findViewById(R.id.materialButton);
        recyclerView = findViewById(R.id.list_view_id);
        layoutManager = new LinearLayoutManager(this);
        setupSpinner();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trainsAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoaderManager().restartLoader(0,null,SearchActivity.this);
                Toast.makeText(SearchActivity.this, "Searching", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void setupSpinner() {
        //Adapter for TYPE spinner
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_search, R.layout.search_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.search_spinner_item_dropdown);


        spinnerOne.setAdapter(typeSpinnerAdapter);

        spinnerOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    switch (selection){
                        case "Model Name" :
                            selection = TrainsContract.TrainsEntry.COLUMN_MODEL;
                            break;
                        case "Manufacturer" :
                            selection = TrainsContract.TrainsEntry.COLUMN_MANUFACTURER;
                            break;
                        case "Railway Company" :
                            selection = TrainsContract.TrainsEntry.COLUMN_COMPANY;
                            break;
                        case "Article" :
                            selection = TrainsContract.TrainsEntry.COLUMN_ARTICLE;
                            break;
                    }
                    selectionSP = selection;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchParams = inputOne.getText().toString();
        String selection = selectionSP + " =?";
        String [] selectionArgs = new String[]{searchParams};
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
         return new CursorLoader(this, TrainsContract.SEARCH_URI, projection, selection, selectionArgs, null);



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
