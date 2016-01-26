package com.example.brandnewpeterson.projectone;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brandnewpeterson on 1/26/16.
 */
public class MyParcelable implements Parcelable {
    public Bitmap poster;
    public String title;
    public String synopsis;
    public String rank;
    public String rating;
    public String year;

    public MyParcelable(String title, String synopsis, String rank, String rating, String year, Bitmap poster) {
        this.title = title;
        this.synopsis = synopsis;
        this.rank = rank;
        this.rating = rating;
        this.year = year;
        this.poster = poster;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(rank);
        dest.writeString(rating);
        dest.writeString(year);
        dest.writeParcelable(poster, flags);
    }

    // Creator
    public static final Parcelable.Creator<MyParcelable> CREATOR
            = new Parcelable.Creator<MyParcelable>() {

        @Override
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        @Override
        public MyParcelable[] newArray(int size) {
            return new MyParcelable[0];
        }
    };

    private MyParcelable(Parcel in) {
        this.title = in.readString();
        this.synopsis = in.readString();
        this.rank = in.readString();
        this.rating = in.readString();
        this.year = in.readString();
        this.poster = in.readParcelable(null);

    }
}