package com.rtaborda.popularmoviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rtaborda.popularmoviesapp.R;
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

        TextView textView = (TextView) view.findViewById(R.id.list_item_video_name);
        textView.setText(_items.get(position).name);

        return view;
    }
}
