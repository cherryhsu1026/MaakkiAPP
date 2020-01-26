package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/4.
 */

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

//import android.support.v7.app.ActionBarActivity;


//import me.leolin.shortcutbadger.ShortcutBadger;


public class WebMain extends AppCompatActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    //private String shareUrl = "http://www.maakki.cc/";
    private static final String TAG = WebMain.class.getSimpleName();
    private static final int REQUEST_CODE_QR_SCAN = 101;
    //MainFragment mf = new MainFragment();
    Bundle args = new Bundle();
    Toolbar toolbar;
    String imageUrl;
    Intent shareIntent;
    ImageView imgview;
    private ShareActionProvider mShareActionProvider;
    private String redUrl = "http://www.maakki.com/community/ecard.aspx";
    //private String mMemID="";
    private String mName = "";
    private ImageLoader imageLoader;
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ShortcutBadger.with(getApplicationContext()).remove();
        Toast.makeText(getApplicationContext(), "WebMainonCreat()", Toast.LENGTH_LONG).show();
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            redUrl = bundle.getString("redirUrl");
        } else {
            redUrl = "http://www.maakki.com/community/NotifyMain.aspx";
        }
        args.putString("redUrl", redUrl);
        //args.putString("mMemID", mMemID);
        //args.putString("mName", mName);
        //mf.setArguments(args);
        /*if (savedInstanceState == null) {
            Log.v(TAG, "MainFragment Creation");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mf, "mf")
                    .commit();
        }*/
        //AsyncCallWS_PrepareShare PreShareTask = new AsyncCallWS_PrepareShare();
        //PreShareTask.execute();
        /*imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();

        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.getInstance().init(config);*/
        //PrepareShare();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu1, menu);

        //Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.options_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        //setMenuBackground();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //MainFragment fragment = (MainFragment) getFragmentManager().findFragmentByTag("mf");
        //WebView wv=(WebView)fragment.getView().findViewById(R.id.fragment_main_webview);
        //shareUrl=wv.getUrl();
        //mShareActionProvider.setOnShareTargetSelectedListener(this);
        //mShareActionProvider.setShareIntent(createShareIntent(shareUrl));
        //mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        //mShareActionProvider.setShareIntent(getShareIntent());
        return true;
    }

    public void PrepareshareIntent(String url) {

        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String title = SharedPreferencesHelper.getSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key9, "Maakki.cc 幸福小玛吉") + "\n" + url;
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT,title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        /*imgview.setVisibility(View.VISIBLE);
        Uri bmpUri = getLocalBitmapUri(imgview);
        //Uri bmpUri= Uri.parse("android.resource://" + getPackageName()+ "/drawable/" + "mgs_upward");
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            //shareIntent.setType("image*//**//*");
        }
        else{
            imgview.setVisibility(View.INVISIBLE);
        }*/
        mShareActionProvider.setShareIntent(shareIntent);
    }
/*190109
    public void PrepareshareIntent2(String url) {
        String title = SharedPreferencesHelper.getSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key9, "Maakki.cc 幸福小玛吉") + "\n" + url;
        //shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);

        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> resInfo = pm.queryIntentActivities(shareIntent, 0);

        ArrayList<String> packageAllowed = new ArrayList();
        packageAllowed.add("com.twitter.android");
        packageAllowed.add("com.facebook.katana");
        packageAllowed.add("com.google.android.gm");
        packageAllowed.add("com.whatsapp");
        packageAllowed.add("com.evernote");
        packageAllowed.add("ch.threema.app");
        packageAllowed.add("com.skype.raider");
        packageAllowed.add("com.tencent.mm");
        packageAllowed.add("com.tencent.mm.ui.tools.ShareToTimeLineUI");

        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo resolveInfo : resInfo) {
                String packageName = resolveInfo.activityInfo.packageName;

                if (packageAllowed.contains(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Text");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");

                    // This is new!!!
                    intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));

                    intent.setPackage(packageName);
                    targetedShareIntents.add(intent);
                }
            }

            int size = targetedShareIntents.size();
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(size - 1), "Share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
            //startActivity(chooserIntent);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
*/

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        //MainFragment fragment = (MainFragment) getFragmentManager().findFragmentByTag("mf");
        //WebView wv = (WebView) fragment.getView().findViewById(R.id.fragment_main_webview);
        //String shareUrl = wv.getUrl();
        //MainFragment.LoadListener s
        //String imgurl=wv.getHtml
        Uri uri = Uri.parse("file://my_picture");
        //Toast.makeText(getApplicationContext(), "url:"+shareUrl , Toast.LENGTH_SHORT).show();
        intent = new Intent(Intent.ACTION_SEND);
        //intent.setType("text/plain");
        intent.setType("image*//**//*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //intent.putExtra(Intent.EXTRA_TEXT,shareUrl);
        //intent.putExtra(Intent.EXTRA_TEXT, wv.getTitle());

        source.setShareIntent(intent);
        //changeShareIntent(shareUrl);*//*

        return (false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int item_id = item.getItemId();

        switch (item_id) {
            case R.id.qr_Activity:
                Intent i = new Intent(WebMain.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
                try {
                    //start the scanning activity from the com.google.zxing.client.android.SCAN intent
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    //on catch, show the download dialog
                    //showDialog(WebMain.this, "欢迎使用二维码系统", "请先下载扫描器，再重新开启玛吉APP，这个程序只需要一次。", "开始", "退出").show();
                }
                break;
//          case R.id.uploadFile:
//                Intent intent=new Intent(this,uploadFile.class);
//                startActivity(intent);
//                break;
            case R.id.maakki_web:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.chat_Activity:
                //String nName=p.gettitle();
                //Toast.makeText(getApplicationContext(), "p.gettitle():"+p.gettitle(), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("isPrivate", "");
                bundle.putString("Contactid", "");
                Intent intent3 = new Intent(this, Chat_main.class);
                intent3.putExtras(bundle);
                startActivity(intent3);
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
/*190109
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            //Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(WebMain.this).create();
                alertDialog.setTitle("扫码错误");
                alertDialog.setMessage("没有正确扫描到二维码");
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
            MainFragment f1 = new MainFragment();
            Bundle args1 = new Bundle();
            redUrl = result;
            args1.putString("redUrl", redUrl);
            //args1.putString("mName", mName);
            //args1.putString("mMemID", mMemID);
            //MainFragment f = new MainFragment();
            f1.setArguments(args1);
            //if (savedInstanceState == null) {
            Log.v(TAG, "MainFragment Creation");
            getFragmentManager().beginTransaction()
                    .add(R.id.container, f1, "mf")
                    .commit();
            //}
            *//*AlertDialog alertDialog = new AlertDialog.Builder(WebMain.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();*//*

        }
    }


    private class AsyncCallWS_PrepareShare extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");

            //imageUrl=SharedPreferencesHelper.getSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key10, "");
            imageUrl = "http://www.maakki.com/ashx/showImage.ashx?width=689&height=689&file_id=E16EDECC-ffafcjiejfGcCiHhjpg13";
            if (!imageUrl.equals("")) {
                imageLoader.displayImage(imageUrl, imgview);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String Msg = "完成";
            imgview.setVisibility(View.VISIBLE);
            //Uri bmpUri = getLocalBitmapUri(imgview);
            //Uri bmpUri= Uri.parse("android.resource://" + getPackageName()+ "/drawable/" + "mgs_upward");
            //if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            //shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            //shareIntent.setType("image/*");
            //}
            //else{
            //imgview.setVisibility(View.INVISIBLE);
        }
        //mShareActionProvider.setShareIntent(shareIntent);
        //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_LONG).show();
    }

    //@Override
    protected void onPreExecute() {
        Toast.makeText(getApplicationContext(), "下载中..", Toast.LENGTH_LONG).show();
    }

    //@Override
    protected void onProgressUpdate(Void... values) {
        //Log.i(TAG, "onProgressUpdate");
    }*/

    /*190109@Override
    protected void onStart() {
        super.onStart();
        Context context = getApplicationContext();
        Boolean isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(context, SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        Boolean isServiceRunning = isMyServiceRunning(context, DefaultService.class);
        //Toast.makeText(context, "/On:"+String.valueOf(isServiceOn)+"/R:"+String.valueOf(isServiceRunning), Toast.LENGTH_LONG).show();
        //if isServiceOn 是false 表示是logout状态 不要重启ApplicationContext(),
        if (isServiceOn) {
            //Toast.makeText(context, "Reconnecting..", Toast.LENGTH_LONG).show();
            if (!isServiceRunning) {
                //context.stopService(new Intent(context, DefaultService.class));
                *//*try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }*//*
                Alarm alarm = new Alarm();
                alarm.SetAlarm_imm(context.getApplicationContext());
                Toast.makeText(getApplicationContext(), "Service Reactivated", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("com.maakki.maakkiapp")) {
                return true;
            }
        }
        return false;
    }
}
