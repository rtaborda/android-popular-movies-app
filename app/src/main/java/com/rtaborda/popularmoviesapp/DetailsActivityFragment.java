package com.rtaborda.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtaborda.popularmoviesapp.entities.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    public DetailsActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Activity parentActivity = getActivity();
        Intent intent = parentActivity.getIntent();

        if (intent != null && intent.hasExtra("title") && intent.hasExtra("overview")
                && intent.hasExtra("big_poster") && intent.hasExtra("rating")
                && intent.hasExtra("release_date")) {

            String title = intent.getStringExtra("title");
            // Set activity title to movie's title
            parentActivity.setTitle(title);

            ((TextView) rootView.findViewById(R.id.textView_title))
                    .setText(title);

            ((TextView) rootView.findViewById(R.id.textView_overview))
                    .setText(intent.getStringExtra("overview"));

            ((TextView) rootView.findViewById(R.id.textView_rating))
                    .setText(intent.getDoubleExtra("rating",  0.00) + "");

            ((TextView) rootView.findViewById(R.id.textView_release))
                    .setText(intent.getStringExtra("release_date"));

            Picasso.with(getActivity()).load(
                    intent.getStringExtra("big_poster")
            ).into(
                    ((ImageView) rootView.findViewById(R.id.imageView_poster))
            );
        }

        return rootView;

    }
}
