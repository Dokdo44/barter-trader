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
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class ChatsListActivity extends AppCompatActivity {

    RecyclerView chatsListRecyclerView;
    ChatsListItemAdapter chatsListItemAdapter;
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
        onGetUserSuccess(currUserId);
    }

    private ChildEventListener chatChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Chat chat = snapshot.getValue(Chat.class);
            if(chat == null) {
                Log.e("e", "Could not get chat from snapshot");
                return;
            }
            chatsListItemAdapter.addChat(chatsListItemAdapter.getItemCount(), chat);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private void onGetUserSuccess(String userId) {
        FirebaseManager.getInstance().addChildEventListenerToUserChats(userId, chatChildEventListener);
    }
}
