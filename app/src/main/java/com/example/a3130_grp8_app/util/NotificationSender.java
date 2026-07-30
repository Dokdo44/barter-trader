package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;

public interface NotificationSender {

    void sendPreferredProductNotification(Product product);
    void sendMessageNotification(Message message);
}