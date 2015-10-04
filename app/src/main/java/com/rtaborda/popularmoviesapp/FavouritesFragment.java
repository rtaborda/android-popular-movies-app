package com.rtaborda.popularmoviesapp;

import android.support.v4.app.Fragment;
import android.content.Context;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.GridView;

import com.rtaborda.popularmoviesapp.adapters.FavouritesAdapter;
import com.rtaborda.popularmoviesapp.data.FavouriteProvider;
import com.rtaborda.popularmoviesapp.data.FavouriteTitleColumns;

/**
 * Created by Rui on 03/10/2015.
 */
public class FavouritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FAVOURITES_LOADER = 0;

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_FAVOURITE_ID = 0;
    static final int COL_FAVOURITE_TITLE = 1;
    static final int COL_FAVOURITE_OVERVIEW = 2;
    static final int COL_FAVOURITE_RELEASE_DATE = 3;
    public static final int COL_FAVOURITE_POSTER_PATH = 4;
    static final int COL_FAVOURITE_VOTE_AVERAGE= 5;


    private FavouritesAdapter _mFavouritesAdapter;

    public FavouritesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView.
        _mFavouritesAdapter = new FavouritesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(_mFavouritesAdapter);

        // We'll call our MainActivity
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // CursorAdapter returns a cursor at the correct position for getItem(), or null
//                // if it cannot seek to that position.
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));
//                    startActivity(intent);
//                }
//            }
//        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVOURITES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(getActivity(),
                FavouriteProvider.Favourites.CONTENT_URI,
                new String[]{
                        FavouriteTitleColumns._ID,
                        FavouriteTitleColumns.ORIGINAL_TITLE,
                        FavouriteTitleColumns.OVERVIEW,
                        FavouriteTitleColumns.RELEASE_DATE,
                        FavouriteTitleColumns.POSTER_PATH,
                        FavouriteTitleColumns.VOTE_AVERAGE
                },
                null,
                null,
                null
        );

        return loader;
    }


    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        _mFavouritesAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        _mFavouritesAdapter.swapCursor(null);
    }
}
