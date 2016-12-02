package com.example.ari.nearby;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by ari on 2016/01/02.
 */
public class PlacemarkManager {
    private final String TAG = "PlacemarkManager";

    private Context context;

    private Placemark[] placemarkArray = null;

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public Placemark[] getPlacemarkArray() {
        return placemarkArray;
    }

    public PlacemarkManager(Context context) throws FileNotFoundException {
        this.context = context.getApplicationContext();

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File map_data = new File(path, Constants.KML_FILENAME);
        Log.d(TAG, map_data.toString());
        ArrayList<Placemark> placemarks = getPlacemarks(new FileInputStream(map_data));
        placemarkArray = new Placemark[placemarks.size()];

        placemarkArray = placemarks.toArray(placemarkArray);
    }

    private ArrayList<Placemark> getPlacemarks(InputStream kmlInputStream) {
        ArrayList<Placemark> placemarks = null;

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            PlacemarkSAXHandler saxHandler = new PlacemarkSAXHandler();

            xmlReader.setContentHandler(saxHandler);
            xmlReader.parse(new InputSource(kmlInputStream));

            placemarks = saxHandler.getParsedData();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return placemarks;
    }

    public void updateWithNewLocation(Location location) {
        for (Placemark place : placemarkArray) {
            float[] results = new float[1]; // length 1 to ignore bearing
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), place.getLatitude(), place.getLongitude(), results);
            place.setDistance(results[0]);
            Log.d(TAG, "LatLong: " + place.getLatitude() + ", " + place.getLongitude());
            Log.d(TAG, "Distance: " + results[0]);
        }
    }
}
