package com.developer.joe.popmovies.utilites;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.developer.joe.popmovies.Constants;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String THE_MOVIES_DB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TYPE_PATH = "movie";
    private static final String PARAM_API_KEY = "api_key";
    private static final String API_KEY = "";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String IMAGE_SIZE_PATH = "w185";

    private static URL makeUrl(Uri buildUri) {
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl() {
        Uri buildUri = Uri.parse(THE_MOVIES_DB_BASE_URL).buildUpon()
                .appendPath(TYPE_PATH)
                .appendPath(Constants.POPULAR_PATH)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();


        return makeUrl(buildUri);
    }

    public static URL buildUrl(String sortBy) {
        Uri buildUri = Uri.parse(THE_MOVIES_DB_BASE_URL).buildUpon()
                .appendPath(TYPE_PATH)
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        return makeUrl(buildUri);
    }

    public static URL buildImageUrl(String posterPath) {
        Uri buildUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendPath(posterPath)
                .build();

        return makeUrl(buildUri);
    }

    public static String getResponseFromHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean haveNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
