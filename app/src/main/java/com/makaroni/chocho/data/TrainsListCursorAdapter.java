package com.makaroni.chocho.data;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaroni.chocho.EditorActivity;
import com.makaroni.chocho.HelperMethods;
import com.makaroni.chocho.R;

import static android.support.v4.content.ContextCompat.startActivity;


public class TrainsListCursorAdapter extends CursorRecyclerViewAdapter<TrainsListCursorAdapter.ViewHolder>{
    RecyclerView mRecyclerView;

    public TrainsListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView ;
        private TextView typeView ;
        private TextView subTypeView;
        private TextView modelView ;
        private TextView companyView  ;
        private TextView articleView ;
        private TextView manufacturerView ;
        private int trainID;

        public ViewHolder(View view) {
            super(view);
            view.setTag(this);
            imageView = view.findViewById(R.id.imageTrain);
            typeView = view.findViewById(R.id.typeTextView);
            subTypeView = view.findViewById(R.id.subTypeTextView);
            modelView = view.findViewById(R.id.modelTextView);
            companyView = view.findViewById(R.id.companyTextView);
            articleView = view.findViewById(R.id.articleValueView);
            manufacturerView = view.findViewById(R.id.manufacturerTextView);
        }

        public int getTrainID() {
            return trainID;
        }

        public void setTrainID(int trainID) {
            this.trainID = trainID;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                int id = vh.getTrainID();
                Uri currentTrain = ContentUris.withAppendedId(TrainsContract.TRAINS_CONTENT_URI,id);
                Intent newIntent = new Intent(parent.getContext(),EditorActivity.class);
                newIntent.setData(currentTrain);
                startActivity(parent.getContext(),newIntent,null);
            }
        });
        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;

    }

    @Override
    public void onBindViewHolder(ViewHolder view, Cursor cursor) {
        byte[] blobArray = cursor.getBlob(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_IMAGE));
        HelperMethods.setScaledImage(view.imageView,blobArray);

        view.typeView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_TYPE)));
        view.subTypeView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_SUBTYPE)));
        view.modelView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_MODEL)));
        view.companyView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_COMPANY)));
        view.articleView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_ARTICLE)));
        view.manufacturerView.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_MANUFACTURER)));
        view.setTrainID(cursor.getInt(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_ID)));
    }
    public void delete(){

    }
}
