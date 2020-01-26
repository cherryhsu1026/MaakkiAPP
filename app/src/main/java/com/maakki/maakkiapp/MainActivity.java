package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    private String redUrl = "http://www.maakki.com/community/ecard.aspx";
    public final static String PREFERENCE_NAME = "mypreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Context ctx=getApplicationContext();
        SharedPreferencesHelper.putSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        SharedPreferencesHelper.putSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        SharedPreferencesHelper.putSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        SharedPreferencesHelper.putSharedPreferencesBoolean(this, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        SharedPreferencesHelper.putSharedPreferencesBoolean(this, SharedPreferencesHelper.SharedPreferencesKeys.key5, false);
        //SharedPreferencesHelper.putSharedPreferencesBoolean(this, SharedPreferencesHelper.SharedPreferencesKeys.key6, true);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String greetings = sharedPrefs.getString("welcome_message", "因为分享 所以丰盛");
        Toast.makeText(getApplicationContext(), greetings, Toast.LENGTH_LONG).show();
        /*190109if (!isMyServiceRunning()) {
            this.stopService(new Intent(this, DefaultService.class));
        }*/
        /* Intent intent = new Intent(this, WebMain.class);
        Bundle bundle = new Bundle();
        bundle.putString("redirUrl", redUrl);
        intent.putExtras(bundle);
        startActivity(intent);*/

        Intent intent = new Intent(this, WebMain2.class);
        intent.putExtra("redirUrl", redUrl);
        startActivity(intent);
    }

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

    /*@Override
    protected void onStart() {
        super.onStart();
        if(isMyServiceRunning()){
            Intent intent=new Intent(MainActivity.this,CoreService.class);
            stopService(intent);
            Toast.makeText(getApplicationContext(), "Service killed", Toast.LENGTH_LONG).show();
        }

    }*/
    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("maakkiapp.CoreService")) {
                return true;
            }
        }
        return false;
    }
}


