package com.maakki.maakkiapp;


/**
 * Created by ryan on 2016/7/24.
 */

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
//import com.codebutler.android_websockets.WebSocketClient;

public class Chat_red extends AppCompatActivity {
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebService.asmx";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    //private Toolbar toolbar;
    private static final String HUB_URL = StaticVar.webURL;
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    //private static final String HUB_chat_public_Send="chat_public";
    private static final String HUB_chat_private_Send = "chatSend";
    //private static final String HUB_chat_public_Receive="chat_public";
    private static final String HUB_EVENT_Receive = "chatReceive";
    // LogCat tag
    private static final String TAG = Chat_main.class.getSimpleName();
    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";
    //MenuItem menuItem;
    Menu menu;
    SwitchPreference enable_sound, enable_notification;
    List<Chat> listChat;
    Toolbar myToolbar;
    SharedPreferences.Editor prefs;
    Boolean isNotify_ori;
    private boolean isSelf = true;
    private HubProxy mHub;
    private String mMemID = "";
    private String mName = "";
    private String mPicfile = "";
    //private String nMemID="";
    private String nName = "";
    //MyPreferenceActivity MA;
    //private WebSocketClient client;
    private String nMessage = "";
    private String Contactid = "", errMsg = "";
    private Boolean isStranger = false, isCustomer = false;
    private SignalRFuture<Void> mSignalRFuture;
    private Button btnSend;
    private EditText inputMsg;
    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<maakkiMessage> listMessages;
    private ListView listViewMessages;
    private Chat chat;
    private ChatDAO chatDAO;
    private FriendDAO friendDAO;
    private Friend friend;
    private SharedPreferences sharedPrefs;
    private Context context;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.notify_active:
                    //Toast.makeText(getApplicationContext(), "isNotify:"+isNotify.toString(),Toast.LENGTH_LONG).show();
                    if (isNotify_ori) {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_notifications_off_white_18dp));
                        isNotify_ori = false;
                    } else {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_notifications_active_white_18dp));
                        isNotify_ori = true;
                        //prefs.putBoolean("chat_private", isNotify);
                    }
                    if(!isStranger){
                        friend.setIsNotify(isNotify_ori);
                        friendDAO.update(friend);
                    }
                    break;
                case R.id.Block:
                    BlockOrInvite("Block");
                    menuItem.setVisible(false);
                    //friend.setFriendType("Block");
                    //friendDAO.update(friend);
                    break;
                case R.id.InviteFriend:
                    BlockOrInvite("addFriend");
                    menuItem.setVisible(false);
                    //friend.setFriendType("InviteFriend");
                    //friendDAO.update(friend);
                    break;
            }
            return true;
        }
    };

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        context=this;
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, "");

        // Get the person name from notification
        Bundle bundle = this.getIntent().getExtras();
        Contactid = bundle.getString("Contactid");
        nName = bundle.getString("nName");
        friendDAO = new FriendDAO(context);
        friend = friendDAO.getbyMemberID(Integer.parseInt(Contactid));
        //Can not find friend in friendDAO that means friend is a stranger
        if(friend==null){
            isStranger=true;
            friend=new Friend();
            friend.setMemid(Integer.parseInt(Contactid));
            friend.setNickName(nName);
            isNotify_ori=true;
        }else{
            isStranger=false;
            isNotify_ori = friend.getIsNotify();
        }
        chat = new Chat();
        chatDAO = new ChatDAO(this);
        listChat = new ArrayList<Chat>();
        listChat = chatDAO.getbyContactID(Integer.parseInt(Contactid));
        //開啟連線
        try {
            mSignalRFuture.get();
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        listMessages = new ArrayList<maakkiMessage>();
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        if (listChat.size() > 0) {
            for (int i = 0; i < listChat.size(); i++) {
                maakkiMessage m = new maakkiMessage();
                m.setMessage(listChat.get(i).getContentText());
                Boolean isSelf = false;
                if (String.valueOf(listChat.get(i).getSenderid()).equals(mMemID)) {
                    isSelf = true;
                }
                //ServiceUtil.showAlertDialog(this,"Parameters","count:"+listChat.size());
                if (isSelf) {
                    m.setIsSelf(true);
                    m.setMemid(mMemID);
                } else {
                    m.setIsSelf(false);
                    m.setMemid(Contactid);
                }
                m.setFromName(getCurrentTime(listChat.get(i).getCreateDate()));
                //Message m = new Message(mMemID, nName, nMessage, isSelf);
                // Appending the message to chat list
                appendMessage(m);
                //listMessages.add(m);
            }
            //adapter.notifyDataSetChanged();
        }

        if (nName.equals("")) {
            AsyncCallWS_getNickname ag = new AsyncCallWS_getNickname();
            ag.execute();
        }
        if (bundle.getString("nMessage").length() > 0) {
            nMessage = bundle.getString("nMessage");
        }
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);

        //if (bundle.getString("isPrivate").length() > 0) {
            //if (bundle.getString("isPrivate").equals("true")) {
                //isPrivate = true;
                if (bundle.getString("isCustomer").length() > 0) {
                    if (bundle.getString("isCustomer").equals("true")) {
                        //这是一则客服来的私讯
                        isCustomer = true;
                        myToolbar.setTitle("与玛吉客服私讯中..");
                        //Toast.makeText(getApplicationContext(), "这是客服来的私讯", Toast.LENGTH_LONG).show();
                    } else {
                        //这是一则好友来的私讯
                        myToolbar.setTitle(nName);
                        isCustomer = false;
                    }
                }

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Sending message to web socket server
                //isSelf=true;
                String mess = inputMsg.getText().toString();
                if (mess.length() > 0) {
                    if (isCustomer) {
                        mess = "MsGseNdedtoCusTSErv:" + mess;
                    } else {
                        chat.setCreateDate(new Date().getTime());
                        chat.setSenderid(Integer.parseInt(mMemID));
                        chat.setReceiverid(Integer.parseInt(Contactid));
                        chat.setContentText(inputMsg.getText().toString());
                        chatDAO.insert(chat);
                    }
                    if (Contactid.length() > 0) {
                        ArrayList L = new ArrayList();
                        L.add(Contactid);
                        L.add(mMemID);

                        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
                        try {
                            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入信息", Toast.LENGTH_LONG).show();
                }
                inputMsg.setText("");
            }
        });

        //On receiving the message from SignalR

        mHub.on(HUB_EVENT_Receive, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                //如果这则信息不是来自我
                if (!memberid.equals(mMemID)) {
                    isSelf = false;

                } else {
                    isSelf = true;
                }
                String mess = "";
                if (isCustomer) {
                    //如果这是客服对话，并且不是自己发话，那发话人昵称改为“玛吉客服”
                    if (!isSelf) {
                        name = "玛吉客服";
                    }
                    mess = message.split(":")[1];
                } else {
                    mess = message;
                }
                maakkiMessage m = new maakkiMessage(memberid, name, mess, isSelf);
                m.setFromName(getCurrentTime(new Date().getTime()));
                //如果信息不是来自 Alarm，而且来自自己或是这对话框中的对象（Contactid）
                //if(!message.equals("MeSsFrOmAlaRm")){
                if (!message.equals("") && !message.contains("MeSsFrOmAlaRm")) {
                    if (!message.contains("MeSsFrOmHanDsHaKGin:")) {
                        if (!message.contains("MeSsHanDsHaKGinaNs:")) {
                            if (!message.contains("agrEeToTaKesPONsor:")) {
                                if (!message.contains("fiDbAkSpoNtorResUlT:")) {
                                    if (!message.contains("sendSpooSorNoticeToReCeIv:")) {
                                        if (!message.contains("fEdbaKUniKALlyIdtoFoUNdr:")) {
                                            if (memberid.equals(Contactid) || isSelf) {
                                                appendMessage(m);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, String.class, String.class, String.class, String.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();        
        finish();
    }

    /*
     * Appending message to list view
     */
    private void appendMessage(final maakkiMessage m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();
                // Playing device's notification
                //playBeep();
            }
        });
    }

    private void showToast(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
     * Plays device's default notification sound
     */
    public void playBeep() {
        Boolean isSound = sharedPrefs.getBoolean("enable_sound", true);
        if (isSound) {

            try {
                //Uri notification = RingtoneManager
                //        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),notification);
                String ringtone = sharedPrefs.getString("notifications_ringtone", "content://settings/system/notification_sound");
                Uri sound_effect = Uri.parse(ringtone);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound_effect);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_red, menu);
        this.menu = menu;
        if(isStranger){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
        }else{
            if (!isNotify_ori) {
                menu.getItem(0)
                        .setIcon(ContextCompat
                                .getDrawable(this, R.drawable.baseline_notifications_off_white_18dp));
            }
        }

        return true;
    }

    private void getWebService_getNickName() {
        //Create request
        //getNickname(int id, bool isMaakkiID)
        String METHOD_NAME = "getNickname";
        String SOAP_ACTION = StaticVar.namespace+"getNickname";
        //String METHOD_NAME = "getOnlineList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String bo_maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        request.addProperty("id", Contactid);
        request.addProperty("isMaakkiID", false);
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
            //soap = (SoapObject)envelope.getResponse();
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
            errMsg = json_read.getString("errMsg");
            nName = json_read.getString("nickname");
        } catch (Exception e) {
            errMsg += e.toString();
        }/**/
        //nickname=soapPrimitive.toString();
    }

    private class AsyncCallWS_getNickname extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_getNickName();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            myToolbar.setTitle(nName);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private String getCurrentTime(Long t) {
        Date d = new Date(t);
        SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm");
        return dt1.format(d.getTime());
    }
    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
    private void BlockOrInvite(String action_type){
        String title,message;
        if(action_type.equals("InviteFriend")){
            title="邀请 "+nName+" 为好友？"+Contactid;
            message="先列入邀请名单中，必须等到 "+nName+" 同意后，才会是你的好友！";
        }else{
            title="封锁 "+nName+"？"+Contactid;
            message="列入拒绝往来名单中，对方此后发给你的信息将不会再出现！";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(Chat_red.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //FriendList fl=new FriendList();
                        //friendAction(action_type,Contactid);
                        AsyncCallWS_friendAction friendActionTask=new AsyncCallWS_friendAction();
                        friendActionTask.execute(action_type,Contactid);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    public void friendAction(String actType, String friend_id) {
        //Create
        String METHOD_NAME = "friendAction";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = MemID + "Community-M@@kki.cc" + timeStamp + friend_id + actType;
        String identifyStr = ServiceUtil.getHashCode(encryptStr).toUpperCase();
        //Token=MemID + "Community-M@@kki.cc" + timestamp + friend_id + getType
        request.addProperty("member_id", MemID);
        request.addProperty("friend_id", friend_id);
        request.addProperty("actType", actType);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        /*errMsg="member_id:"+MemID+
                "\nfriend_id:"+friend_id+
                "\nactType:"+actType;*/
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        SoapPrimitive soapPrimitive = null;
        try {
            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
        String tmp = soapPrimitive.toString();
        //String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        try {
            json_read = new JSONObject(tmp);
            //JSONArray jsonArray = json_read.getJSONArray("friendList");
            String errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "查无此会员";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "已经是好友，咋又发送交友邀请呢？";
            } else if (errCode.equals("6")) {
                errMsg = "已發送過交友邀請；勿重覆發送";
            } else if (errCode.equals("7")) {
                errMsg = "對方正在等待你確認交友邀請";
            } else if (errCode.equals("8")) {
                errMsg = "已经封鎖了對方，無法發送交友邀請";
            } else if (errCode.equals("9")) {
                errMsg = "對方封鎖你，無法加好友";
            } else if (errCode.equals("10")) {
                errMsg = "對方沒有發送交友邀請，無法確認";
            } else if (errCode.equals("11")) {
                errMsg = "沒有發送交友邀請，無法取消交友邀請";
            } else if (errCode.equals("12")) {
                errMsg = "對方不是你的好友，無須刪除好友";
            } else if (errCode.equals("13")) {
                errMsg = "已經封鎖，勿重覆封鎖";
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }
    private class AsyncCallWS_friendAction extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String actType = params[0];
            String friend_id = params[1];
            friendAction(actType, friend_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.equals("")) {
                ServiceUtil.showAlertDialog(context,"Error", errMsg);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}