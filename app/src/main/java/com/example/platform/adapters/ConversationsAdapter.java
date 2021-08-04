package com.example.platform.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.activities.BaseActivity;
import com.example.platform.activities.ChatsActivity_Messaging;
import com.example.platform.models.Conversation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{

    public static final String TAG = "ConversationsAdapter";

    private Context context;
    private List<Conversation> conversations;

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Conversation conversation = conversations.get(position);
                        Intent intent = new Intent(context, ChatsActivity_Messaging.class);
                        intent.putExtra("channelName", conversation.getName());
                        context.startActivity(intent);
                    }
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
}
