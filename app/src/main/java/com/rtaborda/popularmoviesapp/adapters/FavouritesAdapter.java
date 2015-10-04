package com.rtaborda.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.rtaborda.popularmoviesapp.FavouritesFragment;
import com.rtaborda.popularmoviesapp.R;
import com.rtaborda.popularmoviesapp.data.FavouriteTitleColumns;
import com.squareup.picasso.Picasso;

/**
 * Created by Rui on 03/10/2015.
 */
public class FavouritesAdapter extends CursorAdapter {

    public FavouritesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_img);

        Picasso.with(context).load(
                cursor.getString(FavouritesFragment.COL_FAVOURITE_POSTER_PATH)
        ).into(imageView);
    }

}
