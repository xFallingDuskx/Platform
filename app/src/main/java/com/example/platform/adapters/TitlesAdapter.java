package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.activities.MovieTitleDetailsActivity;
import com.example.platform.activities.TvTitleDetailsActivity;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

public class TitlesAdapter extends RecyclerView.Adapter<TitlesAdapter.ViewHolder>{

    public static final String TAG = "TitlesAdapter";

    private Context context;
    private List<Title> titles;
    ParseUser currentUser = ParseUser.getCurrentUser();
    Boolean titleLiked = false;
    HashMap<String, Object> userLikedTitles;


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
        public ImageView ivLike;
        public ImageView ivComment;
        public ImageView ivShare;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.ivCover_Home);

            tvName = itemView.findViewById(R.id.tvName_Home);
            tvDescription = itemView.findViewById(R.id.tvDescription_Home);
            ivLike = itemView.findViewById(R.id.ivLike_Home);
            ivComment = itemView.findViewById(R.id.ivComment_Home);
            ivShare = itemView.findViewById(R.id.ivShare_Home);

            // User clicks on Heart icon to like the title
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    handleUserLikeAction(position, ivLike);
                }
            });

            // User double-taps screen to like the title
            // Source: https://stackoverflow.com/questions/4804798/doubletap-in-android
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.i(TAG, "onDoubleTap");
                        int position = getAdapterPosition();
                        handleUserLikeAction(position, ivLike);
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Log.i(TAG, "onSingleTap");
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Title title = titles.get(position);
                            Log.i(TAG, "The title is " + title.getName() + " /Type: " + title.getType() + " /TMDB ID: " + title.getId());
                            Intent intent;

                            // Determine where to send Intent based of the type associated with a Title
                            if (title.getType().equals("TV Show")) { // TV Show type
                                Log.i(TAG, "Type is: " + title.getType() + " for TV Show");
                                intent = new Intent(context, TvTitleDetailsActivity.class);
                            } else { // Movie type
                                Log.i(TAG, "Type is: " + title.getType() + " for Movies");
                                intent = new Intent(context, MovieTitleDetailsActivity.class);
                            }
                            // Put extra - Share titleLiked status
                            intent.putExtra("id", title.getId());
                            intent.putExtra("name", title.getName());
                            intent.putExtra("posterPath", title.getPosterPath());
                            intent.putExtra("type", title.getType());
                            intent.putExtra("description", title.getDescription());
                            intent.putExtra("releaseDate", title.getReleaseDate());
                            intent.putExtra("titleLiked", titleLiked);
                            intent.putExtra("userLikedTitles", userLikedTitles);
                            context.startActivity(intent);
                            Log.i(TAG, "Opening TvTitleDetailsActivity w/ title: " + title + " name: " + title.getName() + " and TMDB ID: " + title.getId() + " at position: " + position + " with like status: " + titleLiked);
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

            // User clicks on share icon
            // Source: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "clicked on Share icon");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Title title = titles.get(position);
                        String shareText = title.getName() + System.lineSeparator() + System.lineSeparator() + title.getDescription();

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ivCover.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImageandText(bitmap, shareText);
                    }
                }
            });

        }

        public void bind(Title title) {
            Log.i(TAG, "Binding for the title: " + title);
            tvName.setText(title.getName());
            tvDescription.setText(title.getDescription());

            // Handle unique User information about each Title (i.e Like status)
            handleTitleData(title, ivLike);

            Glide.with(context)
                    .load(title.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .centerCrop() // scale image to fill the entire ImageView
                    .into(ivCover);

            // Change slide in effect on title cover depending on its position
            // Source: https://stackoverflow.com/questions/31562833/how-to-slide-an-imageview-from-left-to-right-smoothly-in-android
            // Source: https://stackoverflow.com/questions/5151591/android-left-to-right-slide-animation
            if (getAdapterPosition() % 2 == 0) { // title slides in from right to left if at an even position
                // Load the animation like this
                Animation animSlide = AnimationUtils.loadAnimation(context,
                        R.anim.right_to_left);

                // Start the animation like this
                ivCover.startAnimation(animSlide);
            } else { // title slides in from left to right if at an odd position
                // Load the animation like this
                Animation animSlide = AnimationUtils.loadAnimation(context,
                        R.anim.left_to_right);

                // Start the animation like this
                ivCover.startAnimation(animSlide);
            }

        }
    }

    private void handleTitleData(Title title, ImageView ivLike) {
        JSONObject jsonObject = currentUser.getJSONObject(User.KEY_LIKED_TITLES);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No titles currently liked by the user");
            userLikedTitles = new HashMap<>();
            titleLiked = false;
        } else {
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            //Convert Map to JSON
            try {
                userLikedTitles = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
            } catch (JsonProcessingException e) {
                Log.d(TAG, "Issue accessing tiles liked by user");
                e.printStackTrace();
            }

            Log.i(TAG, "The returned query is: " + userLikedTitles.toString());

            if (userLikedTitles.containsKey(String.valueOf(title.getId()))) { // If a user has liked a title
                Log.i(TAG, "The title: " + title.getName() + " has an ID of: " + title.getId());
                titleLiked = true;
                ivLike.setImageResource(R.drawable.ic_heart_filled);
            } else { // If a user has not liked a title
                titleLiked = false;
                ivLike.setImageResource(R.drawable.ic_heart_empty);
            }
        }

        Log.i(TAG, "The title " + title.getName() + " is liked by the user " + currentUser.getUsername() + ": " + titleLiked);
    }

    private void handleUserLikeAction(int position, ImageView ivLike) {
        Log.i(TAG, "Entering onClickListener for likes");
        Title title = titles.get(position);
        Integer titleTmdbID = title.getId();

        // If the Title is currently liked by the User and they desire to unlike it
        if (titleLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_empty); // Change to empty heart
            userLikedTitles.remove(String.valueOf(titleTmdbID)); // Remove title based on its TMDB ID #
            currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
            titleLiked = false; // Title is no longer liked by the user
            Toast.makeText(context, context.getString(R.string.liked_title) + title.getName(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "User " + currentUser.getUsername() + " has disliked the title: " + title.getName());
        } else {  // Title is currently not liked by the User and they desire to like it
            ivLike.setImageResource(R.drawable.ic_heart_filled); // Change to filled-in heart
            userLikedTitles.put(String.valueOf(titleTmdbID), 0); // Add title based on its TMDB ID #
            currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
            titleLiked = true; // Title is now liked by the user
            Toast.makeText(context, context.getString(R.string.unliked_title) + title.getName(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "User " + currentUser.getUsername() + " has liked the title: " + title.getName());
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "User: Issue saving like action by user " + e.getMessage());
                } else {
                    Log.i(TAG, "User: Success saving like action by user");
                }
            }
        });
        Log.i(TAG, "Title currently liked by the user after clicking are: " + currentUser.getMap(User.KEY_LIKED_TITLES));
    }

    // To share image with text
    private void shareImageandText(Bitmap bitmap, String shareText) {
        Uri uri = getmageToShare(bitmap);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, "Sharing information for this title"));
    }

    // Retrieving the url to share
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Log.d(TAG, "Issue retrieving the url to share");
        }
        return uri;
    }
}

