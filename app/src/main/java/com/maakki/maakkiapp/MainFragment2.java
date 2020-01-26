package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/4.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ant.liao.GifView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainFragment2 extends Fragment {

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    ImageView iv_goc, iv_customerservive, iv_errpage;
    RelativeLayout RL_Errpage;
    private WebView mWebView;
    private String redUrl = StaticVar.webURL+"/community/ecard.aspx";
    private String mMaakkiID = "";
    private String mMemID = "";
    private String mName = "";
    private String CSId = "";
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private String app_name = "";
    //private boolean isDownload = true;
    private boolean isServiceOn;
    private String mPicfile = "";
    private Alarm alarm = new Alarm();
    private DefaultService ds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ds = new DefaultService();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.fragment_main_webview);
        iv_goc = (ImageView) rootView.findViewById(R.id.GocImage);
        iv_customerservive = (ImageView) rootView.findViewById(R.id.CustomerIcon);
        iv_errpage = (ImageView) rootView.findViewById(R.id.Errpage);
        RL_Errpage = (RelativeLayout) rootView.findViewById(R.id.ErrpageLayout);
        setUpWebViewDefaults(mWebView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL and history stack
            mWebView.restoreState(savedInstanceState);
        }
        //Loading image
        final GifView mGifView = (GifView) rootView.findViewById(R.id.gifView);
        mGifView.setGifImage(R.drawable.loading6);
        mWebView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String currentUrl = mWebView.getUrl();
                    //Toast.makeText(getActivity(), "currentUrl:"+currentUrl, Toast.LENGTH_LONG).show();
                    if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                        //&& mWebView.canGoBack()
                        //Toast.makeText(getActivity(), "right:"+currentUrl, Toast.LENGTH_LONG).show();
                        if (currentUrl.contains(StaticVar.DomainName+"/community/ecard.aspx")) {
                            //ignore the click;
                            //
                        } else {
                            mWebView.goBack();
                        }
                        //Toast.makeText(getActivity(), "currentUrl:"+currentUrl, Toast.LENGTH_LONG).show();
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        //set WebView Url
        Bundle args = getArguments();
        redUrl = args.getString("redUrl", "");
        if (mWebView.getUrl() == null) {
            mWebView.loadUrl(redUrl);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    //Pbar.setVisibility(ProgressBar.VISIBLE);
                    mGifView.setVisibility(View.VISIBLE);
                }
                //Pbar.setProgress(progress);
                if (progress == 100) {
                    //Pbar.setVisibility(ProgressBar.GONE);
                    mGifView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                String[] msg = message.split("/");
                if (url.contains(StaticVar.DomainName+"/community/ecard.aspx")) {
                    if (mMemID.equals("")) {
                        mMaakkiID = msg[1];
                        mMemID = msg[2];
                        mPicfile = msg[3];
                        for (int i = 4; i < msg.length; i++) {
                            mName += msg[i];
                            if (i < msg.length - 1) {
                                mName += "/";
                            }
                        }
                        //mName=msg[4];
                        //Toast.makeText(getActivity(), mName + "，Welcome !", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getActivity(), "MemberID:"+mMemID , Toast.LENGTH_LONG).show();
                        // 儲存3個參數在SharedPreferencesHelper+ String.valueOf(isServiceOn)
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, mMaakkiID);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, mMemID);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, mName);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, mPicfile);
                        SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key5, false);
                        //如果isServiceOn=false;
                        if (!isServiceOn) {
                            isServiceOn = true;
                            SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key4, isServiceOn);
                            startLocalService();
                        }/*190109*/
                    }

                }
                result.confirm();
                //result.cancel();
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        //Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }

        });/*190109*/
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new LoadListener(), "HTMLOUT");
        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        mWebView.setWebViewClient(new MyWebViewClient());

        //mWebView.setDownloadListener(new MyWebViewDownLoadListener());
    }

    public void startLocalService() {
        alarm.SetAlarm_imm(getActivity());
    }

    public void stopLocalService() {
        //Toast.makeText(getActivity(), "StopLocalService!", Toast.LENGTH_LONG).show();
        //當使用者登出時，如果service仍在运转，停止DefaultService
        if (isMyServiceRunning(DefaultService.class)) {
            Intent intent = new Intent(getActivity(), DefaultService.class);
            getActivity().stopService(intent);
            alarm.CancelAlarm(getActivity());
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("com.maakki.maakkiapp")) {
                //Toast.makeText(getActivity(), service.service.getClassName(), Toast.LENGTH_LONG).show();
                return true;
            }
        }
        //Toast.makeText(getActivity(), "DefaultService is off!", Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean isCustomerServiceOnlineOrNot() {
        Long currenttime = (new Date()).getTime();
        Long LastCustomerOnlineTime = Long.parseLong(
                SharedPreferencesHelper.getSharedPreferencesString(getActivity(),
                        SharedPreferencesHelper.SharedPreferencesKeys.key13, "0").split(":")[0]
        );
        Long intervalTime = currenttime - LastCustomerOnlineTime;
        Boolean isCustomerServiceOnline = false;
        if (intervalTime < 60 * 1000) {
            isCustomerServiceOnline = true;
            CSId = SharedPreferencesHelper.getSharedPreferencesString(getActivity(),
                    SharedPreferencesHelper.SharedPreferencesKeys.key13, "0").split(":")[1];
        }
        //Toast.makeText(getActivity(), "is:"+currenttime +"/" +intervalTime+"/"+isCustomerServiceOnline+"/"+LastCustomerOnlineTime, Toast.LENGTH_LONG).show();
        return isCustomerServiceOnline;
    }

    class LoadListener {
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void getHtml(final String html) {
            String s = "<title>";
            int ix = html.indexOf(s) + s.length();
            String title = html.substring(ix, html.indexOf("<", ix + 1));
            SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key9, title);
            s = "<img src=\"";
            ix = html.indexOf(s) + s.length();
            String ImageUrl = StaticVar.webURL + html.substring(ix, html.indexOf("\"", ix + 1));
            //Toast.makeText(getActivity(),ImageUrl,Toast.LENGTH_LONG).show();
            SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key10, ImageUrl);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            ErrorWebPage(view);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            // Redirect to deprecated method, so you can use it in all SDK versions
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            ErrorWebPage(view);
            //String url=request.getUrl().toString();
            //view.loadUrl(url);
        }

        public void ErrorWebPage(WebView view) {
            //final String mimeType = "text/html";
            //final String encoding = "utf-8";
            //final String html = "<html><head><h1></h1></head><body style=\"text-align:center;vertical-align:middle;\"><img src=\"page_err.png\" width=\"100%\"/></body></html>";
            //final String html = "<html><head><h1></h1></head><body style=\"text-align:center;vertical-align:middle;\"><h1>hello</h1></body></html>";
            //C:\MaakkiAPP\app\src\main\res\drawable\page_err.png
            //view.loadDataWithBaseURL("file:///android_res/drawable/", html, mimeType, encoding, "");
            //view.loadDataWithBaseURL("file:///android_drawable/", html, mimeType, encoding, "");
            RL_Errpage.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "网速不给力..", Toast.LENGTH_LONG).show();
            SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key5, true);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.contains(StaticVar.DomainName+"/maakki-manual/fileDownload_oncall.aspx")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            if (url.startsWith("https://drive.google.com/file/")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            if (url.startsWith("http://line.me/R/msg/text/")
                    || url.startsWith("https://line.me/ti/p/")
//                    ||url.startsWith("https://m.facebook.com/")
                    ) {

                //Toast.makeText(getActivity(), "parsetUrl:"+url, Toast.LENGTH_LONG).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            //Chat_public
            if (url.contains(StaticVar.DomainName+"/community/Chat.aspx")) {
                Intent intent = new Intent(getActivity(), Chat_main.class);
                Bundle bundle = new Bundle();
                bundle.putString("isPrivate", "");
                bundle.putString("Contactid", "");
                intent.putExtras(bundle);
                startActivity(intent);
            }
            //Chat_private
            if (url.contains(StaticVar.DomainName+"/community/chat.aspx?Contact=")) {
                //Toast.makeText(getActivity(), "url:"+url, Toast.LENGTH_LONG).show();
                int n = url.indexOf('?');
                String u = url.substring(n + 1);
                String[] sa = u.split("&");
                String cMemID = "";
                //Toast.makeText(getActivity(), "u:"+u, Toast.LENGTH_LONG).show();
                if (!sa[0].split("=")[1].equals("")) {
                    cMemID = sa[0].split("=")[1];
                }
                String cName = "";
                if (!sa[1].split("=")[1].equals("")) {
                    cName = sa[1].split("=")[1];
                }
                ;
                try {
                    //cName= URLDecoder.decode(cName.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
                    cName = java.net.URLDecoder.decode(cName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                //Toast.makeText(getActivity(), "cName:"+cName, Toast.LENGTH_LONG).show();
                if (!cMemID.equals(mMemID) & !cMemID.equals("") & !cName.equals("")) {
                    Intent i = new Intent(getActivity(), Chat_red.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("isPrivate", "");
                    bundle.putString("nName", cName);
                    bundle.putString("Contactid", cMemID);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
            if (url.contains(StaticVar.DomainName+"/logout")) {
                mName = SharedPreferencesHelper.getSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
                Toast.makeText(getActivity(), mName + ",bye~", Toast.LENGTH_LONG).show();
                stopLocalService();
                //表示 DefaultService 还在执行

                //清空SharedPreferences資料,isServiceOn設為falsee;
                isServiceOn = false;
                //Toast.makeText(getActivity(), "logout:"+String.valueOf(isServiceOn), Toast.LENGTH_LONG).show();
                SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
                SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
                SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
                SharedPreferencesHelper.putSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
                SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key4, isServiceOn);
                mMaakkiID = "";
                mMemID = "";
                mName = "";
            }
            //if(url.startsWith("http://www.maakki.com/BOStoreData.aspx")){
            //Toast.makeText(getActivity(), ",Hi~"+url, Toast.LENGTH_LONG).show();
            //}

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.contains(StaticVar.DomainName+"/MCoins/MCoinsQuery.aspx")) {
                iv_goc.setVisibility(View.VISIBLE);
                iv_goc.setOnClickListener(new View.OnClickListener() {
                    //@Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), GOCMain2.class);
                        startActivity(i);
                    }
                });
            } else {
                iv_goc.setVisibility(View.INVISIBLE);
            }
            //客服专线图案
            if (url.contains(StaticVar.DomainName+"/Maakki-manual/")) {
                String Contactid = "90";
                //((WebMain2) getActivity()).getContactLastMessageTime(Contactid);
                //((WebMain2) getActivity()).PrepareshareIntent(url);
                //ds.getContactLastMessageTime("90");
/*                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Boolean isCustomerServiceOnline = isCustomerServiceOnlineOrNot();
                //SharedPreferencesHelper.getSharedPreferencesBoolean(getActivity(),SharedPreferencesHelper.SharedPreferencesKeys.key12,false);
                //Boolean isCustomerServiceOnline=true;
                if (isCustomerServiceOnline) {
                    iv_customerservive.setVisibility(View.VISIBLE);
                    if (CSId.equals("53")) {
                        iv_customerservive.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tips_circle_purple));
                    }
                    iv_customerservive.setOnClickListener(new View.OnClickListener() {
                        //@Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Chat_red.class);
                            Bundle bundle = new Bundle();
                            //90 is the Contactid of customerservice
                            //String cMemID = "90";
                            String cName = "客服人员";
                            //isPrivate=false,这是一则客服私讯
                            bundle.putString("isPrivate", "true");
                            bundle.putString("isCustomer", "true");
                            bundle.putString("nName", cName);
                            bundle.putString("Contactid", CSId);
                            bundle.putString("nMessage", "");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
            } else {
                iv_customerservive.setVisibility(View.GONE);
            }
            //只要再回到電子名片主頁，就检视getMaakkiAppData()回应的信息
            if (url.contains(StaticVar.DomainName+"/community/ecard.aspx")) {
                String str = "javascript:alert(getMaakkiAppData10())";
                view.loadUrl(str);
            } else if (url.contains(StaticVar.DomainName+"/BOlist.aspx") || url.contains(StaticVar.DomainName+"/BOStoreData.aspx")) {
                String lat = SharedPreferencesHelper.getSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key7, "0");
                String lon = SharedPreferencesHelper.getSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key8, "0");
                if (!lat.equals("0")) {
                    String str = "javascript:alert(gethide(" + lat + "," + lon + "))";
                    view.loadUrl(str);
                }
            }
            view.loadUrl("javascript:window.HTMLOUT.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            /*imageUrl=SharedPreferencesHelper.getSharedPreferencesString(this, SharedPreferencesHelper.SharedPreferencesKeys.key10, "");
            imageUrl=StaticVar.webURL+"/ashx/showImage.ashx?width=689&height=689&file_id=E16EDECC-ffafcjiejfGcCiHhjpg13";
            if(!imageUrl.equals("")){
                imageLoader.displayImage(imageUrl, imgview);
            }*/
            ((WebMain2) getActivity()).PrepareshareIntent(url);
            //((WebMain) getActivity()).PrepareshareIntent2(url);
            //如果isServiceOn is false,先讓onJsAlert啟動DefaulService.class
            //如果isServiceOn is true,re-check it is running
            //Toast.makeText(getActivity(), "Page is finished:"+String.valueOf(isServiceOn), Toast.LENGTH_LONG).show();
            if (isServiceOn) {
                if (!isMyServiceRunning(DefaultService.class)) {
                    //Toast.makeText(getActivity(), "Restart Service!", Toast.LENGTH_LONG).show();
                    startLocalService();
                }
            }/*190109*/


        }
    }

}
