package com.example.platform.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.activities.EpisodeDetailsActivity;
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.ProfileCommentsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Comment;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

            // User double-taps screen to like the title
            // Source: https://stackoverflow.com/questions/4804798/doubletap-in-android
            // Solved 'Unable to add window' error: https://stackoverflow.com/questions/18755847/dialog-in-custom-adapter-in-android
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        Log.i(TAG, "onLongPress");
                        // Give user the option to delete the comment
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Comment comment = comments.get(position);
                            String commentObjectId = comment.getObjectId();
                            // Source: https://developer.android.com/guide/topics/ui/dialogs
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Delete Comment?")
//                                .setMessage("Keep in mind that, once a comment has been deleted, it can be recovered.")
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        Log.i(TAG, "User is onTrack to delete the comment");
//                                        // TODO: add necessary logic
//                                    }
//                                })
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        Log.i(TAG, "User has decided not to delete the comment");
//                                    }
//                                })
//                                .create()
//                                .show();

                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Delete Comment?")
                                    .setContentText("Keep in mind that, once a comment has been deleted, it can be recovered.")

                                    .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.i(TAG, "User is onTrack to delete the comment");
                                            // Change SweetAlert to loading type
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                                            sweetAlertDialog.setTitleText("Deleting...");
                                            sweetAlertDialog.setContentText("Your comment is being deleted");
                                            sweetAlertDialog.setCancelable(false);

                                            deleteComment(commentObjectId);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemRemoved(position);

                                                    sweetAlertDialog.setTitleText("Deleted!")
                                                            .setContentText("Your comment has been successfully deleted!")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(null)
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                            }, 8000);
                                        }
                                    })
                                    .show();
                        }
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Log.i(TAG, "onSingleTap");
                        // Go to detailed view w intent to scroll to comments
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Comment comment = comments.get(position);
                            Log.i(TAG, "The comment by " + comment.getUser() + " /Text: " + comment.getText() + " /Item Name: " + comment.getName() + " /Item TMDB ID: " + comment.getTmdbId() + " / Item Type: " + comment.getType());
                            Intent intent;

                            // Determine where to send Intent based of the type associated with a Title
                            if (! comment.getType().equals("episode")) {
                                if (comment.getType().equals("tv")) { // TV Show type
                                    Log.i(TAG, "Type is: " + comment.getType() + " for TV Show");
                                    intent = new Intent(context, TvTitleDetailsActivity.class);
                                } else { // Movie type
                                    Log.i(TAG, "Type is: " + comment.getType() + " for Movies");
                                    intent = new Intent(context, MovieTitleDetailsActivity.class);
                                }
                                intent.putExtra("posterPath", comment.getCoverPath());
                                intent.putExtra("scrollToComments", true);
                            } else { // Episode type
                                Log.i(TAG, "Type is: " + comment.getType() + " for Episodes");
                                intent = new Intent(context, EpisodeDetailsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("cover", comment.getEpisodeCoverPath());
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("id", comment.getTmdbId());
                            intent.putExtra("name", comment.getName());
                            intent.putExtra("type", comment.getType());
                            intent.putExtra("description", comment.getDescription());
                            intent.putExtra("releaseDate", comment.getReleaseDate());
                            context.startActivity(intent);
                            Log.i(TAG, "Opening DetailsActivity w/ comment: " + comment + " name: " + comment.getName() + " and TMDB ID: " + comment.getTmdbId() + " at position: " + position + " within the list: " + comments.toString());

                        }
                        return super.onSingleTapUp(e);
                    }


                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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

    public void deleteComment(String commentObjectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");

        // Retrieve the comment by its object id
        query.getInBackground(commentObjectId, (object, e) -> {
            if (e == null) {
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Log.i(TAG, "Comment was successfully deleted from Parse Server");
                    }else{
                        Log.d(TAG, "Failed to delete comment from Parse Server / Error: " + e2.getMessage());
                    }
                });
            } else{
                Log.d(TAG, "Failed to get comment in background to delete / Error: " + e.getMessage());
            }
        });
    }
}