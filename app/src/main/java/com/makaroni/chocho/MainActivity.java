package com.makaroni.chocho;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.makaroni.chocho.data.TrainsContract;
import com.makaroni.chocho.data.TrainsSQL;

public class MainActivity extends AppCompatActivity {
    private View allTrainsView;
    private View locomotivesView;
    private View wagonsView;
    private View searchView;
    private TextView trainsCount;
    @Override
    protected void onResume() {
        //Showing trains count
        super.onResume();
        if (getTrainsCount()>0){
            trainsCount.setText(String.valueOf(getTrainsCount()));
        } else
            trainsCount.setText("0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        allTrainsView = findViewById(R.id.text_trains);
        locomotivesView = findViewById(R.id.text_locomotives);
        wagonsView = findViewById(R.id.text_wagons);
        searchView = findViewById(R.id.text_search);
        trainsCount = findViewById(R.id.trains_count);

        allTrainsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }
        });
        locomotivesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.setData(TrainsContract.LOCOMOTIVE_URI);
                startActivity(intent);
            }
        });
        wagonsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.setData(TrainsContract.WAGONS_URI);
                startActivity(intent);
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

    }
    public long getTrainsCount() {
        TrainsSQL sql = new TrainsSQL(this);
        SQLiteDatabase db = sql.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TrainsContract.TRAINS_TABLE_NAME);
        db.close();
        return count;
    }
}
