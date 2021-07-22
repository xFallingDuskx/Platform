package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.platform.R;
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitlesAdapter extends RecyclerView.Adapter<TitlesAdapter.ViewHolder>{

    public static final String TAG = "TitlesAdapter";

    private Context context;
    private List<Title> titles;
    ParseUser currentUser = ParseUser.getCurrentUser();
    Boolean titleLiked;


    public TitlesAdapter(Context context, List<Title> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TitlesAdapter.ViewHolder holder, int position) {
        Title title = titles.get(position);
        holder.bind(title);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvDescription;
        public TextView tvGenres;
        public ImageView ivLike;
        public TextView tvLikes;
        public ImageView ivComment;
        public TextView tvComments;
        public ImageView ivShare;
        public TextView tvShares;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.ivCover_Home);
            tvName = itemView.findViewById(R.id.tvName_Home);
            tvDescription = itemView.findViewById(R.id.tvDescription_Home);
            tvGenres = itemView.findViewById(R.id.tvGenres_Home);
            ivLike = itemView.findViewById(R.id.ivLike_Home);
            tvLikes = itemView.findViewById(R.id.tvLikes_Home);
            ivComment = itemView.findViewById(R.id.ivComment_Home);
            tvComments = itemView.findViewById(R.id.tvComments_Home);
            ivShare = itemView.findViewById(R.id.ivShare_Home);
            tvShares = itemView.findViewById(R.id.tvShares_Home);

            // User clicks on Like
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Entering onClickListener for likes");

                    int position = getAdapterPosition();
                    Title title = titles.get(position);
                    Integer titleTmdbID = title.getId();
                    Integer currentLikes = Integer.valueOf(String.valueOf(title.getLikes()));

                    // If the Title is currently liked by the User and they desire to unlike it
                    if (titleLiked) {
                        ivLike.setImageResource(R.drawable.ic_heart_empty); // Change to empty heart
                        title.setLikes(currentLikes - 1); // Decrease the likes for the Title (as displayed to the User) by 1


                    } else {  // Title is currently not liked by the User and they desire to like it
                        ivLike.setImageResource(R.drawable.ic_heart_filled); // Change to filled-in heart
                        title.setLikes(currentLikes + 1); // Increment the likes for the Title (as displayed to the User) by 1

                    }

                    title.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(TAG, "Issue saving like action by user");
                            } else {
                                Log.i(TAG, "Success saving like action by user");
                            }
                        }
                    });
                    notifyDataSetChanged();
//                    notifyItemChanged(position);
                }
            });

            // User clicks on View
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Title title = titles.get(position);
                        Log.i(TAG, "The title is " + title.getName() + " /Type: " + title.getType() + " /TMDB ID: " + title.getId() + " /Object ID:" + title.getObjectId());
                        Intent intent;

                        // Determine where to send Intent based of the type associated with a Title
                        if (title.getType().equals("TV Show")) { // TV Show type
                            Log.i(TAG, "Type is: " + title.getType() + " for TV Show");
                            intent = new Intent(context, TvTitleDetailsActivity.class);
                        } else { // Movie type
                            Log.i(TAG, "Type is: " + title.getType() + " for Movies");
                            intent = new Intent(context, MovieTitleDetailsActivity.class);
                        }
                        intent.putExtra("id", title.getId());
                        intent.putExtra("name", title.getName());
                        intent.putExtra("posterPath", title.getPosterPath());
                        intent.putExtra("type", title.getType());
                        intent.putExtra("description", title.getDescription());
                        intent.putExtra("releaseDate", title.getReleaseDate());
                        context.startActivity(intent);
                        Log.i(TAG, "Opening TvTitleDetailsActivity w/ title: " + title + " name: " + title.getName() + " and TMDB ID: " + title.getId() + " at position: " + position + " within the list: " + titles.toString());
                    }
                }
            });
        }

        public void bind(Title title) {
            Log.i(TAG, "Binding for the title: " + title);
            tvName.setText(title.getName());
            tvDescription.setText(title.getDescription());
            //TODO: tvGenres.setText(title.getGenres);
            tvLikes.setText(String.valueOf(title.getLikes()));
            tvComments.setText(String.valueOf(0));
            tvShares.setText(String.valueOf(title.getShare()));

            // Handle unique User information about each Title (i.e Like status)
            handleTitleData(title, ivLike);

            Glide.with(context)
                    .load(title.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .into(ivCover);
        }
    }

    private void handleTitleData(Title title, ImageView ivLike) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.whereContains("titleLikes", String.valueOf(title.getId()));

        try {
            if (query.count() == 0) { // Returns 0 if a user has not liked a title
                titleLiked = false;
                ivLike.setImageResource(R.drawable.ic_heart_empty);
            } else { // If user has liked a title
                titleLiked = true;
                ivLike.setImageResource(R.drawable.ic_heart_filled);
            }
        } catch (ParseException e) {
            Log.d(TAG, "Issue getting title data for " + title.getName() + "for the user " + currentUser.getUsername());
            e.printStackTrace();
        }

        Log.i(TAG, "The title " + title.getName() + " is liked by the user " + currentUser.getUsername() + ": " + titleLiked);
    }
}
