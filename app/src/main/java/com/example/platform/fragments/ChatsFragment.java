package com.example.platform.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.activities.ProfileActivity;
import com.example.platform.models.Conversation;
import com.example.platform.models.User;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadata;
import com.pubnub.api.models.consumer.objects_api.channel.PNGetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNSetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNGetUUIDMetadataResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNSetUUIDMetadataResult;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    public static final String TAG = "ChatsFragment";
    private ImageView ivProfile;
    TextView tvNotAvailable;
    RelativeLayout rlComposePopUp;
    EditText etCompose;
    Button btnComposeConfirm;
    PubNub pubnub;
    List<Conversation> allConversations;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alert view that menu items exist
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.toolbar_chat_main, menu);

        // Allow user to start new chats
        MenuItem composeItem = menu.findItem(R.id.miCompose);
        composeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i(TAG, "onMenuItemClick to compose new chat");
                newComposePopUp();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_Chats);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = getActivity().findViewById(R.id.ivProfileIcon_Chats);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        tvNotAvailable = view.findViewById(R.id.tvNotAvailable_Chats);
        rlComposePopUp = view.findViewById(R.id.rlComposePopUp);
        etCompose = view.findViewById(R.id.etComposePopUp);
        btnComposeConfirm = view.findViewById(R.id.btnComposePopUp);

        // Connect to PubNub and establish a session with the user
        establishPubnubConnection();
        pubnub.getUUIDMetadata().async(new PNCallback<PNGetUUIDMetadataResult>() {
            @Override
            public void onResponse(@Nullable final PNGetUUIDMetadataResult result, @NotNull final PNStatus status) {
                if (status.isError()) {
                    Log.d(TAG, "Issue getting PN user data / Error: " + status.toString());

                    if (status.toString().contains("Requested object was not found")) { // If the user does not yet been saved in PubNub
                        createPubnubUser(pubnub);
                    }
                }
                else {
                    Log.i(TAG, "Success getting PN user data");
                    fetchUserConversations();
                }
            }
        });
    }

    public void establishPubnubConnection() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(ParseUser.getCurrentUser().getObjectId()); // Set the PubNub unique user ID as the User's Object ID in the Parse server
        pubnub =  new PubNub(pnConfiguration);
    }

    // Save user in PubNub
    public void createPubnubUser(PubNub pubnub) {
        pubnub.setUUIDMetadata()
                .uuid(ParseUser.getCurrentUser().getObjectId())
                .name(ParseUser.getCurrentUser().getUsername())
                .email(ParseUser.getCurrentUser().getEmail())
                .async(new PNCallback<PNSetUUIDMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetUUIDMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Issue create new Pubnub user object /Error: " + status.toString());
                        }
                        else {
                            Log.i(TAG, "Success creating new Pubnub user object");
                            establishPubnubConnection();
                        }
                    }
                });
    }

    public void fetchUserConversations() {
        List<String> conversations = pubnub.getSubscribedChannels();
        if (conversations.isEmpty()) {
            Log.i(TAG, "There are no conversations to display for the current user");
            tvNotAvailable.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "onTrack to display conversations for the current user");
            for (int i = 0; i < conversations.size(); i++) {
                String channel = conversations.get(i);
                pubnub.getChannelMetadata()
                        .channel(channel)
                        .async(new PNCallback<PNGetChannelMetadataResult>() {
                            @Override
                            public void onResponse(@Nullable final PNGetChannelMetadataResult result, @NotNull final PNStatus status) {
                                if (status.isError()) {
                                    Log.d(TAG, "Issue fetching user channel data");
                                } else {
                                    Conversation conversation = new Conversation();
                                    PNChannelMetadata data = result.getData();
                                    conversation.setId(data.getId());
                                    conversation.setName(data.getName());
                                    conversation.setDescription(data.getDescription());
                                    Log.i(TAG, "New conversation / ID: " + data.getId() + " / Name: " + data.getName() + " / Description: " + data.getDescription());
                                    allConversations.add(conversation);
                                }
                            }
                        });
            }
            // TODO: get unread messages count for each conversation (channel)
            // Source: https://www.pubnub.com/docs/chat/features/unread-counts
        }
    }

    public void newComposePopUp() {
        rlComposePopUp.setVisibility(View.VISIBLE);
        btnComposeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etCompose.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(getContext(), "Username field must not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        searchUser(username);
                    } catch (ParseException e) {
                        Log.d(TAG, "Issue searching for user to send chat to");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Search for the user in the Parse server
    private void searchUser(String username) throws ParseException {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereEqualTo(User.KEY_USERNAME, username);
        if (userQuery.count() == 0) { // if the user does not exist
            Toast.makeText(getContext(), "That user does not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser searchedUser = userQuery.getFirst();
        String searchedUserUsername = searchedUser.getUsername();
        String searchedUserObjectId = searchedUser.getObjectId();
        String channelName;

        // determine private channel name based upon their unique usernames - lower username will always be first
        String currentUserUsername = ParseUser.getCurrentUser().getUsername();
        if (currentUserUsername.compareTo(searchedUserUsername) > 0) {
            channelName = currentUserUsername + " & " + searchedUserUsername;
        } else {
            channelName = searchedUserUsername + " & " + currentUserUsername;
        }

        sendMessage(currentUserUsername, searchedUserUsername, searchedUserObjectId, channelName);
    }

    // Send greeting message to desired user
    private void sendMessage(String currentUserUsername, String searchedUserUsername, String searchedUserObjectId, String channelName) {
        // First create the channel
        pubnub.setChannelMetadata()
                .channel(channelName)
                .name(searchedUserUsername)
                .description("This is a private channel between " + currentUserUsername + " and " + searchedUserUsername)
                .async(new PNCallback<PNSetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            //handle error
                        } else {
                            //handle result
                        }
                    }
                });

        // Second subscribe the currentUser
        pubnub.subscribe()
                .channels(Arrays.asList(channelName))
                .withPresence()
                .execute();

        // Third subscribe the searchedUser
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(searchedUserObjectId);
        PubNub secondPubnub =  new PubNub(pnConfiguration);
        secondPubnub.subscribe()
                .channels(Arrays.asList(channelName))
                .withPresence()
                .execute();

        // Fourth send a greeting message to the searched user
        pubnub.publish()
                .message("Hey " + searchedUserUsername)
                .channel(channelName)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (!status.isError()) {
                            // message is sent
                            Long timetoken = result.getTimetoken(); // message timetoken
                        } else {
                            Log.d(TAG, "Issue sending greeting message to searched user");
                            Toast.makeText(getContext(), "Failed to send greeting message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Five close the pop up
        rlComposePopUp.setVisibility(View.GONE);
    }
}