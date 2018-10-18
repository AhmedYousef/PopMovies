package com.developer.joe.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.joe.popmovies.adapters.MovieAdapter;
import com.developer.joe.popmovies.model.Movie;
import com.developer.joe.popmovies.utilities.JsonUtils;
import com.developer.joe.popmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mCheckConnectionMessage;
    private TextView mErrorMessage;
    private ImageView mFixImage;
    private Button mRetryButton;
    private ProgressBar mLoadingIndicator;
    private ProgressBar mBtnLoadingIndicator;
    private Toast mConnectionToast;
    private SharedPreferences mSharedPreferences;

    private ArrayList<Movie> mMovieList;
    private static String stateKey = "";



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher_pop_movies_round);

        mRecyclerView = findViewById(R.id.recyclerview_movie);
        mCheckConnectionMessage = findViewById(R.id.tv_connection_message);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mFixImage = findViewById(R.id.image_fix);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mBtnLoadingIndicator = findViewById(R.id.pb_btn_loading_indicator);
        mRetryButton = findViewById(R.id.button_retry);
        mSharedPreferences = getPreferences(MODE_PRIVATE);

        if (NetworkUtils.haveNetwork(this)) {
            GridLayoutManager gridLayoutManager =
                    new GridLayoutManager(this, Constants.NUM_OF_COLUMNS, LinearLayoutManager.VERTICAL, false);

            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setHasFixedSize(true);


            mMovieAdapter = new MovieAdapter(this, this);
            mRecyclerView.setAdapter(mMovieAdapter);

            if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.MOVIE_LIST_KEY) || mMovieList == null) {
                loadPopMovies();
            } else {
                mMovieList = savedInstanceState.getParcelableArrayList(Constants.MOVIE_LIST_KEY);
                mMovieAdapter.setMovieList(mMovieList);
            }
        } else {
            mCheckConnectionMessage.setVisibility(View.VISIBLE);
            mRetryButton.setVisibility(View.VISIBLE);
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRetryButton.setVisibility(View.INVISIBLE);
                mBtnLoadingIndicator.setVisibility(View.VISIBLE);
                recreate();
            }
        });
    }

    private void loadPopMovies() {
        String sortByLastState = loadStateData();
        URL popMoviesUrl;
        if (sortByLastState == null) {
            popMoviesUrl = NetworkUtils.buildUrl();
        } else {
            popMoviesUrl = NetworkUtils.buildUrl(sortByLastState);
        }
        new popMoviesQueryTask().execute(popMoviesUrl);
    }

    private void loadPopMovies(String sortBy) {
        URL popMoviesUrl = NetworkUtils.buildUrl(sortBy);
        new popMoviesQueryTask().execute(popMoviesUrl);
    }

    private void saveStateData(String sortBy) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (sortBy.equals(Constants.POPULAR_PATH)) {
            editor.putString(stateKey, sortBy);
        }

        if (sortBy.equals(Constants.TOP_RATED_PATH)) {
            editor.putString(stateKey, sortBy);
        }
        editor.apply();
    }

    private String loadStateData() {
        return mSharedPreferences.getString(stateKey, null);
    }

    private void showCheckConnectionMessage() {
        if (mConnectionToast != null) {
            mConnectionToast.cancel();
        }
        mConnectionToast =
                Toast.makeText(this, "No internet found. Please check your connection", Toast.LENGTH_SHORT);
        mConnectionToast.show();
    }

    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
        mFixImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        if (movie != null && NetworkUtils.haveNetwork(this)) {
            detailIntent.putExtra(Constants.MOVIE_KEY, movie);
            startActivity(detailIntent);
        } else {
            showCheckConnectionMessage();
        }
    }

    public class popMoviesQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            URL popMoviesUrl = urls[0];
            String popMoviesJsonResults;
            mMovieList = new ArrayList<>();

            try {
                popMoviesJsonResults = NetworkUtils.getResponseFromHttpRequest(popMoviesUrl);
                mMovieList = JsonUtils.getMovieListFromJson(popMoviesJsonResults);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return mMovieList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieList != null && movieList.size() != 0) {
                mMovieAdapter.setMovieList(movieList);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.MOVIE_LIST_KEY, mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (!NetworkUtils.haveNetwork(this)) {
            showCheckConnectionMessage();
            return true;
        }

        if (mMovieList == null || mMovieList.size() == 0) {
            return true;
        }

        switch (itemId) {
            case R.id.sort_by_most_popular:
                loadPopMovies(Constants.POPULAR_PATH);
                saveStateData(Constants.POPULAR_PATH);
                return true;
            case R.id.sort_by_top_rated:
                loadPopMovies(Constants.TOP_RATED_PATH);
                saveStateData(Constants.TOP_RATED_PATH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
