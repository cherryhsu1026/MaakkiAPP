package com.maakki.maakkiapp;


/**
 * Created by ryan on 2016/7/24.
 */

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
//import com.codebutler.android_websockets.WebSocketClient;

public class Chat_CustomerService extends AppCompatActivity {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    //private Toolbar toolbar;
    private static final String HUB_URL = "http://www.maakki.com/";
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
    List<Chat> listChat;
    Toolbar myToolbar;
    SharedPreferences.Editor prefs;
    Boolean isSound;
    private boolean isSelf = true;
    private HubProxy mHub;
    private String mMemID = "";
    private String mName = "";
    private String mPicfile = "";
    //private String nMemID="";
    private String nName = "";
    private String nMessage = "";

    //private WebSocketClient client;
    private String Contactid = "";
    private Boolean isPrivate = false, isCustomer = false;
    private SignalRFuture<Void> mSignalRFuture;
    private Menu menu;
    private Button btnSend;
    private EditText inputMsg;
    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<maakkiMessage> listMessages;
    private ListView listViewMessages;
    private Chat chat;
    private ChatDAO chatDao;
    private SharedPreferences sharedPrefs;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.sound_active:
                    //Toast.makeText(getApplicationContext(), "isNotify:"+isNotify.toString(),Toast.LENGTH_LONG).show();
                    if (isSound) {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_off_white_24dp));
                        isSound = false;
                        //prefs.putString("enable_notification", "false");
                        //prefs.putBoolean("enable_sound", false);
                    } else {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_volume_up_white_24dp));
                        isSound = true;
                        prefs.putBoolean("enable_notification", isSound);
                    }
                    prefs.putBoolean("enable_sound", isSound);
                    prefs.commit();
                    //Intent intent=new Intent(Chat_main.this,FriendlistActivity.class);
                    //startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "Check friends!",Toast.LENGTH_LONG).show();
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
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        //mName = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mName = "玛吉客服";
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        //SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key6, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        isSound = sharedPrefs.getBoolean("enable_sound", true);
        //Innitiate Chat DB
        chat = new Chat();
        chatDao = new ChatDAO(this);
        listChat = new ArrayList<Chat>();
        listChat = chatDao.getAll();
        //開啟連線
        try {
            mSignalRFuture.get();
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.chat_main);

        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);


        listMessages = new ArrayList<maakkiMessage>();
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        // Getting the person name from notification
        Bundle bundle = this.getIntent().getExtras();

        Contactid = bundle.getString("Contactid");
        nName = bundle.getString("nName");
        if (bundle.getString("nMessage").length() > 0) {
            nMessage = bundle.getString("nMessage");
        }/* */
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object

        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        if (bundle.getString("isPrivate").length() > 0) {
            if (bundle.getString("isPrivate").equals("true")) {
                isPrivate = true;
                if (bundle.getString("isCustomer").length() > 0) {
                    if (bundle.getString("isCustomer").equals("true")) {
                        //这是一则客服来的私讯
                        isCustomer = true;
                        myToolbar.setTitle("客服私讯：" + nName);
                        Toast.makeText(getApplicationContext(), "这是通过客服发过来的信息", Toast.LENGTH_LONG).show();
                    } else {
                        //这是一则好友来的私讯
                        myToolbar.setTitle("To：" + nName);
                        isCustomer = false;
                    }
                }
            }
        } else {
            isPrivate = false;
        }

        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        //nName = bundle.getString("nName");

        //nMemID=bundle.getString("nMemID");
        if (!Contactid.equals(mMemID)) {
            isSelf = false;
        } else {
            isSelf = true;
        }
        //
        //Toast.makeText(getApplicationContext(), "isPrivate.length()>0"+nName+nMessage,Toast.LENGTH_LONG).show();
        if (!nMessage.contains("MeSsFrOmAlaRm") && !nMessage.equals("")) {
            maakkiMessage m = new maakkiMessage(mMemID, nName, nMessage, isSelf);
            // Appending the message to chat list
            appendMessage(m);
        }


        //Send Message Public
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Sending message to web socket server
                //sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
                //      .toString()));
                //isSelf=true;
                String mess = inputMsg.getText().toString();
                if (mess.length() > 0) {
                    if (isCustomer) {
                        mess = "MsGseNdedFrCuSto:" + mess;
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
                //Message m = new Message(name, mess, isSelf);
                // Appending the message to chat list
                //appendMessage(m);
                // Clearing the input filed once message was sent
                inputMsg.setText("");
            }
        });

        //On receiving the message from SignalR

        mHub.on(HUB_EVENT_Receive, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                if (!memberid.equals(mMemID)) {
                    isSelf = false;
                } else {
                    isSelf = true;
                    name = "玛吉客服";
                }
                String mess = "";
                if (isCustomer) {
                    //如果这是客服对话，并且不是自己发话，那发话人昵称改为“玛吉客服”
                    if (!isSelf) {
                        name = "玛吉客服";
                    }
                    mess = message.split(":")[1];
                }
                maakkiMessage m = new maakkiMessage(memberid, name, mess, isSelf);
                //如果信息不是来自 Alarm，而且来自自己或是这对话框中的对象（Contactid）
                //if(!message.equals("MeSsFrOmAlaRm")){
                if (!message.equals("") && !message.contains("MeSsFrOmAlaRm")) {
                    //if (!message.contains("MsGseNdedtoCusTSErv:")) {
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
                    //}
                }
            }
        }, String.class, String.class, String.class, String.class);
        /**/
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
        getMenuInflater().inflate(R.menu.menu_issound, menu);
        this.menu = menu;
        if (!isSound) {
            menu.getItem(0)
                    .setIcon(ContextCompat
                            .getDrawable(this, R.drawable.baseline_volume_off_white_24dp));
        }
        return true;
    }
}