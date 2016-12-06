package com.example.ari.nearby;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
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

import static com.example.ari.nearby.Constants.FILTER_DISTANCE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class LocationService extends IntentService {

    private final int mId = 2324;

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!LocationResult.hasResult(intent)) return;

        final LocationResult locationResult = LocationResult.extractResult(intent);
        Location location = locationResult.getLastLocation();

        // get cursor from content provider
        // then create the wrapper which 'sorts' the cursor by distance
        CursorWrapper cursor = new DistanceCursorWrapper(getContentResolver().query(PlacesContentProvider.CONTENT_URI, null, null, null, null), location);

        // build the list of places to be shown in the notification
        // note the list will automatically be sorted
        ArrayList<Placemark> filteredPlaces = new ArrayList<>();
        while (cursor.moveToNext()) {
            Placemark p = Placemark.fromCursor(cursor);
            p.setDistance(Utils.distanceToPlace(location, p));
            if (p.getDistance() < FILTER_DISTANCE) {
                filteredPlaces.add(p);
            }
        }

        showNotification(filteredPlaces);
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
