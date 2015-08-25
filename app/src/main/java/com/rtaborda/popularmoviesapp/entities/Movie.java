package com.rtaborda.popularmoviesapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Rui on 23/08/2015.
 */
public class Movie implements Parcelable {
    public String Id;
    public String Title;
    public String Overview;
    public double Rating;
    public Date ReleaseDate;
    public String PosterSmallURL;
    public String PosterBigURL;


    public Movie(){}

    private Movie(Parcel in) {
        Id = in.readString();
        Title = in.readString();
        Overview = in.readString();
        Rating = in.readDouble();
        ReleaseDate = new Date(in.readLong());
        PosterSmallURL = in.readString();
        PosterBigURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Title);
        dest.writeString(Overview);
        dest.writeDouble(Rating);
        // This writing and reading Dates from a Parcelable was taken from here:
        // http://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
        if(ReleaseDate != null) {
            dest.writeLong(ReleaseDate.getTime());
        }
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
