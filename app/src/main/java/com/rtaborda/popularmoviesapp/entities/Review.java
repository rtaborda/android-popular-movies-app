package com.rtaborda.popularmoviesapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rui on 21/09/2015.
 */
public class Review implements Parcelable {
    public String id;
    public String author;
    public String content;
    public URL url;

    private Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        try {
            url = new URL(in.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url.toString());
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
