package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.details.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 * Created by Nikos on 6/3/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Context mContext;
    private LinkedList moviesList;

    public MovieAdapter(Context c,LinkedList<Movie> list) {
        mContext = c;
        moviesList= list;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;
        public final View mView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.poster);
            mView = itemView;
        }

    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    public Movie getMoviePosition(int position){
        return (Movie) moviesList.get(position);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, final int position) {
        Movie movie= (Movie) moviesList.get(position);
        Picasso.with(holder.imageView.getContext()).load("http://image.tmdb.org/t/p/w185/"+ movie.getImg()).
                into(holder.imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = getMoviePosition(position);
                Intent intent = new Intent(mContext,DetailActivity.class);
                intent.putExtra("movie", (Parcelable) movie);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


}
