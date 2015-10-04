package com.rtaborda.popularmoviesapp;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private String _sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            // Get the sort by preference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            _sortBy = prefs.getString(getString(R.string.pref_sort_movies_by_key),
                    getString(R.string.sort_by_rating_desc_value));

            if (!_sortBy.equals(getString(R.string.sort_by_favourites_value))) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MoviesFragment(), "movies")
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new FavouritesFragment(), "favourites")
                        .commit();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Get the sort by preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString(getString(R.string.pref_sort_movies_by_key),
                getString(R.string.sort_by_rating_desc_value));

        if(!sortBy.equals(_sortBy)) {
            _sortBy = sortBy;

            if (!sortBy.equals(getString(R.string.sort_by_favourites_value))) {
                loadMoviesFragment();
            } else {
                loadFavouritesFragment();
            }
        }
    }

    private void loadMoviesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment f = getSupportFragmentManager().findFragmentByTag("favourites");

        if (f != null) {
            fragmentTransaction.remove(f);
        }

        fragmentTransaction
                .add(R.id.container, new MoviesFragment(), "movies")
                .commit();
    }

    private void loadFavouritesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment f = getSupportFragmentManager().findFragmentByTag("movies");

        if (f != null) {
            fragmentTransaction.remove(f);
        }

        fragmentTransaction
                .add(R.id.container, new FavouritesFragment(), "favourites")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
