package com.example.a3130_grp8_app.util;

import android.util.Log;

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
        if(firebaseUser != null) {
            onGetCurrUserSuccess(firebaseUser.getUid());
        }
    }

    private void onGetCurrUserSuccess(String userId) {
        FirebaseManager.getInstance().addChildEventListenerForUserMessageInbox(userId, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageId = snapshot.getKey();
                Boolean isValid = snapshot.getValue(Boolean.class);
                if(isValid != null && isValid) {
                    FirebaseManager.getInstance().getMessageById(messageId)
                            .addOnSuccessListener(message -> {
                                notificationSender.sendMessageNotification(message);
                                FirebaseManager.getInstance().removeMessageIdFromUseInbox(messageId, userId)
                                        .addOnFailureListener(this::onRemoveMessageFailure);
                            });
                }
            }

            private void onRemoveMessageFailure(Exception e) {
                Log.e("e", "Failed to delete message from inbox: " + e.getMessage());
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
