package com.developer.joe.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.developer.joe.popmovies.R;
import com.developer.joe.popmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final static String TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieList;
    private Context mContext;
    private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMoviePosterImage;
        private final ImageView mNotFoundImage;

        private MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImage = itemView.findViewById(R.id.image_movie_poster);
            mNotFoundImage = itemView.findViewById(R.id.image_not_found);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = mMovieList.get(position);

        Picasso.with(mContext)
                .load(currentMovie.getMoviePoster())
                .into(holder.mMoviePosterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        holder.mNotFoundImage.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) {
            Log.i(TAG, "mMovieList = null");
            return 0;
        }
        return mMovieList.size();
    }

    public void setMovieList(ArrayList<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }
}
