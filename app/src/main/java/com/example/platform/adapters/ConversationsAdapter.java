package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.activities.ChatsActivity_Messaging;
import com.example.platform.models.Comment;
import com.example.platform.models.Conversation;
import com.example.platform.models.Title;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{

    public static final String TAG = "ConversationsAdapter";

    private Context context;
    private List<Conversation> conversations;
    private ArrayList<String> currentMembers;
    ParseObject conversationParseObject;
    String currentUser;

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvTimestamp;
        TextView tvLastMessage;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername_Conversation);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp_Conversation);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage_Conversation);

            // User double-taps screen to like the title
            // Source: https://stackoverflow.com/questions/4804798/doubletap-in-android
            // Solved 'Unable to add window' error: https://stackoverflow.com/questions/18755847/dialog-in-custom-adapter-in-android
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        Log.i(TAG, "onLongPress");
                        // Give user the option to delete the conversation
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Conversation conversation = conversations.get(position);
                            String conversationObjectId = conversation.getObjectId();

                            // Fetch conversation member in case user decides to delete the conversation
                            try {
                                currentMembers = new ArrayList<>();
                                fetchConversationMembers(conversation);
                            } catch (ParseException | JSONException parseException) {
                                Log.d(TAG, "Issue fetching the members for the conversation");
                                parseException.printStackTrace();
                            }

                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Remove Conversation?")
                                    .setContentText("While it is possible to rejoin a conversation, it is recommended that you only do this if you are sure.")

                                    .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.i(TAG, "User is onTrack to delete the conversation");
                                            // Change SweetAlert to loading type
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                                            sweetAlertDialog.setTitleText("Removing...");
                                            sweetAlertDialog.setContentText("You are being removed from the conversation");
                                            sweetAlertDialog.setCancelable(false);

                                            removeFromConversation();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemRemoved(position);

                                                    sweetAlertDialog.setTitleText("Removed!")
                                                            .setContentText("Your conversation has been successfully deleted!")
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(null)
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                            }, 6000);
                                        }
                                    })
                                    .show();
                        }
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Log.i(TAG, "onSingleTap");
                        // Go to detailed view w intent to scroll to conversations
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Conversation conversation = conversations.get(position);
                            Intent intent = new Intent(context, ChatsActivity_Messaging.class);
                            intent.putExtra("channelName", conversation.getName());
                            context.startActivity(intent);
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
        }

        public void bind(Conversation conversation) {
            Log.i(TAG, "Binding for the conversation: " + conversation.getVisibleName());
            tvUsername.setText(conversation.getVisibleName());
            tvTimestamp.setText(conversation.getTimestamp());
            tvLastMessage.setText(conversation.getLastMessage());
        }
    }

    public void fetchConversationMembers(Conversation conversation) throws ParseException, JSONException {
        // Query for all channels the current user is a member of
        currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseObject> conversationQuery = ParseQuery.getQuery("Conversation").whereEqualTo(Conversation.KEY_NAME, conversation.getName());
        conversationParseObject = conversationQuery.getFirst();
        JSONArray jsonArray = conversationParseObject.getJSONArray(Conversation.KEY_MEMBERS);
        for (int i = 0; i < jsonArray.length(); i++) {
            String member = jsonArray.getString(i);
            currentMembers.add(member);
        }
    }

    private void removeFromConversation() {
        int index = currentMembers.indexOf(currentUser);
        currentMembers.remove(index);

        conversationParseObject.put(Conversation.KEY_MEMBERS, currentMembers);
        conversationParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Issue updating conversation members /Error: " + e.getMessage());
                } else {
                    Log.i(TAG, "Success updating conversation members");
                }
            }
        });
    }
}
