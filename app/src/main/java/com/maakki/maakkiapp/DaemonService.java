package com.maakki.maakkiapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class DaemonService extends Service {
    private static final int CORE_CHECK_INTERVAL = 1000 * 10;
    private Intent daemonIntent = new Intent("com.maakki.maakkiapp.DAEMON");
    private Thread daemonThread = null;
    Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context=getApplicationContext();
        startThread();
    }

    private void startThread() {
        daemonThread = new Thread(new CoreCheckThread());
        daemonThread.start();
    }

    private class CoreCheckThread implements Runnable {

        public CoreCheckThread() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    SharedPreferences spf =context.getSharedPreferences(MainActivity.PREFERENCE_NAME,Context.MODE_PRIVATE);
                    Long LastMessageTime=spf.getLong("LastMessageTime",0l);
                    daemonIntent.putExtra("LastMessageTime",LastMessageTime);
                    sendBroadcast(daemonIntent);
                    Thread.sleep(CORE_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}
