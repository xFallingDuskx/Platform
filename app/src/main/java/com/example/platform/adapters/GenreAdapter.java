package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.activities.CatalogActivity_TitleDisplay;
import com.example.platform.models.Genre;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder>{

    public static final String TAG = "GenreAdapter";

    private Context context;
    private List<Genre> genres;
    FragmentManager fragmentManager;

    public GenreAdapter(Context context, List<Genre> genres) {
        this.context = context;
        this.genres = genres;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.bind(genre);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvGenreName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Genre genre = genres.get(position);
                        Intent intent = new Intent(context, CatalogActivity_TitleDisplay.class);
                        intent.putExtra("id", genre.getId());
                        intent.putExtra("type", genre.getType());
                        intent.putExtra("objective", "genre");
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Genre genre) {
            tvName.setText(genre.getName());
        }
    }
}
