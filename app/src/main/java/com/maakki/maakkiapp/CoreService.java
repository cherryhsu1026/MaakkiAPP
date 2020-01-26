package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

/**
 * Created by ryan on 2016/2/26.
 */
public class CoreService extends Service {
    private static final int DAEMON_CHECK_INTERVAL = 200;
    private static final int DAEMON_SLEEP_INTERVAL = 1000 * 20;
    private static final int intervalMessageTime = 500;
    private Long intervalTime = 0L;
    private static final String HUB_URL = "http://www.maakki.com/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_DisConnection = "userDisConnected";
    private static final String HUB_AsyncStoreData = "AsyncStoreData";
    private static final String HUB_EVENT_chatpublic = "chat_public";
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_EVENT_Receive = "chatReceive";
    private static final String HUB_Hidden_Signal_Receive = "Hidden_Signal_Receive";
    private static final String HUB_Hidden_Signal_Send = "Hidden_Signal_Send";
    private static final String HUB_EVENT_Admin = "adminReceiver";
    private static final String HUB_EVENT_notify_msg = "notify_message";
    private static final String HUB_Event_Brocast = "broadcastMessage";
    private static final String HUB_Event_GetLocation = "getlocation";
    private static final String HUB_Event_receiveMessage = "receiveMessage";
    private static final String HUB_Method_Connection = "userConnected";
    private static final String HUB_EVENT_simpleNotice = "simpleNotice";
    private static final String HUB_Method_adminmsg = "admin_msg";
    private static final String HUB_Method_userFeedLocation = "userFeedLocation";
    private static final String HUB_Method_GMS = "userLocation";
    private static final String HUB_Method_Reconnected = "reconnected";
    private static final String HUB_chat_public_Send = "chat_public";
    Bitmap remote_picture;
    private static String NAMESPACE = "http://www.maakki.com/";
    private static String URL_WS = StaticVar.webURL+"/WebService.asmx";
    static String errMsg,errCode;
    //Alarm alarm = new Alarm();
    Boolean isVibrate, isDisplay, isNotify, isSound;
    Boolean isSignalRAlive = false;
    private Intent coreIntent = new Intent("com.maakki.maakkiapp.CORE");
    private Thread coreThread = null, WindowServiceThread = null, checkThread;
    private boolean isChatNotify;
    private int notificationId = 0;
    private String nMessage = "";
    private SignalRFuture<Void> mSignalRFuture;
    private HubProxy mHub;
    //private Integer interval_time = 5000;
    private NotificationManager mNotificationManager;
    private String NotifySender = "", ringtone, nContentTitle;
    private PendingIntent pIntent;
    private String redUrl = "";
    private String cMemID = "";
    private String cName = "";
    private String cpicfile = "00003.jpg";
    private long Lasttime_milliseconds, notification_millisecs;
    private String mMaakkiID = "";
    private String connMaakkiID = "";
    private String mMemID = "";
    private String mName = "";
    private String mPicfile = "", messagetitle, prevmsgforMessageType1;
    private String app_name = "";
    private Boolean isServiceOn;
    private PreNotification prev;
    private StoreDAO storeDAO;
    private PreNotificationDAO prevDAO;
    private SharedPreferences sharedPrefs;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    private NotificationManager notificationManager;
    private int icon, MessageType;
    private Context context;
    private GPSTracker gps;
    AllyDAO allyDAO;
    Ally ally;
    ActivityScoreRecordDAO asrDAO;
    String memId_ally_founder = "";
    private ChatDAO chatDAO;
    private Chat chat;
    private Friend friend;
    private FriendDAO friendDAO;
    private int activityscore;
    private int SCORE_NEED_TO_GET_REDENVELOPE=60*30*1000;
    Blockchain blockchain;

    @Override
    public void onCreate() {
        context = this;
        prev = new PreNotification();
        Lasttime_milliseconds = 0;
        gps = new GPSTracker(context);
        chatDAO = new ChatDAO(context);
        chat = new Chat();
        friendDAO = new FriendDAO(context);
        asrDAO=new ActivityScoreRecordDAO(context);
        blockchain = new Blockchain(4);
    }

    private void startThread() {
        //coreThread = new Thread(new DaemonCheckThread());
        coreThread = new Thread(new DaemonCheckThread(context));
        coreThread.start();
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
            setHubConnect();
            startThread();
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
        mSignalRFuture.cancel();
        super.onDestroy();
    }

    public void setLastMessageTime() {
        Date date = new Date();
        Lasttime_milliseconds = date.getTime();
        /*SharedPreferences pref = context.getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("LastMessageTime", Lasttime_milliseconds);
        editor.commit();*/
        SharedPreferencesHelper.putSharedPreferencesLong(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key11, Lasttime_milliseconds);
    }

    public Long getLastMessageTime() {
        //Long lastmessagetime=SharedPreferencesHelper.getSharedPreferencesLong(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key11, 0l);
        //return lastmessagetime;
        return Lasttime_milliseconds;
    }

    private void setHubConnect() {

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
                Boolean isMsgNotify = sharedPrefs.getBoolean("enable_notification", true);
                if (!msg.contains("剛登")) {
                    /*ignore*/
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
                    storeDAO = new StoreDAO(getApplicationContext());
                    Store store1 = storeDAO.getbyMaakkiID(mid);
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
                /*Intent i = new Intent("INVOKE_to_FRIENDLIST2");
                Bundle b=new Bundle();
                b.putString("msgTypeContent",msgTypeContent);
                b.putString("sender",sender);
                b.putString("notifyUrl",notifyUrl);
                i.putExtras(b);
                sendBroadcast(i);*/
                String[] msg = msgTypeContent.split("/");
                nMessage = msg[1];
                cName = "";
                //String msgType = msg[0];
                NotifySender = sender;
                redUrl = StaticVar.webURL+"/" + notifyUrl;
                //mNotificationId++;
                StatusBarNotifications("inform");

            }
        }, String.class, String.class, String.class);
        //Client端建立一個事件處理回報現在位置
        /*mHub.on(HUB_Event_GetLocation, new SubscriptionHandler2<String, String>() {
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
        }, String.class, String.class);*/
        mHub.on(HUB_EVENT_chatpublic, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                if (!memberid.equals(mMemID)) {
                    //broadcast message to ask if there is any redEnvelope available
                    if (message.equals("InquiryNotFinishedRE:")) {
                        cMemID = memberid;
                        cpicfile = picFile;
                        redEnvelopeMasterDAO reDAO=new redEnvelopeMasterDAO(context);
                        if (!cpicfile.equals("00003.jpg")) {
                            if(reDAO.getNotFinishedRE(cMemID)!=null){
                                if(mMaakkiID.equals("22545")||mMaakkiID.equals("17141")){
                                    Intent i = new Intent("INVOKE_InquiryNotFinishedRE_CORESERVICE");
                                    //i.putExtra("What",cMemID+" : "+name+" : "+reDAO.getNotFinishedRE(cMemID).getId());
                                    blockchain.addBlock(blockchain.newBlock(cMemID+" : "+name+" : "+reDAO.getNotFinishedRE(cMemID).getId()));
                                    i.putExtra("What",blockchain.newBlock(cMemID+"-"+name+"\n"+reDAO.getNotFinishedRE(cMemID).getId()+"-"+reDAO.getNotFinishedRE(cMemID).getTakenAmt()+"/"+reDAO.getNotFinishedRE(cMemID).getDonateAmt()).toString());
                                    //i.putExtra("What",blockchain.toString());
                                    sendBroadcast(i);
                                }
                                //SignalRUtil.setHubConnect(context);
                                feedBackredEnelopeInfo_notFinishedYet(mMaakkiID,cMemID,reDAO.getNotFinishedRE(cMemID));
                            }
                        }

                        //nMessage=cMemID+" : "+name+" / "+"来问"+"/";
                        //StatusBarNotifications("chat_private");
                    } else if (message.contains("sEnLatTiMCutMeSVieOLe:")) {
                        String currenttimeNId = String.valueOf((new Date()).getTime()) + ":" + memberid;
                        SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key13, currenttimeNId);
                    } else if (message.contains("seNdReDeNvEloQpe:")) {
                        //String[] mess = message.split(":");
                        cMemID = memberid;
                        cName = name;
                        cpicfile = picFile;
                        //mess[3]=mess[3].replace(":","_");
                        nMessage = message;
                        if (!mPicfile.equals("00003.jpg")) {
                            StatusBarNotifications("redEnvelope");
                        }
                        if (friendDAO.getbyMemberID(Integer.parseInt(cMemID)) == null) {
                            //nMessage+="\nadd friend!";
                            //if sender is not my friend then start the WindowService
                            Intent serviceIntent = new Intent(context, WindowService.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("getType", "InviteFriend");
                            bundle.putString("cName", cName);
                            bundle.putString("cMemID", cMemID);
                            serviceIntent.putExtras(bundle);
                            startService(serviceIntent);
                        }
                        //AsyncCallWS_getRedEnvelopeNoticeTask getRedEnvelopeNoticeTask = new AsyncCallWS_getRedEnvelopeNoticeTask();
                        //getRedEnvelopeNoticeTask.execute(memberid, name, picFile, message);
                    }
                }
            }
        }, String.class, String.class, String.class, String.class);

        mHub.on(HUB_Hidden_Signal_Receive, new SubscriptionHandler4<String, String, String, String>() {
            public void run(String sender, String mType, String message, String submessage) {
                if (mType.equals("sendAllyInformationtoGetUniqueAllyID")) {
                    //what 22545 need to do
                    allyDAO = new AllyDAO(getApplicationContext());
                    ally = new Ally();
                    String[] a = submessage.split(":");
                    ally.setId(Long.parseLong(a[1]));
                    ally.setFounder_id(Integer.parseInt(a[2]));
                    ally.setDonationAmt(Integer.parseInt(a[3]));
                    ally.setAllyStatus(Integer.parseInt(a[4]));
                    ally.setKickStartTime(a[5] + ":" + a[6]);
                    ally.setToken(a[7]);
                    ally.setAlly_name(message);
                    Bitmap bm = RealPathUtil.decodeBase64AndSetImage(a[8]);
                    memId_ally_founder = sender.split(":")[0];
                    saveImage(bm);
                } else
                if (mType.equals("seNdReDeNvEloQpe")) {
                    cMemID = sender.split(":")[0];
                    cpicfile = sender.split(":")[1];
                    cName = sender.split(":")[2];
                    nMessage = message;
                    if (!mPicfile.equals("00003.jpg")) {
                        StatusBarNotifications("redEnvelope");
                    }
                } else
                if (mType.equals("feedBackredEnelopeInfo_notFinishedYet")) {
                    cMemID = sender.split(":")[0];
                    cpicfile = sender.split(":")[1];
                    cName = sender.split(":")[2];
                    nMessage = message;
                    Intent serviceIntent = new Intent(context, WindowService.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("getType", "getRedEnvelope_NotFinished");
                    bundle.putString("cName", cName);
                    bundle.putString("cMemID", cMemID);
                    bundle.putString("nMessage", nMessage);
                    serviceIntent.putExtras(bundle);
                    startService(serviceIntent);
                    //StatusBarNotifications("chat_private");
                }
            }
        }, String.class, String.class, String.class, String.class);

        mHub.on(HUB_EVENT_Receive, new SubscriptionHandler4<String, String, String, String>() {
            public void run(String memberid, String name, String picFile, String message) {
                if (message.contains("MeSsFrOmAlaRm")) {
                    setLastMessageTime();
                }
                if (!memberid.equals(mMemID)) {
                    cMemID = memberid;
                    cName = name;
                    cpicfile = picFile;
                    if (message.contains("fEdbaKUniKALlyIdtoFoUNdr:")) {
                        allyDAO = new AllyDAO(context);
                        ally = allyDAO.get(Long.parseLong(message.split(":")[1]));
                        ally.setUniqueAllyId(message.split(":")[2]);
                        allyDAO.update(ally);
                        Intent i = new Intent("INVOKE_AllyList_from22545");
                        Bundle bundle = new Bundle();
                        bundle.putString("ally_id", message.split(":")[1]);
                        bundle.putString("Unique_id", message.split(":")[2]);
                        i.putExtras(bundle);
                        sendBroadcast(i);
                        //nMessage=message;
                        //StatusBarNotifications("chat_private");
                    } else if (message.contains("sendSpooSorNoticeToReCeIv:")) {
                        Boolean isAnonymous = Boolean.parseBoolean(message.split(":")[2]);
                        cpicfile = picFile;
                        if (isAnonymous) {
                            name = "Anonymous";
                            cpicfile = "00003.jpg";
                            remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.redenvelope);
                        }
                        cName = "玛吉梦想众筹";
                        nMessage = name + " 指定赞助了你 " + message.split(":")[1];
                        StatusBarNotifications("chat_private");
                    } else if (message.contains("fiDbAkSpoNtorResUlT:")) {
                        /*Long send_time=SharedPreferencesHelper.getSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key6,0l);
                        Long timemillisecs=new Date().getTime();
                        Long IntervalTime=timemillisecs-send_time;*/
                        Boolean isAnonymous = Boolean.parseBoolean(message.split(":")[2]);
                        //cpicfile = picFile;
                        if (isAnonymous) {
                            //name = "Anonymous"+IntervalTime;
                            name = "Anonymous";
                            cpicfile = "00003.jpg";
                        }
                        cName = "玛吉梦想众筹";
                        if (!message.split(":")[1].equals(" ")) {
                            if (message.split(":")[1].contains("USD(i)")) {
                                nMessage = "你领取了" + name + "发的赞助红包" + message.split(":")[1];
                                //activityscore=asrDAO.getTotalScore();
                                Long Last_getRE_Time=SharedPreferencesHelper.getSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key24,new Date().getTime());
                                Long intervalTime=new Date().getTime()-Last_getRE_Time;
                                int activescore_negative=Integer.parseInt(String.valueOf(intervalTime))-SCORE_NEED_TO_GET_REDENVELOPE;
                                activescore_negative=0-Math.min(asrDAO.getTotalScore(),Math.abs(activescore_negative));
                                if(activescore_negative<0){
                                    ActivityScoreRecord asr=new ActivityScoreRecord();
                                    asr.setCreateTime(new Date().getTime());
                                    asr.setScoreType(1);
                                    asr.setScore(activescore_negative);
                                    asrDAO.insert(asr);
                                }
                                SharedPreferencesHelper.putSharedPreferencesLong(context, SharedPreferencesHelper.SharedPreferencesKeys.key24, new Date().getTime());
                            } else {
                                nMessage = message.split(":")[1];
                            }
                        } else {
                            nMessage = "你和这个红包，距离只有不到一毫米..";
                        }
                        cMemID = memberid;
                        StatusBarNotifications("chat_private");
                    }
                    //想要领取这个红包发来的信息
                    else if (message.contains("agrEeToTaKesPONsor:")) {
                        Long envelope_id = Long.parseLong(message.split(":")[4]);
                        redEnvelopeMasterDAO reDAO = new redEnvelopeMasterDAO(context);
                        redEnvelope_Master rem = reDAO.get(envelope_id);
                        String maakki_id = message.split(":")[3];
                        String target_id = message.split(":")[1];
                        String isUSD = "false";
                        String currency = rem.getCurrency();
                        String _currency = currency;
                        if (currency.equals("USD")) {
                            isUSD = "true";
                            _currency = "USD";
                        }
                        int envelope_no = rem.getReceiver_no();
                        String sponsorAmt = String.valueOf(rem.getDonateAmt() / envelope_no);
                        String strSponsorAmt = formatDoubleToString(rem.getDonateAmt() / envelope_no);
                        String setDonateAmt = formatDoubleToString(rem.getDonateAmt());

                        //String =rem.getDonateAmt()/envelope_no
                        String iCreditPassword = rem.getiCreditPassword();
                        String isAnonymous = String.valueOf(rem.getIsAnonymous());
                        String isAverage = String.valueOf(rem.getIsAverage());
                        Boolean isFinish = rem.getIsFinish();
                        String slogan = rem.getSlogan();
                        cMemID = memberid;
                        cName = "玛吉梦想众筹";
                        if (!isFinish & !cpicfile.equals("00003.jpg")) {
                            //检查是否超过额度
                            if (add(rem.getTakenAmt(), rem.getDonateAmt() / envelope_no) <= rem.getDonateAmt()) {
                                //执行领取赞助红包程序
                                MDFSponsor(context,maakki_id, target_id, memberid, sponsorAmt, iCreditPassword, isUSD, isAnonymous);
                                rem.setTakenAmt(add(rem.getTakenAmt(), rem.getDonateAmt() / envelope_no));
                                nMessage = name + " 领取了你的赞助红包(" + envelope_id + ")\n" + strSponsorAmt + "/" + formatDoubleToString(rem.getTakenAmt()) + "/" + setDonateAmt + " " + currency + "(i)";
                                StatusBarNotifications("chat_private");
                            } else {
                                rem.setIsFinish(true);
                            }
                            reDAO.update(rem);
                        } else {
                            //这个红包已经被领完
                            //SignalRUtil.setHubConnect(context);
                            feedBackSponsorResult(cMemID, " :" + isAnonymous);
                        }
                    }
                    //敲门询问供应商是否在线
                    else if (message.contains("MeSsFrOmHanDsHaKGin:")) {
                        ArrayList L = new ArrayList();
                        L.add(cMemID);
                        String mess = "MeSsHanDsHaKGinaNs:";
                        try {
                            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else if (mMaakkiID.equals("1") & message.contains("sEnLocAtintOAdniM")) {
                        String[] m = message.split(":");
                        SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key18, m[1]);
                        SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key19, m[2]);
                        Intent i = new Intent("INVOKE_ONm_markerLOCATIONCHANGED");
                        Bundle bundle = new Bundle();
                        bundle.putString("latitude", m[1]);
                        bundle.putString("longitude", m[2]);
                        i.putExtras(bundle);
                        sendBroadcast(i);
                        Boolean isLocationNotify = sharedPrefs.getBoolean("enable_location_notification", false);
                        if (isLocationNotify) {
                            nMessage = message.split(":")[0];
                            cMemID = memberid;
                            cName = name;
                            cpicfile = picFile;
                            StatusBarNotifications("chat_private");
                        }
                    } else if (!message.contains("MeSsHanDsHaKGinaNs:")) {
                        nMessage = message;
                        cMemID = memberid;
                        cName = name;
                        cpicfile = picFile;
                        StatusBarNotifications("chat_private");
                    }
                }
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
                    //mNotificationId++;
                    StatusBarNotifications("admin_receiver");
                }

            }
        }, String.class, String.class, String.class, String.class);
        mHub.on(HUB_EVENT_simpleNotice, new SubscriptionHandler2<String, String>() {
            //@Override
            public void run(String msgType, String msgContent) {
                nMessage = msgContent;
                StatusBarNotifications("imaimai");
            }
        }, String.class, String.class);
        mHub.on(HUB_Event_Brocast, new SubscriptionHandler2<String, String>() {
            //@Override
            public void run(String name, String message) {
                if (!name.equals(mName)) {
                    nMessage = message;
                    cName = name;
                    //mNotificationId++;
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
                            nMessage = buyorsell + ":" + String.valueOf(add(dealunit, undealunit)) + "(" + String.valueOf(price) + ")";
                            if (dealunit > 0) {
                                nMessage += "/成交:" + String.valueOf(dealunit) + "(" + String.valueOf(dealprice) + ")";
                            }
                            NotifySender = "";
                            cName = "";
                            //mNotificationId++;
                            StatusBarNotifications("MGSinform");
                        }
                        break;
                    default:
                        break;
                }
            }
        }, String.class, String.class, String.class);

    }

    public double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public void StatusBarNotifications(String msgType) {
        notificationId++;
        //如果service關掉，以下程序不需要處理直接跳過
        isServiceOn = SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key4, false);
        if (isServiceOn) {
            isNotify = sharedPrefs.getBoolean("enable_notification", true);
            //Boolean isChatPubic=sharedPrefs.getBoolean("chat_public", true);
            nContentTitle = "";
            MessageType = 0;
            String pic_url = "http://www.maakki.com/image/icon.png";
            Intent intent = new Intent(getApplicationContext(), WebMain2.class);
            Bundle bundle = new Bundle();
            bundle.putString("mName", mName);
            bundle.putString("nName", cName);
            bundle.putString("mMemID", mMemID);
            bundle.putString("nMemID", cMemID);
            bundle.putString("Contactid", cMemID);
            messagetitle = cMemID + "/" + cName + "/" + nMessage;
            switch (msgType) {
                case "redEnvelope":
                    //redUrl="http://www.maakki.com/community/chat.aspx";
                    //nMessage=nMessage+":"+cName;
                    nContentTitle = cName + " 发了一个赞助红包：";
                    messagetitle = cName;
                    //nMessage=nMessage+"/"+mMemID;
                    if (cName.equals("Anonymous")) {
                        cpicfile = "00003.jpg";
                    }
                    bundle.putBoolean("isRedenvelope", true);
                    bundle.putString("sendernickname", cName);
                    intent = new Intent(getApplicationContext(), PreNotificationList.class);
                    MessageType = 1;
                    isNotify = sharedPrefs.getBoolean("sponsor_notification", true);
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
                        //} cName="玛吉梦想众筹"
                    } else if (cName.equals("玛吉梦想众筹") & (
                            nMessage.contains("领取了你的赞助红包") ||
                                    nMessage.contains("发的赞助红包") ||
                                    nMessage.contains("你和这个红包，距离只有不到一毫米..") ||
                                    nMessage.contains("，这个红包领不了。") ||
                                    nMessage.contains(" 指定赞助了你 ")
                    )) {
                        nContentTitle = cName + "通知你：";
                        //cpicfile = "00003.jpg";
                        remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.redenicon);
                        MessageType = 12;
                        redUrl = StaticVar.webURL+"/MCoins/MCoinsQuery.aspx";
                        isNotify = sharedPrefs.getBoolean("sponsor_notification", true);
                        //intent = new Intent(getApplicationContext(), PreNotificationList.class);
                    } else {
                        //private_chat
                        isNotify = sharedPrefs.getBoolean("chat_notification", true);
                        bundle.putString("isCustomer", "false");
                        nContentTitle = cName + "私訊給你：";
                        MessageType = 2;

                        chat.setCreateDate(new Date().getTime());
                        chat.setSenderid(Integer.parseInt(cMemID));
                        chat.setReceiverid(Integer.parseInt(mMemID));
                        chat.setContentText(nMessage);
                        chatDAO.insert(chat).getId();
                        /*if (isNotify) {
                            startService(new Intent(context, WindowService.class));
                        }*/
                        //Check if Message from someone stranger
                        friend = friendDAO.getbyMemberID(Integer.parseInt(cMemID));
                        friend.setLastMessageTime(new Date().getTime());
                        friendDAO.update(friend);
                        intent = new Intent(getApplicationContext(), Chat_red.class);
                        Intent i = new Intent("INVOKE_to_FRIENDLIST");
                        sendBroadcast(i);
                        //nMessage+=":hi,"+friend.getIsNotify()+","+friend.getFriendType();
                        if (friend != null) {
                            //Check if Message from someone I block
                            if (!friend.getFriendType().equals("B")) {
                                isNotify = friend.getIsNotify();
                                //check chat_notification=true
                                if (isNotify) {
                                    isNotify = sharedPrefs.getBoolean("chat_notification", true);
                                }
                                //在这两个 class 上就不通知了
                                String[] classname_no_Window = {"FriendList", "Chat_red"};
                                if (isSpecificClassAtBackground(getApplicationContext(), classname_no_Window)) {
                                    isNotify = false;
                                } else {
                                    startService(new Intent(context, WindowService.class));
                                }
                            } else {
                                isNotify = false;
                            }
                        } else {
                            //是否接受陌生信息
                            isNotify = sharedPrefs.getBoolean("chat_stranger", true);
                        }
                    }
                    break;
                case "admin_receiver":
                    redUrl = StaticVar.webURL+"/community/ecard.aspx?mid=" + cMemID;
                    nContentTitle = mName + "，系统通知你：";
                    //nMessage=cName+"："+nMessage;
                    if (nMessage.contains("成为BO")) {
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
                        if (!notifier[2].isEmpty()) {
                            cpicfile = notifier[2];
                        } else {
                            cpicfile = "00003.jpg";
                        }
                        if (nMessage.contains("了i点")) {
                            nMessage = notifier[2] + nMessage;
                        }
                        if (redUrl.equals("")) {
                            redUrl = StaticVar.webURL+"/community/NotifyMain.aspx";
                            MessageType = 4;

                        } else if (redUrl.contains("/community/FriendList.aspx?act_type=Invite")) {
                            bundle.putString("getType", "InviteMe");
                            MessageType = 2;
                            intent = new Intent(getApplicationContext(), FriendList.class);
                            MessageType = 13;
                        } else {
                            messagetitle = redUrl;
                            MessageType = 5;
                            if (nMessage.equals("有人留了客服讯息")) {
                                remote_picture = BitmapFactory.decodeResource(this.getResources(),
                                        R.drawable.logo_s);
                            }
                            if (nMessage.contains("920交易")) {
                                MessageType = 10;
                                cpicfile = "00003.jpg";
                                remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.drawable._imaimai);
                                intent = new Intent(getApplicationContext(), imaimai_order.class);
                            }
                            if (nMessage.contains("申请IA：")) {
                                MessageType = 11;
                                cpicfile = "00003.jpg";
                                remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.drawable.ia);
                                intent = new Intent(getApplicationContext(), Open_IA.class);
                            }
                        }
                    } else {
                        cpicfile = "00003.jpg";
                    }
                    if (MessageType == 10) {
                        nContentTitle = mName + "，imaimai通知你：";
                    } else {
                        nContentTitle = mName + "，Maakki通知你：";
                    }

                    break;
                case "MGSinform":
                    cpicfile = "00003.jpg";
                    isNotify = sharedPrefs.getBoolean("mgs_notification", true);
                    redUrl = StaticVar.webURL+"/MGS/MGSMainpage.aspx";
                    nContentTitle = mName + "，MGS通知你：";
                    MessageType = 6;
                    break;
                case "imaimai":
                    cpicfile = "00003.jpg";
                    isNotify = sharedPrefs.getBoolean("imaimai_notification", true);
                    nContentTitle = mName + "，imaimai通知你：";
                    remote_picture = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.favorite);
                    MessageType = 9;
                    break;
                default:
                    break;
            }
            if (isNotify) {
                bundle.putString("nMessage", nMessage);
                bundle.putString("redirUrl", redUrl);
                prevmsgforMessageType1 = cMemID + "/" + nMessage;
                if (MessageType == 1) {
                    remote_picture = BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.redenvelope);
                    nMessage = nMessage.split(":")[6];
                }
                if (!cpicfile.equals("00003.jpg")) {
                    pic_url = StaticVar.webURL+"/ashx/showImage.ashx?file_id=" + cpicfile + "&width=40&height=40&forcibly=Y&dimg=Y";
                    if (MessageType != 12) {
                        try {
                            remote_picture = BitmapFactory.decodeStream(
                                    (InputStream) new URL(pic_url).getContent());
                        } catch (IOException e) {
                            remote_picture = BitmapFactory.decodeResource(this.getResources(),
                                    R.drawable.logo_s);
                        }
                    }
                }
                //reset cpicfile="00003.jpg"
                //cpicfile = "00003.jpg";
                notification_millisecs = new Date().getTime();
                long notification_id = getPreNotificationID();
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                bundle.putLong("notification_id", notification_id);
                bundle.putLong("notification_millisecs", notification_millisecs);
                intent.putExtras(bundle);
                pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                // Create the style object with BigPictureStyle subclass.
                NotificationCompat.BigPictureStyle notiStyle = new
                        NotificationCompat.BigPictureStyle();
                notiStyle.setBigContentTitle(nContentTitle);
                notiStyle.setSummaryText(nMessage);
                //指定通知欄位要顯示的圖示
                icon = R.drawable.ic_launcher;
                //nContentTitle=nContentTitle+"("+mNotificationId+")";
                //NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                NotificationCompat.Builder nb = new NotificationCompat.Builder(this, "default")
                        .setContentTitle(nContentTitle)
                        .setContentText(nMessage)
                        .setSmallIcon(icon)
                        .setContentIntent(pIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setLargeIcon(remote_picture)
                        .setPriority(Notification.PRIORITY_HIGH)
                        //.setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true);

                isDisplay = sharedPrefs.getBoolean("enable_display", true);
                //if(isDisplay&isNotify){n.setPriority(Notification.PRIORITY_MAX);}
                //n.setStyle(notiStyle);
                // Moves the big view style object into the notification object.


                // 建立音效效果，放在res/raw下的音效檔
                isSound = sharedPrefs.getBoolean("enable_sound", true);
                ringtone = sharedPrefs.getString("notifications_ringtone", "content://settings/system/notification_sound");
                if (isSound & isNotify) {
                    Uri sound_effect = Uri.parse(ringtone);
                    //Uri sound_effect= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    nb.setSound(sound_effect);
                }
                //建立震動效果，陣列中元素依序為停止、震動的時間，單位是毫

                isVibrate = sharedPrefs.getBoolean("enable_vibrate", true);
                int a = 0;
                int b = 0;
                if (isVibrate & isNotify) {
                    a = 100;
                    b = 300;
                }
                long[] vibrate_effect = {a, b};
                nb.setVibrate(vibrate_effect);
                // 設定閃燈效果，參數依序為顏色、打開與關閉時間，單位是毫秒
                //n.setLights(Color.GREEN, 1000, 1000);
                /**/

                //prev.setIntent(intent);
                //最後用starforeground顯示最新的一筆通知
                //Toast.makeText(getApplicationContext(),"isDisplay:"+String.valueOf(isDisplay),Toast.LENGTH_LONG).show();
                Boolean isPriority = sharedPrefs.getBoolean("priority_On", true);
                if (isDisplay & isPriority) {
                    initChannels(this);
                    notificationManager.notify(notificationId, nb.build());
                }
                ShortcutBadger.with(getApplicationContext()).count(notificationId);
                //建立资料表格「PreNotification」
                //存到资料库里
                Intent i = new Intent("INVOKE_FROM_DEFAULTSERVICE");
                sendBroadcast(i);
            }
        }
    }

    private long getPreNotificationID() {
        if (MessageType == 10 || MessageType == 1 || MessageType == 2 || MessageType == 5 || MessageType == 7 || MessageType == 8 || MessageType == 12) {
            prev.settitle(messagetitle);
        } else {
            prev.settitle(nContentTitle);
        }
        if (MessageType == 1) {
            prev.setmessage(prevmsgforMessageType1);
        } else {
            prev.setmessage(nMessage);
        }
        prev.setPicfile(cpicfile);
        prev.setmessagetype(MessageType);
        prev.setLastModify(notification_millisecs);
        //if(prevDAO==null){
        prevDAO = new PreNotificationDAO(context);
        //}
        return prevDAO.insert(prev).getid();
    }

    public void initChannels(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        //only sound
        NotificationChannel channel_sound = new NotificationChannel(
                "default",
                "Notification_Channel_Sound",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel_sound.setDescription("Channel description");
        channel_sound.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        String ringtone = sharedPrefs.getString("notifications_ringtone", "content://settings/system/notification_sound");
        Uri sound_effect = Uri.parse(ringtone);
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        channel_sound.setSound(sound_effect, att);
        channel_sound.enableVibration(false);
        //only vibration
        NotificationChannel channel_vibrate = new NotificationChannel(
                "vibrate",
                "Notification_Channel_Vibrate",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel_vibrate.setDescription("Channel description");
        channel_vibrate.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel_vibrate.setSound(null, null);
        channel_vibrate.enableVibration(true);
        channel_vibrate.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        //sound & vibration
        NotificationChannel channel_both = new NotificationChannel(
                "default",
                "Notification_Channel_Both",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel_both.setDescription("Channel description");
        channel_both.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel_both.setSound(sound_effect, att);
        channel_vibrate.enableVibration(true);
        channel_both.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        //no sound,no vibration
        NotificationChannel channel_none = new NotificationChannel(
                "default",
                "Notification_Channel_None",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel_none.setDescription("Channel description");
        channel_none.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel_none.setSound(null, null);
        channel_none.enableVibration(false);
        if (isVibrate) {
            if (isSound) {
                notificationManager.createNotificationChannel(channel_both);
            } else {
                notificationManager.createNotificationChannel(channel_vibrate);
            }
        } else {
            if (isSound) {
                notificationManager.createNotificationChannel(channel_sound);
            } else {
                notificationManager.createNotificationChannel(channel_none);
            }
        }
        //notificationManager.createNotificationChannel(channel_sound);
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

    private class DaemonCheckThread implements Runnable {
        Context context;

        public DaemonCheckThread(Context ctx) {
            context = ctx;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    corealarm();
                    if (gps.canGetLocation) {
                        gps.onLocationChanged(gps.getLocation());
                    }
                    Thread.sleep(DAEMON_SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private void corealarm() {
        //Revise LastMessageTime
        sendMessageToMe();
        //feeback Location
        if (mMaakkiID.equals("10006")) {
            sendLocationToAdmin();
        }
        String[] CSId = context.getResources().getStringArray(R.array.CustomerServiceContactId);
        int count = CSId.length;
        Boolean isCS = false;
        for (int i = 0; i < count; i++) {
            if (CSId[i].equals(mMemID)) {
                isCS = true;
            }
        }
        //Update lastMessageTime of CustomerService online
        if (isCS) {
            sendLastTimeCustomerServiceOnline();
        }
        InquiryNotFinishedRE();
    }

    private void sendLocationToAdmin() {
        if (!mMaakkiID.equals("10006") || !gps.canGetLocation) {
            return;
        }
        ArrayList L = new ArrayList();
        String mess = "sEnLocAtintOAdniM" + ":" + gps.getLatitude() + ":" + gps.getLongitude();
        L.add("90");
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, "", "", mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToMe() {
        ArrayList L = new ArrayList();
        String mess = "MeSsFrOmAlaRm:" + (new Date()).getTime();
        //L.add(Contactid);
        L.add(mMemID);
        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void InquiryNotFinishedRE(){
        //ArrayList L=new ArrayList();
        //L.add(198947);
        String mess = "InquiryNotFinishedRE:";
        if (!mPicfile.equals("00003.jpg")) {
            prevDAO = new PreNotificationDAO(context);
            TimeZone china = TimeZone.getTimeZone("GMT+08:00");
            Calendar cal = Calendar.getInstance(china);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            Long Last_getRE_Time=SharedPreferencesHelper.getSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key24,new Date().getTime());
            Long intervalTime=new Date().getTime()-Last_getRE_Time;
            if(intervalTime==0){
                SharedPreferencesHelper.putSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key24,new Date().getTime());
            }
            int intervalTime2=Integer.parseInt(String.valueOf(intervalTime/1000));
            activityscore=asrDAO.getTotalScore();
            int activityscore2=Integer.parseInt(String.valueOf(activityscore/1000));
            //int activityscore=0;
            if(mMaakkiID.equals("22545")||mMaakkiID.equals("1")||mMaakkiID.equals("17141")){
                Intent i = new Intent("INVOKE_intervalTime_CORESERVICE");
                i.putExtra("intervalTime2",intervalTime2+" / "+activityscore2);
                sendBroadcast(i);
            }
            //超过30mins才可以问有没有红包
            if( intervalTime+activityscore >SCORE_NEED_TO_GET_REDENVELOPE & hour>6){
                try {
                    //(String memberid, String name, String picFile, String message)
                    mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
                    //mHub.invoke(HUB_Hidden_Signal_Send, L, mMemID, mType, mName, mPicfile).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void sendLastTimeCustomerServiceOnline() {
        String mess = "sEnLatTiMCutMeSVieOLe:" + (new Date()).getTime();
        try {
            //(String memberid, String name, String picFile, String message)
            mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private String formatDoubleToString(Double d) {
        DecimalFormat df;
        BigDecimal a = new BigDecimal(d.toString());
        if (d > 10) {
            df = new DecimalFormat(",###,##0");
        } else {
            df = new DecimalFormat(",###,##0.00");
        }
        df.format(a);
        return df.format(a);
    }

    private void saveImage(Bitmap bitmap) {
        SaveImageTask saveImageTask = new SaveImageTask(context, filePath -> onSaveComplete(filePath));
        saveImageTask.execute(bitmap);
    }

    private void onSaveComplete(File filePath) {
        if (filePath == null) {
            Log.w(TAG, "onSaveComplete: file dir error");
            return;
        }
        long id_ori = ally.getId();
        ally.setIconpath(filePath.getPath());
        ally.setLastModifyTime(new Date().getTime());
        //get new id as UniqueAllyID
        //long Unique_Ally_id = allyDAO.insert(ally).getId();
        //ally.setUniqueAllyId(Unique_Ally_id);
        ally.setUniqueAllyId(getUniqueAllyId());
        allyDAO.insert(ally);
        Intent i = new Intent("INVOKE_AllyList_to22545");
        sendBroadcast(i);
        String idStr = id_ori + ":" + getUniqueAllyId();
        //feedback UniqueAllyId to AllyFounder
        feedbackUniqueAllyIdtofounder(memId_ally_founder, idStr);
    }

    private String getUniqueAllyId() {
        String UniqueAllyId = null;
        String str_date = "2019/06/15";
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        try {
            date = (Date) formatter.parse(str_date);
        } catch (Exception e) {
            //
        }
        TimeZone china = TimeZone.getTimeZone("GMT+08:00");
        Calendar c = Calendar.getInstance(china);
        Date current = c.getTime();
        int value = (int) Math.round((current.getTime() - date.getTime()) / 1000);
        UniqueAllyId = Integer.toString(value, 16).toUpperCase();
        return UniqueAllyId;
    }
    public void feedbackUniqueAllyIdtofounder(String MemID_founder, String idStr) {
        ArrayList L = new ArrayList();
        L.add(MemID_founder);
        //mMemID="198947";
        String mess = "fEdbaKUniKALlyIdtoFoUNdr:" + idStr;
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean isSpecificClassAtBackground(final Context context, final String[] classname) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            //if (!topActivity.getPackageName().equals(context.getPackageName())) {
            //if(Arrays.asList(classname).contains(topActivity.getClassName().split(".")[3])){
            String s = topActivity.getClassName();
            boolean contains = false;
            for (int i = 0; i < classname.length; i++) {
                if (s.contains(classname[i])) {
                    contains = true;
                }
            }
            return contains;
            //}
            //}
        }
        return false;
    }

    private String getCityName() {
        String strCityName = "";
        //Context c = context;
        //GPSTracker gps = new GPSTracker(context);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        String strLat = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key7, "");
        String strLon = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key8, "");
        if (gps.canGetLocation & !strLat.equals("") && !strLon.equals("")) {
            Double latitude = Double.parseDouble(strLat);
            Double longitude = Double.parseDouble(strLon);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.get(0).getLocality() != null) {
                    strCityName += addresses.get(0).getLocality();
                }
                //Toast.makeText(context,"State/City:"+stateName+"/"+cityName,Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (Exception e) {

            }
        }
        return strCityName;
    }

    public void feedBackredEnelopeInfo_notFinishedYet(String cMaakkiid, String cMemid, redEnvelope_Master rem) {
        ArrayList L = new ArrayList();
        L.add(cMemid);
        String sponsorAmt = formatDoubleToString(Double.parseDouble(String.valueOf(rem.getDonateAmt())) / rem.getReceiver_no());
        String greeting="恭喜发财，平安喜乐！";
        if(rem.getSlogan()!=null){
            greeting=rem.getSlogan();
        }
        String message = "feedBackredEnelopeInfo_notFinishedYet:" +cMaakkiid + ":" + rem.getId() + ":" + sponsorAmt + ":" + rem.getCurrency() + ":" + "" + ":" + greeting;
        if (rem.getIsAnonymous()) {
            mName = "Anonymous";
        }
        String mType = "feedBackredEnelopeInfo_notFinishedYet";
        String subMessage = "";
        String sender = mMemID + ":" + mPicfile + ":" + mName;
        try {
            mHub.invoke(HUB_Hidden_Signal_Send, L, sender, mType, message, subMessage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void feedBackSponsorResult(String cMemid, String result) {
        ArrayList L = new ArrayList();
        String mess = "fiDbAkSpoNtorResUlT:" + result;
        L.add(cMemid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void MDFSponsor(Context context,String maakki_id,String target_id,String cMemID,String sponsorAmt,String iCreditPassword,String isUSD,String isAnonymous) {
        String METHOD_NAME = "MDFSponsor";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "MDF-M@@kki.cc" + timeStamp.trim() + target_id + sponsorAmt + iCreditPassword;
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("target_id", target_id);
        request.addProperty("sponsorAmt", sponsorAmt);
        request.addProperty("isUSD", isUSD);
        request.addProperty("iCreditPassword", iCreditPassword);
        request.addProperty("isAnonymous", isAnonymous);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);

        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            errMsg = "";
            if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "MDF开放赞助时间为：北京时间 06:00~24:00 !";
            } else if (errCode.equals("4")) {
                errMsg = "查无赞助者卡号）";
            } else if (errCode.equals("5")) {
                errMsg = "请先设定转点密码！";
            } else if (errCode.equals("6")) {
                errMsg = "转点密码错误";
            } else if (errCode.equals("7")) {
                errMsg = "赞助点数必须大于1USD(i)";
            } else if (errCode.equals("8")) {
                errMsg = "i点余额不足";
            } else if (errCode.equals("9")) {
                errMsg = "指定赞助对象尚未建立FG(众筹目标)";
            } else if (errCode.equals("10")) {
                errMsg = "超过今日可赞助额度";
            } else if (errCode.equals("11")) {
                errMsg = "今日所有的FG(众筹目标)均已被满足";
            } else if (errCode.equals("12")) {
                errMsg = "可赞助的FG(众筹目标)不足！";
            } else if (errCode.equals("13")) {
                errMsg = "系统繁忙中，请稍后再试！";
            } else if (errCode.equals("14")) {
                errMsg = "系统繁忙中，请稍后再试！";
            } else if (errCode.equals("15")) {
                errMsg = "您没有参与MDF的权限";
            } else if (errCode.equals("16")) {
                errMsg = "请勿指定自己为赞助对象";
            }
            Double actualSponsor = json_read.getDouble("actualSponsorAmt");
            String message;
            if(actualSponsor>0){
                message=formatDoubleToString(actualSponsor)+" USD(i):"+isAnonymous;
            }else{
                message=errMsg+"，这个红包领不了。:"+isAnonymous;
            }
            //SignalRUtil.setHubConnect(context);
            feedBackSponsorResult(cMemID,message);
            //String targetStr = json_read.getString("targetStr");
        } catch (Exception e) {
        }
    }
    public static String getHashCode(String Gkey) { //得到毫秒数

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


}
