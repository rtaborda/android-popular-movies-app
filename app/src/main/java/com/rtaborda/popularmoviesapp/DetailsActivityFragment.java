package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private TMDBApiClient _tmdbApiClient = null;

    private Movie _movie;

    private ReviewsArrayAdapter _mReviewsAdapter;
    private ArrayList<Review> _reviewArrayList;

    private VideosArrayAdapter _mVideosAdapter;
    private ArrayList<Video> _videoArrayList;

    @Bind(R.id.textView_title) TextView _title;
    @Bind(R.id.textView_overview) TextView _overview;
    @Bind(R.id.textView_rating) TextView _rating;
    @Bind(R.id.textView_release) TextView _release;
    @Bind(R.id.imageView_poster) ImageView _poster;
    @Bind(R.id.listview_reviews) ListView _listViewReviews;
    @Bind(R.id.listview_videos) ListView _listViewVideos;
    @Bind(R.id.imageView_favourite) ImageView _imgFavourite;
    @Bind(R.id.textView_favourite) TextView _txtFavourite;

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

                _movie = movie;
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

                loadFavouriteInfo(movie);

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

        if(_movie != null) {
            if (_reviewArrayList == null || _reviewArrayList.size() == 0) {
                getReviews();
            } else {
                ArrayList<Review> aux = new ArrayList<>(_reviewArrayList);
                _mReviewsAdapter.clear(); // Clear actually clears the contents in the _movieArrayList
                _reviewArrayList = aux;
                _mReviewsAdapter.addAll(_reviewArrayList);
                LayoutUtils.setListViewHeightBasedOnChildren(_listViewReviews);
            }

            if (_videoArrayList == null || _videoArrayList.size() == 0) {
                getVideos();
            } else {
                ArrayList<Video> aux = new ArrayList<>(_videoArrayList);
                _mVideosAdapter.clear(); // Clear actually clears the contents in the _movieArrayList
                _videoArrayList = aux;
                _mVideosAdapter.addAll(_videoArrayList);
                LayoutUtils.setListViewHeightBasedOnChildren(_listViewVideos);
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

        _listViewReviews.setAdapter(_mReviewsAdapter);
    }

    private void setVideosAdapter(){
        _videoArrayList = new ArrayList<>();
        _mVideosAdapter = new VideosArrayAdapter(
                getActivity(),
                R.layout.list_item_video,
                _videoArrayList
        );

        _listViewVideos.setAdapter(_mVideosAdapter);

        _listViewVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Video video = _mVideosAdapter.getItem(position);
                // based on http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.key));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + video.key));
                    startActivity(intent);
                }
            }
        });
    }

    private void getReviews(){
        if(_tmdbApiClient == null){
            initializeTMDBApiClient();
        }
        new FetchReviewsTask().execute(_movie.id);
    }

    private void getVideos(){
        if(_tmdbApiClient == null){
            initializeTMDBApiClient();
        }
        new FetchVideosTask().execute(_movie.id);
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
            return result.results;
        }

        @Override
        protected void onPostExecute(Video[] result) {
            if (result != null) {
                _mVideosAdapter.clear();
                _mVideosAdapter.addAll(result);
                LayoutUtils.setListViewHeightBasedOnChildren(_listViewVideos);
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
            return result.results;
        }

        @Override
        protected void onPostExecute(Review[] result) {
            if (result != null) {
                _mReviewsAdapter.clear();
                _mReviewsAdapter.addAll(result);
                LayoutUtils.setListViewHeightBasedOnChildren(_listViewReviews);
            }
        }
    }


    @OnClick(R.id.textView_favourite)
    public void submit(View view) {
        if(_txtFavourite.getText() == getText(R.string.activity_details_favourites_add)) {
            addToFavourites(_movie);
        }
        else {
            removeFromFavourites(_movie);
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

        setFavouriteOn();
    }

    public void removeFromFavourites(Movie movie) {
        getActivity().getContentResolver().delete(
                FavouriteProvider.Favourites.CONTENT_URI,
                FavouriteTitleColumns._ID + " = ?",
                new String[]{movie.id}
        );

        setFavouriteOff();
    }

    public void loadFavouriteInfo(Movie movie) {
        Cursor favouriteCursor = getActivity().getContentResolver().query(
                FavouriteProvider.Favourites.CONTENT_URI,
                new String[]{FavouriteTitleColumns._ID},
                FavouriteTitleColumns._ID + " = ?",
                new String[]{movie.id},
                null
        );

        if (favouriteCursor.getCount() == 0) {
            setFavouriteOff();
        } else {
            setFavouriteOn();
        }
    }

    public void setFavouriteOn(){
        _txtFavourite.setText(R.string.activity_details_favourites_remove);
        _imgFavourite.setImageResource(R.mipmap.ic_favourite_on);
    }

    public void setFavouriteOff(){
        _txtFavourite.setText(R.string.activity_details_favourites_add);
        _imgFavourite.setImageResource(R.mipmap.ic_favourite_off);
    }

}
