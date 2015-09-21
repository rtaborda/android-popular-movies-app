package com.rtaborda.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.rtaborda.popularmoviesapp.adapters.MoviePosterArrayAdapter;
import com.rtaborda.popularmoviesapp.entities.Movie;
import com.rtaborda.popularmoviesapp.entities.MoviesResult;
import com.rtaborda.popularmoviesapp.helpers.TMDBApiClient;
import com.rtaborda.popularmoviesapp.helpers.TMDBConfiguration;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {
    private TMDBApiClient _tmdbApiClient;
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
                intent.putExtra("movie", movie);
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

        if(_movieArrayList == null || _movieArrayList.size() == 0 || !_sortBy.equals(newSortBy)) {
            _sortBy = newSortBy;
            getConfigurations();
        } else {
            ArrayList<Movie> aux = new ArrayList<>(_movieArrayList);
            _mMoviesAdapter.clear(); // Clear actually clears the contents in the _movieArrayList
            _movieArrayList = aux;
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
        initializeTMDBApiClient();
        FetchConfigurationsTask configurationsTask = new FetchConfigurationsTask();
        configurationsTask.execute();
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(_sortBy);
    }

    private void initializeTMDBApiClient(){
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();
        // for full retrofit logs
        adapter.setLogLevel(RestAdapter.LogLevel.FULL);

        _tmdbApiClient = adapter.create(TMDBApiClient.class);
    }


    private class FetchConfigurationsTask extends AsyncTask<Void, Void, TMDBConfiguration>{
        @Override
        protected TMDBConfiguration doInBackground(Void... params) {
            return _tmdbApiClient.getConfiguration();
        }

        @Override
        protected void onPostExecute(TMDBConfiguration result){
            if(result != null){
                _configuration = result;
                updateMovies();
            }
        }
    }


    private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            MoviesResult result = _tmdbApiClient.getMovies(params[0], "1");

            for (Movie movie : result.results){
                movie.PosterSmallURL = _configuration.images.base_url + _configuration.images.logo_sizes[4] + movie.poster_path;
                movie.PosterBigURL = _configuration.images.base_url + _configuration.images.poster_sizes[4] + movie.poster_path;
            }

            return result.results;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                _mMoviesAdapter.clear();
                _mMoviesAdapter.addAll(result);
            }
        }
    }

}
