package com.rtaborda.popularmoviesapp.helpers;

import com.rtaborda.popularmoviesapp.entities.Movie;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Rui on 13/09/2015.
 */
public interface TMDBApiClient {
    String TMDB_API_KEY = ""; //TODO Remove before pushing to master

    @GET("/configuration?api_key=" + TMDB_API_KEY)
    TMDBConfiguration getConfiguration();

    @GET("/discover/movie?api_key=" + TMDB_API_KEY)
    Movie[] getMovies(@Query("sort_by") String sortBy, @Query("page") String page);
}
