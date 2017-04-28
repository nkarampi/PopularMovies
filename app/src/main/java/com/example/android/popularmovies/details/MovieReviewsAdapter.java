package com.example.android.popularmovies.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;

import java.util.ArrayList;

/**
 * This class represents the Adapter for our Reviews.
 * We take the list with our reviews and create a list with all the information
 * we need to provide to the user.
 */

public class MovieReviewsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Review> reviews= new ArrayList<Review>();
    private Context context;

    public MovieReviewsAdapter(ArrayList<Review> list,Context cont){
        reviews=list;
        context=cont;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_reviews_item, null);
        }
        TextView tv_author = (TextView)view.findViewById(R.id.tvAuthor);
        tv_author.setText(reviews.get(position).getAuthor());

        TextView tv_cont = (TextView)view.findViewById(R.id.tvContent);
        tv_cont.setText(reviews.get(position).getContent());

        return view;
    }
}
