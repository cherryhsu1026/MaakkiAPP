package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


/**
 * Created by ryan on 2017/7/17.
 */

public class WebMain2 extends AppCompatActivity implements ShareActionProvider.OnShareTargetSelectedListener {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    Bundle args = new Bundle();
    Context context;
    private String redUrl = "https://www.maakki.cc/community/ecard.aspx";
    //float scrollp=0;
    int yPos=0;
    private ShareActionProvider mShareActionProvider;
    private static final String CORE_SERVICE = "com.maakki.maakkiapp.CoreService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main2);
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            redUrl = bundle.getString("redirUrl");
            yPos = bundle.getInt("yPos");
        } else {
            redUrl = "https://www.maakki.cc/community/NotifyMain.aspx";
        }
        //MainFragment2 mf = new MainFragment2();
        CoreFragment mf = new CoreFragment();
        args.putString("redUrl", redUrl);
        args.putInt("yPos", yPos);
        mf.setArguments(args);
        if (savedInstanceState == null) {
            //Log.v(TAG, "MainFragment Creation");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mf, "mf")
                    .commit();
        }
        //finally,I can get message from coreservice
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_intervalTime_CORESERVICE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intervalTime=intent.getStringExtra("intervalTime2");
                Toast.makeText(context,"intervalTime:"+intervalTime,Toast.LENGTH_LONG).show();
                //ServiceUtil.showAlertDialog(context,"Parameters",intervalTime);
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ShortcutBadger.with(getApplicationContext()).remove();
        /*Boolean isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        if (isServiceOn) {
            if (!ServiceUtil.isServiceRunning(context, CORE_SERVICE)) {
                ServiceUtil.startService(context, CORE_SERVICE);
            }
        }*/
        //Toast.makeText(getApplicationContext(), "order_id:"+order_id+"\nlistOrderDetail.size():"+listOrderDetail.size(), Toast.LENGTH_LONG).show();
    }/**/

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        CoreFragment fragment = (CoreFragment) getFragmentManager().findFragmentByTag("mf");
        WebView wv = (WebView) fragment.getView().findViewById(R.id.fragment_main_webview);
        String shareUrl = wv.getUrl();
        //MainFragment.LoadListener s
        //String imgurl=wv.getHtml
        Uri uri = Uri.parse("file://my_picture");
        //Toast.makeText(getApplicationContext(), "url:"+shareUrl , Toast.LENGTH_SHORT).show();
        intent = new Intent(Intent.ACTION_SEND);
        //intent.setType("text/plain");
        intent.setType("image*//**//*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //intent.putExtra(Intent.EXTRA_TEXT,shareUrl);
        intent.putExtra(Intent.EXTRA_TEXT, wv.getTitle());

        source.setShareIntent(intent);
        //changeShareIntent(shareUrl);*//*

        return (false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu1, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        //setMenuBackground();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.qr_Activity:
                Intent i = new Intent(this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                break;
            case R.id.maakki_web:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                //Intent intent1 = new Intent(this, WebMain2.class);
                //intent1.putExtra("redirUrl", redUrl);
                //startActivity(intent1);
                break;
            case R.id.chat_Activity:
                Intent intent = new Intent(this, FriendlistActivity.class);
                startActivity(intent);
                break;

            case R.id.Map_Activity:
                Intent intentMap = new Intent(this, StoreList.class);
                startActivity(intentMap);

                break;
            case R.id.notice_Activity:
                Intent intent4 = new Intent(this, PreNotificationList.class);
                startActivity(intent4);
                break;
            case R.id.settings:
                Intent intent2 = new Intent(this, QuickPrefsActivity.class);
                startActivity(intent2);
                break;
            case R.id.pos:
                Intent intent5 = new Intent(this, POS_MainActivity.class);
                intent5.putExtra("order_id", "0");
                startActivity(intent5);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void PrepareshareIntent(String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String title = SharedPreferencesHelper.getSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key9, "Maakki.cc 幸福小玛吉") + "\n" + url;
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT,title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        mShareActionProvider.setShareIntent(shareIntent);
    }
    //for ErrPage onClick
    public void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            //Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(WebMain2.this).create();
                alertDialog.setTitle("扫码错误");
                alertDialog.setMessage("亲~这是二维码吗？");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            //Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            CoreFragment f1 = new CoreFragment();
            Bundle args1 = new Bundle();
            redUrl = result;
            args1.putString("redUrl", redUrl);
            //args1.putString("mName", mName);
            //args1.putString("mMemID", mMemID);
            //MainFragment f = new MainFragment();
            f1.setArguments(args1);
            //if (savedInstanceState == null) {
            //Log.v(TAG, "MainFragment Creation");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, f1, "mf")
                    .commit();
            //}
            /*AlertDialog alertDialog = new AlertDialog.Builder(WebMain2.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();*/

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}