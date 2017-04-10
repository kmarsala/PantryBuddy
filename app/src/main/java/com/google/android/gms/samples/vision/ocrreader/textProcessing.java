package com.google.android.gms.samples.vision.ocrreader;

import android.app.ListActivity;

import java.util.ArrayList;

/**
 * Created by josh on 3/20/17.
 */

public class textProcessing extends ListActivity {
    public void processItems(ArrayList<String> it) {
        DatabaseHandler db = new DatabaseHandler(this);
        String[] arrProcess = it.toArray(new String[it.size()]);
        FoodItem f;
        for (int i = 0; i < arrProcess.length; i++) {
            if (checkValidity(arrProcess[i])) {
                double num = 1.0;
                    if (nextAmountValid(arrProcess, i))
                        num = Double.parseDouble(arrProcess[i+1]);
                f = new FoodItem(arrProcess[i], num);
                db.addFood(f);
            }
        }
    }

    private boolean checkValidity(String s) {
        DatabaseHandler db = new DatabaseHandler(this);
        FoodItem f = db.getFoodItem(s.toLowerCase());
        return (f.getOldMillis() > 600000);
    }

    
    private boolean nextAmountValid(String[] toCheck, int i){
        boolean rVal = false;
        if(toCheck[i+1] != null)
            if(Double.parseDouble(toCheck[i+1]) >= 0.0)
                rVal = true;
        return rVal;
    }
}