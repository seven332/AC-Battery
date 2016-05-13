package com.hippo.acbattery;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class AbettorService extends Service {

    public static final String KEY_NOTIFY_ID = "notify_id";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int notifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0);
            if (notifyId != 0) {
                Notification notify = new Notification.Builder(
                        getApplicationContext()).setContentTitle("Bully").build();
                startForeground(notifyId, notify);
            }
        }

        stopForeground(false);
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
