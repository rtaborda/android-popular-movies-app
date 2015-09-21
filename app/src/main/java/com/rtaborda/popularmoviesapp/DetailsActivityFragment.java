package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtaborda.popularmoviesapp.entities.Movie;
import com.rtaborda.popularmoviesapp.entities.Review;
import com.rtaborda.popularmoviesapp.entities.ReviewsResult;
import com.rtaborda.popularmoviesapp.entities.Video;
import com.rtaborda.popularmoviesapp.entities.VideosResult;
import com.rtaborda.popularmoviesapp.helpers.TMDBApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private TMDBApiClient _tmdbApiClient;

    @Bind(R.id.textView_title) TextView _title;
    @Bind(R.id.textView_overview) TextView _overview;
    @Bind(R.id.textView_rating) TextView _rating;
    @Bind(R.id.textView_release) TextView _release;
    @Bind(R.id.imageView_poster) ImageView _poster;


    public DetailsActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Activity parentActivity = getActivity();
        Intent intent = parentActivity.getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");

            if(movie != null) {
                // Set activity title to movie's title
                parentActivity.setTitle(movie.original_title);

                _title.setText(movie.original_title);
                _overview.setText(movie.overview);
                _rating.setText(movie.vote_average.toString());

                if(!isStringNullOrEmpty(movie.release_date)) {
                    _release.setText(movie.release_date);
                }
                else {
                    _release.setText("Information not available");
                }

                Picasso.with(getActivity()).load(movie.PosterBigURL).into(_poster);


                initializeTMDBApiClient();
                // Get the trailers
                new FetchVideosTask().execute(movie.id);
                // Get the reviews
                new FetchReviewsTask().execute(movie.id);
            }
        }

        return rootView;
    }

    // TODO Extract this code as it's a duplicate from MoviesFragment
    private void initializeTMDBApiClient(){
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();
        // for full retrofit logs
        adapter.setLogLevel(RestAdapter.LogLevel.FULL);

        _tmdbApiClient = adapter.create(TMDBApiClient.class);
    }

    private Boolean isStringNullOrEmpty(String str){
        return str == null || str.equals("") || str.equals("null");
    }




    private class FetchVideosTask extends AsyncTask<String, Void, Video[]> {
        @Override
        protected Video[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            VideosResult result = _tmdbApiClient.getVideos(params[0]);

//            for (Movie movie : result.results){
//                movie.PosterSmallURL = _configuration.images.base_url + _configuration.images.logo_sizes[4] + movie.poster_path;
//                movie.PosterBigURL = _configuration.images.base_url + _configuration.images.poster_sizes[4] + movie.poster_path;
//            }

            return result.results;
        }

        @Override
        protected void onPostExecute(Video[] result) {
            if (result != null) {
//                _mMoviesAdapter.clear();
//                _mMoviesAdapter.addAll(result);
            }
        }
    }



    private class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {
        @Override
        protected Review[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            ReviewsResult result = _tmdbApiClient.getReviews(params[0]);

//            for (Movie movie : result.results){
//                movie.PosterSmallURL = _configuration.images.base_url + _configuration.images.logo_sizes[4] + movie.poster_path;
//                movie.PosterBigURL = _configuration.images.base_url + _configuration.images.poster_sizes[4] + movie.poster_path;
//            }

            return result.results;
        }

        @Override
        protected void onPostExecute(Review[] result) {
            if (result != null) {
//                _mMoviesAdapter.clear();
//                _mMoviesAdapter.addAll(result);
            }
        }
    }




}
