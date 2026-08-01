package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.adapters.ChatsListItemAdapter;
import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.ChatAdapterChatObserver;
import com.example.a3130_grp8_app.util.ChatNotifier;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsListActivity extends AppCompatActivity {

    RecyclerView chatsListRecyclerView;
    ChatsListItemAdapter chatsListItemAdapter;
    ChatAdapterChatObserver chatAdapterChatObserver;
    String currUserId;

    /**
     * Create view variables and start child listeners
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatsListActivityMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatsListRecyclerView = findViewById(R.id.chatsListRecyclerView);
        chatsListItemAdapter = new ChatsListItemAdapter(this, new ArrayList<>());
        chatsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsListRecyclerView.setAdapter(chatsListItemAdapter);


        currUserId = FirebaseManager.getInstance().getCurrentUserID();
        if(currUserId == null) {
            throw new RuntimeException("Unable to get user");
        }

        ChatNotifier chatNotifier = new ChatNotifier(currUserId);

        StorageManager storageManager = new StorageManager(this);
        ChatStorage chatStorage = ChatStorage.getInstance(storageManager, chatNotifier);
        chatAdapterChatObserver = new ChatAdapterChatObserver(chatNotifier, chatStorage, chatsListItemAdapter);

        FirebaseManager.getInstance().addValueEventListenerForConnection(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if(connected == null) {
                    Log.e("e", "could not get snapshot value");
                    return;
                }
                if(connected) {
                    chatAdapterChatObserver.attachToChatNotifier();
                } else {
                    chatAdapterChatObserver.attachToChatStorage();
                    chatStorage.feedObservers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
