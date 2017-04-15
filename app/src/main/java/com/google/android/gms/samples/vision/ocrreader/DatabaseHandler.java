package com.google.android.gms.samples.vision.ocrreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by MatthewBrown on 3/4/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 11;

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
    private static final String KEY_USAGE_PERDAY = " _usagePerDay"; //5
    private static final String KEY_DAYS_ELAPSED = "_daysElapsed"; //6
    private static final String KEY_ITEM_PRICE = "_itemPrice"; //7
    private static final String KEY_ITEM_MILLIS_OLD = "_itemMillisOld"; //8
    private static final String KEY_ITEM_MILLIS_NEW = "_itemMillisNew"; //9


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + PANTRY_TABLE + "("+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_FOOD_NAME + " TEXT,"+ KEY_QUANTITY + " REAL," + KEY_DATE_PURCHASED + " TEXT," + KEY_DAYS_LEFT + " INTEGER," + KEY_USAGE_PERDAY + " REAL,"+ KEY_DAYS_ELAPSED + " TEXT," + KEY_ITEM_PRICE + " REAL," + KEY_ITEM_MILLIS_OLD + " REAL," + KEY_ITEM_MILLIS_NEW + " REAL " + ")";
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

    public void addFood(FoodItem foodItem) {


        long milliSecondsCurrent = System.currentTimeMillis();

        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(("MM-dd-yyyy"));
        ContentValues values = new ContentValues();
        if(itemIsInDatabase(foodItem.getItemName())) //Old item
        {

            //TODO: If item is in database, construct temp foodItem object, delete row, then insert new one.
            SQLiteDatabase db = this.getWritableDatabase();
            //TODO: If item is in DB, add the quantity and such.
            System.out.println("Item exists in the DB");
            FoodItem tempFood = getFoodItem(foodItem.getItemName()); //In theory this will have grabbed everything we need
           System.out.println("help1");
            deleteContact(foodItem); //Maybe this won't break?
            System.out.println("help2");
            //This stuff below will change
            //values.put(KEY_FOOD_NAME, foodItem.getItemName()); // Food Name
            double previousQuantity = tempFood.getAmount();
            //New amount is equal to previous amount + old amount
            values.put(KEY_QUANTITY, foodItem.getAmount() + previousQuantity);
            values.put(KEY_DATE_PURCHASED, sdf.format(d.getTime()));
            long oldMilliseconds = tempFood.getNewMillis();
            values.put(KEY_ITEM_MILLIS_OLD, oldMilliseconds);
            values.put(KEY_ITEM_MILLIS_NEW,milliSecondsCurrent);
            values.put(KEY_ITEM_PRICE,foodItem.getPrice());
            // Inserting Row
            db.insert(PANTRY_TABLE, null, values);
            db.close(); // Closing database connection
        }
        else if(!itemIsInDatabase(foodItem.getItemName())) //New item
        {
            SQLiteDatabase db = this.getWritableDatabase();
            System.out.println("Item is not in DB");
            values.put(KEY_FOOD_NAME, foodItem.getItemName()); // Food Name
            values.put(KEY_QUANTITY, foodItem.getAmount());
            values.put(KEY_DATE_PURCHASED, sdf.format(d.getTime())) ;
            values.put(KEY_ITEM_MILLIS_NEW,milliSecondsCurrent);
            values.put(KEY_ITEM_PRICE,foodItem.getPrice());
            // Inserting Row
            db.insert(PANTRY_TABLE, null, values);
            db.close(); // Closing database connection
        }


    }

    public boolean itemIsInDatabase(String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sql ="SELECT " + KEY_FOOD_NAME + " FROM "+PANTRY_TABLE+" WHERE _foodName = '"+fieldValue+"' ";
        cursor= db.rawQuery(sql,null);
        if(cursor.getCount()>0){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    //Todo: Fix this.
    public FoodItem getFoodItem(String fieldValue) {
        System.out.println("fieldValue is: " + fieldValue);
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println("dragon1");
      /*  Cursor cursor = db.query(PANTRY_TABLE, new String[] { KEY_ID,
                        KEY_FOOD_NAME, KEY_QUANTITY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);*/
        Cursor cursor = null;
        System.out.println("dragon2");
        String sql ="SELECT " + KEY_FOOD_NAME + " FROM "+PANTRY_TABLE+" WHERE _foodName = '"+fieldValue+"' ";
        System.out.println("dragon3");
        cursor= db.rawQuery(sql,null);
        System.out.println("dragon4");
        cursor.moveToFirst();
        System.out.println("dragon5");
        System.out.println(cursor.getString(1));
        System.out.println("dragon6");
        FoodItem f1 = new FoodItem(  (cursor.getString(1)), Double.valueOf((cursor.getString(2))));
        f1.setOldMillis(Integer.parseInt(cursor.getString(8)));
        f1.setDatePurchased(cursor.getString(3));
        return f1;
    }

    public ArrayList<FoodItem> getAllFoods() {
        ArrayList<FoodItem> foodList = new ArrayList<FoodItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + PANTRY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        System.out.println("Inside the getallfoods");
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FoodItem f1 = new FoodItem();
                f1.setID(Integer.parseInt(cursor.getString(0)));

                f1.setItemName(cursor.getString(1));
                f1.setAmount(Double.valueOf(cursor.getString(2)));

                f1.setDatePurchased(cursor.getString(3));

                f1.setPrice(Double.valueOf(cursor.getString(7)));

//                f1.setPrice(Double.valueOf(cursor.getString(7)));

                foodList.add(f1);
            } while (cursor.moveToNext());
        }

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

    //I hate this method and everything about it. Please kill me.
    //Don't use this method. Shit will crash, yo.
    public int updateItem(FoodItem f1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOOD_NAME, f1.getItemName());
        values.put(KEY_QUANTITY, f1.getAmount());

        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(("MM-dd-yyyy"));
        sdf.format(d.getTime());
        System.out.println(sdf.format(d));

        System.out.println("here be dragons");
     //   System.out.println((sdf.substring(3,5)));
        Double currentDays = Double.valueOf(sdf.toString().substring(3,5));
        System.out.println("dragons gone");
  //      Double oldMonth = Double.valueOf(f1.getDatePurchased().substring(0,2));
        Double oldDays = Double.valueOf(f1.getDatePurchased().substring(3,5));
        System.out.println("new dragons");
        double elapsedDays = currentDays - oldDays;
        System.out.println("slayed dragon");
        //Ignore month for now
        values.put(KEY_DAYS_ELAPSED,elapsedDays);

        // updating row
        return db.update(PANTRY_TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(f1.getID()) });
    }


    public void deleteContact(FoodItem contact) {
        System.out.println("debug1");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PANTRY_TABLE, KEY_FOOD_NAME + " = ?",
                new String[] { String.valueOf(contact.getItemName()) });
        db.close();
    }




}
