package com.rob.stlbluesfan46.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/* Movie Fragment
 * Inflates the main display of the app and adds a sort menu to the action bar
 */
public class MovieFragment extends Fragment {

    private String LOG_TAG = MovieFragment.class.getSimpleName();

    // Custom array adapter to hold Movie objects
    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState  ) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.moviefragment, menu);
    }

    /*
     * The sort menu inflation sets the sort order of movies to display
     * Pulls new data from themoviedb.org after sort order is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (id) {
            case R.id.sort_popular:
                editor.putString(getString(R.string.sort_mode_key), getString(R.string.sort_popular));
                editor.commit();
                updateMovies();
                break;
            case R.id.sort_top_rated:
                editor.putString(getString(R.string.sort_mode_key), getString(R.string.sort_top_rated));
                editor.commit();
                updateMovies();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * A GridView that uses the custom MovieAdapter to display the movie posters
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mMovieAdapter);

        // Sets a ClickListener to launch a detail display of the movie
        // Passes a Parcelable Movie object through the intent
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieData = mMovieAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie_data", movieData);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /*
     * updateMovies fetches new movies
     */
    private void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    /*
     * FetchMoviesTask is a class that uses AsyncTask to pull movie data in the background
     */
    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson (String movieJsonStr, int numResults)
            throws JSONException {

            //These are the names of the JSON objects to be extracted
            final String MDB_RESULTS = "results";
            final String MDB_ORIGINAL_TITLE = "original_title";
            final String MDB_MOVIE_POSTER_PATH = "poster_path";
            final String MDB_SYNOPSIS = "overview";
            final String MDB_RATING = "vote_average";
            final String MDB_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            Movie[] movieResults = new Movie[numResults];
            for (int i = 0; i < movieArray.length(); i++) {
                String title;
                String posterPath;
                String synopsis;
                String rating;
                String releaseDate;

                Movie movie;

                // Get JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);

                // Get movie info
                title = movieData.getString(MDB_ORIGINAL_TITLE);

                posterPath = movieData.getString(MDB_MOVIE_POSTER_PATH);

                synopsis = movieData.getString(MDB_SYNOPSIS);

                rating = movieData.getString(MDB_RATING);

                releaseDate = movieData.getString(MDB_RELEASE_DATE);

                // Put movie info into a Movie object
                movie = new Movie(title, posterPath, synopsis, rating, releaseDate);

                movieResults[i] = movie;
            }

            return movieResults;
        }

        @Override
        protected Movie[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // Each page returned by the API call contains 20 results. Since we are
            // just calling one page for now, number of results is set to 20.
            int numResults = 20;

            try {
                // Construct the URL for the themoviedb.org query
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                // the sortType is selected by the user
                // default is popular movies
                String sortType = sharedPreferences.getString(getString(R.string.sort_mode_key), getString(R.string.sort_mode_default));

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendEncodedPath(sortType)
                        .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr, numResults);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        public void onPostExecute(Movie[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                for (Movie item : result) {
                    mMovieAdapter.add(item);
                }
            }
        }
    }
}
