package com.maakki.maakkiapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

//import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class GPSTracker extends Service implements LocationListener {
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_URL =StaticVar.webURL+ "/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    private HubProxy mHub;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 1 mins
    private final Context mContext;
    private final int RESULT_OK = -1;
    // Declaring a Location Manager
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
    //String title="请开启定位功能";
    //String message="请进入应用程式管理，授权"+app_name+"定位权限";
    int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private String app_name = "";
    private String maakki_id,mMemID;
    private SignalRFuture<Void> mSignalRFuture;
    String mess;
    public GPSTracker(Context context) {
        this.mContext = context;
        app_name = context.getString(R.string.app_name);
        getLocation();
    }

    public Location getLocation() {
        try {
            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //&& !isNetworkEnabled
            if (!isGPSEnabled ) {
                errortype=2;
                //canGetLocation = false;
                //showSettingsAlert();
            } else {
                // if GPS Enabled get lat/long using GPS Service
                if (isGPSEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //Log.d("Network", "Network");
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
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            //Log.d("GPS Enabled", "GPS Enabled");
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
                    //showSettingsAlert();
                }else{
                    canGetLocation = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return  ;
        }
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
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

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        String title = "请授权" + app_name + "定位功能";
        String message = "1.点选【应用程序权限】\n2.【读取位置权限】→允许";
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        // Setting Dialog Message
        if (errortype > 1) {
            title = "请开启定位功能";
            message = "手机定位功能尚未开启，请现在开启！";
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        // On pressing Settings button
        alertDialog.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                if (errortype > 1) {
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                }
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                mContext.startActivity(intent);/**/
                //showInstalledAppDetails(mContext,getPackageName());
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
    }

    public static void showInstalledAppDetails(Context context, String packageName) {

        Intent intent = new Intent();

        final String SCHEME = "package";

        final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

        final String APP_PKG_NAME_22 = "pkg";

        final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

        final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    public int getErrorType() {
        return this.errortype;
    }

    public void setErrorType(int errortype) {
        this.errortype = errortype;
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean isLocationChanged=false;
        String strLat = SharedPreferencesHelper.getSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key7, "");
        String strLon = SharedPreferencesHelper.getSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key8, "");
        if (!strLat.equals("") && !strLon.equals("")) {
            if(!String.valueOf(location.getLatitude()).equals(strLat)||!String.valueOf(location.getLongitude()).equals(strLon)){
                isLocationChanged=true;
            }
        }
        if(isLocationChanged){
            Intent intent = new Intent("INVOKE_ONLOCATIONCHANGED");
            mContext.sendBroadcast(intent);
            SharedPreferencesHelper.putSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key7, String.valueOf(latitude));
            SharedPreferencesHelper.putSharedPreferencesString(mContext, SharedPreferencesHelper.SharedPreferencesKeys.key8, String.valueOf(longitude));
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

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

