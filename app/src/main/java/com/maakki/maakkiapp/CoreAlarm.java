package com.maakki.maakkiapp;
/**
 * Created by ryan on 2016/8/5.
 */

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static android.content.Context.NOTIFICATION_SERVICE;


public class CoreAlarm extends BroadcastReceiver {
    Integer a = 0;
    Context context;
    //GPSTracker gps;
    double latitude = 0;
    double longitude = 0;
    private static final int Timer_Check_INTERVAL = 1000 * 15; // 15 seconds
    private String mMaakkiID;
    //redEnvelopeMasterDAO reDAO;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        //reDAO=new redEnvelopeMasterDAO(context);
        /*if (intent.getAction() != null) {
            if( intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Toast.makeText(context, "ACTION_USER_PRESENT", Toast.LENGTH_LONG).show();
            }else if( intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Toast.makeText(context, "ACTION_SCREEN_ON", Toast.LENGTH_LONG).show();
            }
        }*/
        mMaakkiID= SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        Long LastMessageTime= SharedPreferencesHelper.getSharedPreferencesLong(context, SharedPreferencesHelper.SharedPreferencesKeys.key11, 0l);
        Long currentTime=new Date().getTime();
        Long intervalTime= currentTime-LastMessageTime;
        Boolean isSignalRAlive=false;
        if(intervalTime<Timer_Check_INTERVAL*2){
            isSignalRAlive=true;
        }
        Boolean isServiceRunning = isMyServiceRunning(context, CoreService.class);
        Boolean isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        Intent intentservice = new Intent(context, CoreService.class);
        String onReactivate = "";
        if (isServiceOn) {
            //if CoreService is not running Or LastMessageTime pastby over 20 secs,reStart CoreService!
            //超过2秒就重启服务
            if (isServiceRunning) {
                if (!isSignalRAlive) {
                    //if service is running,stop it!
                    context.stopService(intentservice);
                    context.startService(intentservice);
                    onReactivate = "stop & start";
                } else {
                    onReactivate = "normal";
                }
            } else {
                onReactivate = "Service restart";
                context.startService(intentservice);
            }
        }
        if(mMaakkiID.equals("1")||mMaakkiID.equals("17141")){
            //Toast.makeText(context, "CoreAlarm:"+onReactivate+"/"+intervalTime, Toast.LENGTH_LONG).show();
        }
    }

    public void setAlarm_imm(Context context) {
        this.context=context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, CoreAlarm.class);
        //i.putExtra("actType","check_coreservice_running");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //立即啟動，每？秒钟重设一次alarm
        /*try {
            am.cancel(pi);
        } catch (Exception ignored) {
        }*/
        if (am!= null) {
            am.cancel(pi);
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Timer_Check_INTERVAL, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context) {
        Intent i = new Intent(context, CoreAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(pi);
        } catch (Exception ignored) {
        }
    }

    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
}
