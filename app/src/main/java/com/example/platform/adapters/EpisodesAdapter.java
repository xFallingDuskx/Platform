package com.example.platform.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.platform.R;
import com.example.platform.models.Episode;
import com.example.platform.models.Title;
import com.example.platform.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//                    .apply(new RequestOptions().transforms(new RoundedCorners(10)))
                    .into(ivEpisodeCover);
        }
    }
}
