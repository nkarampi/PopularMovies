package com.example.android.popularmovies.favorites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.favorites.MoviesContract.MoviesEntry;

/**
 * This class is the Content Provider to store the favorite movies locally.
 * It supports all the CRUD operations the way Udacity taught us.
 */

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES +  "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    private MoviesDbHelper mMoviesDbHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMoviesDbHelper = new MoviesDbHelper(context);
        return true;
    }

    /**
     * We use this method to query the db via Content Providers.
     * All the below parameters are given from the class that calls this method to query the cursor
     * on the db.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return a cursor.
     */
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case MOVIES:
                returnCursor = db.query(MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    /**
     * We use this method to insert rows in the db via Content Providers.
     * All the below parameters are given from the class that calls this method to insert the data
     * in the db.
     * @param uri
     * @param values are Content Values which we put at the class we call this method.
     * @return a Uri.
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                }
                else throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    /**
     * We use this method to delete data from the db via Content Providers.
     * All the below parameters are given from the class that calls this method to delete data
     * from the db.
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return an int which stores the number of objects deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int movieDeleted;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);

                movieDeleted = db.delete(MoviesEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (movieDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return movieDeleted;
    }

    /*
        We don't use any of update and getType methods in our code but we implemented the methods.
     */
@Override
public int update(@NonNull Uri uri, ContentValues values, String selection,
                  String[] selectionArgs) {
    int moviesUpdated;
    int match = sUriMatcher.match(uri);

    switch (match) {
        case MOVIES_WITH_ID:
            String id = uri.getPathSegments().get(1);
            moviesUpdated = mMoviesDbHelper.getWritableDatabase().update(MoviesEntry.TABLE_NAME, values, "_id=?", new String[]{id});
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (moviesUpdated != 0) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
    return moviesUpdated;
}

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_MOVIES;
            case MOVIES_WITH_ID:
                return "vnd.android.cursor.item" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
