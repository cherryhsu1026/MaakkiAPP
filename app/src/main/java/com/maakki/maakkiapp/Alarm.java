package com.maakki.maakkiapp;
/**
 * Created by ryan on 2016/8/5.
 */

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


public class Alarm extends BroadcastReceiver {
    private static final String HUB_URL = StaticVar.webURL;
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_chat_public_Send = "chat_public";
    Integer a = 0;
    Context context;
    private HubProxy mHub;
    private String mMemID = "";
    private String mName = "";
    private String mPicfile = "";
    //private Integer intervaltime;
    private String nName = "";
    private String nMessage = "";
    private String Contactid = "";
    private Boolean isPrivate = false, isCustomer = false;
    private SignalRFuture<Void> mSignalRFuture;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //intervaltime=1000*60*5;
        Long currenttime = (new Date()).getTime();
        Long LastMessageTime = SharedPreferencesHelper.getSharedPreferencesLong(context, SharedPreferencesHelper.SharedPreferencesKeys.key11, 0);
        Long intervalTime = currenttime - LastMessageTime;
        //SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key6, false);
        a++;
        Boolean isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        Boolean isServiceRunning = isMyServiceRunning(context, DefaultService.class);

        //Toast.makeText(context, "alarm is onReceive! "+(a++), Toast.LENGTH_LONG).show();
        String onReactivate = "";
        if (isServiceOn) {
            //if Default is not running Or LastMessageTime pastby over 20 secs,reStart DefaultService!
            if (!isServiceRunning || intervalTime > 1000 * 60 * 5) {
                //ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                //toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                //Toast.makeText(context, "DefaultService not running..", Toast.LENGTH_LONG).show();
                context.stopService(new Intent(context, DefaultService.class));
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.startService(new Intent(context, DefaultService.class));
                //Toast.makeText(context, "Reactivated", Toast.LENGTH_LONG).show();
                onReactivate = "reactivate";
            } else {
                onReactivate = "normal";
            }
        }
        SetAlarm_imm(context);
        setHubConnect();
        sendMessageToMe();
        Toast.makeText(context, "Alarm:" + intervalTime + "/" + onReactivate + "/count:" + CountMyServiceRunning(context, DefaultService.class), Toast.LENGTH_LONG).show();
        //if(mMemID.equals("90")){
        //boolean contains = Arrays.stream(values).anyMatch("s"::equals);
        String[] CSId = context.getResources().getStringArray(R.array.CustomerServiceContactId);
        int count = CSId.length;
        //Toast.makeText(context, "CSId:" +count+"/"+mMemID, Toast.LENGTH_LONG).show();
        Boolean isCS = false;
        //Arrays.stream(CustomerServiceContactId).anyMatch(mMemID:true:false);
        for (int i = 0; i < count; i++) {
            if (CSId[i].equals(mMemID)) {
                isCS = true;
            }
            //Toast.makeText(context, "CSId:" +CSId[i]+"/"+mMemID, Toast.LENGTH_LONG).show();

        }
        if (isCS) {
            sendLastTimeCustomerServiceOnline();
        }
    }

    public void SetAlarm_imm(Context context) {
        //CancelAlarm(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //立即啟動，每？秒钟重设一次alarm
        Integer intervalMillis = 1000 * 60 * 5;
        //Integer intervalMillis=1000*15;
        try {
            am.cancel(pi);
        } catch (Exception ignored) {
        }
        //Integer intervalMillis=1000*200*(a++);
        //Toast.makeText(context, "reActivating.."+(intervalMillis/1000), Toast.LENGTH_LONG).show();
        //Log.e("Alarm", "Time:"+System.currentTimeMillis());
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, pi); // Millisec * Second * Minute
        /*190109*/
    }

    public void CancelAlarm(Context context) {
        //Toast.makeText(context, "CancelAlarm!", Toast.LENGTH_LONG).show();
        //context.stopService(new Intent(context, DefaultService.class));
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(pi);
        } catch (Exception ignored) {
        }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //if(service.service.getClassName().contains("com.maakki.maakkiapp")) {
            //Toast.makeText(context,service.service.getClassName(), Toast.LENGTH_LONG).show();
            if (service.service.getClassName().contains("maakkiapp.DefaultService")) {
                return true;
            }
            //}
        }
        return false;
    }

    private String CountMyServiceRunning(Context context, Class<?> serviceClass) {
        int count = 0;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //if(service.service.getClassName().contains("com.maakki.maakkiapp")) {
            //Toast.makeText(context,service.service.getClassName(), Toast.LENGTH_LONG).show();
            if (service.service.getClassName().contains("maakkiapp.DefaultService")) {
                count++;
                //return true;
            }
            //}
        }
        return String.valueOf(count);
    }

    private void sendMessageToMe() {
        ArrayList L = new ArrayList();
        String mess = "MeSsFrOmAlaRm";
        //L.add(Contactid);
        L.add(mMemID);
        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendLastTimeCustomerServiceOnline() {
        ArrayList L = new ArrayList();
        String mess = "sEnLatTiMCutMeSVieOLe:" + (new Date()).getTime();
        //L.add(Contactid);
        L.add(mMemID);
        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void setHubConnect() {
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        //開啟連線
        try {
            mSignalRFuture.get();
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getContactLastMessageTime(String Contactid) {
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        //開啟連線
        try {
            mSignalRFuture.get();
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String mess = "gTCuStorESeViCLaTMeSsaGeT";
        ArrayList L = new ArrayList();
        L.add(Contactid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
