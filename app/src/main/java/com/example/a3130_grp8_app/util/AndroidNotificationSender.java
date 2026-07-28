package com.example.a3130_grp8_app.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.activities.ProductChatActivity;
import com.example.a3130_grp8_app.activities.ProductDetailsActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Message;
import com.google.android.gms.tasks.Task;

public class AndroidNotificationSender implements NotificationSender {

    private static final String CHANNEL_ID = "preferred_product_channel";

    private final Context context;

    public AndroidNotificationSender(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Preferred Products",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifies you when a preferred product is posted");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void sendPreferredProductNotification(Product product) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra("PRODUCT", product);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(product.getName())
                .setContentText(product.getProviderUid())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            int notificationId = product.getId() != null ? product.getId().hashCode() : product.hashCode();
            notificationManager.notify(notificationId, builder.build());
        }
    }

    public void sendMessageNotification(Message message) {
        Task<Chat> chatTask = FirebaseManager.getInstance().getChatById(message.getChatId());
        chatTask.addOnSuccessListener(chat -> onGetChatSuccess(chat, message));
        chatTask.addOnFailureListener(this::onGetChatFailure);
    }

    private void onGetChatFailure(Exception e) {
        Log.e("e", e.getMessage());
    }

    private void onGetChatSuccess(Chat chat, Message message) {
        Intent intent = new Intent(context, ProductChatActivity.class);
        intent.putExtra("CHAT", chat);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New message from " + message.getSenderName())
                .setContentText(message.getMessageString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            int notificationId = message.getId() != null ? message.getId().hashCode() : message.hashCode();
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
