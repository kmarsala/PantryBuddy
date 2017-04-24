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

    EditText name1;
    EditText name2;
    EditText name3;
    EditText name4;
    EditText number1;
    EditText number2;
    EditText number3;
    EditText number4;
    EditText price1;
    EditText price2;
    EditText price3;
    EditText price4;
    TextView label;
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

        name1 = (EditText) findViewById(R.id.item1);
        name2 = (EditText) findViewById(R.id.item2);
        name3 = (EditText) findViewById(R.id.item3);
        name4 = (EditText) findViewById(R.id.item4);
        number1 = (EditText) findViewById(R.id.qty1);
        number2 = (EditText) findViewById(R.id.qty2);
        number3 = (EditText) findViewById(R.id.qty3);
        number4 = (EditText) findViewById(R.id.qty4);
        price1 = (EditText) findViewById(R.id.price1);
        price2 = (EditText) findViewById(R.id.price2);
        price3= (EditText) findViewById(R.id.price3);
        price4 = (EditText) findViewById(R.id.price4);
        label = (TextView) findViewById(R.id.labelText);


        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);

    }

    public void addLineToDB(EditText name, EditText number, EditText price)
    {
        //TODO: Remove special characters. Probably use regex.
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        if(!name.getText().toString().equals("") && !number.getText().toString().equals("") && !price.getText().toString().equals(""))
        {
            if(name.getText().toString().contains("'"))
            {
                label.setText("Please remove the apostrophe from your item name");
            }
            else {

                FoodItem f1 = new FoodItem();
                f1.setItemName(name.getText().toString());
                f1.setAmount((Double.valueOf(number.getText().toString())));
                System.out.println("Displaying price purchased");
                System.out.println(Double.valueOf(price.getText().toString()));
                f1.setPrice(Double.valueOf(price.getText().toString()));
                //  f1.setDatePurchased();
                dbHandler.addFood(f1);

                label.setText("Items successfully added");
            }

        }
       // dbHandler.close();
    }

    public void submitList(View v) {


        addLineToDB(name1, number1, price1);
        name1.setText("");
        number1.setText("");
        price1.setText("");

        addLineToDB(name2, number2, price2);
        name2.setText("");
        number2.setText("");
        price2.setText("");

        addLineToDB(name3, number3, price3);
        name3.setText("");
        number3.setText("");
        price3.setText("");

        addLineToDB(name4, number4, price4);
        name4.setText("");
        number4.setText("");
        price4.setText("");


    }

    public void deleteDB(View v)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.deletePantryRows();
        /*
        ArrayList<FoodItem> foods = dbHandler.getAllFoods();
        for(int i = 0; i < foods.size(); i++)
        {
            dbHandler.deleteContact(foods.get(i));
        }
        */
        label.setText("Pantry has been deleted");
    }

    public void recalcValues(View v)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.recalcFoodAmounts();
    }

    public void deleteEmptyItems(View v)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.deleteEmpty();
    }


}


