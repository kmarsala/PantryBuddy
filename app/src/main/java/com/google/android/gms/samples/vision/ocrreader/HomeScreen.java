package com.google.android.gms.samples.vision.ocrreader;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.AlarmClock.EXTRA_MESSAGE;



public class HomeScreen extends AppCompatActivity {


    private boolean notifSent = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("Handler run");
      /* do what you need to do */
            checkTrendsNotifs();
      /* and here comes the "trick" */
           handler.postDelayed(this, 100000);//(86400000/8)/4);

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler.postDelayed(runnable, 100);


    }



    @Override
    protected void onResume()
    {
        super.onResume();
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        System.out.println("on resume");
        //dbHandler.recalcFoodAmounts();
    }

    public void checkTrendsNotifs()
    {
        System.out.println("checking stuff");
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        dbHandler.recalcFoodAmounts();
        if( dbHandler.checkAmountsAndSendNotification() )
        {
            if(!notifSent) {
                notifSent = true;
                addNotification();
            }
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



    public void inputList(View view)
    {
        notifSent = false;
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void viewList(View view)
    {
        notifSent = false;
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
        checkTrendsNotifs();
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

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}

