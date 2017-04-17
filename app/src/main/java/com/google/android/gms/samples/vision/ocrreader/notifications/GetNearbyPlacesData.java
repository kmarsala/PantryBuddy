package com.google.android.gms.samples.vision.ocrreader.notifications;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
// Example from https://www.androidtutorialpoint.com/intermediate/google-maps-search-nearby-displaying-nearby-places-using-google-places-api-google-maps-api-v2/

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;
    NotificationService notificationServ;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            url = (String) params[0];
            // Keep the notification service to notify the user if needed.
            notificationServ = (NotificationService) params[1];;
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);

            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");

        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        NotifyIfNearby(nearbyPlacesList);

        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void NotifyIfNearby(List<HashMap<String, String>> nearbyPlacesList) {
        if(nearbyPlacesList.size() > 0) {
            HashMap<String, String> googlePlace = nearbyPlacesList.get(0);
            String placeId = googlePlace.get("place_id");
            // Notify.
            notificationServ.notifyMe(true, placeId);
        } else {
            // Notify if not nearby so the notification service can update the lastPlaceId and determine when to update again.
            notificationServ.notifyMe(false, new String(""));
        }
    }
}
