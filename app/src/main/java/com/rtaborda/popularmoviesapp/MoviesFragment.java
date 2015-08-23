package com.rtaborda.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.rtaborda.popularmoviesapp.adapters.MoviePosterArrayAdapter;
import com.rtaborda.popularmoviesapp.entities.Movie;
import com.rtaborda.popularmoviesapp.helpers.HttpClient;
import com.rtaborda.popularmoviesapp.helpers.TMDBConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private final String TMDB_API_KEY = "ff272392c96fcc9d214c82c49e0631be";

    private TMDBConfiguration _configuration;
    private MoviePosterArrayAdapter _mMoviesAdapter;


    public MoviesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        // The ArrayAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        _mMoviesAdapter = new MoviePosterArrayAdapter(getActivity(), 0, new ArrayList<Movie>());

        gridView.setAdapter(_mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = _mMoviesAdapter.getItem(position);
                //Intent intent = new Intent(getActivity(), DetailActivity.class)
                //        .putExtra(Intent.EXTRA_TEXT, forecast);
                //startActivity(intent);
                Log.d(LOG_TAG, movie.Id);
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
        getConfigurations();
        updateMovies();
    }



    public class FetchConfigurationsTask extends AsyncTask<Void, Void, TMDBConfiguration>{
        private final String LOG_TAG = FetchConfigurationsTask.class.getSimpleName();

        @Override
        protected TMDBConfiguration doInBackground(Void... params) {
            // Will contain the raw JSON response as a string.
            String configurationJsonStr;

            try {
                // Construct the URL for the TheMovieDB query
                // Possible parameters are available at TMDB's movies API page, at
                // http://docs.themoviedb.apiary.io/#reference/discover/discovermovie
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/configuration?";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, TMDB_API_KEY)
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
        protected void onPostExecute(TMDBConfiguration result){
            if(result != null){
                _configuration = result;
            }
        }

        /**
         * Take the String representing the movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private TMDBConfiguration getConfigurationDataFromJson(String configurationJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_CONFIG_IMAGES = "images";
            final String TMDB_CONFIG_BASEURL = "base_url";
            final String TMDB_CONFIG_POSTERSIZES = "poster_sizes";

            JSONObject configurationJson = new JSONObject(configurationJsonStr);
            JSONObject imgsConfigJson = configurationJson.getJSONObject(TMDB_CONFIG_IMAGES);

            TMDBConfiguration config = new TMDBConfiguration();
            config.BaseURL = imgsConfigJson.getString(TMDB_CONFIG_BASEURL);

            JSONArray posterSizes = imgsConfigJson.getJSONArray(TMDB_CONFIG_POSTERSIZES);
            config.SmallPosterSize = posterSizes.getString(2);
            config.BigPosterSize = posterSizes.getString(4);
            return config;
        }

    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
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
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                _mMoviesAdapter.clear();
                for(Movie movie : result) {
                    _mMoviesAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }


        /**
         * Take the String representing the movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException, ParseException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_MOVIE_ID = "id";
            final String TMDB_MOVIE_TITLE = "original_title";
            final String TMDB_MOVIE_OVERVIEW = "overview";
            final String TMDB_MOVIE_RELEASE = "release_date";
            final String TMDB_MOVIE_RATING = "vote_average";
            final String TMDB_MOVIE_POSTER_IMAGE = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Movie[] movies = new Movie[moviesArray.length()];
            JSONObject movieJson;
            String posterPath;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for(int i=0; i<moviesArray.length();++i) {
                movieJson = moviesArray.getJSONObject(i);

                movies[i] = new Movie();
                movies[i].Id = movieJson.getString(TMDB_MOVIE_ID);
                movies[i].Title = movieJson.getString(TMDB_MOVIE_TITLE);
                movies[i].Overview = movieJson.getString(TMDB_MOVIE_OVERVIEW);
                movies[i].ReleaseDate = format.parse(movieJson.getString(TMDB_MOVIE_RELEASE));
                movies[i].Rating = Double.parseDouble(movieJson.getString(TMDB_MOVIE_RATING));
                posterPath = movieJson.getString(TMDB_MOVIE_POSTER_IMAGE);
                movies[i].PosterSmallURL = _configuration.BaseURL + _configuration.SmallPosterSize + posterPath;
                movies[i].PosterBigURL = _configuration.BaseURL + _configuration.BigPosterSize + posterPath;
            }

            return movies;
        }
    }


}
