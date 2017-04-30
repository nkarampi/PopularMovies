package com.example.android.popularmovies;


import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.details.DetailActivity;
import com.example.android.popularmovies.favorites.MoviesContract;

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

    /*
        These two Strings are being used on our saveInstance so when we go back or rotate
        we don't lose the sorting selection the user has made.
     */
    private static final String SORT_BY = "sort-by";
    private static final String IS_FAVORITE = "favorite";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;


    private boolean sortBy; //This boolean determines how we will sort the results
    private boolean isFavorite; //This boolean determines if we will show the local-favorite movies

    private int isFavorite_cursorID; //This int stores the _ID column of our table so we can store it in our Movie class

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
        isFavorite=false;

        getSupportLoaderManager().initLoader(LOADER_ID,null,MainActivity.this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        if (savedInstanceState!=null){
            boolean sorting =savedInstanceState.getBoolean(SORT_BY);
            boolean favoriting = savedInstanceState.getBoolean(IS_FAVORITE);

            isFavorite = favoriting;
            sortBy=sorting;
        }
    }

    /**
     * We use onRestart so when we favorite/unfavorite a movie the recycler view gets updated.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        movieList.clear();
        recyclerView.invalidate();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    /**
     * We override this method to implement the Swipe to refresh for our app.
     * When we refresh we clear our list and our recycler and restart the
     * loader so we fetch new data.
     */
    @Override
    public void onRefresh(){
        if (recyclerView==null) {}
        else {
            swipeRefreshLayout.setRefreshing(true);
            movieList.clear();
            //recyclerView.invalidate();
            recyclerView.getRecycledViewPool().clear();
            movieAdapter.notifyDataSetChanged();
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
            swipeRefreshLayout.setRefreshing(false);
        }
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

            /**
             * The isFavorite boolean takes action here. When it's true we add in our list all the
             * data from our favorite movies DB. We take every column and create the new Movies and
             * then we add them on the list and on recycler view. If it's false we fetch new data
             * depending on our sortBy boolean for popularity/rating sorting.
             */
            @Override
            public LinkedList<Movie> loadInBackground() {
                if (isFavorite){
                    Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    if (cursor .moveToFirst()) {
                        while (cursor.isAfterLast() == false) {

                            int id_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
                            int idCursorIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry._ID);
                            int img_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMG);
                            int title_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
                            int synopsis_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS);
                            int rating_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING);
                            int release_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE);

                            int id = cursor.getInt(id_index);
                            final int cursor_id = cursor.getInt(idCursorIndex);
                            String moviePath = cursor.getString(img_index);
                            String title = cursor.getString(title_index);
                            String synopsis= cursor.getString(synopsis_index);
                            double rating= Double.parseDouble(cursor.getString(rating_index));
                            String date = cursor.getString(release_index);
                            Movie newMovie = new Movie(id,cursor_id,title,moviePath,synopsis,rating,date,true);
                            movieList.add(newMovie);

                            cursor.moveToNext();
                        }
                    }
                    return movieList;
                }
                else {
                    URL mdbUrl = NetworkUtils.buildUrl(sortBy);
                    try {
                        String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbUrl);
                        JSONObject JSONString = new JSONObject(httpResponse);

                        JSONArray moviesArray = JSONString.getJSONArray("results");

                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject movie = moviesArray.getJSONObject(i);
                            int id = movie.getInt("id");
                            String moviePath = movie.getString("poster_path");
                            String title = movie.getString("original_title");
                            String synopsis = movie.getString("overview");
                            double rating = movie.getDouble("vote_average");
                            String date = movie.getString("release_date");


                            boolean favoriteMovie;

                            favoriteMovie=isMovieFavorite(id); //For isMovieFavorite see commenting below

                            if (!favoriteMovie){
                                isFavorite_cursorID=-1; //if the movie isn't favorite we use -1 as the cursor id
                            }

                            Movie newMovie = new Movie(id, isFavorite_cursorID, title, moviePath, synopsis, rating, date, favoriteMovie);
                            movieList.add(newMovie);
                        }

                        return movieList;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        };
    }

    /**
     * We use this method to determine when we fetch new data if we have them in our db.
     * If we have them it means that the user has marked it as favorite.
     * @param id is the movie_id from the TMDB API.
     * @return a boolean which determines if the movie is favorite or not.
     */
    private boolean isMovieFavorite(int id) {
        boolean fav;
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                int id_index = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
                int dbMovie_id = cursor.getInt(id_index);
                if (id == dbMovie_id) {
                    fav = true;
                    int idCursorIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry._ID);
                    final int cursor_id = cursor.getInt(idCursorIndex);
                    isFavorite_cursorID=cursor_id; //Here we store the _ID of the movie so we can unfavorite the movie and not re-add it.
                    return fav;
                }
                cursor.moveToNext();
            }
        }
        return false;
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
        inflater.inflate(R.menu.main_activity_menu, menu);
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
            isFavorite=false;
            movieList.clear();
            recyclerView.invalidate();
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
            return true;
        }

        if (id == R.id.menuSortPop) {
            sortBy=true;
            isFavorite=false;
            movieList.clear();
            recyclerView.invalidate();
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
            return true;
        }

        if(id==R.id.favorite){
            isFavorite=true;
            movieList.clear();
            recyclerView.invalidate();
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
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
        boolean favoriting = isFavorite;

        outState.putBoolean(SORT_BY,sorting);
        outState.putBoolean(IS_FAVORITE,favoriting);
    }

}
