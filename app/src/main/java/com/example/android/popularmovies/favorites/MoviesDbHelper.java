package com.example.android.popularmovies.favorites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies.favorites.MoviesContract.*;

/**
 * This class creates the table for our SQLite to store favorite movies locally.
 * We also have the onUpgrade method so we change the table if we change it's structure.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasksDb.db";

    private static final int VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID                + " INTEGER PRIMARY KEY, " +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_IMG + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
