package com.example.android.popularmovies.details;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.StringDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieInfo;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.favorites.MoviesContract;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * This is the Detail activity which is prompted when a user clickes on a movie poster.
 * It shows the main info as the poster, title, rating etc.
 *
 * At this stage we also fetch the trailers and the reviews for each movie.
 *
 * Because we didn't want to have a scrollable list view and to expand all it's data in our layout
 * we use the below dependency found from github to replace the basic listView.
 * https://github.com/PaoloRotolo/ExpandableHeightListView
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieInfo>{

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private ImageView imageView;

    private TextView tvReviewsError;
    private TextView tvTrailersError;

    private Button favButton;

    private int movieID;
    private int cursorID;
    private String title;
    private String img;
    private String synopsis;
    private double rating;
    private String release;
    private boolean isFavorite;

    /*
        These are the adapters and list views for Trailers and Reviews.
     */
    private ExpandableHeightListView expandableListView ;
    private MovieInfoAdapter movieInfoTrailersAdapter;

    private ExpandableHeightListView expandableListViewforReviews ;
    private MovieReviewsAdapter movieReviewsAdapter;

    private static final int LOADER_ID = 266;

    private MovieInfo movieInfo;

    private boolean noTrailers=false; //This boolean determines if we have trailers for the movie or not.
    private boolean noReviews=false;  //This boolean determines if we have reviews for the movie or not.

    private boolean isExpanded=false;   //This boolean determines if the list is expanded or not.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        loadData();
    }


    /**
     * With this loader we fetch the trailers and reviews for each movie.
     * We store them in a MovieInfo object which can store all the info we want
     * in ArrayLists.
     * We also have text views which become visible if there aren't any reviews or trailers.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<MovieInfo> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieInfo>(this) {

            MovieInfo aMovieInfo = null;

            @Override
            protected void onStartLoading() {
                if (aMovieInfo!=null){
                    deliverResult(aMovieInfo);
                }
                else{
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(MovieInfo data) {
                aMovieInfo=data;
                super.deliverResult(data);
            }

            @Override
            public MovieInfo loadInBackground() {
                    ArrayList<String> trailerKeys=new ArrayList<String>();
                    ArrayList<Review> reviews=new ArrayList<Review>();

                    URL mdbTrailerUrl = NetworkUtils.buildTrailerUrl(movieID);
                    try{
                        String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbTrailerUrl);
                        JSONObject JSONString = new JSONObject(httpResponse);

                        JSONArray moviesArray = JSONString.getJSONArray("results");
                        if (moviesArray.length()<=1){
                            noTrailers=true;
                        }
                        for (int i=0;i<moviesArray.length();i++){
                            JSONObject movie = moviesArray.getJSONObject(i);
                            String key = movie.getString("key");
                            String type = movie.getString("type");
                            if (type.equals("Trailer"))
                                trailerKeys.add(key);
                        }
                     }
                    catch (Exception e){
                      e.printStackTrace();
                    // return null;
                   }

                URL mdbReviewUrl = NetworkUtils.buildReviewUrl(movieID);
                try{
                    String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbReviewUrl);
                    JSONObject JSONString = new JSONObject(httpResponse);

                    JSONArray moviesArray = JSONString.getJSONArray("results");
                    String id=null,content=null,author=null;
                    if (moviesArray.length()<=1){
                        noReviews=true;
                    }
                    else{
                        for (int i=0;i<moviesArray.length();i++){
                            JSONObject movie = moviesArray.getJSONObject(i);

                            id = movie.getString("id");
                            author = movie.getString("author");
                            content = movie.getString("content");
                            Review review = new Review(id,author,content);
                            reviews.add(review);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    //return null;
                }

                movieInfo = new MovieInfo(trailerKeys,reviews);
                return movieInfo;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieInfo> loader, MovieInfo data) {
        if (noReviews)  tvReviewsError.setVisibility(View.VISIBLE);
        if (noTrailers) tvTrailersError.setVisibility(View.VISIBLE);
        ArrayList<Review> dataReviews=new ArrayList<Review>();
        ArrayList<String> dataKeys=new ArrayList<String>();
        try {
            dataReviews = data.getReviews();
            dataKeys = data.getKeys();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (dataReviews != null && dataKeys != null) {
            if (noTrailers==false) {
                movieInfoTrailersAdapter = new MovieInfoAdapter(dataKeys, this);

                expandableListView.setAdapter(movieInfoTrailersAdapter);
                expandableListView.setExpanded(true);
            }

            if (noReviews==false){
                movieReviewsAdapter = new MovieReviewsAdapter(dataReviews,this);

                expandableListViewforReviews.setAdapter(movieReviewsAdapter);
                expandableListViewforReviews.setExpanded(true);
            }

        }
        else
        {
           // errorTextView.setVisibility(View.VISIBLE);
          //  yt.setText("error");
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieInfo> loader) {

    }

    public void loadData(){
        textView1 = (TextView) findViewById(R.id.t1);
        textView2 = (TextView) findViewById(R.id.t2);
        textView3 = (TextView) findViewById(R.id.t3);
        textView4 = (TextView) findViewById(R.id.t4);
        imageView = (ImageView) findViewById(R.id.i1);

        tvReviewsError = (TextView) findViewById(R.id.reviews_error);
        tvTrailersError = (TextView) findViewById(R.id.trailers_error);

        favButton = (Button) findViewById(R.id.buttonFavorites);

        expandableListView = (ExpandableHeightListView) findViewById(R.id.listViewYt) ;
        expandableListViewforReviews = (ExpandableHeightListView) findViewById(R.id.listViewRv);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("movie")) {
                Movie movie= (Movie) intent.getExtras().getParcelable("movie");
                movieID = movie.getId();
                cursorID = movie.getCursorId();
                title = movie.getTitle();
                synopsis = movie.getSynopsis();
                rating = movie.getRating();
                release = movie.getRelease();
                img = movie.getImg();
                isFavorite = movie.getFav();

                textView1.setText(title);
                textView2.setText(synopsis);
                textView3.setText(String.valueOf(rating)+" /10");
                textView4.setText(release);

                Picasso.with(this).load("http://image.tmdb.org/t/p/w185/"+ movie.getImg()).resize(150,150).into(imageView);

            if (!isFavorite){
                favButton.setText(getString(R.string.add_fav));
                favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_yellow_24dp,0,0,0);
            }
            else {
                favButton.setText(getString(R.string.del_fav));
                favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_24dp,0,0,0);
            }

                getSupportLoaderManager().initLoader(LOADER_ID,null,DetailActivity.this);

            }
        }
    }

    /**
     * This method is being used when we click on Reviews so we will expand the list of reviews
     * or "close" it.
     * @param view
     */
    public void expandColapseReviews(View view) {
        if (isExpanded){
            expandableListViewforReviews.setVisibility(View.GONE);
            isExpanded=false;
        }
        else {
            expandableListViewforReviews.setVisibility(View.VISIBLE);
            isExpanded=true;
        }
    }

    /**
     * This method is used from the favorite button so we can favorite or unfavorite a movie.
     * We have the cursorID variable so we can know if we have the movie on our db.
     * We insert and delete via content providers depending if the movie is already marked as favorite
     * by the user or not.
     * @param view
     */
    public void makeFavorite(View view) {
        if (isFavorite){
            if (cursorID>-1){
                String stringId = Integer.toString(cursorID);
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                if (uri != null) {
                    //Toast.makeText(getBaseContext(), "Delete From Favorites", Toast.LENGTH_LONG).show();
                    favButton.setText(getString(R.string.add_fav));
                    favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_yellow_24dp,0,0,0);
                    isFavorite=false;
                }
            }
        }
        else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMG, img);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieID);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, rating);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE, release);
                contentValues.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, synopsis);

                Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

                String id = uri.getPathSegments().get(1);
                cursorID = Integer.parseInt(id);

                if (uri != null) {
                    //Toast.makeText(getBaseContext(), "Added to Favorites", Toast.LENGTH_LONG).show();
                    favButton.setText(getString(R.string.del_fav));
                    favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black_24dp,0,0,0);

                    isFavorite = true;
                }

        }
    }

}
