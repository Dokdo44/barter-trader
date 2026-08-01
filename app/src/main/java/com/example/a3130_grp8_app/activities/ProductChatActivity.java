package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.data.MessageStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.MessageNotifier;
import com.example.a3130_grp8_app.util.MessageAdapterChatMessageObserver;
import com.example.a3130_grp8_app.util.MessageSender;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProductChatActivity extends AppCompatActivity {

    MessageSender messageSender;
    User currUser;
    Chat chat;
    boolean initialized = false;

    Button sendMessageButton;
    EditText messageInput;
    RecyclerView messageRecyclerView;

    MessagesAdapter messagesAdapter;

    MessageStorage messageStorage;
    MessageNotifier messageNotifier;
    MessageAdapterChatMessageObserver messageAdapterChatObserver;

    /**
     * Create view variables and set click listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_chat);

        if(!getIntent().hasExtra("CHAT")) {
            throw new IllegalStateException("This view must have a chat extra");
        }

        chat = getIntent().getSerializableExtra("CHAT", Chat.class);
        try {
            FirebaseManager.getInstance().getCurrentUserProfile()
                    .addOnSuccessListener(this::onGetUserSuccess)
                    .addOnFailureListener(this::onGetUserFailure);
        } catch (FirebaseConnectionError e) {
            Log.e("e", "Unable to get current user profile from firebase");
        }

        sendMessageButton = findViewById(R.id.sendButton);
        messageInput = findViewById(R.id.messageInput);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);

        messagesAdapter = new MessagesAdapter(this, new ArrayList<>());
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messagesAdapter);

        messageNotifier = new MessageNotifier(chat);

        StorageManager storageManager = new StorageManager(this);
        messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);

        messageAdapterChatObserver = new MessageAdapterChatMessageObserver(messageNotifier, messageStorage, messagesAdapter);

        sendMessageButton.setOnClickListener(sendButtonOnClickListener);

        FirebaseManager.getInstance().addValueEventListenerForConnection(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if(connected == null) {
                    return;
                }
                if(connected) {
                    messageAdapterChatObserver.attachToMessageNotifier();
                } else {
                    messageAdapterChatObserver.attachToMessageStorage();
                    messageStorage.feedObservers(chat.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private final View.OnClickListener sendButtonOnClickListener = view -> {
        if(!initialized) {
            Snackbar.make(view, "Please wait for initialization...", Snackbar.LENGTH_SHORT).show();
            return;
        }
        messageSender.sendMessage(LocalDateTime.now().toString(), messageInput.getText().toString())
                .addOnSuccessListener(result -> getSendSuccess(view))
                .addOnFailureListener(e -> getSendFailure(view, e));
    };

    private void onGetUserSuccess(User user) {
        currUser = user;
        createMessageSender();
        initialized = true;
    }

    private void createMessageSender() {
        messageSender = new MessageSender(FirebaseManager.getInstance().getCurrentUserID(),
                currUser.getEmail(), chat);
    }

    private void onGetUserFailure(Exception e) {
        Log.e("e", "Exception occurred: " + e.getMessage());
    }

    private void getSendSuccess(View view) {
        Snackbar.make(view, "Message sent.", Snackbar.LENGTH_SHORT).show();
        messageInput.setText("");
    }

    private void getSendFailure(View view, Exception e) {
        Snackbar.make(view, "Failed to send message: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
}