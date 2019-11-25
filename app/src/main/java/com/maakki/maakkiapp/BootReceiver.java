package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ryan on 2016/2/26.
 */
public class BootReceiver extends BroadcastReceiver {
    Alarm alarm = new Alarm();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //better delay some time.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Boolean isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        Boolean isServiceRunning = isMyServiceRunning(context, DefaultService.class);
        //Toast.makeText(context, "Reboot.."+"/On:"+String.valueOf(isServiceOn)+"/R:"+String.valueOf(isServiceRunning), Toast.LENGTH_LONG).show();

        //if isServiceOn 是false 表示是logout状态 不要重启ApplicationContext(),
        if (isServiceOn) {
            //if Default is not running,Start it!
            if (!isServiceRunning) {
                //context.stopService(new Intent(context.getApplicationContext(), DefaultService.class));
                //alarm.CancelAlarm(context);
                //alarm.SetAlarm_imm(context);
                //context.startService(new Intent(context, DefaultService.class));
            }
        }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("com.maakki.maakkiapp")) {
                return true;
            }
        }
        return false;
    }
    /*@Override
    public void onReceive(Context context, Intent intent) {

        //better delay some time.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(context, "Maakki's Reactivated!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }*/
}
