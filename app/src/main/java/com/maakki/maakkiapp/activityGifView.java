package com.maakki.maakkiapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

/**
 * Created by ryan on 2015/12/13.
 */
public class activityGifView extends Activity {

    private GifView mGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init() {
        mGifView = (GifView) findViewById(R.id.gifView);
        mGifView.setGifImage(R.drawable.splash);
        mGifView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(" Click ");
            }
        });
        mGifView.setShowDimension(300, 300);
        // /加载方式
        mGifView.setGifImageType(GifImageType.COVER);
    }
}
