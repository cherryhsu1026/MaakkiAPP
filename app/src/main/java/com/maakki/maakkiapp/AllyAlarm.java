package com.maakki.maakkiapp;
/**
 * Created by ryan on 2016/8/5.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AllyAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Long ally_id=intent.getExtras().getLong("ally_id");
        Toast.makeText(context,"onReceive:"+ally_id,Toast.LENGTH_LONG).show();
        Intent serviceIntent = new Intent(context,WindowService.class);
        String getType="StartAllySponsor";
        Bundle bundle=new Bundle();
        bundle.putString("getType", getType);
        bundle.putLong("ally_id", ally_id);
        serviceIntent.putExtras(bundle);
        context.startService(serviceIntent);
    }
}
