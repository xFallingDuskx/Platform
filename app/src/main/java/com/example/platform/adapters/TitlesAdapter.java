package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.platform.models.Comment;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
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
    HashMap<String, Object> userLikedTitles;


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

            // User clicks on Heart icon to like the title
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    handleUserLikeAction(position, ivLike);
                }
            });

            // User double-taps screen to like the title
            // Source: https://stackoverflow.com/questions/4804798/doubletap-in-android
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d(TAG, "onDoubleTap");
                        int position = getAdapterPosition();
                        handleUserLikeAction(position, ivLike);
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.d(TAG, "onSingleTap");
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
                            // TODO: Put extra - Share titleLiked status
                            intent.putExtra("id", title.getId());
                            intent.putExtra("name", title.getName());
                            intent.putExtra("posterPath", title.getPosterPath());
                            intent.putExtra("type", title.getType());
                            intent.putExtra("description", title.getDescription());
                            intent.putExtra("releaseDate", title.getReleaseDate());
                            context.startActivity(intent);
                            Log.i(TAG, "Opening TvTitleDetailsActivity w/ title: " + title + " name: " + title.getName() + " and TMDB ID: " + title.getId() + " at position: " + position + " within the list: " + titles.toString());
                        }
                        return super.onSingleTapUp(e);
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            // User clicks on View
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        Title title = titles.get(position);
//                        Log.i(TAG, "The title is " + title.getName() + " /Type: " + title.getType() + " /TMDB ID: " + title.getId() + " /Object ID:" + title.getObjectId());
//                        Intent intent;
//
//                        // Determine where to send Intent based of the type associated with a Title
//                        if (title.getType().equals("TV Show")) { // TV Show type
//                            Log.i(TAG, "Type is: " + title.getType() + " for TV Show");
//                            intent = new Intent(context, TvTitleDetailsActivity.class);
//                        } else { // Movie type
//                            Log.i(TAG, "Type is: " + title.getType() + " for Movies");
//                            intent = new Intent(context, MovieTitleDetailsActivity.class);
//                        }
//                        intent.putExtra("id", title.getId());
//                        intent.putExtra("name", title.getName());
//                        intent.putExtra("posterPath", title.getPosterPath());
//                        intent.putExtra("type", title.getType());
//                        intent.putExtra("description", title.getDescription());
//                        intent.putExtra("releaseDate", title.getReleaseDate());
//                        context.startActivity(intent);
//                        Log.i(TAG, "Opening TvTitleDetailsActivity w/ title: " + title + " name: " + title.getName() + " and TMDB ID: " + title.getId() + " at position: " + position + " within the list: " + titles.toString());
//                    }
//                }
//            });
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
        JSONObject jsonObject = currentUser.getJSONObject(User.KEY_LIKED_TITLES);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No titles currently liked by the user");
            userLikedTitles = new HashMap<>();
            titleLiked = false;
            ivLike.setImageResource(R.drawable.ic_heart_empty);
        } else {
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            //Convert Map to JSON
            try {
                userLikedTitles = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
            } catch (JsonProcessingException e) {
                Log.d(TAG, "Issue accessing tiles liked by user");
                e.printStackTrace();
            }

            Log.i(TAG, "The returned query is: " + userLikedTitles.toString());

            if (!userLikedTitles.containsKey(String.valueOf(title.getId()))) { // If a user has not liked a title
                titleLiked = false;
                ivLike.setImageResource(R.drawable.ic_heart_empty);
            } else { // If user has liked a title
                titleLiked = true;
                ivLike.setImageResource(R.drawable.ic_heart_filled);
            }
        }

        Log.i(TAG, "The title " + title.getName() + " is liked by the user " + currentUser.getUsername() + ": " + titleLiked);
    }

    private void handleUserLikeAction(int position, ImageView ivLike) {
        Log.i(TAG, "Entering onClickListener for likes");

        Title title = titles.get(position);
        Log.i(TAG, "The title " + title.getName() + " has a position of: " + position);

        Integer titleTmdbID = title.getId();
        Integer currentLikes = Integer.valueOf(String.valueOf(title.getLikes()));

        // If the Title is currently liked by the User and they desire to unlike it
        if (titleLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_empty); // Change to empty heart
            title.setLikes(currentLikes - 1); // Decrease the likes for the Title (as displayed to the User) by 1
            userLikedTitles.remove(String.valueOf(titleTmdbID)); // Remove title based on its TMDB ID #
            currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
            Log.i(TAG, "User " + currentUser.getUsername() + " has disliked the title: " + title.getName());
        } else {  // Title is currently not liked by the User and they desire to like it
            ivLike.setImageResource(R.drawable.ic_heart_filled); // Change to filled-in heart
            title.setLikes(currentLikes + 1); // Increment the likes for the Title (as displayed to the User) by 1
            userLikedTitles.put(String.valueOf(titleTmdbID), 0); // Add title based on its TMDB ID #
            currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
            Log.i(TAG, "User " + currentUser.getUsername() + " has liked the title: " + title.getName());
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "User: Issue saving like action by user " + e.getMessage());
                } else {
                    Log.i(TAG, "User: Success saving like action by user");
                }
            }
        });

        title.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Title: Issue saving like action by user " + e.getMessage());
                } else {
                    Log.i(TAG, "Title: Success saving like action by user");
                }
            }
        });
        Log.i(TAG, "Title currently liked by the user after clicking are: " + currentUser.getMap(User.KEY_LIKED_TITLES));
        notifyDataSetChanged();
//        notifyItemChanged(position);
    }
}
