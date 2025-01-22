package com.gamay.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;

    private FirebaseFirestore db;
    private CollectionReference messagesRef;

    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("messages");

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Initialize RecyclerView
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load existing messages
        loadMessages();

        // Send message on button click
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String sender = senderEmail != null ? senderEmail.split("@")[0] : "Anonymous";

        // Create message object
        Map<String, Object> message = new HashMap<>();
        message.put("text", messageText);
        message.put("timestamp", Timestamp.now());
        message.put("sender", sender);

        // Add message to Firestore
        messagesRef.add(message)
                .addOnSuccessListener(documentReference -> {
                    messageEditText.setText(""); // Clear the input box
                    Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    Log.e("ChatActivity", "Error adding message to Firestore", e);
                });
    }

    private void loadMessages() {
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        Log.e("ChatActivity", "Error loading messages", error);
                        return;
                    }

                    // Clear and reload messages
                    messageList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            String text = document.getString("text");
                            String sender = document.getString("sender");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            ChatMessage message = new ChatMessage(text, timestamp, sender);
                            messageList.add(message);
                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
                    }
                });
    }
}
