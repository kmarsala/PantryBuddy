package com.google.android.gms.samples.vision.ocrreader;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;


//This class is almost the same as the ViewLoadPantry class.
public class ViewTrends extends ListActivity {

    ArrayList<FoodItem> foods;

    String[] allFoods = new String[0];



    public void makeArray()
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        foods = dbHandler.getAllFoods();
        for(int i = 0; i < foods.size(); i++)
        {

            if(foods.get(i).getUsagePerDay() == 0 || foods.get(i).getWeeklySpending() == 0)
            {
                String temp = foods.get(i).getItemName() + " / Usage per day: Unknown" + " / Weekly Spending Estimate: Unknown";
                String[] newArray = new String[allFoods.length+1];
                System.arraycopy(allFoods,0,newArray,0,allFoods.length);
                allFoods = newArray;
                allFoods[i] = temp;
            }
            else
            {
                String temp = foods.get(i).getItemName() + " / Usage per day: " + foods.get(i).getUsagePerDay() + " / Weekly Spending Estimate: $" + foods.get(i).getWeeklySpending();
                String[] newArray = new String[allFoods.length+1];
                System.arraycopy(allFoods,0,newArray,0,allFoods.length);
                allFoods = newArray;
                allFoods[i] = temp;
            }


        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeArray();

        // no more this
        // setContentView(R.layout.list_fruit);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_fruit2,allFoods));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}