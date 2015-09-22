package com.rtaborda.popularmoviesapp.helpers;

import com.rtaborda.popularmoviesapp.entities.MoviesResult;
import com.rtaborda.popularmoviesapp.entities.ReviewsResult;
import com.rtaborda.popularmoviesapp.entities.VideosResult;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Rui on 13/09/2015.
 */
public interface TMDBApiClient {
    String TMDB_API_KEY = "ff272392c96fcc9d214c82c49e0631be"; //TODO Remove before pushing to master

    @GET("/configuration?api_key=" + TMDB_API_KEY)
    TMDBConfiguration getConfiguration();

    @GET("/discover/movie?api_key=" + TMDB_API_KEY)
    MoviesResult getMovies(@Query("sort_by") String sortBy, @Query("page") String page);

    @GET("/movie/{id}/videos?api_key=" + TMDB_API_KEY)
    VideosResult getVideos(@Path("id") String movieId);

    @GET("/movie/{id}/reviews?api_key=" + TMDB_API_KEY)
    ReviewsResult getReviews(@Path("id") String movieId);
}
