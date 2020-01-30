package com.maakki.maakkiapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 2016/8/16.
 */
public class FriendlistActivity extends AppCompatActivity {
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+ "WebService.asmx";
    private final String SOAP_ACTION =  StaticVar.namespace+"getOnlineList";
    private final String METHOD_NAME = "getOnlineList";
    //private static String fahren;
    //TextView tv;
    GridView gridview;
    ImageView iv_invitefriend;
    Toolbar myToolbar;
    private String TAG = "PGGURU";
    private ImageAdapter adapter;
    private List<Friend> listFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlistactivity);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //Intent intent1 = new Intent(FriendlistActivity.this, Chat_main.class);
                //startActivity(intent1);
            }
        });
        gridview = (GridView) findViewById(R.id.gridview);
        listFriend = new ArrayList<>();
        iv_invitefriend = (ImageView) findViewById(R.id.iv_invitefriend);
        iv_invitefriend.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                goFriendlist();
            }
        });
        //Create instance for AsyncCallWS
        AsyncCallWS_getOnlineList getOnlineListTask = new AsyncCallWS_getOnlineList();
        //Call execute
        getOnlineListTask.execute();
    }


    public void goFriendlist() {
        String redUrl = StaticVar.webURL+"community/Friendlist.aspx?act_type=Invite";
        //Toast.makeText(getApplicationContext(), "redUri="+redUrl, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(FriendlistActivity.this, WebMain2.class);
        Bundle bundle = new Bundle();
        bundle.putString("redirUrl", redUrl);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void getOnlineList() {
        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        request.addProperty("mid", MemID);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        SoapObject friendDetails = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            friendDetails = (SoapObject) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        for (int i = 0; i < friendDetails.getPropertyCount(); i++) {
            Friend f = new Friend();
            Object property = friendDetails.getProperty(i);
            //if (property instanceof SoapObject) {
            SoapObject friendObj = (SoapObject) property;
            f.setMemid(Integer.parseInt(friendObj.getProperty("g").toString()));
            //f.setConnid(friendObj.getProperty("c").toString());
            f.setNickName(friendObj.getProperty("n").toString());
            //f.setCreatedate(friendObj.getProperty("t").toString());
            f.setPicfilePath(friendObj.getProperty("p").toString());
            listFriend.add(f);
        }
    }
    private class AsyncCallWS_getOnlineList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getOnlineList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.i(TAG, "onPostExecute");
            //tv.setText(fahren);
            myToolbar.setTitle("在线好友(" + listFriend.size() + ")");
            adapter = new ImageAdapter(getApplicationContext(), listFriend);
            gridview.setAdapter(adapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Friend f = listFriend.get(position);
                    Toast.makeText(FriendlistActivity.this, "開始和 " + f.getNickName().toString() + " 私訊", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FriendlistActivity.this, Chat_red.class);
                    Bundle bundle = new Bundle();
                    String cMemID = String.valueOf(f.getMemid());
                    String cName = f.getNickName();
                    //isPrivate=true,这是一则私讯
                    bundle.putString("isPrivate", "true");
                    bundle.putString("isCustomer", "false");
                    bundle.putString("nName", cName);
                    bundle.putString("Contactid", cMemID);
                    bundle.putString("nMessage", "");
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
            if (listFriend.size() == 0) {
                iv_invitefriend.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //tv.setText("Loading..");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }
    }


}
