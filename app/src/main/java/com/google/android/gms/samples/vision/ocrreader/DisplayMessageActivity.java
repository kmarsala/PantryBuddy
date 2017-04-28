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
    String predefinedText1, predefinedText2, predefinedText3, predefinedText4;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //The bundle is used to accept the input from the receipt scanner
        Bundle b = getIntent().getExtras();
        int val = b.getInt("key");
        System.out.println(val);

        if(b.getString("predefined1") != null)
        {
            predefinedText1 = b.getString("predefined1");
        }
        if(b.getString("predefined2") != null)
        {
            predefinedText2 = b.getString("predefined2");
        }
        if(b.getString("predefined3") != null)
        {
            predefinedText3 = b.getString("predefined3");
        }
        if(b.getString("predefined4") != null)
        {
            predefinedText4 = b.getString("predefined4");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        TextView textView = new TextView(this);
        textView.setTextSize(40);

        //Tell the objects what they are
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

        if(predefinedText1 != null)
        {
            name1.setText(predefinedText1);
        }
        if(predefinedText2 != null)
        {
            name2.setText(predefinedText2);
        }

        if(predefinedText3 != null)
        {
            name3.setText(predefinedText3);
        }

        if(predefinedText4 != null)
        {
            name4.setText(predefinedText4);
        }


        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);

    }

    //Adds an entry to the database
    public void addLineToDB(EditText name, EditText number, EditText price)
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        //Make sure the entry is valid
        if(!name.getText().toString().equals("") && !number.getText().toString().equals("") && !price.getText().toString().equals(""))
        {
            //Apostophes break the database
            if(name.getText().toString().contains("'"))
            {
                label.setText("Please remove the apostrophe from your item name");
            }
            else {

                FoodItem f1 = new FoodItem();
                f1.setItemName(name.getText().toString());
                f1.setAmount((Double.valueOf(number.getText().toString())));
               // System.out.println("Displaying price purchased");
                //System.out.println(Double.valueOf(price.getText().toString()));
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

        label.setText("Pantry has been deleted");
    }

    //Unused for now
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


