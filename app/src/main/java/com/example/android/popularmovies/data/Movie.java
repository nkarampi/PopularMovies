package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the Movie class which saves all the data we take from The MovieDB API.
 * It's implementing Parceable because we need to parse it as an extra to an intent
 * when the user chooses to view details for movies.
 * Also we have all the getters for our variables
 */

public class Movie implements Parcelable{
    private int id;
    private int cursor_id;
    private String title;
    private String img;
    private String synopsis;
    private double rating;
    private String release;
    private boolean isFavorite;

    public Movie(int aID, int cId, String aTitle,String aImg, String aSyn, double aRat, String aDate, boolean aFav){
        id=aID;
        cursor_id=cId;
        title=aTitle;
        img=aImg;
        synopsis=aSyn;
        rating=aRat;
        release=aDate;
        isFavorite=aFav;
    }

    private Movie(Parcel in) {
        id= in.readInt();
        cursor_id = in.readInt();
        title= in.readString();
        img= in.readString();
        synopsis= in.readString();
        rating= in.readDouble();
        release= in.readString();
        isFavorite = in.readByte() !=0;
    }

    public int getId() {
        return id;
    }

    public int getCursorId() {
        return cursor_id;
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

    public boolean getFav() {return isFavorite;}

    public void setFavorite(boolean aFav) {isFavorite=aFav;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(cursor_id);
        parcel.writeString(title);
        parcel.writeString(img);
        parcel.writeString(synopsis);
        parcel.writeDouble(rating);
        parcel.writeString(release);
        parcel.writeByte((byte) (isFavorite ? 1:0));
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
