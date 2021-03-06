package com.example.android.popularmovies.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;

/**
 * This class represents the Adapter for our trailers.
 * We store our keys for the trailers in a list and we create some buttons in a listView
 * so we have all of them in our layout.
 * When a button is clicked we create the link with our keys and we start the video on youtube
 * or browser.
 */

public class MovieInfoAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> trailerKeys= new ArrayList<String>();
    private Context context;

    public MovieInfoAdapter(ArrayList<String> list,Context cont){
        trailerKeys=list;
        context=cont;
    }

    @Override
    public int getCount() {
        return trailerKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return trailerKeys.get(position);
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
            view = inflater.inflate(R.layout.list_trailers_item, null);
        }
        TextView listItemText = (TextView)view.findViewById(R.id.tvTrailers);
        listItemText.setText("Play trailer "+ (position+1));

        ImageButton playBtn = (ImageButton)view.findViewById(R.id.buttonPlay);

        playBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String trailerKey= trailerKeys.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+trailerKey));
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
