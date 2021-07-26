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
import com.example.platform.models.Episode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>{

    public static final String TAG = "EpisodeAdapter";

    private Context context;
    private List<Episode> episodes;


    public EpisodesAdapter(Context context, List<Episode> episodes) {
        this.context = context;
        this.episodes = episodes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
        return new EpisodesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivEpisodeCover;
        public TextView tvEpisodeName;
        public TextView tvSeasonAndEpisode;
        public TextView tvEpisodeDetails;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivEpisodeCover = itemView.findViewById(R.id.ivEpisodeCover_TV_Show);
            tvEpisodeName = itemView.findViewById(R.id.tvEpisodeName_TV_Show);
            tvSeasonAndEpisode = itemView.findViewById(R.id.tvEpisodeSE_TV_Show);
            tvEpisodeDetails = itemView.findViewById(R.id.tvEpisodeDetails_TV_Show);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Episode episode = episodes.get(position);
                        Intent intent = new Intent(context, EpisodeDetailsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("cover", episode.getStillPath());
                        intent.putExtra("name", episode.getName());
                        intent.putExtra("description", episode.getDescription());
                        intent.putExtra("releaseDate", episode.getReleaseDate());
                        intent.putExtra("id", episode.getId());
                        context.startActivity(intent);
                        Log.i(TAG, "Opening EpisodeDetailsActivity w/ episode: " + episode + " name: " + episode.getName() + " and TMDB ID: " + episode.getId() + " at position: " + position + " within the list: " + episodes.toString());
                    }
                }
            });
        }

        public void bind(Episode episode) {
            Log.i(TAG, "Binding for the episode: " + episode);
            tvEpisodeName.setText(episode.getName());
            String seasonAndEpisode = "- S" + episode.getSeasonNumber() + " E" + episode.getEpisodeNumber();
            tvSeasonAndEpisode.setText(seasonAndEpisode);
            tvEpisodeDetails.setText(episode.getDescription());


            Glide.with(context)
                    .load(episode.getStillPath())
                    .placeholder(R.drawable.backdrop_placeholder)
                    .fitCenter()
                    .into(ivEpisodeCover);
        }
    }
}
