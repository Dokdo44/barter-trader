package com.example.a3130_grp8_app.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class MessageNotificationManager {

    NotificationSender notificationSender;

    public MessageNotificationManager(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    /**
     * Starts listening for messages in the user's inbox
     */
    public void start() {
        FirebaseUser firebaseUser = FirebaseManager.getInstance().getCurrentUser();
        Task<User> currUserTask = FirebaseManager.getInstance().getUserByUid(firebaseUser.getUid());
        currUserTask.addOnSuccessListener(this::onGetCurrUserSuccess);
    }

    private void onGetCurrUserSuccess(User user) {
        FirebaseManager.getInstance().addChildEventListenerForUserMessageInbox(user, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageId = snapshot.getValue(String.class);
                FirebaseManager.getInstance().getMessageById(messageId)
                                .addOnSuccessListener(message ->
                                        notificationSender.sendMessageNotification(message));
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
        });
    }
}
