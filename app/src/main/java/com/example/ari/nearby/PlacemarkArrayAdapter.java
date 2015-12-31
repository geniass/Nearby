package com.example.ari.nearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Comparator;

/**
 * Created by ari on 2015/12/31.
 */
public class PlacemarkArrayAdapter extends ArrayAdapter<Placemark> {
    private final Context context;
    private final Placemark[] objects;

    public static Comparator<Placemark> COMPARATOR = new Comparator<Placemark>() {
        @Override
        public int compare(Placemark lhs, Placemark rhs) {
            if (lhs.getDistance() < rhs.getDistance()) return -1;
            if (lhs.getDistance() > rhs.getDistance()) return 1;
            else return 0;
        }
    };

    public PlacemarkArrayAdapter(Context context, Placemark[] objects) {
        super(context, R.layout.placemark_list_item, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.placemark_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text1);
        textView.setText(objects[position].getTitle());

        TextView distanceTextView = (TextView) rowView.findViewById(R.id.text2);
        distanceTextView.setText(objects[position].getDistance() / 1000.f + " km");
        distanceTextView.setText(String.format("%1$.2f km", objects[position].getDistance() / 1000.));

        return rowView;
    }
}
