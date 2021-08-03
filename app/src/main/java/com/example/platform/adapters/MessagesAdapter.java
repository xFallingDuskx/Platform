package com.example.platform.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.activities.ChatsActivity_Messaging;
import com.example.platform.models.Message;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Source: https://guides.codepath.com/android/Building-Simple-Chat-Client-with-Parse
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    public static final String TAG = "MessagesAdapter";
    private static final int MESSAGE_OUTGOING = 0;
    private static final int MESSAGE_INCOMING = 1;

    private Context context;
    private List<Message> messages;
    private String username;

    public MessagesAdapter(Context context, List<Message> messages, String username) {
        this.context = context;
        this.messages = messages;
        this.username = username;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.item_message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.item_message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = messages.get(position);
        return message.getType().equals(Message.KEY_TYPE_SENT);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends ViewHolder {
        TextView tvBody;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            tvBody.setText(message.getText());
        }
    }

    public class OutgoingMessageViewHolder extends ViewHolder {
        TextView tvBody;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            tvBody.setText(message.getText());
        }
    }
}
