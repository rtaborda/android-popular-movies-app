package com.rtaborda.popularmoviesapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rui on 23/08/2015.
 */
public class Movie implements Parcelable {
    public String id;
    public String original_title;
    public String overview;
    public String release_date;
    public String poster_path;
    public Double vote_average;

    // Custom fields
    public String PosterSmallURL;
    public String PosterBigURL;


    private Movie(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        PosterSmallURL = in.readString();
        PosterBigURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
        dest.writeString(PosterSmallURL);
        dest.writeString(PosterBigURL);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
