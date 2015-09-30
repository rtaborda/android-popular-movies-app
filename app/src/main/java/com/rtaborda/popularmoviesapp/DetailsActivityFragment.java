package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rtaborda.popularmoviesapp.adapters.MoviePosterArrayAdapter;
import com.rtaborda.popularmoviesapp.adapters.ReviewsArrayAdapter;
import com.rtaborda.popularmoviesapp.adapters.VideosArrayAdapter;
import com.rtaborda.popularmoviesapp.data.FavouriteProvider;
import com.rtaborda.popularmoviesapp.data.FavouriteTitleColumns;
import com.rtaborda.popularmoviesapp.entities.Movie;
import com.rtaborda.popularmoviesapp.entities.Review;
import com.rtaborda.popularmoviesapp.entities.ReviewsResult;
import com.rtaborda.popularmoviesapp.entities.Video;
import com.rtaborda.popularmoviesapp.entities.VideosResult;
import com.rtaborda.popularmoviesapp.helpers.LayoutUtils;
import com.rtaborda.popularmoviesapp.helpers.TMDBApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private TMDBApiClient _tmdbApiClient = null;

    private String _movieId;

    private ReviewsArrayAdapter _mReviewsAdapter;
    private ArrayList<Review> _reviewArrayList;

    private VideosArrayAdapter _mVideosAdapter;
    private ArrayList<Video> _videoArrayList;

    @Bind(R.id.textView_title) TextView _title;
    @Bind(R.id.textView_overview) TextView _overview;
    @Bind(R.id.textView_rating) TextView _rating;
    @Bind(R.id.textView_release) TextView _release;
    @Bind(R.id.imageView_poster) ImageView _poster;
    @Bind((R.id.listview_reviews)) ListView listViewReviews;
    @Bind((R.id.listview_videos)) ListView listViewVideos;

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

            if (movie != null) {
                // Set activity title to movie's title
                parentActivity.setTitle(movie.original_title);

                _movieId = movie.id;
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

                // set adapters
                setReviewsAdapter();
                setVideosAdapter();

                // Get the reviews list from the saved instance state
                if(savedInstanceState != null && savedInstanceState.containsKey("reviewArrayList")) {
                    _reviewArrayList = savedInstanceState.getParcelableArrayList("reviewArrayList");
                }

                // Get the videos list from the saved instance state
                if(savedInstanceState != null && savedInstanceState.containsKey("videoArrayList")) {
                    _videoArrayList = savedInstanceState.getParcelableArrayList("videoArrayList");
                }
            }
        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(_movieId != null && _movieId != "") {
            if (_reviewArrayList == null || _reviewArrayList.size() == 0) {
                getReviews();
            } else {
                ArrayList<Review> aux = new ArrayList<>(_reviewArrayList);
                _mReviewsAdapter.clear(); // Clear actually clears the contents in the _movieArrayList
                _reviewArrayList = aux;
                _mReviewsAdapter.addAll(_reviewArrayList);
                LayoutUtils.setListViewHeightBasedOnChildren(listViewReviews);
            }

            if (_videoArrayList == null || _videoArrayList.size() == 0) {
                getVideos();
            } else {
                ArrayList<Video> aux = new ArrayList<>(_videoArrayList);
                _mVideosAdapter.clear(); // Clear actually clears the contents in the _movieArrayList
                _videoArrayList = aux;
                _mVideosAdapter.addAll(_videoArrayList);
                LayoutUtils.setListViewHeightBasedOnChildren(listViewVideos);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the reviews list to the saved instance state
        outState.putParcelableArrayList("reviewArrayList", _reviewArrayList);

        // Save the videos list to the saved instance state
        outState.putParcelableArrayList("videoArrayList", _videoArrayList);

        super.onSaveInstanceState(outState);
    }


    private void setReviewsAdapter(){
        _reviewArrayList = new ArrayList<>();
        _mReviewsAdapter = new ReviewsArrayAdapter(
                getActivity(),
                R.layout.list_item_review,
                _reviewArrayList
        );

        listViewReviews.setAdapter(_mReviewsAdapter);
    }

    private void setVideosAdapter(){
        _videoArrayList = new ArrayList<>();
        _mVideosAdapter = new VideosArrayAdapter(
                getActivity(),
                R.layout.list_item_video,
                _videoArrayList
        );

        listViewVideos.setAdapter(_mVideosAdapter);
    }

    private void getReviews(){
        if(_tmdbApiClient == null){
            initializeTMDBApiClient();
        }
        new FetchReviewsTask().execute(_movieId);
    }

    private void getVideos(){
        if(_tmdbApiClient == null){
            initializeTMDBApiClient();
        }
        new FetchVideosTask().execute(_movieId);
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
                _mVideosAdapter.clear();
                _mVideosAdapter.addAll(result);
                LayoutUtils.setListViewHeightBasedOnChildren(listViewVideos);
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
                _mReviewsAdapter.clear();
                _mReviewsAdapter.addAll(result);
                LayoutUtils.setListViewHeightBasedOnChildren(listViewReviews);
            }
        }
    }



    public void addToFavourites(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(FavouriteTitleColumns._ID, movie.id);
        values.put(FavouriteTitleColumns.ORIGINAL_TITLE, movie.original_title);
        values.put(FavouriteTitleColumns.OVERVIEW, movie.overview);
        values.put(FavouriteTitleColumns.POSTER_PATH, movie.poster_path);
        values.put(FavouriteTitleColumns.RELEASE_DATE, movie.release_date);
        values.put(FavouriteTitleColumns.VOTE_AVERAGE, movie.vote_average);

        getActivity().getContentResolver().insert(
                FavouriteProvider.Favourites.CONTENT_URI,
                values
        );
    }

}
