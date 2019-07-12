package com.makaroni.chocho;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.CursorLoader;

import com.makaroni.chocho.data.TrainsContract;

import java.io.ByteArrayOutputStream;


public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {
    private Spinner typeSpinner;
    private Spinner subTypeSpinner;

    private static final int RESULT_LOAD_IMAGE = 1;

    private EditText articleET;
    private EditText manufacturerET;
    private EditText modelET;
    private EditText companyET;
    private EditText noteET;
    private EditorImageView imageV;

    private Bitmap imageValue;
    private Uri mCurrentTrain;
    private boolean mTrainHasChanged = false;
    private boolean mImageHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction()==MotionEvent.ACTION_SCROLL) return false;
            mTrainHasChanged = true;
            return false;
        }
    };
    private View.OnTouchListener mImageTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mImageHasChanged = true ;
            return false;
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);
        // Setting up touch listeners for showUnsavedChangesDialog method
        typeSpinner = findViewById(R.id.type_spinner);
        typeSpinner.setOnTouchListener(mTouchListener);
        subTypeSpinner = findViewById(R.id.subtype_spinner);
        subTypeSpinner.setOnTouchListener(mTouchListener);

        imageValue = BitmapFactory.decodeResource(getResources(),R.drawable.imagetest);
        articleET = findViewById(R.id.articleET);
        articleET.setOnTouchListener(mTouchListener);

        manufacturerET = findViewById(R.id.manufacturerET);
        manufacturerET.setOnTouchListener(mTouchListener);

        modelET = findViewById(R.id.modelNameET);
        modelET.setOnTouchListener(mTouchListener);

        companyET = findViewById(R.id.railwayCompanyET);
        companyET.setOnTouchListener(mTouchListener);

        noteET = findViewById(R.id.noteET);
        noteET.setOnTouchListener(mTouchListener);

        imageV = findViewById(R.id.imageView);
        imageV.setOnTouchListener(mImageTouchListener);

        Intent intent = getIntent();
        mCurrentTrain = intent.getData();
        if (mCurrentTrain == null){
            setTitle("Add a model");
            setupSpinner();
            invalidateOptionsMenu();
        } else {
            setTitle("Edit model");
            getLoaderManager().initLoader(0,null,EditorActivity.this);
        }
        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
        getWindow().getDecorView().clearFocus();
    }

    public void getImage(){
        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntent,RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            try{
                imageValue = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageV.setImageBitmap(imageValue);
                int [] heightWeight = HelperMethods.calculateInSampleSize(imageValue,imageV.getWidth(),imageV.getHeight());
                imageValue = Bitmap.createScaledBitmap(imageValue,heightWeight[0],heightWeight[1],false);
            } catch (Exception e){
                String stackTrace = e.getMessage();
                Toast.makeText(this, stackTrace, Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean saveTrain(){
        String artcl = articleET.getText().toString();
        if (artcl.isEmpty()){
            Toast.makeText(this,"Article must be added",Toast.LENGTH_SHORT).show();
            return false;
        }
        int article = Integer.parseInt(articleET.getText().toString());

        String manufacturer = manufacturerET.getText().toString().trim();
        if (manufacturer.isEmpty()){
            Toast.makeText(this,"Manufacturer must be added",Toast.LENGTH_SHORT).show();
            return false;
        }

        String model = modelET.getText().toString().trim();
        if (model.isEmpty()){
            Toast.makeText(this,"Model must be added",Toast.LENGTH_SHORT).show();
            return false;
        }

        String company = companyET.getText().toString().trim();
        if (company.isEmpty()){
            Toast.makeText(this,"Railway Company must be added",Toast.LENGTH_SHORT).show();
            return false;
        }

        String note = noteET.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String subtype = subTypeSpinner.getSelectedItem().toString();
        //Put data into content values
        ContentValues values = new ContentValues();
        values.put(TrainsContract.TrainsEntry.COLUMN_ARTICLE,article);
        values.put(TrainsContract.TrainsEntry.COLUMN_MANUFACTURER,manufacturer);
        values.put(TrainsContract.TrainsEntry.COLUMN_MODEL,model);
        values.put(TrainsContract.TrainsEntry.COLUMN_COMPANY,company);
        values.put(TrainsContract.TrainsEntry.COLUMN_NOTE,note);
        values.put(TrainsContract.TrainsEntry.COLUMN_TYPE,type);
        values.put(TrainsContract.TrainsEntry.COLUMN_SUBTYPE,subtype);

        // Creating new Train or updating existing
        if (mCurrentTrain==null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageValue.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bArray = bos.toByteArray();

            ContentValues imageValues = new ContentValues();
            imageValues.put(TrainsContract.TrainsEntry.COLUMN_IMAGE, bArray);
            getContentResolver().insert(TrainsContract.TRAINS_CONTENT_URI,values);
            getContentResolver().insert(TrainsContract.IMAGE_CONTENT_URI, imageValues);
        }else {
            if (mImageHasChanged){
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imageValue.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                byte[] bArray = bos.toByteArray();
                values.put(TrainsContract.TrainsEntry.COLUMN_IMAGE, bArray);
            }

            int rowsAffected = getContentResolver().update(mCurrentTrain, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Update failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        return true;
    }

    private void deleteTrain() {
        if (mCurrentTrain!=null){
            int rows = getContentResolver().delete(mCurrentTrain,null,null);
            if (rows == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with delete ",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Delete successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setupSpinner() {
        //Adapter for TYPE spinner
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_spinner, R.layout.spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        // Adapters for SUBTYPE spinners
        final ArrayAdapter subTypeLocomotiveAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_locomotive_spinner, R.layout.spinner_item);
        final ArrayAdapter subTypeWagonAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_wagon_spinner, R.layout.spinner_item);
        subTypeLocomotiveAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        subTypeWagonAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_locomotive))) {
                        subTypeSpinner.setAdapter(subTypeLocomotiveAdapter);
                    } else {
                        subTypeSpinner.setAdapter(subTypeWagonAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setupSpinner(String type , final String subtype) {
        //Adapter for TYPE spinner
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_spinner, R.layout.spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        // Adapters for SUBTYPE spinners
        final ArrayAdapter subTypeLocomotiveAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_locomotive_spinner, R.layout.spinner_item);
        final ArrayAdapter subTypeWagonAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_wagon_spinner, R.layout.spinner_item);
        subTypeLocomotiveAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        subTypeWagonAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

        typeSpinner.setAdapter(typeSpinnerAdapter);
        //Setting up typeSpinner value
        if (type.equals(TrainsContract.TrainsEntry.TYPE_LOCOMOTIVES)){
             typeSpinner.setSelection(0);
        }
        else
            typeSpinner.setSelection(1);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_locomotive))) {
                        subTypeSpinner.setAdapter(subTypeLocomotiveAdapter);
                        switch (subtype) {
                            case TrainsContract.TrainsEntry.SUBTYPE_DIESEL : subTypeSpinner.setSelection(0);
                                 break;
                            case TrainsContract.TrainsEntry.SUBTYPE_ELECTRIC : subTypeSpinner.setSelection(1);
                                 break;
                            case TrainsContract.TrainsEntry.SUBTYPE_STEAM : subTypeSpinner.setSelection(2);break;
                            default: subTypeSpinner.setSelection(0);
                            break;
                        }

                    } else {
                        subTypeSpinner.setAdapter(subTypeWagonAdapter);
                        switch (subtype) {
                            case TrainsContract.TrainsEntry.SUBTYPE_PASSENGER : subTypeSpinner.setSelection(0);
                                 break;
                            case TrainsContract.TrainsEntry.SUBTYPE_FREIGHT : subTypeSpinner.setSelection(1);
                                 break;
                            default: subTypeSpinner.setSelection(0);break;
                        }
                    }
                }
            }


            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteDialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteTrain();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new train, hide the "Delete" menu item.
        if (mCurrentTrain == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save :
                saveTrain();
                if (mCurrentTrain!=null)getContentResolver().notifyChange(mCurrentTrain, null);
                break;
            case R.id.action_delete :
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                if (!mTrainHasChanged && !mImageHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // If there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if (!mTrainHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                "i._id as " +
                        TrainsContract.TrainsEntry.COLUMN_ID ,
                TrainsContract.TrainsEntry.COLUMN_TYPE,
                TrainsContract.TrainsEntry.COLUMN_SUBTYPE,
                TrainsContract.TrainsEntry.COLUMN_MODEL,
                TrainsContract.TrainsEntry.COLUMN_COMPANY,
                TrainsContract.TrainsEntry.COLUMN_MANUFACTURER,
                TrainsContract.TrainsEntry.COLUMN_ARTICLE,
                TrainsContract.TrainsEntry.COLUMN_NOTE,
                TrainsContract.TrainsEntry.COLUMN_IMAGE
        };
        return new CursorLoader(this, mCurrentTrain, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()){
            byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_IMAGE));
            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
            imageValue = bm ;
            imageV.setImageBitmap(bm);
            setupSpinner((cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_TYPE))),
                    (cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_SUBTYPE))));

            modelET.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_MODEL)));
            companyET.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_COMPANY)));
            articleET.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_ARTICLE)));
            manufacturerET.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_MANUFACTURER)));
            noteET.setText(cursor.getString(cursor.getColumnIndex(TrainsContract.TrainsEntry.COLUMN_NOTE)));
        }
    }
    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
