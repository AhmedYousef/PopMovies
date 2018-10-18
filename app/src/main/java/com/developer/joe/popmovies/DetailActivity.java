package com.developer.joe.popmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.developer.joe.popmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private TextView mOriginalTitleTextView;
    private ImageView mMoviePosterImage;
    private TextView mPlotSynopsis;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mOriginalTitleTextView = findViewById(R.id.tv_original_title);
        mMoviePosterImage = findViewById(R.id.image_mini_movie_poster);
        mPlotSynopsis = findViewById(R.id.tv_plot_synopsis);
        mUserRating = findViewById(R.id.tv_user_rating);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        Intent retrievedIntent = getIntent();
        Movie movie = retrievedIntent.getParcelableExtra(Constants.MOVIE_KEY);


        populateUI(movie);
    }

    private void populateUI(Movie movie) {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        String originalTitle = movie.getOriginalTitle();
        if (originalTitle.length() > 31) {
            mOriginalTitleTextView.setText(originalTitle);
            mOriginalTitleTextView.setTextSize(34);
        } else if (originalTitle.length() > 17) {
            mOriginalTitleTextView.setText(originalTitle);
            mOriginalTitleTextView.setTextSize(48);
        } else {
            mOriginalTitleTextView.setText(originalTitle);
        }

        Picasso.with(this)
                .load(movie.getMoviePoster())
                .into(mMoviePosterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        mMoviePosterImage.setImageResource(R.drawable.ic_not_found);
                        mMoviePosterImage.setPadding(90, 175, 90, 175);
                        mMoviePosterImage.setBackgroundColor(getResources().getColor(R.color.backgroundColorOffWhite));
                    }
                });

        String plotSynopsis = movie.getPlotSynopsis();
        if (!plotSynopsis.isEmpty()) {
            mPlotSynopsis.setText(movie.getPlotSynopsis());
        } else {
            mPlotSynopsis.setText("PLOT SYNOPSIS NOT FOUND!");
        }

        String userRating = movie.getUserRating();
        StringBuilder userRatingSB;
        if (!userRating.isEmpty()) {
            userRatingSB = new StringBuilder(userRating);
            userRatingSB.append("/10");
            mUserRating.setText(userRatingSB);
        } else {
            mUserRating.setText("USER RATING NOT FOUND!");
        }

        String releaseDate = formatDate(movie.getReleaseDate());
        mReleaseDate.setText(releaseDate);
    }

    private String formatDate(String releaseDate) {
        if (releaseDate.isEmpty()) {
            mReleaseDate.setTextSize(24);
            return "DATE NOT FOUND!";
        }
        SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormatIn.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormatOut = new SimpleDateFormat("yyyy");
        return dateFormatOut.format(date);
    }
}
