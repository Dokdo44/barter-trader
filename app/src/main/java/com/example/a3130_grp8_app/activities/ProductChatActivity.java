package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    TextView backButton;


    /**
     * Create view variables and set click listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productChatMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        backButton = findViewById(R.id.backButtonProductChat);
        backButton.setOnClickListener(v -> finish());
        messagesAdapter = new MessagesAdapter(this, new ArrayList<>());
        LinearLayoutManager messageLayoutManager = new LinearLayoutManager(this);
        messageLayoutManager.setReverseLayout(true);
        messageLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        messageRecyclerView.setAdapter(messagesAdapter);
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0) {
                    messageRecyclerView.scrollToPosition(0);
                }
            }
        });

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