package com.rob.stlbluesfan46.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {

        private String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(false);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
            String size = "w185/";
            String posterPath;

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movie_data")) {
                Movie movieData = intent.getParcelableExtra("movie_data");

                // Inflate textviews with movie data
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movieData.getOriginalTitle());
                ((TextView) rootView.findViewById(R.id.release_date)).setText(movieData.getReleaseDate());
                Resources res = getResources();
                String userRating = res.getString(R.string.user_rating, movieData.getUserRating());
                ((TextView) rootView.findViewById(R.id.user_rating)).setText(userRating);
                ((TextView) rootView.findViewById(R.id.synopsis)).setText(movieData.getSynopsis());

                posterPath = movieData.getPosterPath();
                Uri uri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                        .appendEncodedPath(size)
                        .appendEncodedPath(posterPath)
                        .build();

                //Inflate image view with poster image
                Picasso.with(getActivity())
                        .load(uri)
                        .fit()
                        .centerInside()
                        .into((ImageView) rootView.findViewById(R.id.movie_poster));


            }

            return rootView;

        }
    }
}
