package com.nsa.flexjobs.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.nsa.flexjobs.Extra.FireBase;
import com.nsa.flexjobs.R;
import com.nsa.flexjobs.activities.TaskActivity;

public class Notification {

    public static void  showNotification(String title, String message,String channel_id ,Context context) {
        Intent intent
                = new Intent(context, TaskActivity.class);
        // Assign channel ID
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        new FireBase().getReferenceUsers().child(channel_id).child("notifications")
                .push().setValue(title+" :- "+message);// next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(context,
                channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        builder = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);
        //  }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}
