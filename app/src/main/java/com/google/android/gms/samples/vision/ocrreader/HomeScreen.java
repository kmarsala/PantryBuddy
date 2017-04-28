package com.google.android.gms.samples.vision.ocrreader;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;

import com.google.android.gms.samples.vision.ocrreader.notifications.NotificationService;

import java.util.Timer;
import java.util.TimerTask;

import static android.provider.AlarmClock.EXTRA_MESSAGE;



public class HomeScreen extends AppCompatActivity {


    NotificationService gpsAlerter = new NotificationService();
    private boolean canSendNotif = true;
    private boolean notificationsOn = false;


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            checkTrendsNotifs();
      /* and here comes the "trick" */
        //Change this number for how often to check for notifications and trend calc
           handler.postDelayed(this, 86400000/2); //Twice a day

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler.postDelayed(runnable, 100000/2);
    }


    public void checkTrendsNotifs()
    {
      //  System.out.println("checking stuff");
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.recalcFoodAmounts();

     if( dbHandler.checkAmountsAndSendNotification() )
        {

            startNotificationService();
            if(canSendNotif) {
                canSendNotif = false;
                addNotification();
            }

        }
       else if (dbHandler.checkForExpired())
        {
            addNotificationExpired();
        }

        if(!dbHandler.checkAmountsAndSendNotification())
        {
            stopNotificationService();
        }
    }




    public void inputList(View view)
    {
        canSendNotif = true;
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        Bundle b = new Bundle();
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void viewList(View view)
    {
        canSendNotif = true;
        checkTrendsNotifs();
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.recalcFoodAmounts();
        Intent intent = new Intent(this, ViewLoadPantry.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void viewTrends(View view)
    {
        Intent intent = new Intent(this, ViewTrends.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void viewSettings(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void scanReceipt(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Pantry Buddy Low Food Alert")
                        .setContentText("You might be low on some food! Check your Pantry.");

        Intent notificationIntent = new Intent(this, ViewLoadPantry.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void addNotificationExpired() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Pantry Buddy Short Date Alert")
                        .setContentText("Some items you bought a while back may be expired!");

        Intent notificationIntent = new Intent(this, ViewLoadPantry.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void onTutorialClick(View view)
    {
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=XK-81BIMCYg");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void startNotificationService() {
        if(!notificationsOn) {
            startService(new Intent(this, NotificationService.class));
            System.out.println("GPS Started");
            notificationsOn = true;
        }
    }

    private void stopNotificationService() {
        if(notificationsOn) {
            stopService(new Intent(this, NotificationService.class));
            System.out.println("GPS stopped");
            notificationsOn = false;
        }
    }

}

