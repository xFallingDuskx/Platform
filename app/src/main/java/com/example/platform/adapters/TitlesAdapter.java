package com.example.platform.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.models.Title;

import java.util.List;

public class TitlesAdapter extends RecyclerView.Adapter<TitlesAdapter.ViewHolder>{

    public static final String TAG = "TitlesAdapter";

    private Context context;
    private List<Title> titles;

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
        }

        public void bind(Title title) {
            //TODO: ivCover
            tvName.setText(title.getName());
            tvDescription.setText(title.getDescription());
            //TODO: tvGenres.setText(title.getGenres);
            tvLikes.setText(title.getLikes().toString());
            tvComments.setText(String.valueOf(0));
            tvShares.setText(title.getShare().toString());

            Glide.with(context)
                    .load(title.getPosterPath())
                    //.placeholder(placeholder)
                    //.error(placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    //.transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivCover);
        }
    }
}
