package com.gamay.chatapp;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String text;
    private Timestamp timestamp;
    private String sender;

    public ChatMessage(String text, Timestamp timestamp, String sender) {
        this.text = text;
        this.timestamp = timestamp;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }
}
