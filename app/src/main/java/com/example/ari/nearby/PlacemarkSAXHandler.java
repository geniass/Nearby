package com.example.ari.nearby;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by ari on 2015/12/31.
 * https://gist.github.com/RobGThai/3163006
 */
public class PlacemarkSAXHandler extends DefaultHandler {
    private final String TAG = "PlacemarkSAXHandler";
    private ArrayList<Placemark> placemarks = null;
    private Placemark currentPlacemark;
    private boolean in_kmltag = false;
    private boolean in_placemarktag = false;
    private boolean in_nametag = false;
    private boolean in_descriptiontag = false;
    private boolean in_geometrycollectiontag = false;
    private boolean in_linestringtag = false;
    private boolean in_pointtag = false;
    private boolean in_coordinatestag = false;

    public ArrayList<Placemark> getParsedData() {
        return placemarks;
    }

    @Override
    public void startDocument() throws SAXException {
        placemarks = new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    /**
     * Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        if (localName.equals("kml")) {
            this.in_kmltag = true;
        } else if (localName.equals("Placemark")) {
            this.in_placemarktag = true;
//			Log.d(TAG, "startElement localName[" + localName + "] qName[" + qName + "]");
            currentPlacemark = new Placemark();
        } else if (localName.equals("name")) {
            this.in_nametag = true;
        } else if (localName.equals("description")) {
            this.in_descriptiontag = true;
        } else if (localName.equals("GeometryCollection")) {
            this.in_geometrycollectiontag = true;
        } else if (localName.equals("LineString")) {
            this.in_linestringtag = true;
        } else if (localName.equals("point")) {
            this.in_pointtag = true;
        } else if (localName.equals("coordinates")) {
            this.in_coordinatestag = true;
        }
    }

    /**
     * Gets be called on closing tags like:
     * </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (localName.equals("kml")) {
            this.in_kmltag = false;
        } else if (localName.equals("Placemark")) {
            this.in_placemarktag = false;

//			Log.d(TAG, "endElement localName[" + localName + "] qName[" + qName + "] Route[" + navigationDataSet.getCurrentPlacemark().getTitle() + "]");
            placemarks.add(currentPlacemark);
        } else if (localName.equals("name")) {
            this.in_nametag = false;
        } else if (localName.equals("description")) {
            this.in_descriptiontag = false;
        } else if (localName.equals("GeometryCollection")) {
            this.in_geometrycollectiontag = false;
        } else if (localName.equals("LineString")) {
            this.in_linestringtag = false;
        } else if (localName.equals("point")) {
            this.in_pointtag = false;
        } else if (localName.equals("coordinates")) {
            this.in_coordinatestag = false;
        }
    }

    /**
     * Gets be called on the following structure:
     * <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_nametag) {
            if (currentPlacemark == null) currentPlacemark = new Placemark();
            currentPlacemark.setTitle(new String(ch, start, length));
        } else if (this.in_descriptiontag) {
            if (currentPlacemark == null) currentPlacemark = new Placemark();
            currentPlacemark.setDescription(new String(ch, start, length));
        } else if (this.in_coordinatestag) {
            if (currentPlacemark == null) currentPlacemark = new Placemark();
            String[] strings = new String(ch, start, length).trim().split(",");
            if (strings.length < 2) {
                Log.e(TAG, "Invalid KML file");
                return;
            }
            // KML format is Longitude Latitude
            currentPlacemark.setLatitude(Double.parseDouble(strings[1]));
            currentPlacemark.setLongitude(Double.parseDouble(strings[0]));
        }
    }
}
