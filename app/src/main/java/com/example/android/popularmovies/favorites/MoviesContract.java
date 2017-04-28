package com.example.android.popularmovies.favorites;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class represents the contract between the DB and the DBHelper.
 * We have all the strings for all the columns of our db, which are needed to store a movie as
 * favorite locally.
 * MoviesEntry implementing BaseColumns to include the _ID column automatically.
 *
 * All the local CRUD operations are done via Content Provider.
 */

public class MoviesContract {

    public static final String AUTHORITY= "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_IMG = "img_path";


    }
}
