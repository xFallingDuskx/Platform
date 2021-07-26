package com.example.platform.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.models.Comment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    public static final String TAG = "CommentsAdapter";

    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
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

        TextView tvUsername;
        TextView tvTimestamp;
        TextView tvCommentText;
        TextView tvLikes;
        TextView tvReplies;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername_Comment);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvCommentText = itemView.findViewById(R.id.tvCommentText_Comment);
            tvLikes = itemView.findViewById(R.id.tvLikes_Comments);
            tvReplies = itemView.findViewById(R.id.tvReplies_Comments);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getUser());
            tvTimestamp.setText(comment.getTimestamp());
            tvCommentText.setText(comment.getText());
            tvLikes.setText(String.valueOf(comment.getLikes()));
            tvReplies.setText(String.valueOf(comment.getReplies()));
        }
    }
}
