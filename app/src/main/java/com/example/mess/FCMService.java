package com.example.mess;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private Map<String,Integer> translationsMap;
    private SessionManager sessionManager;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        sessionManager = new SessionManager(this);
        translationsMap = new HashMap<>();
        translationsMap.put("en", FirebaseTranslateLanguage.EN);
        translationsMap.put("ru", FirebaseTranslateLanguage.RU);
        translationsMap.put("de", FirebaseTranslateLanguage.DE);
        translationsMap.put("fr", FirebaseTranslateLanguage.FR);
        System.out.println("remoteMessage "+remoteMessage.getData().toString());
        translateMessage(remoteMessage);

        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
    public void createNotification(String title, String message){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "100";
            String channelName = "channel_name";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(this,"100");
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
    public void translateMessage(RemoteMessage remoteMessage){
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(translationsMap.get(remoteMessage.getData().get("language")))
                        .setTargetLanguage(translationsMap.get(sessionManager.getLanguageUser()))
                        .build();
        final FirebaseTranslator translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                translator.translate(remoteMessage.getData().get("message"))
                                        .addOnSuccessListener(
                                                translatedText -> {
                                                    createNotification(remoteMessage.getData().get("name"),translatedText);
                                                })
                                        .addOnFailureListener(
                                                e -> {

                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });
    }
}
