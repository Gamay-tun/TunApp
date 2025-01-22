package com.gamay.chatapp;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.senderTextView.setText(message.getSender());

        // Handle clickable links in the message text
        holder.messageTextView.setText(message.getText());
        holder.messageTextView.setAutoLinkMask(Linkify.WEB_URLS); // Detect and make links clickable
        holder.messageTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Enable link clicks

        // Format and display the timestamp
        Timestamp timestamp = message.getTimestamp();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault());
            holder.timestampTextView.setText(dateFormat.format(date));
        } else {
            holder.timestampTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView, timestampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
