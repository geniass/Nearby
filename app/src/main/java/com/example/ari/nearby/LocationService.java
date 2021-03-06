package com.example.ari.nearby;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationResult;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class LocationService extends IntentService {

    PlacemarkManager placemarkManager;
    private final int mId = 2324;
    private final double FILTER_DISTANCE = 2000; //TODO: make a preference or something

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!LocationResult.hasResult(intent)) return;

        try {
            placemarkManager = new PlacemarkManager(getApplicationContext());
            final LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            placemarkManager.updateWithNewLocation(location);
            Log.d("LocationService", "Lat: " + location.getLatitude());
            ArrayList<Placemark> placemarks = new ArrayList<>(Arrays.asList(placemarkManager.getPlacemarkArray()));
            ArrayList<Placemark> filteredPlaces = new ArrayList<>();
            for (Placemark p : placemarks) {
                if (p.getDistance() < FILTER_DISTANCE) {
                    filteredPlaces.add(p);
                }
            }

            showNotification(filteredPlaces);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(ArrayList<Placemark> places) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION);

        if (places.isEmpty()) return;
        if (places.size() == 1) {
            Placemark place = places.get(0);
            mBuilder.setContentTitle(String.format(getString(R.string.place_nearby), places.get(0).getTitle()))
                    .setContentText(String.format(getString(R.string.place_distance), place.getDistance() / 1000.));

            Uri mapUri = Uri.parse(String.format(getString(R.string.map_uri_format), place.getLatitude(), place.getLongitude(), 15, Uri.encode(place.getTitle())));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
        } else {
            mBuilder.setContentTitle(String.format(getString(R.string.places_nearby), places.size()));
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(getString(R.string.places_nearby));

            for (Placemark p : places) {
                inboxStyle.addLine(String.format(getString(R.string.place_notification_item), p.getTitle(), p.getDistance() / 1000.));
            }
            mBuilder.setStyle(inboxStyle);

            /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(PlacesActivity.class);
       //     stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);*/
        }



/*// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);stackBuilder.addParentStack(ResultActivity.class);stackBuilder.addNextIntent(resultIntent);PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);*/
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
