package com.example.ari.nearby;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by ari on 2016/01/02.
 */
public class PlacemarkManager {
    private final String TAG = "PlacemarkManager";

    private Placemark[] placemarkArray = null;

    public Placemark[] getPlacemarkArray() {
        return placemarkArray;
    }

    public PlacemarkManager(Context context) {
        ArrayList<Placemark> placemarks = getPlacemarks(R.raw.london, context);
        placemarkArray = new Placemark[placemarks.size()];

        placemarkArray = placemarks.toArray(placemarkArray);
    }

    public PlacemarkManager(Context context, File inputFile) {
        ArrayList<Placemark> placemarks = getPlacemarks(inputFile, context);
        placemarkArray = new Placemark[placemarks.size()];

        placemarkArray = placemarks.toArray(placemarkArray);
    }

    private ArrayList<Placemark> getPlacemarks(File inputFile, Context context) {
        ArrayList<Placemark> placemarks = new ArrayList<>();

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            PlacemarkSAXHandler saxHandler = new PlacemarkSAXHandler();

            xmlReader.setContentHandler(saxHandler);
            xmlReader.parse(new InputSource(new FileInputStream(inputFile)));

            placemarks = saxHandler.getParsedData();
            Log.d(TAG, "Size:"+placemarks.size());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            return placemarks;
        }
    }

    private ArrayList<Placemark> getPlacemarks(@android.support.annotation.RawRes int kmlRes, Context context) {
        ArrayList<Placemark> placemarks = null;

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            PlacemarkSAXHandler saxHandler = new PlacemarkSAXHandler();

            xmlReader.setContentHandler(saxHandler);
            xmlReader.parse(new InputSource(context.getResources().openRawResource(kmlRes)));

            placemarks = saxHandler.getParsedData();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            return placemarks;
        }
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
