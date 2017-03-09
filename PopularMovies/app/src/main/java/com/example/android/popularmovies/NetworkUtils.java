package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Nikos on 5/3/2017.
 */

public class NetworkUtils {

   // private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
   //You need to get an API KEY from the movie db
    private static final String API_KEY = "";

    public NetworkUtils(){}

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param sortByPop The sorting user chooses that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(Boolean sortByPop) {
        URL url=null;
        String urlString = null;
        try {
           if (sortByPop)
               urlString = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
           else
               urlString = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
            url= new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Method from lesson's.
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
