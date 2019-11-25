package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.util.Date;


public class CoreReceiver extends BroadcastReceiver {
    Boolean isServiceRunning;
    private Context context;
    private String mMaakkiID;
    boolean isServiceOn;
    private static final int Timer_Check_INTERVAL = 1000 * 1; // 1 second
    boolean isRunning;

    @Override
    public void onReceive(final Context context, Intent intent) {

        //String Lasttime_milliseconds=String.valueOf(new Date().getTime());
        //SharedPreferencesHelper.putSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key20, Lasttime_milliseconds);
        Intent intentDaemonService=new Intent(context,DaemonService.class);
        isServiceRunning=isMyServiceRunning(context, DaemonService.class);
        isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        this.context = context;
        mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        if(isServiceOn){
            if (!isServiceRunning) {
                context.startService(intentDaemonService);
            }else{
                isRunning=true;
                coreAlarm();
            }
        }
        //Toast.makeText(context, "CoreReceiver:"+isRunning, Toast.LENGTH_LONG).show();
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
            /*Intent i = new Intent("INVOKE_FROM_DEFAULTSERVICE");
            //Bundle bundle1=new Bundle();
            i.putExtra("isNotification",false);
            //i.putExtra("message",mess);
            context.sendBroadcast(i);*/
        }
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}