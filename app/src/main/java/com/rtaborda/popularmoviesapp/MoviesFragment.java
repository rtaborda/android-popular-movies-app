package com.rtaborda.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private final String TMDB_API_KEY = ""; //TODO Remove before pushing to master

    private TMDBConfiguration _configuration;
    private MoviePosterArrayAdapter _mMoviesAdapter;
    private ArrayList<Movie> _movieArrayList;
    private String _sortBy;


    public MoviesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get the sort by preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        _sortBy = prefs.getString(getString(R.string.pref_sort_movies_by_key),
                getString(R.string.sort_by_rating_desc_value));

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        _movieArrayList = new ArrayList<>();
        _mMoviesAdapter = new MoviePosterArrayAdapter(
                getActivity(),
                R.layout.grid_item_movie,
                _movieArrayList
        );

        gridView.setAdapter(_mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = _mMoviesAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("title", movie.Title);
                intent.putExtra("overview", movie.Overview);
                intent.putExtra("big_poster", movie.PosterBigURL);
                intent.putExtra("rating", movie.Rating);

                if (movie.ReleaseDate != null) {
                    intent.putExtra("release_date", new SimpleDateFormat("dd-MM-yyyy").format(movie.ReleaseDate));
                } else {
                    intent.putExtra("release_date", "Information not available");
                }

                startActivity(intent);
            }
        });

        // Get the movies list from the saved instance state
        if(savedInstanceState != null && savedInstanceState.containsKey("movieArrayList")) {
            _movieArrayList = savedInstanceState.getParcelableArrayList("movieArrayList");
        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        // If the users' order preference changed we need to get the list of movies again
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String newSortBy = prefs.getString(getString(R.string.pref_sort_movies_by_key),
                getString(R.string.sort_by_rating_desc_value));

        if(_movieArrayList == null || _movieArrayList.isEmpty() || !_sortBy.equals(newSortBy)) {
            _sortBy = newSortBy;
            getConfigurations();
            updateMovies();
        } else {
            _mMoviesAdapter.clear();
            _mMoviesAdapter.addAll(_movieArrayList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the movies list to the saved instance state
        outState.putParcelableArrayList("movieArrayList", _movieArrayList);
        super.onSaveInstanceState(outState);
    }


    private void getConfigurations() {
        FetchConfigurationsTask configurationsTask = new FetchConfigurationsTask();
        configurationsTask.execute();
        try {
            configurationsTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(_sortBy);
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
            JSONArray logoSizes = imgsConfigJson.getJSONArray("logo_sizes");
            config.SmallPosterSize = logoSizes.getString(4);
            config.BigPosterSize = posterSizes.getString(4);
            return config;
        }

    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

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
                        .appendQueryParameter(SORT_BY_PARAM, params[0])
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

            // This will only happen if there was an error getting or parsing the result.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                _mMoviesAdapter.clear();
                _mMoviesAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }
        }


        /**
         * Take the String representing the movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {
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
            String posterPath, releaseDate, rating;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for(int i=0; i<moviesArray.length();++i) {
                try {
                    movieJson = moviesArray.getJSONObject(i);

                    movies[i] = new Movie();
                    movies[i].Id = movieJson.getString(TMDB_MOVIE_ID);
                    movies[i].Title = movieJson.getString(TMDB_MOVIE_TITLE);
                    movies[i].Overview = movieJson.getString(TMDB_MOVIE_OVERVIEW);

                    rating = movieJson.getString(TMDB_MOVIE_RATING);
                    if(!jsonPropertyIsEmpty(rating)) {
                        movies[i].Rating = Double.parseDouble(rating);
                    }

                    releaseDate = movieJson.getString(TMDB_MOVIE_RELEASE);
                    if (!jsonPropertyIsEmpty(releaseDate)) {
                        movies[i].ReleaseDate = format.parse(releaseDate);
                    }

                    posterPath = movieJson.getString(TMDB_MOVIE_POSTER_IMAGE);
                    if (!jsonPropertyIsEmpty(posterPath)) {
                        movies[i].PosterSmallURL = _configuration.BaseURL + _configuration.SmallPosterSize + posterPath;
                        movies[i].PosterBigURL = _configuration.BaseURL + _configuration.BigPosterSize + posterPath;
                    }
                }catch (ParseException ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                    ex.printStackTrace();
                    // continues
                }
            }

            return movies;
        }

        private Boolean jsonPropertyIsEmpty(String prop){
            return prop == null || prop.equals("") || prop.equals("null");
        }
    }


}
