package com.example.android.popularmovies.details;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieInfo;
import com.example.android.popularmovies.data.Review;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * This is the Detail activity which is prompted when a user clickes on a movie poster.
 * It shows the main info as the poster, title, rating etc.
 * Will have trailers,ratings on a future update.
 *
 * https://github.com/PaoloRotolo/ExpandableHeightListView
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieInfo>{

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private ImageView imageView;

    private ExpandableHeightListView expandableListView ;
    private MovieInfoAdapter movieInfoTrailersAdapter;

    private ExpandableHeightListView expandableListViewforReviews ;
    private MovieReviewsAdapter movieReviewsAdapter;

    private static final int LOADER_ID = 266;
    //private ArrayList<String> trailerKeys;
    private int movieID;
    private MovieInfo movieInfo;

    private boolean noTrailers=false;
    private boolean noReviews=false;

    private boolean isExpanded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        loadData();
    }



    @Override
    public Loader<MovieInfo> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieInfo>(this) {

            //ArrayList<String> trailers=null;
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
                            // id=author=content="No reviews yet"; //USE A TEXT VIEW
                            noTrailers=true;
                        }
                        for (int i=0;i<moviesArray.length();i++){
                            JSONObject movie = moviesArray.getJSONObject(i);
                            String key = movie.getString("key");
                            String type = movie.getString("type");
                            if (type.equals("Trailer"))
                                trailerKeys.add(key);
                        }
                       // return trailerKeys;
                     }
                    catch (Exception e){
                      e.printStackTrace();
                     return null;
                   }

                URL mdbReviewUrl = NetworkUtils.buildReviewUrl(movieID);
                try{
                    String httpResponse = NetworkUtils.getResponseFromHttpUrl(mdbReviewUrl);
                    JSONObject JSONString = new JSONObject(httpResponse);

                    //check total results
                    //JSONObject total = JSONString.getJSONObject("total_results");
                    //String totalResults = total.getString("total_results");
                    //if totalResults

                    JSONArray moviesArray = JSONString.getJSONArray("results");
                    String id=null,content=null,author=null;
                    if (moviesArray.length()<=1){
                       // id=author=content="No reviews yet"; //USE A TEXT VIEW
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

                    // return trailerKeys;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

                movieInfo = new MovieInfo(trailerKeys,reviews);
                return movieInfo;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieInfo> loader, MovieInfo data) {
        ArrayList<Review> dataReviews = data.getReviews();
        ArrayList<String> dataKeys = data.getKeys();

        if (dataReviews != null && dataKeys != null) {
      //      if ( data.getKeys() != null) {
            //errorTextView.setVisibility(View.INVISIBLE); use a text for fetching trailers
          //  movieAdapter = new MovieAdapter(MainActivity.this,data);
           // recyclerView.setAdapter(movieAdapter);
            //for (int i = 0 ; i<data.getKeys().size();i++) {
             //   yt.append(data.getKeys().get(i) + "\n \n");
           // }
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

       // yt = (TextView) findViewById(R.id.yt);
        expandableListView = (ExpandableHeightListView) findViewById(R.id.listViewYt) ;
        expandableListViewforReviews = (ExpandableHeightListView) findViewById(R.id.listViewRv);

       // trailerKeys = new ArrayList<String>();

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("movie")) {
                Movie movie= (Movie) intent.getExtras().getParcelable("movie");
                movieID = movie.getId();
                textView1.setText(movie.getTitle());
                textView2.setText(movie.getSynopsis());
                textView3.setText(String.valueOf(movie.getRating())+" /10");
                textView4.setText(movie.getRelease());

               // yt.setText(Integer.toString(movieID));


                Picasso.with(this).load("http://image.tmdb.org/t/p/w185/"+ movie.getImg()).resize(150,150).into(imageView);

                getSupportLoaderManager().initLoader(LOADER_ID,null,DetailActivity.this);

            }
        }
    }

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
}
