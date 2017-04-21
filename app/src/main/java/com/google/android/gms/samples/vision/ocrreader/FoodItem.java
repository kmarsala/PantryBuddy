package com.google.android.gms.samples.vision.ocrreader;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FoodItem {

    private String itemName;
    private int ID;
    private double daysLeft;
    private double daysSincePurchased;
    private String datePurchased;
    private double usagePerDay;
    private double amount;
    private double oldAmount;
    private double oldDays;
    private boolean alreadyBought = false;
    private boolean trendFormed = false;
    private double price;
    private long oldMillis;
    private long newMillis;
    private double weeklySpending;

    public FoodItem()
    {

    }

    public FoodItem(String n, double qty)
    {
        itemName = n;
        setAmount(qty);
    }

    public FoodItem(int i, String n, double qty)
    {
        ID = i;
        itemName = n;
        amount = qty;
    }

    public void setPrice(double p)
    {
        price = p;
    }

    public double getPrice()
    {
        return price;
    }

    public long getOldMillis()
    {
        return oldMillis;
    }

    public void setOldMillis(long om)
    {
        oldMillis = om;
    }

    public long getNewMillis()
    {
        return newMillis;
    }

    public void setNewMillis(long nm)
    {
        newMillis = nm;
    }

    public void setDatePurchased(String s)
    {
        datePurchased = s;
    }

    public String getDatePurchased()
    {
        return datePurchased;
    }

    public void setID(int i)
    {
        ID = i;
    }

    public int getID()
    {
        return ID;
    }

    public void buy(double qty)
    {
        oldAmount = amount; //Keep track of previous amount before buying more
        setAmount(getAmount() + qty);
        oldDays = daysSincePurchased;
        if(alreadyBought)
        {
            usagePerDay = (amount - oldAmount) / ( oldDays );
            trendFormed = true;
        }
        daysSincePurchased = 0;
    }

    public double getDaysLeft() {
        daysLeft = amount / usagePerDay;
        return daysLeft;
    }
    public void setDaysLeft(double daysLeft) {
        this.daysLeft = daysLeft;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public double getDaysSincePurchased() {
        calculateDaysSincePurchased();
        return daysSincePurchased;
    }

    //Todo: Implement based on number of milliseconds
    public void setDaysSincePurchased(double d) {
        daysSincePurchased = d;
        if(trendFormed)
        {
            amount -= daysSincePurchased * usagePerDay;
        }
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double a) {
        amount = a;
    }


    public boolean isAlreadyBought() {
        return alreadyBought;
    }

    public void setAlreadyBought(boolean alreadyBought) {
        this.alreadyBought = alreadyBought;
    }

    public double getUsagePerDay()
    {
        return usagePerDay;
    }

    public void setUsagePerDay(double usage)
    {
        usagePerDay = usage;
    }

    public double getWeeklySpending()
    {
        return weeklySpending;
    }

    public void setWeeklySpending(double weekly)
    {
        weeklySpending = weekly;
    }

    private void calculateDaysSincePurchased()
    {
        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(("MM-dd-yyyy"));
        sdf.format(d.getTime());
        double currentDate = Double.valueOf(sdf.toString().substring(3,5));
        double dayBought = Double.valueOf(datePurchased.substring(3,5));
        daysSincePurchased = currentDate - dayBought;

    }


}