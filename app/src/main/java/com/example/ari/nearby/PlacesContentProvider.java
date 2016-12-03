package com.example.ari.nearby;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.util.Log;

public class PlacesContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.example.ari.nearby.PlacesContentProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/places";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static final int MATCH_PLACES = 1;
    private static final int MATCH_PLACE_ID = 2;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "places", MATCH_PLACES);
        uriMatcher.addURI(PROVIDER_NAME, "places/#", MATCH_PLACE_ID);
    }

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private SQLiteDatabase db;
    private static final String DB_NAME = "places";
    private static final String PLACES_TABLE_NAME = "places";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE_TABLE = "CREATE TABLE " + PLACES_TABLE_NAME +
                                                "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                TITLE + " TEXT NOT NULL, " +
                                                DESCRIPTION + " TEXT, " +
                                                LATITUDE + " REAL NOT NULL, " +
                                                LONGITUDE + " REAL NOT NULL" +
                                                ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // TODO: Don't just delete all data
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public PlacesContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCH_PLACES:
                return "vnd.android.cursor.dir/vnd.example.ari/nearby.places";

            case MATCH_PLACE_ID:
                return "vnd.android.cursor.item/vnd.example.ari/nearby.places";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: check for conflicts/existing entries
        long rowId = db.insert(PLACES_TABLE_NAME, "", values);

        if (rowId > -1) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Unable to insert into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        db = databaseHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PLACES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MATCH_PLACES:
                break;

            case MATCH_PLACE_ID:
                qb.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
                break;

            default:
                break;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
