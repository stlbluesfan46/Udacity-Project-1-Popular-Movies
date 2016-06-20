package com.rob.stlbluesfan46.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie class to hold movie data
 */
public class Movie implements Parcelable {

    private String originalTitle;
    private String posterPath;
    private String synopsis;
    private String userRating;
    private String releaseDate;

    public Movie (String title, String poster, String synopsis, String rating, String date) {
        this.originalTitle = title;
        this.posterPath = poster;
        this.synopsis = synopsis;
        this.userRating = rating;
        this.releaseDate = date;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
