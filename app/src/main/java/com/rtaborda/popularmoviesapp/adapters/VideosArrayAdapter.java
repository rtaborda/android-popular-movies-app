package com.rtaborda.popularmoviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rtaborda.popularmoviesapp.entities.Video;

import java.util.ArrayList;

/**
 * Created by Rui on 21/09/2015.
 */
public class VideosArrayAdapter extends ArrayAdapter<Video> {
    private final String LOG_TAG = VideosArrayAdapter.class.getSimpleName();

    private Context _context;
    private int _resource;
    private ArrayList<Video> _items;

    public VideosArrayAdapter(Context context, int resource, ArrayList<Video> items) {
        super(context, resource, items);
        _items = items;
        _context = context;
        _resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(_context);
            view = vi.inflate(_resource, null);
        }

//            ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_img);
//            String posterURL = _items.get(position).PosterSmallURL;
//            Picasso.with(_context).load(posterURL).into(imageView);

        return view;
    }
}
