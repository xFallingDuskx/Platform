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
import com.example.platform.R;
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

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
    User currentUser = new User();
    Map<Integer, Boolean> titlesLiked = new HashMap<>();


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
                    int position = getAdapterPosition();
                    Title title = titles.get(position);
                    Integer titleTmdbID = title.getId();

                    // If title does not exist in Map
                    if (! titlesLiked.containsKey(titleTmdbID)) {
                        titlesLiked.put(titleTmdbID, false);
                    }

                    // If the Title is currently not liked by the User
                    if (! titlesLiked.get(titleTmdbID)) {
                        ivLike.setImageResource(R.drawable.ic_heart_filled); // Change to filled-in heart
                        Integer currentLikes = Integer.valueOf(tvLikes.getText().toString());
                        tvLikes.setText(String.valueOf(currentLikes + 1)); // Increment the likes for the Title (as displayed to the User) by !

                        try {
                            currentUser.addTitleLike(titleTmdbID);
                        } catch (ParseException e) {
                            Log.i(TAG, "Issue trying to add title like");
                            e.printStackTrace();
                        }

                        titlesLiked.put(titleTmdbID, true);
                    } else {  // Title is currently liked by the User
                        ivLike.setImageResource(R.drawable.ic_heart_empty); // Change to empty heart
                        Integer currentLikes = Integer.valueOf(tvLikes.getText().toString());
                        tvLikes.setText(String.valueOf(currentLikes - 1)); // Decrease the likes for the Title (as displayed to the User) by !

                        try {
                            currentUser.removeTitleLike(titleTmdbID);
                        } catch (ParseException e) {
                            Log.i(TAG, "Issue trying to add title like");
                            e.printStackTrace();
                        }
                        titlesLiked.put(titleTmdbID, false);
                    }
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
                        intent.putExtra(Title.KEY_TMDB_ID, title.getId());
                        intent.putExtra(Title.KEY_NAME, title.getName());
                        intent.putExtra(Title.KEY_COVER_PATH, title.getCoverPath());
                        intent.putExtra(Title.KEY_TYPE, title.getType());
                        intent.putExtra(Title.KEY_DESCRIPTION, title.getDescription());
                        intent.putExtra(Title.KEY_RELEASE_DATE, title.getReleaseDate());
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
                    .load(title.getCoverPath())
                    //.placeholder(placeholder)
                    //.error(placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    //.transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivCover);
        }
    }

    private void handleTitleData(Title title, ImageView ivLike) {
        Integer titleTmdbID = title.getId();
        List<Integer> userLikedTitles;

        try {
            userLikedTitles = currentUser.getTitleLikes();
            if (userLikedTitles.contains(titleTmdbID)) {
                titlesLiked.put(titleTmdbID, true);
                ivLike.setImageResource(R.drawable.ic_heart_filled);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Issue getting User liked titles");
            e.printStackTrace();
        }
    }
}
