package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.MessageSender;
import com.google.firebase.auth.FirebaseUser;

public class ProductChatActivity extends AppCompatActivity {

    MessageSender messageSender;
    User currUser;
    Chat chat;
    boolean initialized = false;

    Button sendMessageButton;


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
    }

    public void onGetUserSuccess(User user) {
        currUser = user;
        createMessageSender();
        initialized = true;
    }

    private void createMessageSender() {
        messageSender = new MessageSender(currUser, chat);
    }

    public void onGetUserFailure(Exception e) {
        Log.e("e", "Exception occurred: " + e.getMessage());
    }
}