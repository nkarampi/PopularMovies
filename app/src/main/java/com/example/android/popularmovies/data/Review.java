package com.example.android.popularmovies.data;

/**
 * Created by Nikos on 11/4/2017.
 */

public class Review {
    private String reviewID;
    private String author;
    private String content;

    public Review(String newID, String newAuthor, String newCont) {
        reviewID=newID;
        author=newAuthor;
        content=newCont;
    }

    public String getReviewID() {
        return reviewID;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
