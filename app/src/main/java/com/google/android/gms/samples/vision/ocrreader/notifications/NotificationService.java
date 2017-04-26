package com.google.android.gms.samples.vision.ocrreader.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;



import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// Notification icon import.
import com.google.android.gms.samples.vision.ocrreader.R;


public class NotificationService extends Service implements LocationListener {
    // Private properties
    private LocationManager locationManager;
    private LocationListener mListener;
    private Location previousBestLocation = null;
    private double latitude;
    private double longitude;
    private String lastPlaceId;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private final Handler handler = new Handler(); // We are going to use a handler to be able to run in our TimerTask

    // Static properties.
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static int PROXIMITY_RADIUS = 100; // Radius for 'radarsearch'.
    private static int RE_ENTER_NOTIFY_TIME = 600000; // 10 minute do not notify if entering same last store vicinity.

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        // Hook up this Notification Service (implements location listener) with the location service so we can listen to location changes.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Must check permissions prior to requesting location updates.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Request location updates by both the network provider and gps provider, we will determine which location is better.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        // Kill off location services.
        try {
            locationManager.removeUpdates(this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }

        // Example code: Not needed
        // Intent intent = new Intent("com.backgroundservice.mkr.backgroundservice.pantrynotification;");
        // intent.putExtra("value","something");
        // sendBroadcast(intent);
    }

    // Update the internal lastPlaceId and start or clear the timer if needed.
    public void updateLastPlaceId(String placeId) {
        // If the place is empty, hook up the timer task to clear the lastPlaceId in 1 minute.
        if (placeId.isEmpty()) {
            // If the timer task is to be ran in the future, cancel the timer and purge any cancelled tasks and null out the timer task so another is created.
            if (mTimerTask != null && mTimerTask.scheduledExecutionTime() > new Date().getTime()) {
                // Cancel any scheduled task so we don't clear the last place id after we set a new one right now.
                mTimer.cancel();
                mTimer.purge();
                mTimerTask = null;
            }

            // Create new timer task if we need one.
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // use a handler in the timer task.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Null out the last place id.
                                lastPlaceId = null;
                                // If the timer task is to be ran in the future, cancel the timer and purge any cancelled tasks and null out the timer task so another is created.
                                if (mTimerTask != null && mTimerTask.scheduledExecutionTime() > new Date().getTime()) {
                                    // Cancel any scheduled task so we don't clear the last place id after we set a new one right now.
                                    mTimer.cancel();
                                    mTimer.purge();
                                    mTimerTask = null;
                                }
                            }
                        });
                    }
                };
            }
            // Use a new timer since it may have been cancelled.
            mTimer = new Timer();
            // Execute timer after the RE_ENTER_NOTIFY_TIME time.
            mTimer.schedule(mTimerTask, RE_ENTER_NOTIFY_TIME);
        } else {
            if (!placeId.equals(lastPlaceId)) {
                // If the new placeId is different than the last. Make sure there is no timed task to clear out lastPlaceId in the future so the setting of this placeId to lastPlaceId is not cleared.
                if (mTimerTask != null && mTimerTask.scheduledExecutionTime() > new Date().getTime()) {
                    // Cancel any scheduled task so we don't clear the last place id after we set a new one right now.
                    mTimer.cancel();
                    mTimer.purge();
                }
                // Set the new last place id.
                lastPlaceId = placeId;
            }
        }
    }

    // Notify method. Accepts a boolean if a store is nearby and the place id of a store that is nearest.
    public void notifyMe(Boolean nearby, String placeId) {
        Boolean notify = nearby;
        // If the placeId exists, check is against the lastPlaceId used. If its the same, do not notify the user.
        if (placeId.isEmpty() && placeId.equals(lastPlaceId)) {
            notify = false;
        }

        // Update the internal lastPlaceId
        this.updateLastPlaceId(placeId);

        // If notify is true, notify the user that a store is nearby.
        if (notify) {
            Log.i("Log", "Notifying");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("RSSPullService");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Context context = getApplicationContext();

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle("Grocery store nearby!")
                    .setContentText("Check Pantry Buddy!")
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher_round);

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    // Example code for finding the best location.
    // From: https://developer.android.com/guide/topics/location/strategies.html
    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    @Override
    public void onLocationChanged(final Location loc) {
        if(isBetterLocation(loc, previousBestLocation)) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            // Build an object to pass into the GetNearbyPlacesData class.
            // Try to get the nearest places for the new location.
            String url = getUrl(latitude, longitude, "grocery_or_supermarket");
            Object[] DataTransfer = new Object[2];
            // Send the url to the nearby places object.
            DataTransfer[0] = url;

            // Include the notification service to send to the nearby places object so it can notify the user.
            DataTransfer[1] = this;

            // Create new nearby places data and execute. (Use a callback when it finishes rather than passing along the notification service)
            com.google.android.gms.samples.vision.ocrreader.notifications.GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
        }
    }

    // Helper function to build the google places radar search url.
    private String getUrl(double latitude, double longitude, String type) {
        //maps/api/place/radarsearch/json?location=48.859294,2.347589&radius=5000&type=cafe&keyword=vegetarian&key=YOUR_API_KEY
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/radarsearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&rankby=distance");
        googlePlacesUrl.append("&key=" + "AIzaSyCz2E27yVfFjDQJfUDWLHroEkN_L1rJddQ");
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
