package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.adapters.ConversationsAdapter;
import com.example.platform.adapters.MessagesAdapter;
import com.example.platform.models.Conversation;
import com.example.platform.models.Message;
import com.google.gson.JsonObject;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNFetchMessageItem;
import com.pubnub.api.models.consumer.history.PNFetchMessagesResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Source: https://github.com/pubnub/android-quickstart-platform
// Source: https://guides.codepath.com/android/Building-Simple-Chat-Client-with-Parse
public class ChatsActivity_Messaging extends AppCompatActivity {

    public static final String TAG = "ChatsActivity_Messaging";
    Context context;
    PubNub pubnub;
    String channelName;
    ParseUser currentUser;
    boolean updateLastMessage = false;

    ImageView ivBack;
    TextView tvHeading;
    EditText etMessageInput;
    ImageView ivSend;

    List<Message> allMessages;
    RecyclerView rvMessages;
    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_messaging);
        context = getApplicationContext();
        currentUser = ParseUser.getCurrentUser();

        // Set up toolbar
        String visibleName = getIntent().getStringExtra("visibleName");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_Messaging);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_Message);
        tvHeading = findViewById(R.id.tvHeading_Messaging);

        tvHeading.setText(visibleName);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up RecyclerView and obtain other bits of necessary information
        channelName = getIntent().getStringExtra("channelName");
        allMessages = new ArrayList<>();
        rvMessages = findViewById(R.id.rvMessages);
        adapter = new MessagesAdapter(context, allMessages, currentUser.getUsername());
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvMessages.setLayoutManager(linearLayoutManager);

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(ParseUser.getCurrentUser().getObjectId()); // Set the PubNub unique user ID as the User's Object ID in the Parse server
        pubnub =  new PubNub(pnConfiguration);

        // Fetch previous conversations
        // Source: https://www.pubnub.com/docs/chat/features/message-history
        pubnub.fetchMessages()
                .channels(Arrays.asList(channelName))
                .maximumPerChannel(100)
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
                                    String sender = messageObject.get(Message.KEY_SENDER).getAsString();
                                    String text = messageObject.get(Message.KEY_TEXT).getAsString();
                                    String type = "";

                                    String sentDateString = messageObject.get(Message.KEY_SENT_DATE).getAsString();

                                    // Determine if the message was sent by the current user or not
                                    if (sender.equals(currentUser.getUsername())) {
                                        type = Message.KEY_TYPE_SENT;
                                    } else {
                                        type = Message.KEY_TYPE_RECEIVED;

                                    }
                                    Message message = new Message(sender, type, text, sentDateString);
                                    allMessages.add(message);

                                    Log.i(TAG, "New Message / Sender: " + sender + " / Type: " + type + " / Text: " + text + " / Sent Date: " + sentDateString);

                                }
                            }

                            // Go to recent message
                            adapter.notifyDataSetChanged();
                            rvMessages.smoothScrollToPosition(allMessages.size() - 1);
                        } else {
                            Log.d(TAG, "Issue fetching previous messages");
                            status.getErrorData().getThrowable().printStackTrace();
                        }
                    }
                });

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                Log.i(TAG, "New message received");

                JsonObject messageObject = event.getMessage().getAsJsonObject();
                String sender = messageObject.get(Message.KEY_SENDER).getAsString();
                String text = messageObject.get(Message.KEY_TEXT).getAsString();
                String type = "";

                String sentDateString = messageObject.get(Message.KEY_SENT_DATE).getAsString();

                // Determine if the message was sent by the current user or not
                if (sender.equals(currentUser.getUsername())) {
                    type = Message.KEY_TYPE_SENT;
                } else {
                    type = Message.KEY_TYPE_RECEIVED;

                }
                Message message = new Message(sender, type, text, sentDateString);
                allMessages.add(message);
                updateLastMessage = true;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Go to recent message
                        adapter.notifyDataSetChanged();
                        rvMessages.smoothScrollToPosition(allMessages.size() - 1);
                    }
                });

                Log.i(TAG, "New Message / Sender: " + sender + " / Type: " + type + " / Text: " + text + " / Sent Date: " + sentDateString);
            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {

            }

            @Override
            public void signal(PubNub pubnub, PNSignalResult event) { }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) { }

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) { }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) { }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult event) { }

            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {

            }
        });

        pubnub.subscribe().channels(Arrays.asList(channelName)).withPresence().execute();

        etMessageInput = findViewById(R.id.etMessageInput);
        ivSend = findViewById(R.id.ivSend);

        // Handle user sending message
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessageInput.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(context, "Cannot send an empty text message", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "onTrack to send new message");
                JsonObject messagePayload = Message.createMessageObject(currentUser.getUsername(), message, new Date());
                publishMessage(messagePayload);

            }
        });
    }

    private void publishMessage(JsonObject messagePayload) {
        pubnub.publish()
                .message(messagePayload)
                .channel(channelName)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (!status.isError()) {
                            Log.i(TAG, "Successfully sent new  message from original user");
                            etMessageInput.setText("");
                            updateLastMessage = true;
                        } else {
                            Log.d(TAG, "Issue sending greeting message to searched user");
                            Toast.makeText(context, "Failed to send greeting message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        try {
            updateParseServer();
            Log.i(TAG, "Success updatingParseServer with onPause()");
        } catch (ParseException e) {
            Log.d(TAG, "Failed to updateParseServer with onPause()");
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try {
            updateParseServer();
            Log.i(TAG, "Success updatingParseServer with onBackPressed()");
        } catch (ParseException e) {
            Log.d(TAG, "Failed to updateParseServer with onBackPressed");
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    // Save the last message that was sent before a user leaves the activity for Conversations Fragment
    public void updateParseServer() throws ParseException {
        if (! updateLastMessage) {
            Log.i(TAG, "No need to update conversation");
            return;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversation");
        query.whereEqualTo(Conversation.KEY_NAME, channelName);
        ParseObject parseObject = query.getFirst();
        String lastMessage = allMessages.get(allMessages.size() - 1).getText();

        // Retrieve the object by id
        query.getInBackground(parseObject.getObjectId(), (object, e) -> {
            if (e == null) {
                object.put(Conversation.KEY_LAST_MESSAGE, lastMessage);
                object.saveInBackground();
                Log.i(TAG, "Successfully updated the conversation's last message");
            } else {
                Log.d(TAG, "Failed to update the conversation's last message / Error: " + e.getMessage());
            }
        });
    }
}