package com.example.ari.nearby;

import android.database.Cursor;

/**
 * Class representing placemark used in Google Maps
 *
 * @author poohdishr
 */
public class Placemark {
    private String title = "";
    private String description = "";
    private double latitude = 0.;
    private double longitude = 0.;
    private double distance = 0.f;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Placemark fromCursor(Cursor cursor) {
        // TODO: exception if column not found
        Placemark place = new Placemark();
        place.setTitle(cursor.getString(cursor.getColumnIndex(PlacesContentProvider.TITLE)));
        place.setDescription(cursor.getString(cursor.getColumnIndex(PlacesContentProvider.DESCRIPTION)));
        place.setLatitude(cursor.getDouble(cursor.getColumnIndex(PlacesContentProvider.LATITUDE)));
        place.setLongitude(cursor.getDouble(cursor.getColumnIndex(PlacesContentProvider.LONGITUDE)));
        return place;
    }
}