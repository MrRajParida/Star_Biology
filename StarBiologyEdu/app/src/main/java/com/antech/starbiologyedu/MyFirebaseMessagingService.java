package com.antech.starbiologyedu;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message: " + remoteMessage.getNotification().getBody());
    }

    @Override
    public void onNewToken(String token) {
        // Handle token refresh
        Log.d(TAG, "Refreshed token: " + token);
    }
}
