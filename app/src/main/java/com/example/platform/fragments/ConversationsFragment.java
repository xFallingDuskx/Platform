package com.example.platform.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.activities.ProfileActivity;
import com.example.platform.adapters.ConversationsAdapter;
import com.example.platform.models.Conversation;
import com.example.platform.models.Message;
import com.example.platform.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNSetChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNGetUUIDMetadataResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNSetUUIDMetadataResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ConversationsFragment extends Fragment {

    public static final String TAG = "ConversationsFragment";
    private ImageView ivProfile;
    TextView tvNotAvailable;
    RelativeLayout rlComposePopUp;
    EditText etCompose;
    Button btnComposeConfirm;
    Button btnCompseCancel;
    PubNub pubnub;

    boolean update = false;

    HashSet<String> currentConversationNames;
    List<Conversation> allConversations;
    RecyclerView rvConversations;
    ConversationsAdapter adapter;

    ShimmerFrameLayout shimmerFrameLayout;

    public ConversationsFragment() {
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
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.toolbar_conversation_main, menu);

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
    public void onStop() {
        Log.i(TAG, "onStop called");
        update = true;
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume called / Update: " + update);
        if (update) {
            try {
                subscribeToChannels();
            } catch (JSONException e) {
                Log.d(TAG, "Issue subscribing to channels again on update");
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_Conversations);
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

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout_Conversations);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        tvNotAvailable = view.findViewById(R.id.tvNotAvailable_Chats);
        rlComposePopUp = view.findViewById(R.id.rlComposePopUp);
        etCompose = view.findViewById(R.id.etComposePopUp);
        btnComposeConfirm = view.findViewById(R.id.btnComposePopUp);
        btnCompseCancel = view.findViewById(R.id.btnCancelPopUp);
        rvConversations = view.findViewById(R.id.rvConversations);

        // Connect to PubNub and establish a session with the user
        establishPubnubConnection();
        // Necessary listeners
        establishListeners();
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

                    try {
                        subscribeToChannels();
                    } catch (JSONException e) {
                        Log.d(TAG, "Issue subscribing user to their conversations (channels) / Error: " + e.getMessage());
                        e.printStackTrace();
                    }
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

    // Users must be resubscribed to their channels each time the app loads
    // Channels names are received from Parse Server
    public void subscribeToChannels() throws JSONException {
        // Initialize list to hold user conversations and hashset to hold the conversation names
        allConversations = new ArrayList<>();
        currentConversationNames = new HashSet<>();

        // Query for all channels the current user is a member of
        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseObject> conversationQuery = ParseQuery.getQuery("Conversation").whereEqualTo(Conversation.KEY_MEMBERS, currentUser);
        conversationQuery.orderByDescending(Conversation.KEY_UPDATED_AT);

        // Get the name of all of the channels
        List<String> allChannelNames = new ArrayList<>();
        conversationQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Issue querying for the current user's conversations");
                } else {
                    Log.i(TAG, "Success querying for the current user's conversations");

                    if (parseObjects.size() == 0) {
                        Log.i(TAG, "Current user does not have any conversations");
                    } else {
                        Log.i(TAG, "Current user has conversations to subscribe to");
                        for (ParseObject parseObject : parseObjects) {
                            // Save channel name to subscribe to it
                            String channelName = parseObject.getString(Conversation.KEY_NAME);
                            String channelLastMessage = parseObject.getString(Conversation.KEY_LAST_MESSAGE);
                            allChannelNames.add(channelName);

                            // create new Conversation object using 3 bits of information
                            String visibleName = Conversation.convertToVisible(channelName, currentUser);
                            currentConversationNames.add(visibleName);
                            Conversation conversation = new Conversation();
                            conversation.setName(channelName);
                            conversation.setVisibleName(visibleName);
                            conversation.setLastMessage(channelLastMessage);
                            conversation.setTimestamp(parseObject.getUpdatedAt());
                            allConversations.add(conversation);
                            Log.i(TAG, "Success: conversation " + conversation.getVisibleName() + " has been added");
                            Log.i(TAG, "The updatedAt Date for this conversation is: " + parseObject.getUpdatedAt().toString());
                            // TODO: get unread messages count for each conversation (channel) ?
                            // Source: https://www.pubnub.com/docs/chat/features/unread-counts
                        }
                    }

                    // Connect to Pubnub and subscribe to channels
                    pubnub.subscribe()
                            .channels(allChannelNames)
                            .withPresence()
                            .execute();
                    Log.i(TAG, "Current user has been subscribed to their channels");

                    displayConversations();
                }
            }
        });
    }

    public void displayConversations() {
        for (Conversation conversation : allConversations) {
            Log.i(TAG, "Conversation: " + conversation.getVisibleName());
        }

        adapter = new ConversationsAdapter(getActivity(), allConversations, currentConversationNames);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvConversations.setLayoutManager(linearLayoutManager);
        rvConversations.setAdapter(adapter);

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    public void newComposePopUp() {
        rlComposePopUp.setVisibility(View.VISIBLE);

        btnComposeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etCompose.getText().toString();

                if (adapter.currentConversationNames.contains(username)) {
                    Toast.makeText(getContext(), "You already have a conversation with this user", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Username to be searched is " + username);
                if (username.isEmpty()) {
                    Toast.makeText(getContext(), "Username field must not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.i(TAG, "onTrack to searchUser by given username");
                        searchUser(username);
                    } catch (ParseException e) {
                        Log.d(TAG, "Issue searching for user to send chat to / Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        btnCompseCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlComposePopUp.setVisibility(View.GONE);
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

        // determine private channel name based upon their unique usernames - 'higher' (occurring later alphabetically) username will always be first
        String currentUserUsername = ParseUser.getCurrentUser().getUsername();
        if (currentUserUsername.compareTo(searchedUserUsername) > 0) {
            channelName = currentUserUsername + "&" + searchedUserUsername;
        } else {
            channelName = searchedUserUsername + "&" + currentUserUsername;
        }

        Log.i(TAG, "Searched User -- Username: " + searchedUserUsername + " / Object ID: " + searchedUserObjectId + " / Channel Name: " + channelName);

        // Check if the user has been part of this conversation before
        checkChannel(currentUserUsername, searchedUserUsername, searchedUserObjectId, channelName, searchedUserUsername);
    }

    private void checkChannel(String currentUserUsername, String searchedUserUsername, String searchedUserObjectId, String channelName, String userUsername) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversation");
        query.whereContains(Conversation.KEY_NAME, channelName);

        // If the channel does not exist
        if (query.count() == 0) {
            sendMessage(currentUserUsername, searchedUserUsername, searchedUserObjectId, channelName, searchedUserUsername);
        } else {
            try {
                rejoinConversation(currentUserUsername, channelName);
            } catch (JSONException e) {
                Log.d(TAG, "Issue adding the user back to a previously joined conversation");
                e.printStackTrace();
            }
        }
    }

    // Simply add the user back to the conversation if they were once part of it
    private void rejoinConversation(String currentUserUsername, String channelName) throws ParseException, JSONException {
        // Get current conversation members
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversation");
        query.whereEqualTo(Conversation.KEY_NAME, channelName);
        ArrayList<String> currentMembers = new ArrayList<>();
        ParseObject conversationParseObject = query.getFirst();
        JSONArray jsonArray = conversationParseObject.getJSONArray(Conversation.KEY_MEMBERS);
        for (int i = 0; i < jsonArray.length(); i++) {
            String member = jsonArray.getString(i);
            currentMembers.add(member);
        }
        currentMembers.add(currentUserUsername);
        conversationParseObject.put(Conversation.KEY_MEMBERS, currentMembers);
        conversationParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Issue updating conversation members /Error: " + e.getMessage());
                } else {
                    Log.i(TAG, "Success updating conversation members");
                    try {
                        Toast.makeText(getContext(), "Rejoining this conversation", Toast.LENGTH_LONG).show();

                        rlComposePopUp.setVisibility(View.GONE);
                        // And close the keyboard
                        // Source: https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);

                        subscribeToChannels();
                    } catch (JSONException jsonException) {
                        Log.d(TAG, "Issue subscribingToChannels after rejoining conversation");
                        jsonException.printStackTrace();
                    }
                }
            }
        });
    }

    // Send greeting message to desired user
    private void sendMessage(String currentUserUsername, String searchedUserUsername, String searchedUserObjectId, String channelName, String searchedUser) {
        // 1) Create the channel
        pubnub.setChannelMetadata()
                .channel(channelName)
                .name(searchedUserUsername)
                .description("This is a private channel between " + currentUserUsername + " and " + searchedUserUsername)
                .async(new PNCallback<PNSetChannelMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetChannelMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Issue creating new channel between users / Error: " + status.toString());
                        } else {
                            Log.i(TAG, "Successfully created new channel between users");
                        }
                    }
                });

        // 2) Subscribe the currentUser
        pubnub.subscribe()
                .channels(Arrays.asList(channelName))
                .withPresence()
                .execute();
        Log.i(TAG, "Current user has been subscribed to new channel");

        // 3) Subscribe the searchedUser
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(searchedUserObjectId);
        PubNub secondPubnub =  new PubNub(pnConfiguration);
        secondPubnub.subscribe()
                .channels(Arrays.asList(channelName))
                .withPresence()
                .execute();
        Log.i(TAG, "Searched user has been subscribed to new channel");

        // 4) Send a greeting message to the searched user
        String greetingMessage = "Hey " + searchedUserUsername;
        JsonObject messagePayload = Message.createMessageObject(currentUserUsername, greetingMessage, new Date());
        pubnub.publish()
                .message(messagePayload)
                .channel(channelName)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (!status.isError()) {
                            Log.i(TAG, "Successfully sent greeting message from original user");
                            Long timetoken = result.getTimetoken(); // message timetoken
                        } else {
                            Log.d(TAG, "Issue sending greeting message to searched user");
                            Toast.makeText(getContext(), "Failed to send greeting message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // 5) Create new channel between the users in Parse Server
        try {
            saveConversation(searchedUserUsername, channelName, greetingMessage);
        } catch (JSONException e) {
            Log.d(TAG, "Issue updating the users' channels in Parse Server");
            e.printStackTrace();
        }

        // 6) Close the pop up
        rlComposePopUp.setVisibility(View.GONE);
        // And close the keyboard
        // Source: https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    private void saveConversation(String searchedUserUsername, String channelName, String greetingMessage) throws JSONException {
        String currentUserUsername = ParseUser.getCurrentUser().getUsername();
        Conversation conversation = new Conversation();
        conversation.put(Conversation.KEY_NAME, channelName);
        conversation.put(Conversation.KEY_MEMBERS, Arrays.asList(currentUserUsername, searchedUserUsername));
        conversation.put(Conversation.KEY_LAST_MESSAGE, greetingMessage);
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Issue saving new conversation in Parse Server");
                } else {
                    Log.i(TAG, "Success saving new conversation in Parse Server");
                    try {
                        subscribeToChannels();
                    } catch (JSONException jsonException) {
                        Log.d(TAG, "Issue subscribingToChannels again after saving new conversation");
                        jsonException.printStackTrace();
                    }
                }
            }
        });
    }

    public void establishListeners() {
        // SubscribeCallback is an Abstract Java class and required all Abstract methods to be implemented
        pubnub.addListener(new SubscribeCallback() {
            // PubNub status
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                switch (status.getOperation()) {
                    // combine unsubscribe and subscribe handling for ease of use
                    case PNSubscribeOperation:
                    case PNUnsubscribeOperation:
                        // Note: subscribe statuses never have traditional errors,
                        // just categories to represent different issues or successes
                        // that occur as part of subscribe
                        switch (status.getCategory()) {
                            case PNConnectedCategory:
                            case PNReconnectedCategory:
                            case PNDisconnectedCategory:
                            case PNUnexpectedDisconnectCategory:
                            case PNAccessDeniedCategory:
                            default:
                        }

                    case PNHeartbeatOperation:
                        // Heartbeat operations can in fact have errors,
                        // so it's important to check first for an error.
                        // For more information on how to configure heartbeat notifications
                        // through the status PNObjectEventListener callback, refer to
                        // /docs/sdks/java/android/api-reference/configuration#configuration_basic_usage
                        if (status.isError()) {
                        } else {
                        }
                    default: {
                    }
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult presence) {

            }

            @Override
            public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {

            }

            @Override
            public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {

            }

            @Override
            public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {

            }

            @Override
            public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {

            }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult pnActionResult) {

            }

            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {

            }
        });

    }
}