package com.example.ari.nearby;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.ari.nearby.Utils.calcDistance;

/**
 * CursorWrapper to make a cursor look like it's items are sorted by the distance from the current location
 */
class DistanceCursorWrapper extends CursorWrapper {

    private int mPos = -1;
    private Cursor mCursor;
    private ArrayList<Pair<Integer, Double>> mSortedPlacesByDistance;

    @Override
    public boolean move(int offset) {
        return super.move(offset);
    }

    @Override
    public boolean moveToPosition(int position) {
        final int count = getCount();
        if (position >= count) {
            mPos = count;
            return false;
        }

        // Make sure position isn't before the beginning of the cursor
        if (position < 0) {
            mPos = -1;
            return false;
        }

        mPos = position;
        super.moveToPosition(mSortedPlacesByDistance.get(position).first);
        return true;
    }

    @Override
    public boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }

    @Override
    public int getPosition() {
        return mPos;
    }

    @Override
    public boolean isFirst() {
        return mPos == 0 && getCount() != 0;
    }

    @Override
    public boolean isLast() {
        return mPos == getCount() - 1 && getCount() != 0;
    }

    @Override
    public boolean isBeforeFirst() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == -1;
    }

    @Override
    public boolean isAfterLast() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == getCount();
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }

    @Override
    public boolean moveToFirst() {
        return super.moveToFirst();
    }

    public DistanceCursorWrapper(Cursor cursor, Location location) {
        super(cursor);

        mCursor = cursor;
        mSortedPlacesByDistance = new ArrayList<>(cursor.getCount());

        if (cursor.getCount() == 0) {
            return;
        }

        // Make a mapping of cursor position -> distance from current location
        cursor.moveToFirst();
        do {
            Placemark place = Placemark.fromCursor(cursor);
            mSortedPlacesByDistance.add(new Pair<>(cursor.getPosition(),
                    (double) calcDistance(location.getLatitude(),
                            location.getLongitude(),
                            place.getLatitude(),
                            place.getLongitude())));
        } while (cursor.moveToNext());

        // sort the mapping by distance
        Collections.sort(mSortedPlacesByDistance, new Comparator<Pair<Integer, Double>>() {
            @Override
            public int compare(Pair<Integer, Double> p1, Pair<Integer, Double> p2) {
                if (p1.second < p2.second) {
                    return -1;
                } else if (p1.second > p2.second) {
                    return  1;
                } else {
                    return 0;
                }
            }
        });
    }
}