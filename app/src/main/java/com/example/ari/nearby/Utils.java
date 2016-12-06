package com.example.ari.nearby;

import android.location.Location;

public class Utils {
    public static float calcDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        float[] results = new float[1]; // length 1 to ignore bearing
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
        return results[0];
    }

    public static float distanceToPlace(Location location, Placemark place) {
        return calcDistance(location.getLatitude(), location.getLongitude(), place.getLatitude(), place.getLongitude());
    }
}
