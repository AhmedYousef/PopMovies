package com.developer.joe.popmovies.utilities;

import android.util.Log;

import com.developer.joe.popmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    private static final String RESULTS_KEY = "results";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String RELEASE_DATE_KEY = "release_date";


    public static ArrayList<Movie> getMovieListFromJson(String popMovieJson) {

        ArrayList<Movie> parsedMovieList = new ArrayList<>();
        try {
            JSONObject currentMovie = new JSONObject(popMovieJson);
            JSONArray resultsArray = currentMovie.optJSONArray(RESULTS_KEY);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultsObject = resultsArray.optJSONObject(i);
                String originalTitle = resultsObject.optString(ORIGINAL_TITLE_KEY, "");

                String posterPath = resultsObject.optString(POSTER_PATH_KEY, "").replace("/", "");
                String moviePoster = NetworkUtils.buildImageUrl(posterPath).toString();

                String plotSynopsis = resultsObject.optString(OVERVIEW_KEY, "");
                String userRating = resultsObject.optString(VOTE_AVERAGE_KEY, "");
                String releaseDate = resultsObject.optString(RELEASE_DATE_KEY, "");

                parsedMovieList.add(new Movie(originalTitle, moviePoster, plotSynopsis, userRating, releaseDate));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the movie JSON results", e);
        }

        return parsedMovieList;
    }
}
