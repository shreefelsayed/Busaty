package com.armjld.busaty.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.armjld.busaty.MainUser.MainScreen;
import com.armjld.busaty.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();

            String statue = data.get("body");
            String action = data.get("action");
            String OrderID = data.get("orderid");
            sendNotification(statue, action, OrderID);

            scheduleJob();
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance(this).beginWith(work).enqueue();
    }

    private void sendNotification(String messageBody, String action, String OrderID) {
        //Home.getNoti();
        Intent intent = new Intent(this, MainScreen.class);

        intent.putExtra("action", action);
        intent.putExtra("order", OrderID);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.noti);

        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("United Express")
                        .setContentText(messageBody)
                        .setSound(soundUri)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(OrderID, createID(), notificationBuilder.build());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "United Express", NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build();

            // Configure the notification channel.
            channel.setDescription(messageBody);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes); // This is IMPORTANT

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());
    }

    public int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.ENGLISH).format(now));
    }
}