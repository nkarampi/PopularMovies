package com.example.android.popularmovies;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.LinkedList;

/**
 * This is the Main Activity for the Popular Movies App.
 * In this class we create the activity_main view initialize the Loader and the RecyclerView with
 * the MovieAdapter.
 * We get all the info from the MovieDB API and we store them in a List as a Movie class.
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<LinkedList<Movie>>{

    private static final int LOADER_ID = 477;

    private static final String SORT_BY = "sort-by";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private boolean sortBy;

    private TextView errorTextView;

    private SwipeRefreshLayout swipeRefreshLayout;

    LinkedList<Movie> movieList= new LinkedList<Movie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorTextView = (TextView) findViewById(R.id.tv_error);
        errorTextView.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(new MovieAdapter(this,movieList));

        sortBy=true;

        getSupportLoaderManager().initLoader(LOADER_ID,null,MainActivity.this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        if (savedInstanceState!=null){
            boolean sorting =savedInstanceState.getBoolean(SORT_BY);

            sortBy=sorting;
        }

    }

    /**
     * We override this method to implement the Swipe to refresh for our app.
     * When we refresh we clear our list and our recycler and restart the
     * loader so we fetch new data.
     */
    @Override
    public void onRefresh(){
        swipeRefreshLayout.setRefreshing(true);
        movieList.clear();
        recyclerView.invalidate();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * We use a Loader instead of an AsyncTaskLoader  to fetch the JSON data
     *
     */

    @Override
    public Loader<LinkedList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<LinkedList<Movie>>(this) {

            LinkedList<Movie> aMovieList=null;

            @Override
            protected void onStartLoading() {
                if (aMovieList!=null){
                    deliverResult(aMovieList);
                }
                else{
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(LinkedList<Movie> data) {
                aMovieList=data;
                super.deliverResult(data);
            }

            @Override
            public LinkedList<Movie> loadInBackground() {
                URL mdbUrl=NetworkUtils.buildUrl(sortBy);
                try {
                    String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbUrl);
                    JSONObject JSONString = new JSONObject(httpResponse);

                    JSONArray moviesArray = JSONString.getJSONArray("results");

                    for (int i=0;i<moviesArray.length();i++){
                        JSONObject movie = moviesArray.getJSONObject(i);
                        int id = movie.getInt("id");
                        String moviePath = movie.getString("poster_path");
                        String title = movie.getString("original_title");
                        String synopsis= movie.getString("overview");
                        double rating= movie.getDouble("vote_average");
                        String date = movie.getString("release_date");
                        Movie newMovie = new Movie(id,title,moviePath,synopsis,rating,date);
                        movieList.add(newMovie);
                    }

                    return movieList;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<LinkedList<Movie>> loader, LinkedList<Movie> data) {
        if (data != null) {
            errorTextView.setVisibility(View.INVISIBLE);
            movieAdapter = new MovieAdapter(MainActivity.this,data);
            recyclerView.setAdapter(movieAdapter);
        }
        else
        {
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<LinkedList<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_acitvity_menu, menu);
        return true;
    }

    /**
     *
     * We have a boolean sortBy to save the user's preference for sorting by popularity or
     * rating. In this menu he get to choose. Depending on his choice the loader restarts with the
     * recycler view and shows the new results.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSortRating) {
            sortBy=false;
            movieList.clear();
            recyclerView.invalidate();
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
            return true;
        }

        if (id == R.id.menuSortPop) {
            sortBy=true;
            movieList.clear();
            recyclerView.invalidate();
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * OnSaveInstance implemented to save the user's sorting choice
     * when the activity gets destroyed (device rotation)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean sorting = sortBy;

        outState.putBoolean(SORT_BY,sorting);
    }

}
