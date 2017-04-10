package com.google.android.gms.samples.vision.ocrreader;

import java.util.*;

public class TrendAnalyzer {

    static ArrayList<FoodItem> listOfFood = new ArrayList<FoodItem>();

    public static void buyFood()
    {
        Scanner sc = new Scanner(System.in);
        FoodItem f = new FoodItem();
        System.out.println("Type in item name");
        String fName = sc.nextLine();
        f.setItemName(fName);
        System.out.println("How many did you buy?");
        double amount = sc.nextDouble();
        //f.buy(amount);
        boolean alreadyExists = false;
        for(int i = 0; i < listOfFood.size(); i++)
        {
            if(listOfFood.get(i).getItemName().equals(fName))
            {
                alreadyExists = true;
                listOfFood.get(i).setAlreadyBought(true);
                //double newAmount = amount + listOfFood.get(i).getAmount();
                listOfFood.get(i).buy(amount);
                listOfFood.get(i).setDaysSincePurchased(0);
            }
        }
        if(!alreadyExists){
            f.buy(amount);
            listOfFood.add(f);
        }
        System.out.println(fName + " bought");
    }

    public static void printList()
    {
        if(listOfFood.size() == 0)
        {
            System.out.println("You are out of food. :( ");
        }
        else
        {

            for(int i = 0; i < listOfFood.size(); i++)
            {
                System.out.print("Item Name:" + listOfFood.get(i).getItemName() + " Quantity: " + listOfFood.get(i).getAmount());
                System.out.println(" Days Since Purchased " + listOfFood.get(i).getDaysSincePurchased() + " Days left: " + listOfFood.get(i).getDaysLeft() + " Usage per day: " + listOfFood.get(i).getUsagePerDay());
            }
        }
    }

    public static void advanceDays(int days)
    {
        for(int i = 0; i < listOfFood.size(); i++)
        {
            listOfFood.get(i).setDaysSincePurchased(days);
        }
    }
}
