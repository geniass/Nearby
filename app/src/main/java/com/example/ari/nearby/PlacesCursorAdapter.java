package com.example.ari.nearby;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static com.example.ari.nearby.Utils.calcDistance;

/**
 * Created by ari on 2016/12/05.
 */

public class PlacesCursorAdapter extends CursorRecyclerViewAdapter<PlacesCursorAdapter.ViewHolder> {

    private Location location;

    public void setCurrentLocation(Location location) {
        this.location = location;
    }

    public PlacesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.placemark_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Placemark place = Placemark.fromCursor(cursor);

        if (location != null) {
            place.setDistance(calcDistance(location.getLatitude(), location.getLongitude(), place.getLatitude(), place.getLongitude()));
        }

        viewHolder.bindPlace(place);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        // only create a CursorWrapper if we have a valid location, otherwise no need to sort
        if (location != null) {
            return super.swapCursor(new DistanceCursorWrapper(newCursor, location));
        } else {
            return super.swapCursor(newCursor);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Placemark place;
        private TextView textView;
        private TextView distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.textView = (TextView) itemView.findViewById(R.id.text1);
            this.distanceTextView = (TextView) itemView.findViewById(R.id.text2);

            itemView.setOnClickListener(this);
        }

        public void bindPlace(Placemark place) {
            this.place = place;
            textView.setText(place.getTitle());
            distanceTextView.setText(String.format("%1$.2f km", place.getDistance() / 1000.));
        }

        @Override
        public void onClick(View view) {
            Log.d("ADAPTER", "ONCLICK");
            Toast.makeText(view.getContext(), "CLICKED", Toast.LENGTH_LONG);
            Uri mapUri = Uri.parse(String.format(Locale.US, view.getContext().getString(R.string.map_uri_format), place.getLatitude(), place.getLongitude(), 15, Uri.encode(place.getTitle())));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(mapIntent);
        }
    }
}
