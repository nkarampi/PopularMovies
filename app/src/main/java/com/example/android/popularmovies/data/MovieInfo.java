package com.example.android.popularmovies.data;

import android.util.Log;

import java.util.ArrayList;

/**
 * This Class has two lists.
 * One is for the Reviews and one for our Trailer Keys. We have this class so in our Detail Activity
 * we can fetch them and with the suitable adapters to create them in our layout.
 */

public class MovieInfo {

    private ArrayList<String> keys;
    private ArrayList<Review> reviews;

    public MovieInfo(ArrayList<String> newKeys,ArrayList<Review> newReviews){
        keys =newKeys;
        reviews=newReviews;
    }

    public ArrayList<String> getKeys() {
        return keys;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
