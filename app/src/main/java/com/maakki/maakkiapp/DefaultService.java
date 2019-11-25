package com.maakki.maakkiapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

/**
 * Created by ryan on 2016/2/26.
 */
public class DefaultService extends Service {
    private static final String HUB_URL = "http://www.maakki.com/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_DisConnection = "userDisConnected";
    private static final String HUB_AsyncStoreData = "AsyncStoreData";
    private static final String HUB_EVENT_chatpublic = "chat_public";
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_EVENT_Receive = "chatReceive";
    private static final String HUB_EVENT_Admin = "adminReceiver";
    private static final String HUB_EVENT_notify_msg = "notify_message";
    private static final String HUB_Event_Brocast = "broadcastMessage";
    private static final String HUB_Event_GetLocation = "getlocation";
    private static final String HUB_Event_receiveMessage = "receiveMessage";
    private static final String HUB_Method_Connection = "userConnected";
    private static final String HUB_Method_ConnectionSimple = "userConnectedSimple";
    private static final String HUB_Method_adminmsg = "admin_msg";
    private static final String HUB_Method_userFeedLocation = "userFeedLocation";
    private static final String HUB_Method_GMS = "userLocation";
    private static final String HUB_Method_Reconnected = "reconnected";
    Bitmap remote_picture;
    GPSTracker gps;
    double latitude = 0;
    double longitude = 0;
    //Alarm alarm = new Alarm();
    Boolean isCustomerMessage = false;
    private boolean isChatNotify;
    private int mNotificationId = 0;
    private String nMessage = "";
    private SignalRFuture<Void> mSignalRFuture;
    private HubProxy mHub;
    private Integer interval_time = 5000;
    private NotificationManager myNotificationManager;
    private String NotifySender = "";
    private String redUrl = "";
    private String cMemID = "";
    private String cName = "";
    private String cpicfile = "00003.jpg";
    private long Lasttime_milliseconds;
    private String mMaakkiID = "";
    private String connMaakkiID = "";
    private String mMemID = "";
    private String mName = "";
    private String mPicfile = "";
    private String app_name = "";
    private Boolean isServiceOn;
    private PrevNotification prev;
    private StoreDAO storeDAO;
    private PrevNotificationDAO prevDAO;
    private SharedPreferences sharedPrefs;
    //private final IBinder mBinder = new LocalBinder();


    /*public class LocalBinder extends Binder {
        DefaultService getService() {
            return DefaultService.this;
        }
    }*/

    @Override
    public void onCreate() {
        prev = new PrevNotification();
        gps = new GPSTracker(DefaultService.this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        remote_picture = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.logo_s);
        //startTimer();

        isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        connMaakkiID = "MGS" + mMaakkiID;
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        app_name = getString(R.string.app_name);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Toast.makeText(getApplicationContext(), "KMNP:"+mMaakkiID+"/"+mMemID+"/"+mName+"/"+mPicfile, Toast.LENGTH_LONG).show();

        //if isSeviceOn=true
        if (isServiceOn) {
            //Toast.makeText(getApplicationContext(), "DefaulService is on.", Toast.LENGTH_LONG).show();
            //Connect to SignalR
            setHubConnect();
            //set alarm to wake service up
            //alarm.SetAlarm(getApplicationContext());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //return mBinder;
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Context context=getApplicationContext();
        //Toast.makeText(context, "Service Destroyed", Toast.LENGTH_LONG).show();
        stopSelf();
    }

    public void setLastMessageTime() {
        Date date = new Date();
        Lasttime_milliseconds = date.getTime();
        SharedPreferencesHelper.putSharedPreferencesLong(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key11, Lasttime_milliseconds);
    }

    public void setHubConnect() {
        // 接收信息
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));

        //開啟連線
        try {
            mSignalRFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "You're connected!", Toast.LENGTH_LONG).show();
        nMessage = "剛從" + app_name + "登入";

        try {
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key7, String.valueOf(latitude));
            SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key8, String.valueOf(longitude));
            /*if (latitude == 0 & longitude == 0) {
                //Toast.makeText(getApplicationContext(), "Maakki.9尚未得到定位權限",Toast.LENGTH_LONG).show();
            } else {
                try {
                    mHub.invoke(HUB_Method_GMS, mMemID, latitude, longitude).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }*/
        }
        //Test if Srvice is Alive
        /*if(mMemID.equals("90")){
            Monitor_process(interval_time);
        }*/

        //Client端建立一個事件處理重新連線
        /*mHub.on(HUB_Method_Reconnected, new SubscriptionHandler() {
            //return mMemID;
            @Override
            public void run() {
                nMessage = "剛剛重新登入" + app_name;
                try {
                    //mHub.invoke(HUB_Method_DisConnection).get();
                    mHub.invoke(HUB_Method_Connection, mMemID).get();
                    //mHub.invoke(HUB_Method_adminmsg,mMemID,mName,mPicfile,nMessage).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    try {
                        mHub.invoke(HUB_Method_GMS, mMemID, latitude, longitude).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        //Client端建立一個事件處理通知信息
        mHub.on(HUB_AsyncStoreData, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String storename, String storeData, String address, String msg) {

                int sid = 0;
                int mid = 0;
                //
                String[] smid = storeData.split("/");
                try {
                    sid = Integer.parseInt(smid[0]);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                try {
                    mid = Integer.parseInt(smid[1]);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                Boolean isMsgNotify = sharedPrefs.getBoolean("message_notification", true);
                if (!msg.contains("剛登")) {
                    if ((mMemID.equals("90") || mMemID.equals("53")) & isMsgNotify) {
                        //msg=String.valueOf(lat)+"/"+String.valueOf(lon);
                        nMessage = storename + "(" + String.valueOf(mid) + ")" + msg;
                        //nMessage = storename+"("+String.valueOf(mid)+")Open?"+smid[3];
                        cName = "";
                        //NotifySender = String.valueOf(mid)+"/"+storename+"/"+smid[2];
                        NotifySender = String.valueOf(mid) + "/" + storename + "/00003.jpg";
                        redUrl = "https://www.maakki.cc/BOStoreData.aspx?entrepot_id=" + String.valueOf(sid) + "&mid=" + mMaakkiID;
                        mNotificationId++;
                        StatusBarNotifications("inform");
                    }
                } else {
                    //nMessage="2.你不是刚刚登入/"+address;
                    //StatusBarNotifications("chat_private");
                    double lat = 0;
                    double lon = 0;

                    Boolean isGoogle = false;
                    if (!address.equals("")) {
                        Address location = getLocationFromAddress(address);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            if (smid[3].equals("Y") & lat != 0 & lon != 0 & lat != 0.0 & lon != 0.0) {
                                isGoogle = true;
                            }
                        } else {
                            //走百度
                        }
                    }
                    //Check if StoreData exists
                    //建立APP SqlLite store的资料表格「Store」

                    //nMessage="3."+lat+"/"+lon+"/"+isOpen+"/"+address;
                    //StatusBarNotifications("chat_private");
                    storeDAO = new StoreDAO(getApplicationContext());
                    Store store1 = storeDAO.getbyMaakkiID(mid);
                    //StatusBarNotifications("chat_private");
                    Boolean hasData = true;
                    if (store1 == null) {
                        hasData = false;
                    }
                    Store store = new Store();
                    //store.setId(0);
                    store.setStoreName(storename);
                    store.setStoreID(sid);
                    store.setMaakkiID(mid);
                    store.setAddress(address);
                    store.setPicfile(smid[2]);
                    store.setIsOpen(smid[3]);
                    //if(isGoogle){}
                    store.setLatitude(lat);
                    store.setLongitude(lon);
                    store.setLastModify(new Date().getTime());
                    //如果本有資料就 update 沒有就新增
                    //nMessage = mid+"/"+hasData+"/"+isGoogle+"/"+smid[3]+"/"+address;
                    //StatusBarNotifications("chat_private");>>isOpen=true资料进不来
                    if (hasData) {
                        if (isGoogle) {
                            store.setId(store1.getId());
                            storeDAO.update(store);
                        } else {
                            getLatitude(hasData, store, address);
                        }
                    } else {
                        if (isGoogle) {
                            //StatusBarNotifications("chat_private");>>noData coming
                            storeDAO.insert(store);
                            Intent i = new Intent("INVOKE_STORELIST");
                            sendBroadcast(i);
                        } else {
                            getLatitude(hasData, store, address);
                        }
                    }

                }
            }
        }, String.class, String.class, String.class, String.class);

        //Client端建立一個事件處理通知信息
        mHub.on(HUB_EVENT_notify_msg, new SubscriptionHandler3<String, String, String>() {
            //@Override
            public void run(String msgTypeContent, String sender, String notifyUrl) {

                String[] msg = msgTypeContent.split("/");
                //nMessage = msgTypeContentent;
                //nMessage = sender+":"+msg[1];
                nMessage = msg[1];
                cName = "";
                //String msgType = msg[0];
                NotifySender = sender;
                redUrl = "https://www.maakki.cc/" + notifyUrl;
                mNotificationId++;
                StatusBarNotifications("inform");

            }
        }, String.class, String.class, String.class);
        //Client端建立一個事件處理回報現在位置
        mHub.on(HUB_Event_GetLocation, new SubscriptionHandler2<String, String>() {
            //@Override
            public void run(String name, String message) {
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    try {
                        mHub.invoke(HUB_Method_GMS, mMemID, latitude, longitude).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, String.class, String.class);
        mHub.on(HUB_EVENT_chatpublic, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                if (!memberid.equals(mMemID)) {
                    if (!message.contains("sEnLatTiMCutMeSVieOLe:")) {
                        nMessage = message;
                        cMemID = memberid;
                        cName = name;
                        cpicfile = picFile;
                        mNotificationId++;
                        StatusBarNotifications("chat");
                    } else {
                        String currenttimeNId = String.valueOf((new Date()).getTime()) + ":" + memberid;
                        SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key13, currenttimeNId);
                    }
                }
            }
        }, String.class, String.class, String.class, String.class);
        mHub.on(HUB_EVENT_Receive, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                //message contains MeSsFrOmAlaRm not notification but reset Lasttime_milliseconds
                //Date date=new Date();
                setLastMessageTime();
                //send a message to customerservice inquiry when the lastMessagetime is
/*                if(message.equals("gTCuStorESeViCLaTMeSsaGeT")){
                    answerLastMessageTime();
                }else if(message.contains("aNsWeLaStMeESgtM:")){
                    isCustomerServiceOnlineOrNot(Long.parseLong(message.split(":")[1]));
                }*/

                if (!memberid.equals(mMemID)) {
                    nMessage = message;
                    cMemID = memberid;
                    cName = name;
                    cpicfile = picFile;
                    mNotificationId++;
                    //isChatNotify=SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key6, true);
                    //if(isChatNotify){

                    StatusBarNotifications("chat_private");
                    //}
                }
                //Toast.makeText(DefaultService.this, "mess:"+nMessage+"/"+timeMilliseconds, Toast.LENGTH_LONG).show();
            }
        }, String.class, String.class, String.class, String.class);
        mHub.on(HUB_EVENT_Admin, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picfile, String message) {
                if (!memberid.equals(mMemID)) {
                    //nMessage = message;
                    nMessage = name + "：" + message;
                    cMemID = memberid;
                    cName = name;
                    cpicfile = picfile;
                    mNotificationId++;
                    StatusBarNotifications("admin_receiver");
                }

            }
        }, String.class, String.class, String.class, String.class);
        mHub.on(HUB_Event_Brocast, new SubscriptionHandler2<String, String>() {
            //@Override
            public void run(String name, String message) {
                if (!name.equals(mName)) {
                    nMessage = message;
                    cName = name;
                    mNotificationId++;
                    StatusBarNotifications("chat");
                }
            }
        }, String.class, String.class);
        mHub.on(HUB_Event_receiveMessage, new SubscriptionHandler3<String, String, String>() {
            //@Override
            public void run(String message, String msgType, String groupID) {
                switch (msgType) {
                    case "MGSTrade":
                        String[] xr = message.split(";");
                        String buyorsell = "";
                        if (xr[0].equals("buy")) {
                            buyorsell = "挂买";
                            remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mgs_upward);
                        } else {
                            buyorsell = "挂卖";
                            remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mgs_downward);
                        }
                        //int dealunit=Integer.valueOf(xr[3]);
                        //int undealunit=Integer.valueOf(xr[1]);
                        double dealunit = Double.parseDouble(xr[3]);
                        double undealunit = Double.parseDouble(xr[1]);
                        double price = Double.parseDouble(xr[2]);
                        double dealprice = Double.parseDouble(xr[4]);
                        //nMessage=xr[0]+"/"+xr[1]+"/"+xr[2]+"/"+xr[3]+"/"+xr[4];
                        if (undealunit >= 0) {
                            nMessage = buyorsell + ":" + String.valueOf(dealunit + undealunit) + "(" + String.valueOf(price) + ")";
                            if (dealunit > 0) {
                                nMessage += "/成交:" + String.valueOf(dealunit) + "(" + String.valueOf(dealprice) + ")";
                            }
                            NotifySender = "";
                            cName = "";
                            mNotificationId++;
                            StatusBarNotifications("MGSinform");
                        }
                        break;
                    default:
                        break;
                }
            }
        }, String.class, String.class, String.class);

    }

    public void StatusBarNotifications(String msgType) {
        //如果service關掉，以下程序不需要處理直接跳過
        isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        if (isServiceOn) {
            Boolean isNotify = sharedPrefs.getBoolean("enable_notification", true);
            //Boolean isChatPubic=sharedPrefs.getBoolean("chat_public", true);
            String nContentTitle = "";
            Integer MessageType = 0;

            String pic_url = "http://www.maakki.com/image/icon.png";
            Intent intent = new Intent(getApplicationContext(), WebMain.class);
            Bundle bundle = new Bundle();
            bundle.putString("mName", mName);
            bundle.putString("nName", cName);
            bundle.putString("mMemID", mMemID);
            bundle.putString("nMemID", cMemID);

            bundle.putString("Contactid", cMemID);
            String messagetitle = cMemID + "/" + cName + "/" + nMessage;
            switch (msgType) {
                case "chat":
                    //redUrl="http://www.maakki.com/community/chat.aspx";
                    nContentTitle = cName + "在「开讲」说：";
                    messagetitle = cName;
                    //nMessage=nMessage+"/"+mMemID;
                    bundle.putString("isPrivate", "false");
                    bundle.putString("isCustomer", "false");
                    intent = new Intent(getApplicationContext(), Chat_main.class);
                    isNotify = sharedPrefs.getBoolean("chat_public", true);
                    MessageType = 1;
                    //bundle.putString("isPrivate", "true");
                    //bundle.putString("isCustomer","true");

                    break;
                case "chat_private":
                    //redUrl="http://www.maakki.com/community/chat.aspx?Contact="+cMemID;

                    bundle.putString("isPrivate", "true");
                    String[] msg = nMessage.split(":");
                    if (msg[0].equals("MsGseNdedtoCusTSErv")) {
                        nMessage = msg[1];
                        bundle.putString("isCustomer", "true");
                        nContentTitle = cName + "发客服信息：";
                        intent = new Intent(getApplicationContext(), Chat_CustomerService.class);
                        MessageType = 7;
                    } else if (msg[0].equals("MsGseNdedFrCuSto")) {
                        //String[] mg = nMessage.split(":");
                        //if (mg[0].equals("MsGseNdedtoCusTSErv")) {
                        nMessage = msg[1];
                        bundle.putString("isCustomer", "true");
                        nContentTitle = cName + "回复信息：";
                        intent = new Intent(getApplicationContext(), Chat_red.class);
                        cpicfile = "00003.jpg";
                        remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.icon_customerservice);
                        MessageType = 8;
                        //}
                    } else {
                        bundle.putString("isCustomer", "false");
                        nContentTitle = cName + "私訊給你：";
                        MessageType = 2;
                        intent = new Intent(getApplicationContext(), Chat_red.class);
                    }

                    //intent = new Intent(getApplicationContext(),Chat_main.class);
                    isNotify = sharedPrefs.getBoolean("chat_notification", true);

                    break;
                case "admin_receiver":
                    redUrl = "https://www.maakki.cc/community/ecard.aspx?mid=" + cMemID;
                    nContentTitle = mName + "，系统通知你：";
                    //nMessage=cName+"："+nMessage;
                    if (nMessage.contains("成为IA")) {
                        remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.ia);
                    } else if (nMessage.contains("成为BO")) {
                        remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.bo);
                    } else if (nMessage.contains("成为CS")) {
                        remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.cs);
                    }

                    MessageType = 3;
                    break;
                case "inform":
                    if (!NotifySender.equals("")) {
                        // 0/1/senderpicfile
                        String[] notifier = NotifySender.split("/");
                        cpicfile = notifier[2];
                        if (nMessage.contains("了i点")) {
                            nMessage = notifier[2] + nMessage;
                        }
                        if (redUrl.equals("")) {
                            redUrl = "https://www.maakki.cc/community/NotifyMain.aspx";
                            MessageType = 4;
                        } else {
                            messagetitle = redUrl;
                            MessageType = 5;
                        }
                    }
                    //else{
                    //redUrl="http://www.maakki.com/MGS/MGSMainpage.aspx";
                    //}
                    nContentTitle = mName + "，Maakki通知你：";
                    break;
                case "MGSinform":
                    isNotify = sharedPrefs.getBoolean("mgs_notification", true);
                    redUrl = "https://www.maakki.cc/MGS/MGSMainpage.aspx";
                    nContentTitle = mName + "，MGS通知你：";
                    /*if(nMessage.contains("挂买")){
                        remote_picture = BitmapFactory. decodeResource(getApplicationContext().getResources(),
                                R.drawable.mgs_upward);
                    }else if(nMessage.contains("挂卖")){
                        remote_picture = BitmapFactory. decodeResource(getApplicationContext().getResources(),
                                R.drawable.mgs_downward);
                    }*/
                    MessageType = 6;
                    break;
                default:
                    break;
            }
            bundle.putString("nMessage", nMessage);
            bundle.putString("redirUrl", redUrl);
            intent.putExtras(bundle);

            //remote_picture = BitmapFactory. decodeResource(this.getResources(),R.drawable.logo_s);
            if (!cpicfile.equals("00003.jpg")) {
                pic_url = "http://www.maakki.com/function/getImage.aspx?file_id=" + cpicfile + "&width=60&height=60&forcibly=Y&dimg=Y";
                //new DownloadBitmapTask(remote_picture)
                //        .execute(pic_url);
                try {
                    remote_picture = BitmapFactory.decodeStream(
                            (InputStream) new URL(pic_url).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //reset cpicfile="00003.jpg"
            cpicfile = "00003.jpg";

            // Create the style object with BigPictureStyle subclass.
            NotificationCompat.BigPictureStyle notiStyle = new
                    NotificationCompat.BigPictureStyle();
            notiStyle.setBigContentTitle(nContentTitle);
            notiStyle.setSummaryText(nMessage);

            // use System.currentTimeMillis() to have a unique ID for the pending intent
            //intent = new Intent(getApplicationContext(),PreNotificationList.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            //指定通知欄位要顯示的圖示
            int icon = R.drawable.ic_launcher;
            //nContentTitle=nContentTitle+"("+mNotificationId+")";
            NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                    .setContentTitle(nContentTitle)
                    .setContentText(nMessage)
                    .setSmallIcon(icon)
                    .setContentIntent(pIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLargeIcon(remote_picture)
                    .setPriority(Notification.PRIORITY_MAX)
                    //.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(true);


            Boolean isDisplay = sharedPrefs.getBoolean("enable_display", true);
            //if(isDisplay&isNotify){n.setPriority(Notification.PRIORITY_MAX);}
            //n.setStyle(notiStyle);
            // Moves the big view style object into the notification object.


            // 建立音效效果，放在res/raw下的音效檔
            Boolean isSound = sharedPrefs.getBoolean("enable_sound", true);
            if (isSound & isNotify) {
                String ringtone = sharedPrefs.getString("notifications_ringtone", "content://settings/system/notification_sound");
                Uri sound_effect = Uri.parse(ringtone);
                //Uri sound_effect= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                nb.setSound(sound_effect);
            }
            //建立震動效果，陣列中元素依序為停止、震動的時間，單位是毫
            int a = 0;
            int b = 0;
            Boolean isVibrate = sharedPrefs.getBoolean("enable_vibrate", true);
            if (isVibrate & isNotify) {
                a = 100;
                b = 300;
            }
            long[] vibrate_effect = {a, b};
            nb.setVibrate(vibrate_effect);
            // 設定閃燈效果，參數依序為顏色、打開與關閉時間，單位是毫秒
            //n.setLights(Color.GREEN, 1000, 1000);
            /**/
            if (isNotify) {
                // Builds the notification and issues it.


                // Gets an instance of the NotificationManager service
                //第二則通知出現時 ，由NotificationManager.notify()負責送出前一則通知
                // 找出PrevNotification.class裡面的上一筆通知資料，用notificationManager顯示舊通知

                /*if (mNotificationId>1) {
                    NotificationCompat.Builder p = new NotificationCompat.Builder(this)
                            .setContentTitle(prev.gettitle())
                            .setContentText(prev.getmessage())
                            .setSmallIcon(icon)
                            .setContentIntent(prev.getpintent())
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setLargeIcon(remote_picture)
                            //.setPriority(Notification.PRIORITY_MAX)
                            //.setDefaults(Notification.DEFAULT_VIBRATE)
                            .setAutoCancel(true)
                            ;
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(mNotificationId, p.build());
                }*/

                //reset Lasttime_milliseconds
                setLastMessageTime();
                //Date LastDate = new Date();
                //Lasttime_milliseconds = LastDate.getTime();
                //存下最新的一筆通知相關資料在PrevNotification.class
                prev.setid(mNotificationId);
                if (MessageType == 1 || MessageType == 2 || MessageType == 5 || MessageType == 7 || MessageType == 8) {
                    prev.settitle(messagetitle);
                } else {
                    prev.settitle(nContentTitle);
                }
                //prev.settitle(nContentTitle);
                prev.setmessage(nMessage);
                prev.setmessagetype(MessageType);
                prev.setLastModify(Lasttime_milliseconds);
                //prev.setIsVisible(true);
                //prev.setpintent(pIntent);
                //最後用starforeground顯示最新的一筆通知
                if (isDisplay) {
                    startForeground(1, nb.build());
                }
                ShortcutBadger.with(getApplicationContext()).count(mNotificationId);
                //建立资料表格「PreNotification」
                prevDAO = new PrevNotificationDAO(getApplicationContext());
                //存到资料库里
                prevDAO.insert(prev);

                Intent i = new Intent("INVOKE_FROM_DEFAULTSERVICE");
                sendBroadcast(i);

            }
        }
    }

    public Address getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address location;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            location = address.get(0);
        } catch (Exception e) {
            return null;
        }
        return location;
    }

    public void getLatitude(Boolean hasData, Store store, String address) {
        String AK = "KCYGc9KlCUWyTmnCIRK0Ds0U2YqNbSYC";
        double lat = 0;
        double lng = 0;
        String Strlng = "";
        String Strlat = "";
        Map<String, String> map = new HashMap<String, String>();
        try {
            address = URLEncoder.encode(address, "UTF-8");
            URL resjson = new URL("http://api.map.baidu.com/geocoder/v2/?address="
                    + address + "&output=json&ak=" + AK);
            //http://api.map.baidu.com/geocoder/v2/?address=台北市忠孝东路四段239号&output=json&ak=ozxQ94KwQZvsKkoqKuAG73nwW7Z9oIwQ
            URLConnection uc = resjson.openConnection();
            uc.setConnectTimeout(1000 * 100);
            uc.setReadTimeout(1000 * 100);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(uc.getInputStream()));

            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();

            if (str != null) {
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if (lngStart > 0 && lngEnd > 0 && latEnd > 0) {
                    Strlng = str.substring(lngStart + 5, lngEnd);
                    Strlat = str.substring(lngEnd + 7, latEnd);
                    map.put("lng", Strlng);
                    map.put("lat", Strlat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lat = Double.parseDouble(Strlat);
        lng = Double.parseDouble(Strlng);
        store.setLatitude(lat);
        store.setLongitude(lng);
        store.setLastModify(new Date().getTime());
        if (hasData) {
            store.setId(store.getId());
            storeDAO.update(store);
            //msg+="/ny";
        } else {
            //StatusBarNotifications("chat_private");>>noData coming
            storeDAO.insert(store);
            Intent i = new Intent("INVOKE_STORELIST");
            sendBroadcast(i);
            getLatitude(hasData, store, address);
        }
        //return map;
    }

    public void getContactLastMessageTime(String Contactid) {
        // String Contactid = "90";
        String mess = "gTCuStorESeViCLaTMeSsaGeT";
        ArrayList L = new ArrayList();
        L.add(Contactid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void answerLastMessageTime() {
        String mess = "aNsWeLaStMeESgtM:" + (new Date()).getTime();
        ArrayList L = new ArrayList();
        L.add(mMemID);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void isCustomerServiceOnlineOrNot(Long LastCustomerOnlineTime) {
        Long currenttime = (new Date()).getTime();
        Long intervalTime = currenttime - LastCustomerOnlineTime;
        Boolean isCustomerServiceOnline = false;
        if (intervalTime < 300 * 1000) {
            isCustomerServiceOnline = true;
        }
        SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key12, isCustomerServiceOnline);
    }
}
