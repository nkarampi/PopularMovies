package com.example.android.popularmovies.data;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Nikos on 11/4/2017.
 */

public class MovieInfo {

    private ArrayList<String> keys;
    private ArrayList<Review> reviews;

    public MovieInfo(ArrayList<String> newKeys,ArrayList<Review> newReviews){
        keys =newKeys;
        reviews=newReviews;
    }

    public ArrayList<String> getKeys() {
        //for (int i = 0 ; i< keys.size();i++)
       // Log.d("myTag", keys.get(i));
        return keys;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
