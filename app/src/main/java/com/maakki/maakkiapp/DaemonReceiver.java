package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Date;

public class DaemonReceiver extends BroadcastReceiver {
    private Context context;
    Boolean isServiceRunning;
    //private GPSTracker gps;
    boolean isServiceOn;
    double latitude = 0;
    double longitude = 0;
    boolean isRunning;
    private static final int Timer_Check_INTERVAL = 1000 * 1; // 1 second
    private String mMaakkiID;

    @Override
    public void onReceive(Context context, Intent intent) {
        //isSignalRAlive = Boolean.parseBoolean(intent.getStringExtra("isSignalRAlive"));
        this.context = context;
        //gps=new GPSTracker(context);

        Intent intentservice = new Intent(context, CoreService.class);
        isServiceRunning = isMyServiceRunning(context, CoreService.class);
        mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        if(isServiceOn){
            if (!isServiceRunning) {
                isRunning = false;
                context.startService(intentservice);
            } else {
                isRunning = true;
                //coreAlarm();
            }
        }
        //Toast.makeText(context,"DaemonReceiver:"+isRunning,Toast.LENGTH_LONG).show();
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
    private void coreAlarm() {
        //mMaakkiID= SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        Long LastMessageTime = SharedPreferencesHelper.getSharedPreferencesLong(context, SharedPreferencesHelper.SharedPreferencesKeys.key11, 0l);
        SharedPreferences spf =context.getSharedPreferences(MainActivity.PREFERENCE_NAME,Context.MODE_PRIVATE);
        //Long LastMessageTime=spf.getLong("LastMessageTime",0l);
        Long currentTime = new Date().getTime();
        Long intervalTime = currentTime - LastMessageTime;
        Boolean isSignalRAlive = false;
        if (intervalTime < Timer_Check_INTERVAL ) {
            isSignalRAlive = true;
        }
        Intent intentservice = new Intent(context, CoreService.class);
        String onReactivate = "";
        if (!isSignalRAlive) {
            //if service is running,stop it!
            context.stopService(intentservice);
            context.startService(intentservice);
            onReactivate = "stop & start";
            //showAlertDialog("stop & start",intervalTime+" ms");
        } else {
            onReactivate = "normal";
        }
        if (mMaakkiID.equals("1") || mMaakkiID.equals("17141")) {
            String mess=onReactivate + ":" + intervalTime;
            Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        }
    }
}

