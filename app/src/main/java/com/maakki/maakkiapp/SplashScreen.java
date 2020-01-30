package com.maakki.maakkiapp;

/**
 * Created by ryan on 2015/12/13.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.ant.liao.GifView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2400;
    private GifView mGifView;
    private ImageView imgV;
    private Context ct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ct = this;
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_splash);
        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                // close this activity
                SplashScreen.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, SPLASH_TIME_OUT);

    }

    private void init() {
        /*imgV=(ImageView) findViewById(R.id.imgView);
        imgV.setImageResource(R.drawable.welcome0013);*/
        mGifView = (GifView) findViewById(R.id.gifView);
        mGifView.setGifImage(R.drawable.logo0504);
        //ImageView iv_splash=(ImageView)findViewById(R.id.iv_splash);
        /*mGifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(" Click ");
            }		});*/
        //mGifView.setShowDimension(300, 300);
        // /加载方式
        //mGifView.setGifImageType(GifView.GifImageType.COVER);
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        new CheckNotice().execute("1");
    }*/

    public class CheckNotice extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<String, String>();
            String result = Utils.callWebService(StaticVar.webURL + "webService.asmx", StaticVar.webURL, "getNotice", map);
            return result;
        }

        protected void onPostExecute(String result) {
            JSONObject json_read = null;
            Log.e("cherry", "get notice result:" + result);
            try {
                json_read = new JSONObject(result);
                String errCode = json_read.getString("errCode");
                Log.e("Cherry", "errCode:" + errCode);
                String msg = json_read.getString("notice");
                if (errCode.equals("1")) {
                    new AlertDialog.Builder(ct)
                            .setTitle("Maakki")
                            .setMessage(msg)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//按下確定按鈕處理的事件
                                    dialog.dismiss();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity

                                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                            startActivity(i);
                                            // close this activity
                                            SplashScreen.this.finish();
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        }
                                    }, SPLASH_TIME_OUT);
                                }
                            })
                            .show();
                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
