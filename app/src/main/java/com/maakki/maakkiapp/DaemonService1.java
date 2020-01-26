package com.maakki.maakkiapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class DaemonService1 extends Service  implements LocationListener {
    private static final int CORE_CHECK_INTERVAL = 1000 * 10;
    private Intent daemonIntent = new Intent("com.maakki.maakkiapp.DAEMON");
    private Thread daemonThread = null;
    private String maakki_id,mMemID;
    private SignalRFuture<Void> mSignalRFuture;
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_URL = StaticVar.webURL+"/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    private HubProxy mHub;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 1 mins
    private Context context;
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    int errortype = 1;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    //Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        getLocation();
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
                    sendBroadcast(daemonIntent);
                    Thread.sleep(CORE_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
    private Location getLocation() {
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");

        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert(2);
                //setErrorType(2);
                //showDialog(new MapsActivity(),title,message, "开启", "取消").show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (latitude == 0 & longitude == 0) {
                    //Toast.makeText(getApplicationContext(), "请进入应用程式管理，开启定位权限",Toast.LENGTH_LONG).show();
                    showSettingsAlert(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public void showSettingsAlert(int errortype) {
        setErrorType(errortype);
        String title = "请授权 Maakki.10 定位功能";
        String message = "进入【应用管理】，授权 Maakki.10 获得定位权限\n\n1.在列表中找到 Maakki.10 \n2.点选【应用程式权限】\n3.【读取位置权限】→允许";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        if (errortype > 1) {
            title = "请开启定位功能";
            message = "手机定位功能尚未开启，请现在开启！";
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        // On pressing Settings button
        alertDialog.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                if (getErrorType() > 1) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                } else {
                    intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                    context.startActivity(intent);
                }
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public int getErrorType() {
        return this.errortype;
    }

    public void setErrorType(int errortype) {
        this.errortype = errortype;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        latitude=getLatitude();
        longitude=getLongitude();
        if(maakki_id.equals("17141")){
            setHubConnect();
            sendLocationToAdmin();
        }
        Intent intent = new Intent("INVOKE_ONLOCATIONCHANGED");
        context.sendBroadcast(intent);
        SharedPreferencesHelper.putSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key7, String.valueOf(latitude));
        SharedPreferencesHelper.putSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key8, String.valueOf(longitude));
    }
    public void setHubConnect() {
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
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
        if(!maakki_id.equals("17141")){return;}
        ArrayList L = new ArrayList();
        String mess = "sEnLocAtintOAdniM"+":"+latitude+":"+longitude;
        L.add("90");
        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, "", "", mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
