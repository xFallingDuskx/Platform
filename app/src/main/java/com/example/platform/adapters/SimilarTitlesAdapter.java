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
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Title;
import com.example.platform.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimilarTitlesAdapter extends RecyclerView.Adapter<SimilarTitlesAdapter.ViewHolder>{

    public static final String TAG = "SimilarTitlesAdapter";

    private Context context;
    private List<Title> titles;
    User currentUser = new User();


    public SimilarTitlesAdapter(Context context, List<Title> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Change view being used depending on whether the title is a TV Show or Movie
        View view;
        if (titles.get(0).getType().equals("TV Show")) {
            view = LayoutInflater.from(context).inflate(R.layout.item_title_similar_tv, parent, false);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_title_similar_movie, parent, false);
        }
        return new SimilarTitlesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Title title = titles.get(position);
        holder.bind(title);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivTitleCover;
        public TextView tvTitleName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // Change reference depending on whether the title is a TV Show or Movie
            if (titles.get(0).getType().equals("TV Show")) {
                ivTitleCover = itemView.findViewById(R.id.ivTitleCover_Similar_TV);
                tvTitleName = itemView.findViewById(R.id.tvTitleName_Similar_TV);
            } else {
                ivTitleCover = itemView.findViewById(R.id.ivTitleCover_Similar_Movie);
                tvTitleName = itemView.findViewById(R.id.tvTitleName_Similar_Movie);
            }

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
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            tvTitleName.setText(title.getName());

            // Change cover image depending on whether the title is a TV Show or Movie
            if (title.getType().equals("TV Show")) {
                Glide.with(context)
                        .load(title.getBackdropPath())
                        .placeholder(R.drawable.backdrop_placeholder)
                        .fitCenter()
                        .into(ivTitleCover);
            } else {
                Glide.with(context)
                        .load(title.getPosterPath())
                        .placeholder(R.drawable.poster_placeholder)
                        .fitCenter()
                        .into(ivTitleCover);
            }
        }
    }
}
