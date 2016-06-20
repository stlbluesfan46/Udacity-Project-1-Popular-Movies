package com.rob.stlbluesfan46.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom Adapter that inflates a GridView item to display the movie poster
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Uri components
        final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
        // Size is hard coded for now, will update when more screen sizes are supported
        String size = "w185/";
        String posterPath;

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);

        posterPath = movie.getPosterPath();

        Uri uri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(size)
                .appendEncodedPath(posterPath)
                .build();

        //Picasso will fetch the movie poster and insert the poster into the imageView
        Picasso.with(getContext())
                .load(uri)
                .fit()
                .centerCrop()
                .into(imageView);

        return convertView;
    }
}
