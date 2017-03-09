package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 * Created by Nikos on 6/3/2017.
 */

public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList moviesList;
    private int width;

    public MovieAdapter(Context c,LinkedList<Movie> list, int w) {
        mContext = c;
        moviesList= list;
        width=w;
    }

    public int getCount() {
        return moviesList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        Movie movie= (Movie) moviesList.get(position);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+ movie.getImg()).resize((int) (width*1.5), (int) (width*2)).into(imageView);

        return imageView;
    }

}
