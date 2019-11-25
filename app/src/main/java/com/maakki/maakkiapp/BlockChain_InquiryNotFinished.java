package com.maakki.maakkiapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class BlockChain_InquiryNotFinished extends AppCompatActivity {
    private MessagesListAdapter adapter;
    private List<maakkiMessage> listMessages;
    private ListView listViewMessages;
    Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blockchain);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        listMessages = new ArrayList<maakkiMessage>();
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);
        //finally,I can get message from coreservice
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_intervalTime_CORESERVICE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intervalTime=intent.getStringExtra("intervalTime2");
                Toast.makeText(context,"intervalTime:"+intervalTime,Toast.LENGTH_LONG).show();
                //ServiceUtil.showAlertDialog(context,"Parameters",intervalTime);
            }
        };
        registerReceiver(receiver, filter);
        //Get InquiryList
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("INVOKE_InquiryNotFinishedRE_CORESERVICE");
        BroadcastReceiver receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess =intent.getStringExtra("What");
                //Toast.makeText(context,mess,Toast.LENGTH_LONG).show();
                //showAlertDialog("Blockchain",mess);
                maakkiMessage m = new maakkiMessage();
                m.setMessage(mess);
                m.setIsSelf(false);
                m.setMemid("");
                m.setFromName("");
                appendMessage(m);
            }
        };
        registerReceiver(receiver2, filter2);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.notification:
                    Intent i = new Intent(BlockChain_InquiryNotFinished.this, PreNotificationList.class);
                    startActivity(i);
                    break;
            }
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blockchain, menu);
        return true;
    }
    private void appendMessage(final maakkiMessage m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
