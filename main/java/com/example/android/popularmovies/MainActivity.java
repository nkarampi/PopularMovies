package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private MovieAdapter movieAdapter;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display= getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        width=size.x/3;

        gridView = (GridView) findViewById(R.id.grid);
        LinkedList<Movie> list= new LinkedList<Movie>();
        gridView.setAdapter(new MovieAdapter(this,list,width));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });


        new FetchMovies().execute(true); //true is sort by popularity

    }


    /**
     * For Parametres we will give a Boolean for sorting
     * if it's 0 we will sort to most popular
     * if it's not 0 we will sort to highest-rated
     */
    public class FetchMovies extends AsyncTask<Boolean,Void, LinkedList<Movie>>
    {
        @Override
        protected LinkedList<Movie> doInBackground(Boolean... params) {
            Boolean sortByPop=params[0];
            URL mdbUrl=NetworkUtils.buildUrl(sortByPop);
            try {
                String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbUrl);
                JSONObject JSONString = new JSONObject(httpResponse);

                JSONArray moviesArray = JSONString.getJSONArray("results");
               // String[] result = new String[moviesArray.length()];

                LinkedList<Movie> movieList= new LinkedList<Movie>();
                for (int i=0;i<moviesArray.length();i++){
                    JSONObject movie = moviesArray.getJSONObject(i);
                    String moviePath = movie.getString("poster_path");
                    String title = movie.getString("original_title");
                    String synopsis= movie.getString("overview");
                    Integer rating= movie.getInt("vote_average");
                    String date = movie.getString("release_date");
                    Movie newMovie = new Movie(i,title,moviePath,synopsis,rating,date);
                    movieList.add(newMovie);
                }
                return movieList;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LinkedList<Movie> data) {
            if (data != null) {
                movieAdapter = new MovieAdapter(MainActivity.this,data,width);
                gridView.setAdapter(movieAdapter);
            }
        }


    }

}
