package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/13.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by ryan on 2016/7/26.
 */

public class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    Bitmap bmp;

    public DownloadBitmapTask(Bitmap bmp) {
        this.bmp = bmp;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            mIcon11 = BitmapFactory.decodeStream(
                    (InputStream) new URL(urldisplay).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }*/
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        //bmp.setImageBitmap(result);
    }
}