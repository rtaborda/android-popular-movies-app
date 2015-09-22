package com.rtaborda.popularmoviesapp.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Rui on 21/09/2015.
 */
@ContentProvider(authority = FavouriteProvider.AUTHORITY, database = FavouriteDatabase.class)
public final class FavouriteProvider {

    public static final String AUTHORITY = "com.rtaborda.popularmoviesapp.data.FavouriteProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVOURITES = "favourites";
        String REVIEWS = "reviews";
        String VIDEOS = "videos";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavouriteDatabase.FAVOURITES)
    public static class Favourites {
        @ContentUri(
                path = Path.FAVOURITES,
                type = "vnd.android.cursor.dir/favourite",
                defaultSort = FavouriteTitleColumns.VOTE_AVERAGE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVOURITES);

        /*@InexactContentUri(
                name = "PLANET_ID",
                path = Path.PLANETS + "/#",
                type = "vnd.android.cursor.item/planet",
                whereColumn = PlanetColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.PLANETS, String.valueOf(id));
        }*/
    }

    @TableEndpoint(table = FavouriteDatabase.REVIEWS)
    public static class Reviews {
        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.REVIEWS);
    }

    @TableEndpoint(table = FavouriteDatabase.VIDEOS)
    public static class Videos {
        @ContentUri(
                path = Path.VIDEOS,
                type = "vnd.android.cursor.dir/video",
                defaultSort = VideoColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.VIDEOS);
    }
}
