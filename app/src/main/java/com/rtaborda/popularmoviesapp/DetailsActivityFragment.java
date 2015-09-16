package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtaborda.popularmoviesapp.entities.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
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
            }
        }

        return rootView;
    }

    private Boolean isStringNullOrEmpty(String str){
        return str == null || str.equals("") || str.equals("null");
    }
}
