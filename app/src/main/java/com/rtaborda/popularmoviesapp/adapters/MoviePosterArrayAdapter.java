package com.rtaborda.popularmoviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.rtaborda.popularmoviesapp.R;
import com.rtaborda.popularmoviesapp.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rui on 22/08/2015.
 */
public class MoviePosterArrayAdapter extends ArrayAdapter<Movie> {
    private final String LOG_TAG = MoviePosterArrayAdapter.class.getSimpleName();

    private Context _context;
    private ArrayList<Movie> _items;

    public MoviePosterArrayAdapter(Context context, int resource, ArrayList<Movie> items) {
        super(context, resource, items);
        _items = items;
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.grid_item_movie, null);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_img);
        String posterURL = _items.get(position).PosterSmallURL;
        Picasso.with(_context).load(posterURL).into(imageView);

        return view;
    }

}
