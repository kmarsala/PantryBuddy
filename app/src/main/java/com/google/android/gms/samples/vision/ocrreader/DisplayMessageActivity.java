package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.*;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class DisplayMessageActivity extends AppCompatActivity {

    EditText mEdit;
    EditText mEdit2;
    EditText mEdit3;
    EditText mEdit4;
    EditText number1;
    EditText number2;
    EditText number3;
    EditText number4;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        TextView textView = new TextView(this);
        textView.setTextSize(40);

        mEdit = (EditText) findViewById(R.id.edit_text_item1);
        mEdit2 = (EditText) findViewById(R.id.edit_text_item2);
        mEdit3 = (EditText) findViewById(R.id.edit_text_item3);
        mEdit4 = (EditText) findViewById(R.id.edit_text_item4);
        number1 = (EditText) findViewById(R.id.itemQty1);
        number2 = (EditText) findViewById(R.id.itemQty2);
        number3 = (EditText) findViewById(R.id.itemQty3);
        number4 = (EditText) findViewById(R.id.itemQty4);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);

    }

    public void addLineToDB(EditText name, EditText number)
    {
        //TODO: Remove special characters. Probably use regex.
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        if(!name.getText().toString().equals("") && !number.getText().toString().equals(""))
        {
            FoodItem f1 = new FoodItem();
            f1.setItemName(name.getText().toString());
            f1.setAmount((Double.valueOf(number.getText().toString())));
          //  f1.setDatePurchased();
            dbHandler.addFood(f1);
        }
    }

    public void submitList(View v) {


            addLineToDB(mEdit, number1);

            addLineToDB(mEdit2, number2);

            addLineToDB(mEdit3, number3);

            addLineToDB(mEdit4, number4);

    }

    public void deleteDB(View v)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        ArrayList<FoodItem> foods = dbHandler.getAllFoods();
        for(int i = 0; i < foods.size(); i++)
        {
            dbHandler.deleteContact(foods.get(i));
        }
    }

    /*public void addDays(View v)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        ArrayList<FoodItem> foods = dbHandler.getAllFoods();
        for(int i = 0; i < foods.size(); i++)
        {

        }
    }*/


}


