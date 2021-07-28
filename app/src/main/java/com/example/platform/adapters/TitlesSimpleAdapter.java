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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TitlesSimpleAdapter extends RecyclerView.Adapter<TitlesSimpleAdapter.ViewHolder>{

    public static final String TAG = "TitlesSimpleAdapter";

    private Context context;
    private List<Title> titles;

    public TitlesSimpleAdapter(Context context, List<Title> titles) {
        this.context = context;
        this.titles = titles;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_title_simple, parent, false);
        return new ViewHolder(view);
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

        ImageView ivCover;
        TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.ivCover_TitleSimple);
            tvName = itemView.findViewById(R.id.tvName_TitleSimple);
        }

        public void bind(Title title) {
            tvName.setText(title.getName());
            Glide.with(context)
                    .load(title.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .into(ivCover);
        }
    }
}
