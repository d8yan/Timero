package com.conestoga.timero;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    public NotificationService() { }

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

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.timero_splash_screen)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Timer timer = new Timer(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                manager.notify(1, notification.build());
                timer.cancel();
                stopSelf();
            }
        }, 3000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
