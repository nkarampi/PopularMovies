package com.example.android.popularmovies;

import java.util.Date;

/**
 * Created by Nikos on 5/3/2017.
 */

public class Movie {
    private int id;
    private String title;
    private String img;
    private String synopsis;
    private int rating;
    private String release;

    public Movie(int aID, String aTitle,String aImg, String aSyn, int aRat, String aDate){
        id=aID;
        title=aTitle;
        img=aImg;
        synopsis=aSyn;
        rating=aRat;
        release=aDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getRating() {
        return rating;
    }

    public String getRelease() {
        return release;
    }

}
