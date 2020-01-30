package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 如果是登出的状态，DeaultService停止，Alarm停止
 */


public class NetworkChangeReceiver extends BroadcastReceiver {
    //private boolean isServiceOn=false;
    private String redUrl = StaticVar.webURL+"community/ecard.aspx";
    Context context;
    //CoreAlarm alarm = new CoreAlarm();
    CoreFragment cf=new CoreFragment();
    //@Override
    public void onReceive(final Context context, final Intent intent) {
        this.context=context;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Toast.makeText(context, "网路连线变动:"+activeNetwork.toString(), Toast.LENGTH_LONG).show();
        boolean isConnected = false ;
        if(activeNetwork != null){
            isConnected = true ;
        }

//        && activeNetwork.isConnectedOrConnecting()
        if(isConnected){
            Boolean isPageErr=SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key5, false);
            Boolean isServiceOn=SharedPreferencesHelper.getSharedPreferencesBoolean(context,SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
            //如果网页显示是无法连结到互联网，重启 MainActivity.class
            if(isPageErr){
                Toast.makeText(context, "Reloading..", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(context, MainActivity.class);
                //context.stopService(new Intent(context, DefaultService.class));
                //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }else if (isServiceOn) {
                //必须先停掉旧的没用的服务
/*                Intent it=new Intent(context,CoreService.class);
                if(isMyServiceRunning()){
                    context.stopService(it);
                }
                context.startService(it);*/
            }
        }
        //如果互联网连线中断

        else{
            //WebView view=(WebView)cf.getView().findViewById(R.id.fragment_main_webview);
            //RelativeLayout RL_Errpage= (RelativeLayout)view.findViewById(R.id.ErrpageLayout);
            //RL_Errpage.setVisibility(View.VISIBLE);
            Toast.makeText(context, "连线不给力..", Toast.LENGTH_LONG).show();
            //SharedPreferencesHelper.putSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key5, true);
        }
    }
    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("maakkiapp.CoreService")) {
                return true;
            }
        }
        return false;
    }


}
