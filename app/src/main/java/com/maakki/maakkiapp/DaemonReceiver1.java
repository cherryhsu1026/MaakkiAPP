package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static android.content.Context.NOTIFICATION_SERVICE;

public class DaemonReceiver1 extends BroadcastReceiver {
    Context context;
    Boolean isServiceRunning;
    private GPSTracker gps;
    String maakki_id,mMemID;
    private static final String HUB_Method_Connection = "userConnected";
    private HubProxy mHub;
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_URL = "http://www.maakki.com/";
    private static final String HUB_NAME = "maakkiHub";
    private SignalRFuture<Void> mSignalRFuture;
    double latitude; // latitude
    double longitude; // longitude
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        gps=new GPSTracker(context);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        latitude=gps.getLatitude();
        longitude=gps.getLongitude();
        //Toast.makeText(context,latitude+":"+longitude,Toast.LENGTH_LONG).show();
        if(maakki_id.equals("10006")){
            setHubConnect();
            sendLocationToAdmin();
        }
        Intent i = new Intent("INVOKE_ONLOCATIONCHANGED");
        context.sendBroadcast(i);
        Intent intentservice=new Intent(context,CoreService.class);
        isServiceRunning=isMyServiceRunning(context, CoreService.class);
        if (!isServiceRunning) {
            context.startService(intentservice);
            //Toast.makeText(context,"CoreServiceRunning:false!",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(context,"CoreServiceRunning:True!",Toast.LENGTH_LONG).show();
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
    public void setHubConnect() {
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        //mName = SharedPreferencesHelper.getSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        //mPicfile = SharedPreferencesHelper.getSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
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

    public void sendLocationToAdmin() {
        if(!maakki_id.equals("10006")){return;}
        ArrayList L = new ArrayList();
        String mess = "sEnLocAtintOAdniM"+":"+latitude+":"+longitude;
        //mess = String.valueOf(hasChanged);
        L.add("90");
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, "", "", mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

