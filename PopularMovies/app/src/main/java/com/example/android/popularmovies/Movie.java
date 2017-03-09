package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Nikos on 5/3/2017.
 */

public class Movie implements Parcelable{
    private int id;
    private String title;
    private String img;
    private String synopsis;
    private double rating;
    private String release;

    public Movie(int aID, String aTitle,String aImg, String aSyn, double aRat, String aDate){
        id=aID;
        title=aTitle;
        img=aImg;
        synopsis=aSyn;
        rating=aRat;
        release=aDate;
    }

    private Movie(Parcel in) {
        id= in.readInt();
        title= in.readString();
        img= in.readString();
        synopsis= in.readString();
        rating= in.readDouble();
        release= in.readString();
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

    public double getRating() {
        return rating;
    }

    public String getRelease() {
        return release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(img);
        parcel.writeString(synopsis);
        parcel.writeDouble(rating);
        parcel.writeString(release);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };


}
