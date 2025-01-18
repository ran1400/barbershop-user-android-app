package com.example.barbershop.notifications;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.barbershop.MainActivity;
import com.example.barbershop.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        String title = null;
        String body = null;

        // Handle Notification Payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // Handle Data Payload
        if (remoteMessage.getData().size() > 0) {
            if (title == null) { // Fallback to data payload if notification title is missing
                title = remoteMessage.getData().get("title");
            }
            if (body == null) { // Fallback to data payload if notification body is missing
                body = remoteMessage.getData().get("body");
            }
        }

        // Show the Notification if there's a title or body
        if (title != null && body != null) {
            showNotification(title, body);
        } else {
            Log.d("FCM", "Message received but no title or body to display.");
        }
    }

    private void showNotification(String title, String body)
    {
        // Notification Channel ID
        String channelId = "default_channel_id";

        // Create the Notification Manager
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0+ (Oreo and above), create the Notification Channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // Intent for Notification Click
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify(0, builder.build());
    }


}
