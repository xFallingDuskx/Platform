package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.activities.EpisodeDetailsActivity;
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Episode;
import com.example.platform.models.Title;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileFollowingsAdapter extends RecyclerView.Adapter<ProfileFollowingsAdapter.ViewHolder>{

    public static final String TAG = "ProfileFollowingsAdapter";
    Context context;
    List<Title> titles;

    public ProfileFollowingsAdapter(Context context, List<Title> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_title_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileFollowingsAdapter.ViewHolder holder, int position) {
        Title title = titles.get(position);
        holder.bind(title);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTitleCover;
        TextView tvTitleName;
        TextView tvTitleDescription;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivTitleCover = itemView.findViewById(R.id.ivCover_Search);
            tvTitleName = itemView.findViewById(R.id.tvName_Search);
            tvTitleDescription = itemView.findViewById(R.id.tvDescription_Search);

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
                        if (! title.getType().equals("episode")) {
                            if (title.getType().equals("tv")) { // TV Show type
                                Log.i(TAG, "Type is: " + title.getType() + " for TV Show");
                                intent = new Intent(context, TvTitleDetailsActivity.class);
                            } else { // Movie type
                                Log.i(TAG, "Type is: " + title.getType() + " for Movies");
                                intent = new Intent(context, MovieTitleDetailsActivity.class);
                            }
                            intent.putExtra("posterPath", title.getPosterPath());
                        } else { // Episode type
                            Log.i(TAG, "Type is: " + title.getType() + " for Episodes");
                            intent = new Intent(context, EpisodeDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("cover", title.getPosterPath());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", title.getId());
                        intent.putExtra("name", title.getName());
                        intent.putExtra("type", title.getType());
                        intent.putExtra("description", title.getDescription());
                        intent.putExtra("releaseDate", title.getReleaseDate());
                        context.startActivity(intent);
                        Log.i(TAG, "Opening DetailsActivity w/ title: " + title + " name: " + title.getName() + " and TMDB ID: " + title.getId() + " at position: " + position + " within the list: " + titles.toString());

                    }
                }
            });
        }

        public void bind(Title title) {
            Log.i(TAG, "Binding for the title: " + title);
            tvTitleName.setText(title.getName());
            tvTitleDescription.setText(title.getDescription());

            // Change view if 'title' is an episode
            if (title.getType().equals("episode")) {
                Log.i(TAG, "Episode Glide is being used");
                Glide.with(context)
                        .load(title.getPosterPath())
                        .placeholder(R.drawable.platform_backdrop_placeholder)
                        .fitCenter()
                        .into(ivTitleCover);
            } else {
                Glide.with(context)
                        .load(title.getPosterPath())
                        .placeholder(R.drawable.platform_portrait_placeholder)
                        .centerCrop() // scale image to fill the entire ImageView
                        .into(ivTitleCover);
            }
        }
    }
}
