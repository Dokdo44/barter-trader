package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.data.FirebaseManager;

public class PreferredProductNotificationManager {

    private final FirebaseManager firebaseManager;
    private NotificationSender notificationSender;

    private final PreferredCategoryMatcher categoryMatcher;
    public PreferredProductNotificationManager(FirebaseManager firebaseManager, NotificationSender notificationSender, PreferredCategoryMatcher categoryMatcher)
    {
        this.firebaseManager = firebaseManager;
        this.notificationSender = notificationSender;
        this.categoryMatcher = categoryMatcher;
    }

    public void start()
    {
        //Evan
    }

    public void stop()
    {
        //Evan
    }
}