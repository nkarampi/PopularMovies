package com.example.android.popularmovies.data;

/**
 * This class has all the information we need for our Reviews.
 * We fetch them and create new Review objects and then store them in our lists.
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
