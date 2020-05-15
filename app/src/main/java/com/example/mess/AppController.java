package com.example.mess;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;

public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

    }
}
