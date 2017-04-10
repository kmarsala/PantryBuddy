package com.google.android.gms.samples.vision.ocrreader;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ViewPantry extends AppCompatActivity {
   // EditText mEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pantry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // mEdit = (EditText) findViewById(R.id.editText5);

    }

    public void loadPantry(View v) {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        ArrayList<FoodItem> foods = dbHandler.getAllFoods();
       // mEdit.setText(foods.get(0).getItemName());
        /*
        File f = new File(getFilesDir().toString() + "/data.txt");
        try {
            FileInputStream fIn = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[10000];
            isr.read(inputBuffer);
            String readString = new String(inputBuffer);
            mEdit.setText(readString);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    */
    }


}




