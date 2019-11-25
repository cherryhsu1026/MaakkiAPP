package com.maakki.maakkiapp;


/**
 * Created by ryan on 2016/7/24.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
//import com.codebutler.android_websockets.WebSocketClient;

public class Chat_main extends AppCompatActivity {
    //private Toolbar toolbar;
    private static final String HUB_URL = "http://www.maakki.com/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    private static final String HUB_chat_public_Send = "chat_public";
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_chat_public_Receive = "chat_public";
    private static final String HUB_EVENT_Receive = "chatReceive";
    // LogCat tag
    private static final String TAG = Chat_main.class.getSimpleName();
    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";
    private boolean isSelf = true;
    private HubProxy mHub;
    private String mMemID = "", isCustomer = "true";
    private String mName = "";
    private String mPicfile = "";
    private String nMemID = "";
    private String nName = "";
    private String nMessage = "";
    private String Contactid = "";
    private Boolean isPrivate = false;
    private SignalRFuture<Void> mSignalRFuture;
    private Button btnSend;
    private EditText inputMsg;
    //private WebSocketClient client;
    private SharedPreferences sharedPrefs;
    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<maakkiMessage> listMessages;
    private maakkiMessage message;
    private ListView listViewMessages;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.friends:
                    Intent intent = new Intent(Chat_main.this, FriendlistActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "Check friends!",Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        //SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key6,false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        /*1123*/
        /*ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);*/

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //Intent intent1 = new Intent(Chat_main.this, MainActivity.class);
                //startActivity(intent1);
            }
        });


        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        message = new maakkiMessage();
        listMessages = new ArrayList<maakkiMessage>();
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        // Getting the sender name from notification
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.getString("Contactid").length() > 0) {
            Contactid = bundle.getString("Contactid");
        }
        if (bundle.getString("isPrivate").length() > 0) {
            if (bundle.getString("isPrivate").equals("true")) {
                isPrivate = true;
            } else {
                isPrivate = false;
            }
            nName = bundle.getString("nName");
            nMessage = bundle.getString("nMessage");
            nMemID = bundle.getString("nMemID");
            isCustomer = bundle.getString("isCustomer");
            if (!nMemID.equals(mMemID)) {

                isSelf = false;
                if (isPrivate) {
                    nName = nName + " 私訊給你";
                }
            } else {
                isSelf = true;
            }
            //Toast.makeText(getApplicationContext(), "nMemID:"+nMemID+"/"+mMemID,Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), nName+"/"+nMessage,Toast.LENGTH_LONG).show();
            maakkiMessage m = new maakkiMessage(nMemID, nName, nMessage, isSelf);
            //if(!nMessage.contains("MsGseNdedtoCusTSErv:")&&!nMessage.contains("MsGseNdedFrCuSto:")&&!isCustomer.equals("true")){
            appendMessage(m);
            //}
            // Appending the message to chat list

        } else {
            //Toast.makeText(getApplicationContext(), "isPrivate:null",Toast.LENGTH_LONG).show();
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
                    //私訊對話
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
                    //公開發言
                    else {
                        try {
                            mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
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
        /*1123  */
        /*mHub.on(HUB_Receive,new SubscriptionHandler4<String,String,String,String>()
        {
            //@Override
            public void run(String memberid,String name,String picFile,String message)
            {
                if (!memberid.equals(mMemID)) {
                    Log.d(TAG, String.format("Got string message! %s", message));
                    parseMessage(message);
                }
            }
        }, String.class, String.class, String.class,String.class);*/

        mHub.on(HUB_chat_public_Receive, new SubscriptionHandler4<String, String, String, String>() {
            @Override
            public void run(String memberid, String name, String picFile, String message) {
                if (!memberid.equals(mMemID)) {
                    isSelf = false;
                    Log.d(TAG, String.format("Got string message! %s", message));

                    // Playing device's notification
                    //playBeep();
                } else {
                    isSelf = true;
                }

                maakkiMessage m = new maakkiMessage(memberid, name, message, isSelf);
                if (!message.contains("MeSsFrOmAlaRm") && !message.contains("MsGseNdedtoCusTSErv:") && !message.contains("MsGseNdedFrCuSto:")) {
                    if (!message.contains("sEnLatTiMCutMeSVieOLe:")) {
                        appendMessage(m);
                    }
                }

            }
        }, String.class, String.class, String.class, String.class);
        mHub.on(HUB_EVENT_Receive, new SubscriptionHandler4<String, String, String, String>() {
            //@Override
            public void run(String memberid, String name, String picFile, String message) {
                if (!memberid.equals(mMemID)) {
                    isSelf = false;
                    name = name + " 私訊給你";

                    // Playing device's notification
                    //playBeep();
                } else {
                    isSelf = true;
                }

                maakkiMessage m = new maakkiMessage(memberid, name, message, isSelf);
                // Appending the message to chat list
                if (!message.contains("MeSsFrOmAlaRm") && !message.contains("MsGseNdedtoCusTSErv:") && !message.contains("MsGseNdedFrCuSto:")) {
                    appendMessage(m);
                }
            }
        }, String.class, String.class, String.class, String.class);

        /*1123*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

/*        if(client != null & client.isConnected()){
            client.disconnect();
        }*/
    }

    /**
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

    /**
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
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }
}