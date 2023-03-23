package com.conestoga.timero;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BatteryNotificationService extends Service {
    public BatteryNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        final NotificationChannel channel = new NotificationChannel(
                "1",
                "Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        manager.createNotificationChannel(channel);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        String title = "Timero";
        String message = "Warning your battery level is running a little low.";
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.timero_splash_screen)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(1, notification.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}