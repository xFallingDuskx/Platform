package com.example.platform.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.adapters.PostsAdapter;
import com.example.platform.models.Community;
import com.example.platform.models.Post;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNFetchMessageItem;
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNGetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNSetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CommunityDetailsActivity extends AppCompatActivity {

    public static final String TAG = "CommunityDetailsActivity";
    Context context;
    Community community;
    List<String> members;
    String currentUser;
    boolean joined;
    TextView tvName, tvDescription, tvParticipationStatus, tvMembers;
    ImageView ivParticipationStatus, ivShare, ivCover;
    Button btnPost;
    EditText etPostInput;
    RelativeLayout rlMakePost;

    RecyclerView rvPosts;
    List<Post> allPosts;
    PostsAdapter adapter;
    PubNub pubnub;
    int numberOfMembers;

    ShimmerFrameLayout shimmerFrameLayout;
    ScrollView svEntireScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);
        context = getApplicationContext();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayoutCommunity);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }
        svEntireScreen = findViewById(R.id.svCommunityDetails);
        svEntireScreen.setVisibility(View.INVISIBLE);

        tvName = findViewById(R.id.tvName_Community_Details);
        tvDescription = findViewById(R.id.tvDescription_Community_Details);
        tvParticipationStatus = findViewById(R.id.tvParticipationStatusText_Community_Details);
        ivParticipationStatus = findViewById(R.id.ivParticipationStatus_Community_Details);
        ivShare = findViewById(R.id.ivShare_Community_Details);
        ivCover = findViewById(R.id.ivCover_Community_Details);
        btnPost = findViewById(R.id.btnPost_Community);
        tvMembers = findViewById(R.id.tvMembersText_Community_Details);
        etPostInput = findViewById(R.id.etPostInput_Community);
        rlMakePost = findViewById(R.id.rlMakePost_Community);
        rlMakePost.setVisibility(View.GONE);
        currentUser = ParseUser.getCurrentUser().getUsername();
        joined = false;

        community = Parcels.unwrap(getIntent().getParcelableExtra(Community.class.getSimpleName()));
        try {
            members = community.getMembers();
        } catch (JSONException e) {
            Log.d(TAG, "Issue getting the members for this community");
            e.printStackTrace();
        }
        Log.i(TAG, "Community details have been wrapped by Parcel");
        Log.i(TAG, "Opened CommunityDetailsActivity w/ community: " + community + " w/ name " + community.getName() + " and description of " + community.getDescription());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set community information
                setCommunityInformation();

                // Load the post
                displayPosts();


                // Handle post being made by user
                handlePosting();

                // Handle the user can share content
                handleShareAction();

                // Handle join action (when a user clicks on the following icon)
                try {
                    handleJoinAction();
                } catch (JSONException e) {
                    Log.d(TAG, "Issue handling user join action /Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    public void setCommunityInformation() {
        tvName.setText(community.getName());
        tvDescription.setText(community.getDescription());
        int numberOfMembers = community.getNumberOfMembers();
        if (numberOfMembers == 1) {
            tvMembers.setText(numberOfMembers + " Member");
        } else {
            tvMembers.setText(numberOfMembers + " Members");
        }

        if (members.contains(currentUser)) {
            ivParticipationStatus.setImageResource(R.drawable.ic_following_true);
            tvParticipationStatus.setText(R.string.joined);
            joined = true;
            rlMakePost.setVisibility(View.VISIBLE);
        }
        ParseFile image = community.getImage();
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .centerCrop()
                    .into(ivCover);
        } else {
            Glide.with(context)
                    .load(R.drawable.platform_backdrop_placeholder)
                    .centerCrop()
                    .into(ivCover);
        }

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        svEntireScreen.setVisibility(View.VISIBLE);
    }

    public void displayPosts() {
        rvPosts = findViewById(R.id.rvPosts_Community);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(context, allPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        rvPosts.setLayoutManager(linearLayoutManager);
        rvPosts.setAdapter(adapter);

        // Setup PubNub connection
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(ParseUser.getCurrentUser().getObjectId()); // Set the PubNub unique user ID as the User's Object ID in the Parse server
        pubnub =  new PubNub(pnConfiguration);

        // First check if channel currently exists for this community
        pubnub.getChannelMetadata()
                .channel(community.getName())
                .includeCustom(true)
                .async(new PNCallback<PNGetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNGetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Error getting channel data for this community -- may not exist on PubNub server / Error: " + status.toString());
                            createChannel();
                        } else {
                            Log.i(TAG, "A PubNub channel currently exists for this community");
                            fetchPosts();
                        }
                    }
                });

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                Log.i(TAG, "New message received");

                JsonObject messageObject = event.getMessage().getAsJsonObject();
                String user = messageObject.get(Post.KEY_USER).getAsString();
                String text = messageObject.get(Post.KEY_TEXT).getAsString();

                // Get timestamp
                // Source: https://www.pubnub.com/docs/chat/features/messages#receive-messages
                long timetoken = event.getTimetoken() / 10_000L;
                Date date = new Date(timetoken);
                String timestamp = Post.calculateTimeAgo(date);

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timetoken);
                String localDateTime = sdf.format(calendar.getTimeInMillis());

                Post post = new Post(user, text, timestamp);
                allPosts.add(post);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Go to recent message
                        adapter.notifyDataSetChanged();
                        rvPosts.smoothScrollToPosition(allPosts.size() - 1);
                    }
                });

                Log.i(TAG, "New Post / User: " + user + " / Text: " + text + " / Timestamp: " + timestamp);
            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {}

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {}

            @Override
            public void signal(PubNub pubnub, PNSignalResult event) {}

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) {}

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) {}

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) {}

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult event) {}

            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {}
        });

        pubnub.subscribe().channels(Arrays.asList(community.getName())).withPresence().execute();
    }

    public void createChannel() {
        String name = community.getName();
        pubnub.setChannelMetadata()
                .channel(name)
                .name(name)
                .description(community.getDescription())
                .async(new PNCallback<PNSetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Issue creating new channel for community / Error: " + status.toString());
                        } else {
                            Log.i(TAG, "Successfully created new channel for community");
                            fetchPosts();
                        }
                    }
                });
    }

    // Source: https://www.pubnub.com/docs/chat/features/message-history
    public void fetchPosts() {
        pubnub.fetchMessages()
                .channels(Arrays.asList(community.getName()))
                .maximumPerChannel(200)
                .async(new PNCallback<PNFetchMessagesResult>() {
                    @Override
                    public void onResponse(PNFetchMessagesResult result, PNStatus status) {
                        if (!status.isError()) {
                            final Map<String, List<PNFetchMessageItem>> channelToMessageItemsMap = result.getChannels();
                            final Set<String> channels = channelToMessageItemsMap.keySet();
                            for (final String channel : channels) {
                                List<PNFetchMessageItem> pnFetchMessageItems = channelToMessageItemsMap.get(channel);
                                for (final PNFetchMessageItem fetchMessageItem: pnFetchMessageItems) {

                                    // Ignore older messages that were not saved as a JsonObject
                                    if (! fetchMessageItem.getMessage().isJsonObject()) {
                                        continue;
                                    }

                                    JsonObject messageObject = fetchMessageItem.getMessage().getAsJsonObject();
                                    String user = messageObject.get(Post.KEY_USER).getAsString();
                                    String text = messageObject.get(Post.KEY_TEXT).getAsString();

                                    // Get timestamp
                                    // Source: https://www.pubnub.com/docs/chat/features/messages#receive-messages
                                    long timetoken = fetchMessageItem.getTimetoken() / 10_000L;
                                    Date date = new Date(timetoken);
                                    String timestamp = Post.calculateTimeAgo(date);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(timetoken);
                                    String localDateTime = sdf.format(calendar.getTimeInMillis());

                                    Post post = new Post(user, text, timestamp);
                                    allPosts.add(post);
                                    Log.i(TAG, "New Post / User: " + user + " / Text: " + text + " / Timestamp: " + timestamp);
                                }
                            }

                            // Go to recent posts
                            adapter.notifyDataSetChanged();
                            rvPosts.smoothScrollToPosition(0);
                        } else {
                            Log.d(TAG, "Issue fetching previous posts");
                            status.getErrorData().getThrowable().printStackTrace();
                        }
                    }
                });
    }

    public void handlePosting() {
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etPostInput.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(context, "Cannot make an empty post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "onTrack to publish post by user");
                JsonObject messagePayload = Post.createPostObject(currentUser, message);
                publishMessage(messagePayload);
            }
        });
    }

    private void publishMessage(JsonObject messagePayload) {
        pubnub.publish()
                .message(messagePayload)
                .channel(community.getName())
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (!status.isError()) {
                            Log.i(TAG, "Successfully sent new  message from original user");
                            etPostInput.setText("");
                        } else {
                            Log.d(TAG, "Issue sending greeting message to searched user");
                            Toast.makeText(context, "Failed to send greeting message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Close the keyboard
        // Source: https://gist.github.com/lopspower/6e20680305ddfcb11e1e
        View view = findViewById(android.R.id.content);
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // User clicks on share icon
    // Source: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
    public void handleShareAction() {
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = getString(R.string.community_share_message) + System.lineSeparator() + System.lineSeparator() + community.getName() + System.lineSeparator() + System.lineSeparator() + community.getDescription();

                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivCover.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap, shareText);
            }
        });
    }

    // If user taps on the join icon
    public void handleJoinAction() throws JSONException {
        ivParticipationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // If the user is part of the community and wishes to leave it
                if (joined) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(CommunityDetailsActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog.setTitleText("Are you sure?")
                            .setContentText("If you leave this community, you will no longer be able to make post with your fellow Platform users.")
                            .setCancelText("Nevermind")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                }
                            })
                            .setConfirmText("Yes, I'm sure")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Log.i(TAG, "User desires to leave this community");
                                    members.remove(currentUser);
                                    updateParseServer(members);

                                    ivParticipationStatus.setImageResource(R.drawable.ic_following_false);
                                    tvParticipationStatus.setText(R.string.not_joined);
                                    Log.i(TAG, "Current members after initial removal are: " + Arrays.toString(members.toArray()));
                                    joined = false;
                                    rlMakePost.setVisibility(View.GONE);
                                    numberOfMembers = community.getNumberOfMembers() - 1;
                                    if (numberOfMembers == 1) {
                                        tvMembers.setText(numberOfMembers + " Member");
                                    } else {
                                        tvMembers.setText(numberOfMembers + " Members");
                                    }

                                    SweetAlertDialog sweetAlertTemp = new SweetAlertDialog(CommunityDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertTemp.setTitleText("Change successful")
                                            .setContentText("You are no longer are member of " + community.getName())
                                            .show();
                                    sweetAlertDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Log.i(TAG, "User desires to join this community");
                    members.add(currentUser);
                    updateParseServer(members);

                    ivParticipationStatus.setImageResource(R.drawable.ic_following_true);
                    tvParticipationStatus.setText(R.string.joined);
                    joined = true;
                    rlMakePost.setVisibility(View.VISIBLE);
                    numberOfMembers = community.getNumberOfMembers() + 1;
                    if (numberOfMembers == 1) {
                        tvMembers.setText(numberOfMembers + " Member");
                    } else {
                        tvMembers.setText(numberOfMembers + " Members");
                    }
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(CommunityDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("Successfully joined")
                            .setContentText("You are now a member of " + community.getName())
                            .setConfirmClickListener(null)
                            .show();

                }


            }
        });
    }

    public void updateParseServer(List<String> members) {
        // Change and update the user following titles within the parse server
        ParseQuery<Community> query = ParseQuery.getQuery("Community");

        // Retrieve the community by id
        query.getInBackground(community.getObjectId(), (community, e) -> {
            Log.d(TAG, "Current members at Parse save are: " + Arrays.toString(members.toArray()));
            if (e == null) {
                community.put(Community.KEY_MEMBERS, new JSONArray(members));
                community.put(Community.KEY_NUMBER_OF_MEMBERS, members.size());
                community.saveInBackground();
                Log.i(TAG, "Success changing the participation status of the current user");
            } else {
                Log.d(TAG, "Issue changing the participation status of the current user");

                // Reverse user action
                if (joined) { // If user desired to join the community, but there was an issue doing so
                    Log.i(TAG, "User was unable to join the community due to issue");
                    ivParticipationStatus.setImageResource(R.drawable.ic_following_false);
                    tvParticipationStatus.setText(R.string.not_joined);
                    this.members.remove(currentUser);
                    Log.i(TAG, "Current members after reverse removal are: " + Arrays.toString(members.toArray()));
                    joined = false;
                    rlMakePost.setVisibility(View.GONE);
                    numberOfMembers = community.getNumberOfMembers() - 1;
                    if (numberOfMembers == 1) {
                        tvMembers.setText(numberOfMembers + " Member");
                    } else {
                        tvMembers.setText(numberOfMembers + " Members");
                    }
                } else {
                    Log.i(TAG, "User was unable to leave the community due to issue");
                    ivParticipationStatus.setImageResource(R.drawable.ic_following_true);
                    tvParticipationStatus.setText(R.string.joined);
                    this.members.add(currentUser);
                    joined = true;
                    rlMakePost.setVisibility(View.VISIBLE);
                    numberOfMembers = community.getNumberOfMembers() + 1;
                    if (numberOfMembers == 1) {
                        tvMembers.setText(numberOfMembers + " Member");
                    } else {
                        tvMembers.setText(numberOfMembers + " Members");
                    }
                }
            }
        });
    }

    // To share image with text
    private void shareImageandText(Bitmap bitmap, String shareText) {
        Uri uri = getmageToShare(bitmap);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, "Sharing information for this title"));
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