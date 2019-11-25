package com.maakki.maakkiapp;

/**
 * Created by ryan on 2015/12/13.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.ant.liao.GifView;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2400;
    private GifView mGifView;
    private ImageView imgV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


}
