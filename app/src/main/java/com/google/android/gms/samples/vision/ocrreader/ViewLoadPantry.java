package com.google.android.gms.samples.vision.ocrreader;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewLoadPantry extends ListActivity {

    ArrayList<FoodItem> foods;

    String[] allFoods = new String[0];

    public void makeArray()
    {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        foods = dbHandler.getAllFoods();

        System.out.println("entering for loop");
        //Updates the days elapsed
       /* for(int i = 0; i < foods.size(); i++)
        {
            dbHandler.updateItem(foods.get(i));
        }
*/
        for(int i = 0; i < foods.size(); i++)
        {
            System.out.println(foods.get(i).getItemName());
            String temp = foods.get(i).getItemName() + "/" + Double.valueOf(foods.get(i).getAmount()).toString() + "/" + foods.get(i).getDatePurchased() + "/" + foods.get(i).getNewMillis();
            String[] newArray = new String[allFoods.length+1];
            System.arraycopy(allFoods,0,newArray,0,allFoods.length);
            allFoods = newArray;
            allFoods[i] = temp;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeArray();

        System.out.println("debug1");
        // no more this
        // setContentView(R.layout.list_fruit);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_fruit,allFoods));
        System.out.println("debug2");

        ListView listView = getListView();
        System.out.println("debug3");

        listView.setTextFilterEnabled(true);
        System.out.println("debug4");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
