package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

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

        if (intent != null && intent.hasExtra("title") && intent.hasExtra("overview")
                && intent.hasExtra("big_poster") && intent.hasExtra("rating")
                && intent.hasExtra("release_date")) {

            String title = intent.getStringExtra("title");
            // Set activity title to movie's title
            parentActivity.setTitle(title);

            _title.setText(title);
            _overview.setText(intent.getStringExtra("overview"));
            _rating.setText(intent.getDoubleExtra("rating",  0.00) + "");
            _release.setText(intent.getStringExtra("release_date"));

            Picasso.with(getActivity()).load(
                    intent.getStringExtra("big_poster")
            ).into(_poster);
        }

        return rootView;

    }
}
