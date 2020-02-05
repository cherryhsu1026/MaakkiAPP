package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/4.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
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

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CoreFragment extends Fragment {

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    ImageView iv_addSponsor,iv_systemadmin, iv_openIA, iv_goc, iv_customerservice, iv_errpage, iv_Supplier;
    private static final String CORE_SERVICE = "com.maakki.maakkiapp.CoreService";
    private int yPos = 0;
    float scrollp;
    private WebView mWebView;
    private String redUrl = StaticVar.webURL+"community/ecard.aspx";
    private String mMaakkiID = "", errMsg = "", identity = "";
    private String mMemID = "";
    private String mName = "";
    private String CSId = "";
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private String ProList_url = "";
    //private boolean isPi;
    private boolean isServiceOn;
    private String mPicfile = "";
    //private DefaultService ds;
    private RelativeLayout RL_suppliericon, RL_Errpage;
    private CoreAlarm ca = new CoreAlarm();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ds = new DefaultService();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.fragment_main_webview);
        iv_openIA = (ImageView) rootView.findViewById(R.id.openIA);
        iv_addSponsor= (ImageView) rootView.findViewById(R.id.addSponsor);
        iv_systemadmin = (ImageView) rootView.findViewById(R.id.systemadmin);
        iv_goc = (ImageView) rootView.findViewById(R.id.GocImage);
        iv_customerservice = (ImageView) rootView.findViewById(R.id.CustomerIcon);
        RL_suppliericon = (RelativeLayout) rootView.findViewById(R.id.RL_suppliericon);
        iv_Supplier = (ImageView) rootView.findViewById(R.id.SupplierIcon);
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
                        } else if(currentUrl.contains(StaticVar.DomainName+"/Mall/proDetail.aspx?pro_id=")){
                            //String historyUrl="";
                            //myWebView = (WebView) findViewById(R.id.webViewContent);
                            //WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
                            //if (mWebBackForwardList.getCurrentIndex() > 0){
                                //historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
                                //Toast.makeText(getActivity(),"ProList_url:"+ProList_url,Toast.LENGTH_LONG).show();
                                if(!ProList_url.isEmpty()){
                                    mWebView.loadUrl(ProList_url);
                                }else{
                                     mWebView.goBack();
                                }
                            //}
                        } else {
                            mWebView.goBack();
                        }
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        //set WebView Url
        Bundle args = getArguments();
        redUrl = args.getString("redUrl", "");
        yPos = args.getInt("yPos",0);
        //Toast.makeText(getActivity(),"yPos:"+yPos,Toast.LENGTH_LONG).show();
        if (mWebView.getUrl() == null) {
            mWebView.loadUrl(redUrl);
            //float webviewsize = mWebView.getContentHeight() - mWebView.getTop();
            //float positionInWV = webviewsize * scrollp;
            //yPos = Math.round(mWebView.getTop() + positionInWV);
        }
        /*mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v,MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_MOVE) {
                   yPos = mWebView.getScrollY();

               }
               return false;
            }
        });
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"putIntyPos:"+yPos, Toast.LENGTH_LONG).show();
                SharedPreferencesHelper.putSharedPreferencesInt(getActivity(),SharedPreferencesHelper.SharedPreferencesKeys.key24,yPos);
            }
        });*/
        mWebView.setWebChromeClient(new WebChromeClient() {

            WebView newWebView;
            @Override
            public void onCloseWindow(WebView window) {//html中，用js调用.close(),会回调此函数
                super.onCloseWindow(window);
                //DebugUtil.err("关闭当前窗口");
                if (newWebView != null) {
                    mWebView.removeView(newWebView);
                }
            }
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg)
            {
                WebView newWebView = new WebView(getActivity());
                mWebView.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
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
                if (url.contains(StaticVar.DomainName+"/community/ecard.aspx")) {
                    //Toast.makeText(getActivity(),"message:"+message,Toast.LENGTH_LONG).show();
                    if (mMemID.equals("")) {
                        String[] msg = message.split("/");
                        mMaakkiID = msg[1];
                        mMemID = msg[2];
                        mPicfile = msg[3];
                        for (int i = 4; i < msg.length; i++) {
                            mName += msg[i];
                            if (i < msg.length - 1) {
                                mName += "/";
                            }
                        }
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, mMaakkiID);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, mMemID);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, mName);
                        SharedPreferencesHelper.putSharedPreferencesString(getActivity().getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, mPicfile);
                        SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key5, false);
                        //SharedPreferencesHelper.putSharedPreferencesLong(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key24, new Date().getTime());
                        //如果isServiceOn=false;
                        if (!isServiceOn) {
                            isServiceOn = true;
                            SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key4, isServiceOn);
                            startLocalService();
                        }
                    }
                }else
                if (url.contains(StaticVar.DomainName+"/Mall/proDetail.aspx")) {
                    //Toast.makeText(getActivity(),"message:"+message,Toast.LENGTH_LONG).show();
                    ProList_url=message;
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
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
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
        //Toast.makeText(getActivity(),"isServiceOn:"+String.valueOf(isServiceOn),Toast.LENGTH_LONG).show();
        if (isServiceOn) {
            //必须先停掉旧的没用的服务
            /*Intent intent=new Intent(getActivity(),CoreService.class);
            if(!isMyServiceRunning()){
                getActivity().stopService(intent);
            }
            getActivity().startService(intent);*/
            ca.setAlarm_imm(getActivity());
        }

    }

    public void stopLocalService() {
        //Toast.makeText(getActivity(), "StopLocalService!", Toast.LENGTH_LONG).show();
        //當使用者登出時，如果service仍在运转，停止DefaultService
        if (isMyServiceRunning()) {
            Intent intent = new Intent(getActivity(), CoreService.class);
            getActivity().stopService(intent);
            ca.CancelAlarm(getActivity());
        }
    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains("maakkiapp.CoreService")) {
                return true;
            }
        }
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
        if (intervalTime < 1000 * 60) {
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
        WebView newWebView;

        /*@SuppressWarnings("deprecation")
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
        }

        public void ErrorWebPage(WebView view) {
            RL_Errpage.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "网速不给力..", Toast.LENGTH_LONG).show();
            SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key5, true);
        }*/

        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.contains("/community/Friendlist.aspx")){
                Intent i=new Intent(getActivity(),FriendList.class);
                startActivity(i);
            }
            else if (url.contains( StaticVar.DomainName+"/maakki-manual/fileDownload_oncall.aspx")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            else if (url.startsWith("https://drive.google.com/file/")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            else if (url.startsWith("http://line.me/R/msg/text/")
                    || url.startsWith("https://line.me/ti/p/")
//                    ||url.startsWith("https://m.facebook.com/")
            ) {

                //Toast.makeText(getActivity(), "parsetUrl:"+url, Toast.LENGTH_LONG).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }

            else if (url.contains( StaticVar.DomainName+"/logout")) {
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
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Toast.makeText(getActivity(), url, Toast.LENGTH_LONG).show();
            /*if(url.contains(StaticVar.DonameName+"/Mall/ProList.aspx?")){
                yPos=SharedPreferencesHelper.getSharedPreferencesInt(getActivity(),SharedPreferencesHelper.SharedPreferencesKeys.key24,0);
                Toast.makeText(getActivity(),"yPos:"+yPos,Toast.LENGTH_LONG).show();
                mWebView.scrollTo(0,100);
            }*/
            if (url.contains(StaticVar.DomainName+"/dream/addSponsor.")) {
                    iv_addSponsor.setVisibility(View.VISIBLE);
            }else{
                iv_addSponsor.setVisibility(View.GONE);
            }
            iv_addSponsor.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    //iv_addSponsor.setVisibility(View.GONE);
                    Intent intent=new Intent(getActivity(), addSponsor.class);
                    startActivity(intent);
                }
            });
            if (url.contains(StaticVar.DomainName+"/Mall/StoreInfo.aspx?supplier_id=")) {
                final String cSupplierId = url.split("=")[1];
                final String sName = "";
                SignalRUtil su = new SignalRUtil();
                //String messAge=su.CheckSomeoneOnline(getActivity(),cSupplierId);
                //if(true){
                if (su.CheckSomeoneOnline(getActivity(), cSupplierId)) {
                    RL_suppliericon.setVisibility(View.VISIBLE);
                    iv_Supplier.setOnClickListener(new View.OnClickListener() {
                        //@Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Chat_red.class);
                            Bundle bundle = new Bundle();
                            //String cName = "客服人员";
                            //isPrivate=false,这是一则客服私讯
                            bundle.putString("isPrivate", "true");
                            bundle.putString("isCustomer", "false");
                            bundle.putString("nName", sName);
                            bundle.putString("Contactid", cSupplierId);
                            bundle.putString("nMessage", "");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                } else {
                    RL_suppliericon.setVisibility(View.INVISIBLE);
                }
                //Toast.makeText(getActivity(),"Supplier is.."+messAge,Toast.LENGTH_LONG).show();

            } else {
                RL_suppliericon.setVisibility(View.INVISIBLE);
            }
            //set OpenIA entry icon visible
            iv_openIA.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    iv_openIA.setVisibility(View.GONE);
                    Intent i = new Intent(getActivity(), Open_IA.class);
                    startActivity(i);
                }
            });
            if (url.contains(StaticVar.DomainName+"/IA")) {
                AsyncCallWS_getIdentity getIdentityTask = new AsyncCallWS_getIdentity();
                getIdentityTask.execute();
            } else {
                iv_openIA.setVisibility(View.INVISIBLE);
            }
            if (url.contains(StaticVar.DomainName+"/RA") & (mMaakkiID.equals("1") || mMaakkiID.equals("10006"))) {
                iv_openIA.setVisibility(View.VISIBLE);
            } else {
                iv_openIA.setVisibility(View.INVISIBLE);
            }
            //set systemadmin entry icon visible
            iv_systemadmin.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    iv_systemadmin.setVisibility(View.GONE);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String bonus_date = df.format(new Date().getTime());
                    Bundle bundle = new Bundle();
                    bundle.putString("role", "admin");
                    bundle.putString("bonus_date", bonus_date);
                    bundle.putString("query_type", "remittance");
                    Intent i = new Intent(getActivity(), GetRemitList.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            if (url.contains(StaticVar.DomainName+"/admin/") & (mMaakkiID.equals("1") || mMaakkiID.equals("10006"))) {
                iv_systemadmin.setVisibility(View.VISIBLE);
            } else {
                iv_systemadmin.setVisibility(View.GONE);
            }
            //set imaimai entry icon visible
            if (url.contains(StaticVar.DomainName+"/MCoins/")) {
                iv_goc.setVisibility(View.VISIBLE);
                Boolean isRead = SharedPreferencesHelper.getSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key15, false);
                Integer sharedpref_date = SharedPreferencesHelper.getSharedPreferencesInt(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key17, 0);
                Integer string_statement_date = Integer.parseInt(getActivity().getResources().getString(R.string.statement_ver_date));
                if (string_statement_date > sharedpref_date) {
                    isRead = false;
                    SharedPreferencesHelper.putSharedPreferencesBoolean(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key15, false);
                }
                final boolean bol = isRead;
                iv_goc.setOnClickListener(new View.OnClickListener() {
                    //@Override
                    public void onClick(View v) {
                        //iv_goc.setVisibility(View.GONE);
                        Intent i;
                        if (bol) {
                            i = new Intent(getActivity(), GOCMain2.class);
                        } else {
                            i = new Intent(getActivity(), imaimai_Main.class);
                        }
                        startActivity(i);
                    }
                });
            } else {
                iv_goc.setVisibility(View.INVISIBLE);
            }
            //客服专线图案
            if (url.contains(StaticVar.DomainName+"/Maakki-manual/")) {
                Boolean isCustomerServiceOnline = isCustomerServiceOnlineOrNot();
                //SharedPreferencesHelper.getSharedPreferencesBoolean(getActivity(),SharedPreferencesHelper.SharedPreferencesKeys.key12,false);
                //Boolean isCustomerServiceOnline=true;
                if (isCustomerServiceOnline) {
                    iv_customerservice.setVisibility(View.VISIBLE);
                    if (CSId.equals("53")) {
                        iv_customerservice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tips_circle_purple));
                    } else if (CSId.equals("90")) {
                        iv_customerservice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tips_circle_orange));
                    }
                    iv_customerservice.setOnClickListener(new View.OnClickListener() {
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
                iv_customerservice.setVisibility(View.GONE);
            }
            //只要再回到電子名片主頁，就检视getMaakkiAppData()回应的信息
            if (url.contains(StaticVar.DomainName+"/community/ecard.aspx")) {
                String str = "javascript:alert(getMaakkiAppData10())";
                view.loadUrl(str);
            } else if (url.contains(StaticVar.DomainName+"/Mall/proDetail.aspx")) {
                String str = "javascript:alert(getWindowlocation_Maakk10())";
                view.loadUrl(str);
            }else if (url.contains(StaticVar.DomainName+"/BOlist.aspx") || url.contains(StaticVar.DomainName+"/BOStoreData.aspx")) {
                String lat = SharedPreferencesHelper.getSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key7, "0");
                String lon = SharedPreferencesHelper.getSharedPreferencesString(getActivity(), SharedPreferencesHelper.SharedPreferencesKeys.key8, "0");
                if (!lat.equals("0")) {
                    String str = "javascript:alert(gethide(" + lat + "," + lon + "))";
                    view.loadUrl(str);
                }
            }
            view.loadUrl("javascript:window.HTMLOUT.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            ((WebMain2) getActivity()).PrepareshareIntent(url);
            //view.scrollTo(0, positionY);
        }
    }

    private float calculateProgression(WebView content) {
        float positionTopView = content.getTop();
        float contentHeight = content.getContentHeight();
        float currentScrollPosition = content.getScrollY();
        float percentWebview = (currentScrollPosition - positionTopView) / contentHeight;
        return percentWebview;
    }

    public String getHashCode(String Gkey) { //得到毫秒数

        MessageDigest shaCode = null;
        Date curDate = new Date();
        String dataStructure = Gkey;
        try {
            shaCode = MessageDigest.getInstance("SHA-256");
            shaCode.update(dataStructure.getBytes());
            //System.out.println("dataStructure="+Gkey);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return byte2Hex(shaCode.digest());
    }

    private static String byte2Hex(byte[] data) {
        String hexString = "";
        String stmp = "";

        for (int i = 0; i < data.length; i++) {
            stmp = Integer.toHexString(data[i] & 0XFF);

            if (stmp.length() == 1) {
                hexString = hexString + "0" + stmp;
            } else {
                hexString = hexString + stmp;
            }
        }
        return hexString.toUpperCase();
    }

    private void getIdentity() {
        String NAMESPACE = StaticVar.namespace;
        String URL =StaticVar.webURL+ "WebService.asmx";
        String METHOD_NAME = "getIdentity";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        String timeStamp = String.valueOf(new Date().getTime());
        String identifyStr = getHashCode(mMaakkiID + "M@@kki.cc" + timeStamp.trim()).toUpperCase();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("maakki_id", mMaakkiID);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            errMsg += e.getMessage();
        }

        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        String errCode = "";
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            identity = json_read.getString("identity");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else {
                errMsg = "errCode:" + errCode;
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }

    private class AsyncCallWS_getIdentity extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getIdentity();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Toast.makeText(getActivity(),"errMsg:"+errMsg,Toast.LENGTH_LONG).show();
            if (identity.contains("IAApplicant")) {
                iv_openIA.setVisibility(View.VISIBLE);
            } else {
                iv_openIA.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //tv_getVerificode.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void validIAReferral() {
        String NAMESPACE =StaticVar.namespace;
        final String URL = StaticVar.webURL+"WebServiceIA.asmx";
        String METHOD_NAME = "validIAReferral";
        String SOAP_ACTION = StaticVar.namespace+"validIAReferral";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("maakki_id", mMaakkiID);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        try {
            json_read = new JSONObject(tmp);
            String errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("3")) {
                errMsg = "查无资料";
            } else if (errCode.equals("4")) {
                errMsg = "这个卡号不是IA";
            } else if (errCode.equals("5")) {
                errMsg = "没有参与过920,无法运用本专案新代理商";
            } else {
                errMsg = "errCode:" + errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_validIAReferral extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            validIAReferral();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                iv_openIA.setVisibility(View.VISIBLE);
            } else {
                iv_openIA.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //tv_getVerificode.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }
}
