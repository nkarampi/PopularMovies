package com.example.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.os.Trace;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * This is the Detail activity which is prompted when a user clickes on a movie poster.
 * It shows the main info as the poster, title, rating etc.
 * Will have trailers,ratings on a future update.
 */
public class DetailActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadData();
    }

    public void loadData(){
        textView1 = (TextView) findViewById(R.id.t1);
        textView2 = (TextView) findViewById(R.id.t2);
        textView3 = (TextView) findViewById(R.id.t3);
        textView4 = (TextView) findViewById(R.id.t4);
        imageView = (ImageView) findViewById(R.id.i1);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("movie")) {
                Movie movie= (Movie) intent.getExtras().getParcelable("movie");
                textView1.setText(movie.getTitle());
                textView2.setText(movie.getSynopsis());
                textView3.setText(String.valueOf(movie.getRating())+" /10");
                textView4.setText(movie.getRelease());

                Picasso.with(this).load("http://image.tmdb.org/t/p/w185/"+ movie.getImg()).resize(150,150).into(imageView);

            }
        }
    }

}
