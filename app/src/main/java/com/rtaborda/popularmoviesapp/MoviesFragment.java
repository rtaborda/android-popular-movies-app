package com.rtaborda.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.rtaborda.popularmoviesapp.helpers.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {
    private final String TMDB_API_KEY = "ff272392c96fcc9d214c82c49e0631be";
    private ArrayAdapter<String> mMoviesAdapter;

    public MoviesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        mMoviesAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        R.layout.grid_item_movie, // The name of the layout ID.
                        R.id.grid_item_movie_img, // The ID of the imageview to populate.
                        new ArrayList<String>());


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // Get a reference to the ListView, and attach this adapter to it.
        GridView listView = (GridView) rootView.findViewById(R.id.gridview_movies);
        listView.setAdapter(mMoviesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //String forecast = mForecastAdapter.getItem(position);
                //Intent intent = new Intent(getActivity(), DetailActivity.class)
                //        .putExtra(Intent.EXTRA_TEXT, forecast);
                //startActivity(intent);
            }
        });

        return rootView;
    }


    private void getConfigurations() {
        FetchConfigurationsTask configurationsTask = new FetchConfigurationsTask();
        configurationsTask.execute();
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        moviesTask.execute("TODO");
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }



    public class FetchConfigurationsTask extends AsyncTask<Void, Void, String>{
        private final String LOG_TAG = FetchConfigurationsTask.class.getSimpleName();

        @Override
        protected String doInBackground(Void... params) {

            // Will contain the raw JSON response as a string.
            String configurationJsonStr;

            try {
                // Construct the URL for the TheMovieDB query
                // Possible parameters are available at TMDB's movies API page, at
                // http://docs.themoviedb.apiary.io/#reference/discover/discovermovie
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY_PARAM = "api_key";
                final String SORT_BY_PARAM = "sort_by";
                final String PAGE_PARAM = "page";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, TMDB_API_KEY)
                        .appendQueryParameter(SORT_BY_PARAM, "vote_average.desc") //TODO Move this to the settings
                        .appendQueryParameter(PAGE_PARAM, "1")
                        .build();

                URL url = new URL(builtUri.toString());
                configurationJsonStr = HttpClient.GetJsonResponse(url);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

            try {
                return getConfigurationDataFromJson(configurationJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            //TODO
        }


        /**
         * Take the String representing the movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private String getConfigurationDataFromJson(String configurationJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            //final String TMDB_RESULTS = "results";
            //final String TMDB_POSTER_IMAGE = "poster_path";

            JSONObject moviesJson = new JSONObject(configurationJsonStr);


            return null;
        }

    }



    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            //if (params.length == 0) {
            //    return null;
            //}

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                // Construct the URL for the TheMovieDB query
                // Possible parameters are available at TMDB's movies API page, at
                // http://docs.themoviedb.apiary.io/#reference/discover/discovermovie
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY_PARAM = "api_key";
                final String SORT_BY_PARAM = "sort_by";
                final String PAGE_PARAM = "page";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, TMDB_API_KEY)
                        .appendQueryParameter(SORT_BY_PARAM, "vote_average.desc") //TODO Move this to the settings
                        .appendQueryParameter(PAGE_PARAM, "1")
                        .build();

                URL url = new URL(builtUri.toString());
                moviesJsonStr = HttpClient.GetJsonResponse(url);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for(String movieImageStr : result) {
                    mMoviesAdapter.add(movieImageStr);
                }
                // New data is back from the server.  Hooray!
            }
        }


        /**
         * Take the String representing the movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private String[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_IMAGE = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            return null;
        }
    }


}
