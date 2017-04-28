package com.google.android.gms.samples.vision.ocrreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.samples.vision.ocrreader.notifications.NotificationService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Array;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.support.v4.app.NotificationCompat;



/**
 * Created by MatthewBrown on 3/4/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    long milliSecondsPerDay = 86400000;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    private static final String DATABASE_NAME = "FoodStorageDB3";

    // Contacts table name
    private static final String PANTRY_TABLE = "Pantry2";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id"; //0
    private static final String KEY_FOOD_NAME = "_foodName"; //1
    private static final String KEY_QUANTITY = "_quantity"; //2
    private static final String KEY_DATE_PURCHASED = "_dateBought"; //3
    private static final String KEY_DAYS_LEFT = "_daysLeft"; //4
    private static final String KEY_USAGE_PERDAY = "_usagePerDay"; //5
    private static final String KEY_DAYS_ELAPSED = "_daysElapsed"; //6
    private static final String KEY_ITEM_PRICE = "_itemPrice"; //7
    private static final String KEY_ITEM_MILLIS_OLD = "_itemMillisOld"; //8
    private static final String KEY_ITEM_MILLIS_NEW = "_itemMillisNew"; //9
    private static final String KEY_WEEKLY_SPENDING = "_weeklySpending"; //10


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + PANTRY_TABLE + "("+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_FOOD_NAME + " TEXT,"+ KEY_QUANTITY + " REAL," + KEY_DATE_PURCHASED + " TEXT," + KEY_DAYS_LEFT + " INTEGER," + KEY_USAGE_PERDAY + " REAL,"+ KEY_DAYS_ELAPSED + " TEXT," + KEY_ITEM_PRICE + " REAL," + KEY_ITEM_MILLIS_OLD + " REAL," + KEY_ITEM_MILLIS_NEW + " REAL," + KEY_WEEKLY_SPENDING + " REAL " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + PANTRY_TABLE);
        // Create tables again
        onCreate(db);
    }



    //Accepts a fooditem object to add to the DB. Required Logic differs if it already exists or not.
    public void addFood(FoodItem foodItem) {
       // long milliSecondsCurrent = System.currentTimeMillis();
       long milliSecondsCurrent = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(("MM-dd-yyyy"));

        ContentValues values = new ContentValues();
        if(itemIsInDatabase(foodItem.getItemName())) //Old item
        {
            SQLiteDatabase db = this.getWritableDatabase();
          //  System.out.println("Item exists in the DB");

            FoodItem tempFood = getFoodItem(foodItem.getItemName());

         //   System.out.println("help1");
            deleteContact(foodItem); //Maybe this won't break?
          //  System.out.println("help2");
            //This stuff below will change
            //values.put(KEY_FOOD_NAME, foodItem.getItemName()); // Food Name
            double previousQuantity = tempFood.getAmount();

            long oldMilliseconds = tempFood.getNewMillis();
            double daysSincePurchase = (milliSecondsCurrent - oldMilliseconds) / milliSecondsPerDay;
            double usagePerDay = foodItem.getAmount() / daysSincePurchase;
        //    System.out.println("Current millis: " + milliSecondsCurrent);
            System.out.println(milliSecondsPerDay);
          //  System.out.println("Magic number: " + (milliSecondsCurrent - oldMilliseconds));
           // System.out.println("Magic number 2: " + (milliSecondsCurrent - oldMilliseconds) / milliSecondsPerDay);
           // System.out.println("Days passed: " + daysSincePurchase);
           // System.out.println("You're using: " + usagePerDay + " a day");

            if(daysSincePurchase < 1.0) //If it has only been 1 day, don't calculate usage per day or else it becomes infinity.
            {
                values.put(KEY_FOOD_NAME,foodItem.getItemName());
                values.put(KEY_QUANTITY, foodItem.getAmount() + previousQuantity);
                values.put(KEY_DATE_PURCHASED, sdf.format(d.getTime()));
                values.put(KEY_ITEM_MILLIS_OLD, oldMilliseconds);
                values.put(KEY_ITEM_MILLIS_NEW,System.currentTimeMillis());
                values.put(KEY_ITEM_PRICE,foodItem.getPrice());
                db.insert(PANTRY_TABLE, null, values);
            }
            else {

                values.put(KEY_USAGE_PERDAY, usagePerDay);
                //This next block updates the previous quantity based on the usage per day, to estimate how much is left from previous purchase.
                double tempPreviousQuantity = (previousQuantity - (daysSincePurchase * usagePerDay));
                if (tempPreviousQuantity <= 0) {
                //    System.out.println("We hit 0");
                    tempPreviousQuantity = 0;
                }
                previousQuantity = tempPreviousQuantity;
            //    System.out.println(previousQuantity);
                //New amount is equal to previous amount + old amount
                values.put(KEY_FOOD_NAME, foodItem.getItemName());
                values.put(KEY_QUANTITY, foodItem.getAmount() + previousQuantity);
                //   System.out.println("help3");
                values.put(KEY_DATE_PURCHASED, sdf.format(d.getTime()));

                //System.out.println("Previous purchase was at: " + oldMilliseconds);
                // System.out.println("Current millis is: " + System.currentTimeMillis());


                // System.out.println("This is a difference of: " + daysSincePurchase + " days");
                // System.out.println("help4");
                values.put(KEY_ITEM_MILLIS_OLD, oldMilliseconds);
                // System.out.println("help5");

                values.put(KEY_ITEM_MILLIS_NEW, System.currentTimeMillis());
                // System.out.println("help6");
                values.put(KEY_ITEM_PRICE, foodItem.getPrice());

                //Weekly spending calculation done here
                double weeklySpending = ((foodItem.getPrice() / foodItem.getAmount()) * usagePerDay * 7);
                //We get the money they spent, divided by the amount to get a base price, then extrapolate that amilliSecondsPerDay * 7cross 7 days.
                //System.out.println("Weekly spending estimate is: " + weeklySpending);
                values.put(KEY_WEEKLY_SPENDING, weeklySpending);
                //System.out.println("help7");
                // Inserting Row
                db.insert(PANTRY_TABLE, null, values);
            }
         //   System.out.println("help8");
           // db.close(); // Closing database connection
        }
        else if(!itemIsInDatabase(foodItem.getItemName())) //New item
        {
            SQLiteDatabase db = this.getWritableDatabase();
          //  System.out.println("Item is not in DB");
            values.put(KEY_FOOD_NAME, foodItem.getItemName()); // Food Name
            values.put(KEY_QUANTITY, foodItem.getAmount());
            values.put(KEY_DATE_PURCHASED, sdf.format(d.getTime())) ;
            milliSecondsCurrent = System.currentTimeMillis();
            values.put(KEY_ITEM_MILLIS_NEW,milliSecondsCurrent);
        //    System.out.println("Item Millis New = " + milliSecondsCurrent);
            values.put(KEY_ITEM_PRICE,foodItem.getPrice());
            // Inserting Row
            db.insert(PANTRY_TABLE, null, values);
            //db.close(); // Closing database connection
        }



    }

    public boolean itemIsInDatabase(String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sql ="SELECT " + KEY_FOOD_NAME + " FROM "+PANTRY_TABLE+" WHERE _foodName = '"+fieldValue+"' ";
        cursor= db.rawQuery(sql,null);
        if(cursor.getCount()>0){
            cursor.close();
           // db.close();
            return true;
        }else{
            cursor.close();
          //  db.close();
            return false;
        }
    }

    public FoodItem getFoodItem(String fieldValue) {

     //   System.out.println("fieldValue is: " + fieldValue);
        SQLiteDatabase db = this.getReadableDatabase();
      //  System.out.println("dragon1");
      /*  Cursor cursor = db.query(PANTRY_TABLE, new String[] { KEY_ID,
                        KEY_FOOD_NAME, KEY_QUANTITY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);*/
        Cursor cursor = null;
     //   System.out.println("dragon2");
        String sql ="SELECT " + KEY_FOOD_NAME + "," + KEY_QUANTITY + "," + KEY_ITEM_MILLIS_NEW + "," + KEY_ITEM_MILLIS_OLD + "," + KEY_DATE_PURCHASED + "," + KEY_WEEKLY_SPENDING + "," + KEY_USAGE_PERDAY + " FROM "+PANTRY_TABLE+" WHERE _foodName = '"+fieldValue+"' ";
      //  System.out.println(sql);
        //System.out.println("dragon3");
        cursor= db.rawQuery(sql,null);
        int nameIndex = cursor.getColumnIndex(KEY_FOOD_NAME);
        int quantityIndex = cursor.getColumnIndex(KEY_QUANTITY);
        int oldMillisIndex = cursor.getColumnIndex(KEY_ITEM_MILLIS_OLD);
        int newMillisIndex = cursor.getColumnIndex(KEY_ITEM_MILLIS_NEW);
        int datePurchasedIndex = cursor.getColumnIndex(KEY_DATE_PURCHASED);
        int weeklySpendingIndex = cursor.getColumnIndex(KEY_WEEKLY_SPENDING);
        int usagePerDayIndex = cursor.getColumnIndex(KEY_USAGE_PERDAY);
   //     System.out.println(quantityIndex);
        //System.out.println("dragon4");
        cursor.moveToFirst();
        //System.out.println("dragon5");
     //   System.out.println(cursor.getString(nameIndex));
        //System.out.println("dragon6");
       // System.out.println(cursor.getString(quantityIndex));
        //System.out.println("dragon7");
        FoodItem f1 = new FoodItem(  (cursor.getString(nameIndex)), Double.valueOf((cursor.getString(quantityIndex))));
        //System.out.println("dragon7.5");
        if(cursor.getString(newMillisIndex) != null) {
            f1.setNewMillis(Double.valueOf(cursor.getString(newMillisIndex)).longValue());
        }
        if(cursor.getString(oldMillisIndex) != null) {
            f1.setOldMillis(Double.valueOf(cursor.getString(oldMillisIndex)).longValue());
        }
        //System.out.println("dragon8");


        if(cursor.getString(weeklySpendingIndex) != null)
        {
            f1.setWeeklySpending(Double.valueOf(cursor.getString(weeklySpendingIndex)));
        }
//        System.out.println(usagePerDayIndex);
  //      System.out.println(cursor.getString(usagePerDayIndex));
        if(cursor.getString(usagePerDayIndex) != null)
        {
            f1.setUsagePerDay(Double.valueOf(cursor.getString(usagePerDayIndex)));
        }
        f1.setDatePurchased(cursor.getString(datePurchasedIndex));
       // f1.setUsagePerDay(Double.valueOf(cursor.getString(usagePerDayIndex)));
       // f1.setWeeklySpending(Double.valueOf(cursor.getString(weeklySpendingIndex)));
        //System.out.println("dragon9");
        return f1;
    }

    public ArrayList<FoodItem> getAllFoods() {
        ArrayList<FoodItem> foodList = new ArrayList<FoodItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + PANTRY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

    //   System.out.println("Inside the getallfoods");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FoodItem f1 = new FoodItem();
                //f1.setID(Integer.parseInt(cursor.getString(0)));


                f1.setItemName(cursor.getString(1));
                f1.setAmount(Double.valueOf(cursor.getString(2)));

                f1.setDatePurchased(cursor.getString(3));

                f1.setPrice(Double.valueOf(cursor.getString(7)));
          //      System.out.println("getallfoods1");
              //  System.out.println(cursor.getString(10));
                if(cursor.getString(10) != null) {

                    f1.setWeeklySpending(Double.valueOf(cursor.getString(10)));
                }
                if(cursor.getString(5) != null) {
                    f1.setUsagePerDay(Double.valueOf(cursor.getString(5)));
                }
                if(cursor.getString(8) != null)
                {
                    f1.setOldMillis(Double.valueOf(cursor.getString(8)).longValue());
                }
                if(cursor.getString(9) != null)
                {
                    f1.setNewMillis(Double.valueOf(cursor.getString(9)).longValue());
                }

                foodList.add(f1);
            } while (cursor.moveToNext());
        }

       // System.out.println("leaving getallfoods");
        // return contact list
        return foodList;
    }

    //Total number of items in the DB
    public int getFoodCount() {
        String countQuery = "SELECT  * FROM " + PANTRY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }



    public void deleteContact(FoodItem contact) {
        //System.out.println("debug1");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PANTRY_TABLE, KEY_FOOD_NAME + " = ?",
                new String[] { String.valueOf(contact.getItemName()) });
       // db.close();
    }

    public void deletePantryRows()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PANTRY_TABLE,null,null);

    }

    //Should be called once a day or so to update quantity
    public void recalcFoodAmounts()
    {
        SQLiteDatabase db = this.getWritableDatabase();
       ArrayList<FoodItem> foodList = getAllFoods();
        for(int i = 0; i < foodList.size(); i++)
        {
            long currentMillis = System.currentTimeMillis();
            if(foodList.get(i).getAmount() == 0)
            {
                //Nothing to do
            }
            else
            {
                ContentValues values = new ContentValues();
                String name = foodList.get(i).getItemName();
                String datePurchased = foodList.get(i).getDatePurchased();
                double price = foodList.get(i).getPrice();
                double oldAmount = foodList.get(i).getAmount();
                double usagePerDay = foodList.get(i).getUsagePerDay();
                long oldMillis = foodList.get(i).getOldMillis();
                long newMillis = foodList.get(i).getNewMillis();
                double weeklySpending = foodList.get(i).getWeeklySpending();

                double daysSinceBought = (currentMillis - newMillis) / milliSecondsPerDay;
             //   System.out.println("Days since bought: " + daysSinceBought);
                double newAmount = (oldAmount - (usagePerDay * daysSinceBought));
                if(newAmount <= 0)
                    newAmount = 0;

             //   System.out.println("New amount: " + newAmount);

                deleteContact(foodList.get(i));

                values.put(KEY_FOOD_NAME, name); // Food Name
                values.put(KEY_QUANTITY, newAmount);
                values.put(KEY_DATE_PURCHASED, datePurchased);
                values.put(KEY_USAGE_PERDAY, usagePerDay);
                values.put(KEY_ITEM_PRICE, price);
                values.put(KEY_ITEM_MILLIS_OLD, oldMillis);
                values.put(KEY_ITEM_MILLIS_NEW, newMillis);
                values.put(KEY_WEEKLY_SPENDING, weeklySpending);
                db.insert(PANTRY_TABLE,null,values);
            }
        }
    }

    public boolean checkForExpired() {
    //    System.out.println("checking for expired");

        ArrayList<FoodItem> foodList = getAllFoods();
        for(int i = 0; i < foodList.size(); i++)
        {
            long currMills = System.currentTimeMillis();
            double dayselapsed = (currMills - foodList.get(i).getNewMillis()) / 86400000;
            if(foodList.get(i).getAmount() > 0 &&  (dayselapsed > 10 )) //If there is less than 2 days worth of food
            {
           //     System.out.println("Current millis:" + currMills);
             //   System.out.println("new millis: " + foodList.get(i).getNewMillis());
               // System.out.println("calc number:" + (currMills - foodList.get(i).getNewMillis()));
              //  double dayselapsed = (currMills - foodList.get(i).getNewMillis()) / 86400000;
                //System.out.println("Days elapsed:" + dayselapsed);
                //Send notification
             //System.out.println("Notification expired sending now");
                //notif.notifyMe(true, "low food");
                return true;
            }
        }
        return false;

    }

    public boolean checkAmountsAndSendNotification()
    {
      //  System.out.println("checking amounts");
        //NotificationService notif = new NotificationService();
        ArrayList<FoodItem> foodList = getAllFoods();
        for(int i = 0; i < foodList.size(); i++)
        {
        //    System.out.println("Amount: " + foodList.get(i).getAmount());
          //  System.out.println("UsagePerDay * 2 " + foodList.get(i).getUsagePerDay() * 2);
            if(foodList.get(i).getAmount() < (foodList.get(i).getUsagePerDay() * 2) ) //If there is less than 2 days worth of food
            {
                //Send notification
            //    System.out.println("Notification sending now");
                //notif.notifyMe(true, "low food");
                return true;
            }
        }
        return false;
        //Loop through all foods in the DB. If: amount  - (usagePerDay*2) < 2, send notification to user
    }

    public void deleteEmpty()
    {
        ArrayList<FoodItem> foodList = getAllFoods();
        for(int i = 0; i < foodList.size(); i++)
        {
            if(foodList.get(i).getAmount() == 0)  //If there is less than 2 days worth of food
            {
              deleteContact(foodList.get(i));
            }
        }
    }






}
