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
import com.example.platform.models.Comment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileCommentsAdapter extends RecyclerView.Adapter<ProfileCommentsAdapter.ViewHolder>{

    public static final String TAG = "ProfileCommentsAdapter";

    private Context context;
    private List<Comment> comments;

    public ProfileCommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTitleCover;
        TextView tvUsername;
        TextView tvTimestamp;
        TextView tvCommentText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivTitleCover = itemView.findViewById(R.id.ivTitleCover_ProfileComment);
            tvUsername = itemView.findViewById(R.id.tvUsername_ProfileComment);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp_ProfileComment);
            tvCommentText = itemView.findViewById(R.id.tvCommentText_ProfileComment);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getUser());
            tvTimestamp.setText(comment.getTimestamp());
            tvCommentText.setText(comment.getText());
            Glide.with(context)
                    .load(comment.getCoverPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .into(ivTitleCover);
        }
    }
}