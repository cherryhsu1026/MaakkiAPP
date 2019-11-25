package com.maakki.maakkiapp;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by ryan on 2017/7/17.
 */

public class FriendList extends AppCompatActivity {
    private String redUrl = "http://www.maakki.cc/community/ecard.aspx";
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    private ArrayList ignoreList;
    private RelativeLayout rl_top,RL_nothing;
    private Toolbar myToolbar;
    ListView listview;
    TextView tv_submit,tv_all, tv_friend, tv_search, tv_iinvite, tv_addme, tv_block, title, tv_message, time;
    ImageView iv_search,icon, no_friends;
    Integer count;
    Bitmap remote_picture;
    FloatingActionButton fab;
    private Long notification_id = 0l, notification_millisecs = 0l;
    private boolean hasDonloaded,isRedenvelope = false, isAscending = false;
    private String getType = "All",getType2, errMsg = "", sendernickname = "", message = "", contactnick = "", mMemID;
    private Boolean isActivate;
    private Context context;
    private List<Friend> listFriend, newlist;
    private FriendDAO friendDAO;
    private Friend friend, friend_need_to_insert;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private FriendAdapter adapter;
    private BroadcastReceiver receiver;
    String cName,cMemID;
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private long lasttime_ChatToThisOne;
    private ChatDAO chatDAO;
    private Chat chat;
    private EditText et_keyword;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.search:
                    displaySearch();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);
        context = this;
        ignoreList=new ArrayList();
        String mess="";
        if (getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            getType2 = bundle.getString("getType");
            cMemID = bundle.getString("cMemID");
            cName = bundle.getString("cName");
            //mess="getType:"+getType+"\ngetType2:"+getType2+"\ncMemID:"+cMemID+"\ncName:"+cName;
            if(getType2.equals("InviteMe")){
                getType = "InviteMe";
                //selectView(getType);
                //AsyncCallWS_getFriend getFriendTask = new AsyncCallWS_getFriend();
                //getFriendTask.execute("InviteMe");
            }/*else if(getType2.equals("InviteFriend")){
                InviteSponsor();
            }*/
        }else{
            getType2="";
        }
        //showAlertDialog("mess",mess);
        friendDAO = new FriendDAO(this);
        friend = new Friend();
        chatDAO = new ChatDAO(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setTitle("全部列表 "+friendDAO.getCount());
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, WebMain2.class);
                intent.putExtra("redirUrl", redUrl);
                startActivity(intent);

            }
        });
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();

        config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        fab = (FloatingActionButton) findViewById(R.id.setting_button);
        fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
        //fab.bringToFront();
        et_keyword=(EditText)findViewById(R.id.et_keyword);
        iv_search=(ImageView)findViewById(R.id.iv_search);
        tv_submit=(TextView)findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_keyword.getText().toString().isEmpty()){
                    ServiceUtil.showAlertDialog(context,"请输入搜寻关键字","您忘记输入关键字了，找啥呢？");
                }else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_keyword.getWindowToken(), 0);
                    et_keyword.clearFocus();
                    rl_top.setVisibility(View.GONE);
                    myToolbar.setTitle("搜寻结果");
                    AsyncCallWS_searchMember searchMemberTask = new AsyncCallWS_searchMember();
                    searchMemberTask.execute();
                }
            }
        });
        title = (TextView) findViewById(R.id.title);
        tv_message = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        icon = (ImageView) findViewById(R.id.icon);
        listview = (ListView) findViewById(R.id.listview);
        RL_nothing = (RelativeLayout) findViewById(R.id.RL_nothing);
        rl_top= (RelativeLayout) findViewById(R.id.rl_top);
        no_friends = (ImageView) findViewById(R.id.no_friend);
        no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_friends));
        //tv = (TextView) findViewById(R.id.tv);
        listFriend = new ArrayList<Friend>();
        count = friendDAO.getCount();
        //listFriend = friendDAO.getAll();
        adapter = new FriendAdapter(this, R.layout.list_item, listFriend);
        listview.setAdapter(adapter);
        fab.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAscending) {
                    isAscending = false;
                    fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
                } else {
                    isAscending = true;
                    fab.setImageResource(R.drawable.ic_arrow_upward_white_18dp);
                }
                Collections.reverse(listFriend);
                adapter.notifyDataSetChanged();
            }
        });
        /*if (count > 0) {
            //Collections.reverse(listFriend);
            //myToolbar.setTitle("全部列表(" + count.toString() + ")");
            if (count > 10) {
                fab.setVisibility(View.VISIBLE);
            }
            setOnClick();
        } else {
            //myToolbar.setTitle("全部列表");
            RL_nothing.setVisibility(View.VISIBLE);
        }*/
        tv_all = (TextView) findViewById(R.id.tv_all);
        tv_all.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getType = "All";
                renewList(context, getType);
            }
        });
        tv_friend = (TextView) findViewById(R.id.tv_friend);
        tv_friend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getType = "Friend";
                renewList(context, getType);
            }
        });
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySearch();
            }
        });
        tv_iinvite = (TextView) findViewById(R.id.tv_iinvite);
        tv_iinvite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getType = "InviteFriend";
                renewList(context, getType);
            }
        });
        tv_addme = (TextView) findViewById(R.id.tv_addme);
        tv_addme.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getType = "InviteMe";
                selectView(getType);
                AsyncCallWS_getFriend getFriendTask = new AsyncCallWS_getFriend();
                getFriendTask.execute("InviteMe","0");
            }
        });
        tv_block = (TextView) findViewById(R.id.tv_black);
        tv_block.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getType = "Block";
                renewList(context, getType);
            }
        });
        no_friends.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySearch();
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_to_FRIENDLIST");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(context,"Hey:"+getType,Toast.LENGTH_LONG).show();
                getType = "Friend";
                renewList(context, getType);
            }
        };
        registerReceiver(receiver, filter);
        //selectView(getType);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("INVOKE_to_FRIENDLIST2");
        BroadcastReceiver receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess="Hello...";
                if(intent.getExtras()!=null){
                    Bundle bundle = intent.getExtras();
                    String getType = bundle.getString("getType");
                    String cMemID = bundle.getString("cMemID");
                    String cName = bundle.getString("cName");
                    mess = "getType:"+getType+
                            "\ncMemID:"+cMemID+
                            "\ncName:"+cName;
                }
                ServiceUtil.showAlertDialog(context,"Parameters",mess);
            }
        };
        registerReceiver(receiver2, filter2);
        //this.setResult(1, new Intent());
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(context,"getType:"+getType,Toast.LENGTH_LONG).show();
        if(getType2.equals("InviteFriend")){
            hasDonloaded=false;
            InviteSponsor(hasDonloaded);
        }else{
            renewList(context, getType);
        }

    }

    private void displaySearch(){
        getType="Search";
        selectView(getType);
        listview.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        et_keyword.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_keyword, InputMethodManager.SHOW_IMPLICIT);
    };

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void setOnClick() {
        //listview.setOnTouchListener(swipeDetector);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent,View v,
                                    int position, long id) {
                friend = listFriend.get(position);
                cMemID = String.valueOf(friend.getMemid());
                cName = friend.getNickName();
                if(getType.equals("Friend")){
                    AlertDialog alertDialog = new AlertDialog.Builder(FriendList.this).create();
                    alertDialog.setTitle("确定删除好友？");
                    alertDialog.setMessage("您确定要删除 " + cName + " ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    v.setVisibility(View.GONE);
                                    deleteCell(v, position);
                                    friendDAO.delete(friend.getId());
                                    AsyncCallWS_friendAction friendActionTask = new AsyncCallWS_friendAction();
                                    friendActionTask.execute("delFriend", cMemID);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "拉黑",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    v.setVisibility(View.GONE);
                                    deleteCell(v, position);
                                    friend.setFriendType("B");
                                    friendDAO.update(friend);
                                    AsyncCallWS_friendAction friendActionTask = new AsyncCallWS_friendAction();
                                    friendActionTask.execute("Block", cMemID);
                                    dialog.dismiss();
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
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                friend = listFriend.get(position);
                //adapter.notifyDataSetChanged();
                String cMemID = String.valueOf(friend.getMemid());
                String cName = friend.getNickName();
                if (!swipeDetector.swipeDetected()) {
                    friend.setLastChatTime(new Date().getTime());
                    friendDAO.update(friend);
                    listFriend.get(position).setLastChatTime(new Date().getTime());
                    Toast.makeText(FriendList.this, "開始和 " + friend.getNickName().toString() + " 私訊", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FriendList.this, Chat_red.class);
                    Bundle bundle = new Bundle();
                    //isPrivate=true,这是一则私讯
                    bundle.putString("isPrivate", "true");
                    bundle.putString("isCustomer", "false");
                    bundle.putString("nName", cName);
                    bundle.putString("Contactid", cMemID);
                    bundle.putString("nMessage", "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    /*AlertDialog alertDialog = new AlertDialog.Builder(FriendList.this).create();
                    alertDialog.setTitle("确定删除好友？");
                    alertDialog.setMessage("您确定要删除 " + cName + " ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    v.setVisibility(View.GONE);
                                    deleteCell(v, position);
                                    friendDAO.delete(friend.getId());
                                    AsyncCallWS_friendAction friendActionTask = new AsyncCallWS_friendAction();
                                    friendActionTask.execute("delFriend", cMemID);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();*/
                }
            }
        });
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            //@Override
            public void onAnimationEnd(Animation arg0) {
                listFriend.remove(index);
                count=listFriend.size();
                count--;
                if (count < 10) {
                    fab.setVisibility(View.INVISIBLE);
                    if (count < 1) {
                        //btn.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                        RL_nothing.setVisibility(View.VISIBLE);
                    }
                }

                //tv.setText("(" + count + ")");
                ViewHolder vh = (ViewHolder) v.getTag();
                vh.needInflate = true;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();
        final int top = v.getTop();
        //Toast.makeText(getApplicationContext(), "collapsetop:"+top , Toast.LENGTH_SHORT).show();
        final int ANIMATION_DURATION = 200;
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }

    public class ViewHolder {
        public TextView tv_msg,text_title, text_count;
        public TextView text_message;
        public TextView text_time;
        public ImageView image_icon, image_notify,iv_invite,iv_yes,iv_no;
        public RelativeLayout rl_time,rl_click;
        public String friendtype,actType;
        public boolean needInflate;
        //public boolean yesChecked,noChecked;
    }

    public class FriendAdapter extends ArrayAdapter<Friend> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public FriendAdapter(Context context, int textViewResourceId, List<Friend> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            Friend f = (Friend) getItem(position);
            //String getType;

            if (convertView == null) {
                //Toast.makeText(FriendList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item_friendlist, parent, false);
                setViewHolder(view);

            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(FriendList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item_friendlist, parent, false);
                setViewHolder(view);

            } else {
                //Toast.makeText(FriendList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
                //setViewHolder(view);
            }
            holder = (ViewHolder) view.getTag();
            String ft = f.getFriendType();
            holder.friendtype = ft;
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            String title = "";
            String mpicfile = f.getPicfilePath();
            String pic_url = "http://www.maakki.cc/ashx/showImage.ashx?file_id=" + mpicfile + "&width=45&height=45&forcibly=Y&dimg=Y";
            imageLoader.displayImage(pic_url, holder.image_icon);
            lasttime_ChatToThisOne = f.getLastChatTime();
            int count = chatDAO.getCountByContactIDSinceLastGetin(String.valueOf(f.getMemid()), lasttime_ChatToThisOne);
            if (count > 0) {
                holder.text_count.setText(String.valueOf(count));
                holder.text_count.setVisibility(View.VISIBLE);
            } else {
                holder.text_count.setVisibility(View.GONE);
            }
            holder.text_title.setText(f.getNickName());
            chat = chatDAO.getLastChatbyContactID(f.getMemid());
            String time;
            if (chat != null) {
                message = chat.getContentText();
                //Long t = chat.getCreateDate();
                Long t=f.getLastMessageTime();
                time = getCurrentTime(t);
            } else {
                message = "";
                time = "";
            }
            holder.text_message.setText(message);
            holder.text_time.setText(time);
            if (f.getIsNotify()) {
                holder.image_notify.setImageDrawable(getResources().getDrawable(R.drawable.baseline_notifications_active_999_14dp));
            } else {
                holder.image_notify.setImageDrawable(getResources().getDrawable(R.drawable.baseline_notifications_off_999_14dp));
            }

            switch (getType){
                case "All":
                    holder.rl_time.setVisibility(View.VISIBLE);
                    holder.rl_click.setVisibility(View.GONE);
                    break;
                case "Friend":
                    holder.rl_time.setVisibility(View.VISIBLE);
                    holder.rl_click.setVisibility(View.GONE);
                    break;
                case "Search":
                    holder.rl_time.setVisibility(View.GONE);
                    holder.rl_click.setVisibility(View.VISIBLE);
                    holder.iv_invite.setVisibility(View.GONE);
                    holder.iv_yes.setVisibility(View.GONE);
                    holder.iv_no.setVisibility(View.GONE);
                    holder.tv_msg.setVisibility(View.VISIBLE);
                    if(f.getFriendType().equals("F")){
                        holder.tv_msg.setText("已是好友");
                    }else if(f.getFriendType().equals("B")){
                        holder.tv_msg.setText("拒绝往来");
                    }else if(f.getFriendType().equals("A")){
                        holder.tv_msg.setText("加我好友");
                    }else if(f.getFriendType().equals("I")){
                        holder.tv_msg.setText("已经邀请");
                    }else{
                        holder.iv_invite.setVisibility(View.VISIBLE);
                        holder.tv_msg.setVisibility(View.GONE);
                        holder.actType="addFriend";
                    }
                    break;
                case "InviteMe":
                    holder.rl_time.setVisibility(View.GONE);
                    holder.rl_click.setVisibility(View.VISIBLE);
                    holder.iv_invite.setVisibility(View.GONE);
                    holder.tv_msg.setVisibility(View.GONE);
                    holder.iv_yes.setVisibility(View.VISIBLE);
                    holder.iv_no.setVisibility(View.VISIBLE);
                    if(f.getFriendType().equals("RI")){
                        holder.tv_msg.setText("已经拒绝");
                        holder.iv_yes.setVisibility(View.GONE);
                        holder.iv_no.setVisibility(View.GONE);
                        holder.tv_msg.setVisibility(View.VISIBLE);
                    }else if(f.getFriendType().equals("F")){
                        holder.tv_msg.setText("已经同意");
                        holder.iv_yes.setVisibility(View.GONE);
                        holder.iv_no.setVisibility(View.GONE);
                        holder.tv_msg.setVisibility(View.VISIBLE);
                    }
                    break;
                case "InviteFriend":
                    holder.rl_time.setVisibility(View.GONE);
                    holder.rl_click.setVisibility(View.VISIBLE);
                    holder.iv_invite.setVisibility(View.GONE);
                    holder.tv_msg.setVisibility(View.GONE);
                    holder.iv_yes.setVisibility(View.GONE);
                    holder.iv_no.setVisibility(View.VISIBLE);
                    if(f.getFriendType().equals("CI")){
                        holder.tv_msg.setText("取消邀请");
                        holder.iv_yes.setVisibility(View.GONE);
                        holder.iv_no.setVisibility(View.GONE);
                        holder.tv_msg.setVisibility(View.VISIBLE);
                    }
                    break;
                case "Block":
                    holder.rl_time.setVisibility(View.GONE);
                    holder.rl_click.setVisibility(View.VISIBLE);
                    holder.iv_invite.setVisibility(View.GONE);
                    holder.tv_msg.setVisibility(View.GONE);
                    holder.iv_yes.setVisibility(View.GONE);
                    holder.iv_no.setVisibility(View.VISIBLE);
                    holder.actType="Unblock";
                    if(f.getFriendType().equals("UB")){
                        holder.tv_msg.setText("已经解除");
                        holder.iv_yes.setVisibility(View.GONE);
                        holder.iv_no.setVisibility(View.GONE);
                        holder.tv_msg.setVisibility(View.VISIBLE);
                    }
                    break;
            }

            holder.iv_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncCallWS_friendAction friendActionTask=new AsyncCallWS_friendAction();
                    friendActionTask.execute(holder.actType,String.valueOf(f.getMemid()));
                    f.setFriendType("I");
                    friendDAO.insert(f);
                    holder.iv_invite.setVisibility(View.GONE);
                    holder.tv_msg.setText("已经邀请");
                    holder.tv_msg.setVisibility(View.VISIBLE);
                }
            });
            holder.iv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.actType="confirmInvite";
                    AsyncCallWS_friendAction friendActionTask=new AsyncCallWS_friendAction();
                    friendActionTask.execute(holder.actType,String.valueOf(f.getMemid()));
                    holder.iv_yes.setVisibility(View.GONE);
                    holder.iv_no.setVisibility(View.GONE);
                    holder.tv_msg.setText("已经同意");
                    holder.tv_msg.setVisibility(View.VISIBLE);
                    f.setFriendType("F");
                    friendDAO.insert(f);
                    //holder.yesChecked=true;
                    //holder.needInflate=true;
                }
            });
            holder.iv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(holder.friendtype){
                        case "I":
                            holder.actType="cancelInvite";
                            holder.tv_msg.setText("取消邀请");
                            f.setFriendType("CI");
                            friendDAO.delete(f.getId());
                            break;
                        case "A":
                            holder.actType="refuseInvite";
                            holder.tv_msg.setText("已经拒绝");
                            f.setFriendType("RI");
                            break;
                        case "B":
                            holder.actType="Unblock";
                            holder.tv_msg.setText("已经解除");
                            f.setFriendType("UB");
                            friendDAO.delete(f.getId());
                    }
                    AsyncCallWS_friendAction friendActionTask=new AsyncCallWS_friendAction();
                    friendActionTask.execute(holder.actType,String.valueOf(f.getMemid()));
                    holder.iv_yes.setVisibility(View.GONE);
                    holder.iv_no.setVisibility(View.GONE);
                    holder.tv_msg.setVisibility(View.VISIBLE);
                    //holder.noChecked=true;
                    //holder.needInflate=true;
                    //listFriend.remove(position);
                    //adapter.notifyDataSetChanged();
                }
            });
            return view;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.tv_msg=(TextView) view.findViewById(R.id.tv_msg);
            vh.rl_time= (RelativeLayout) view.findViewById(R.id.rl_time);
            vh.rl_click= (RelativeLayout) view.findViewById(R.id.rl_click);
            vh.iv_invite=(ImageView) view.findViewById(R.id.iv_invite);
            vh.iv_yes=(ImageView) view.findViewById(R.id.iv_yes);
            vh.iv_no=(ImageView) view.findViewById(R.id.iv_no);
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_count = (TextView) view.findViewById(R.id.tv_count);
            vh.text_title = (TextView) view.findViewById(R.id.title);
            vh.text_message = (TextView) view.findViewById(R.id.message);
            vh.text_time = (TextView) view.findViewById(R.id.time);
            vh.image_notify = (ImageView) view.findViewById(R.id.iv_notify);
            vh.needInflate = false;
            //vh.yesChecked=false;
            //vh.noChecked=false;
            view.setTag(vh);
        }


    }

    private String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dt1.format(d.getTime());
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(FriendList.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void renewList(Context context, String getType) {
        selectView(getType);
        //friendDAO = new FriendDAO(context);
        if (getType.equals("All")) {
            AsyncCallWS_getFriend getFriendTask = new AsyncCallWS_getFriend();
            getFriendTask.execute(getType,"0");
        } else if (getType.equals("Friend")) {
            newlist=new ArrayList<>();
            newlist = friendDAO.getList("F");
            no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_friends));
        } else if (getType.equals("Search")) {
            //displaySearch();
        } else if (getType.equals("InviteFriend")) {
            newlist=new ArrayList<>();
            newlist = friendDAO.getList("I");
            no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_friends));
        } else if (getType.equals("InviteMe")) {
            //getType = "InviteMe";
            selectView(getType);
            AsyncCallWS_getFriend getFriendTask = new AsyncCallWS_getFriend();
            getFriendTask.execute(getType,"0");
        }else if (getType.equals("Block")) {
            newlist=new ArrayList<>();
            newlist = friendDAO.getList("B");
            no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_result));
        }
        if(!getType.equals("Search")&!getType.equals("All")&!getType.equals("InviteMe")){
            if (newlist.size() == 0) {
                listview.setVisibility(View.GONE);
                no_friends.setVisibility(View.VISIBLE);
            } else {
                listFriend.clear();
                for (int i = 0; i < newlist.size(); i++) {
                    listFriend.add(newlist.get(i));
                }
                no_friends.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
        if(getType.equals("Search")){
            rl_top.setVisibility(View.VISIBLE);
        }else{
            rl_top.setVisibility(View.GONE);
        }
    }

    private Bitmap DownloadImagetoBitmap(Friend friend) {
        Bitmap remote_picture = null;
        String pic_url = "http://www.maakki.cc/ashx/showImage.ashx?file_id=" + friend.getPicfilePath() + "&width=40&height=40&forcibly=Y&dimg=Y";
        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(pic_url).getContent());
        } catch (IOException e) {
            //
        }
        return remote_picture;
    }

    private void saveImage(Bitmap bitmap) {
        SaveImageTask saveImageTask = new SaveImageTask(context, filePath -> onSaveComplete(filePath));
        saveImageTask.execute(bitmap);
    }

    private void onSaveComplete(File filePath) {
        if (filePath == null) {
            //Log.w(TAG, "onSaveComplete: file dir error");
            return;
        } else {
            friend_need_to_insert.setPicfilePath(filePath.getPath());
            friendDAO.insert(friend_need_to_insert);
            //addNewFriend();
        }
    }

    public void friendAction(String actType, String friend_id) {
        //Create
        String METHOD_NAME = "friendAction";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = MemID + "Community-M@@kki.cc" + timeStamp + friend_id + actType;
        String identifyStr = ServiceUtil.getHashCode(encryptStr).toUpperCase();
        //Token=MemID + "Community-M@@kki.cc" + timestamp + friend_id + getType
        request.addProperty("member_id", MemID);
        request.addProperty("friend_id", friend_id);
        request.addProperty("actType", actType);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
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
        //return actType;
    }

    public class AsyncCallWS_friendAction extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String actType = params[0];
            String friend_id = params[1];
            friendAction(actType, friend_id);
            return actType;
        }

        //@Override
        protected void onPostExecute(String result) {
            //showAlertDialog("result:",result);
            if (!errMsg.equals("")) {
                ServiceUtil.showAlertDialog(context,"哎哟！咋了？", errMsg);
            }else if(getType2.equals("InviteFriend")){
                getType = "InviteFriend";
                renewList(context, getType);
            }/*else{

                if(result.equals("addFriend")){
                    //renewList(context, "InviteFriend");
                    getType2="after_addFriend";
                    AsyncCallWS_getFriend getFriendTask=new AsyncCallWS_getFriend();
                    getFriendTask.execute("All");
                }else if(result.equals("Block")){
                    //renewList(context, "InviteFriend");
                    getType2="after_Block";
                    AsyncCallWS_getFriend getFriendTask=new AsyncCallWS_getFriend();
                    getFriendTask.execute("All");
                };
            }*/
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void selectView(String getType) {
        no_friends.setVisibility(View.GONE);
        switch (getType) {
            case "All":
                myToolbar.setTitle("全部列表");
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                break;
            case "Friend":
                myToolbar.setTitle("好友列表 "+listFriend.size());
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                break;
            case "Search":
                myToolbar.setTitle("搜寻好友");
                rl_top.setVisibility(View.VISIBLE);
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                break;
            case "InviteFriend":
                myToolbar.setTitle("邀请中，等待对方同意");
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                fab.setVisibility(View.GONE);
                break;
            case "InviteMe":
                myToolbar.setTitle("加我好友，等我同意");
                rl_top.setVisibility(View.GONE);
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                fab.setVisibility(View.GONE);
                break;
            case "Block":
                myToolbar.setTitle("拒绝往来列表");
                tv_all.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_friend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_friend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_search.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_search.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_iinvite.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_iinvite.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_addme.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                tv_addme.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                //
                tv_block.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tv_block.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
                break;
            default:
        }
    }

    private String getCurrentTime(Long t) {
        Date d = new Date(t);
        SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd HH:mm");
        return dt1.format(d.getTime());
    }
    private class AsyncCallWS_searchMember extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            searchMember();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(errMsg.equals("")){
                no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_result));
                listFriend.clear();

                if (newlist.size() == 0) {
                    listview.setVisibility(View.GONE);
                    no_friends.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < newlist.size(); i++) {
                        listFriend.add(newlist.get(i));
                    }
                    no_friends.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }else{
                listview.setVisibility(View.GONE);
                no_friends.setVisibility(View.VISIBLE);
                //ServiceUtil.showAlertDialog(context,"错误讯息","搜寻工作无法完成："+errMsg);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
    private void searchMember() {
        errMsg="";
        newlist = new ArrayList<Friend>();
        String METHOD_NAME = "searchMember";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = MemID + "Community-M@@kki.cc" + timeStamp;
        //Token：member_id + "Community-M@@kki.cc" + timestamp
        String identifyStr = ServiceUtil.getHashCode(encryptStr).toUpperCase();
        request.addProperty("member_id", MemID);
        request.addProperty("keyword", et_keyword.getText().toString().trim());
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        /*errMsg="member_id:"+MemID+
                "\nkeyword:"+et_keyword.getText().toString().trim()+
                "\ntimeStamp:"+timeStamp+
                "\nidentifyStr"+identifyStr;*/
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
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);

        JSONObject json_read;
        try {
            //String tmp = soapPrimitive.toString();
            //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
            //tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("getList");
            String errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
                //將資料丟進JSONObject
                //接下來選擇型態使用get並填入key取值
                for (int i = 0; i < jsonArray.length(); i++) {
                    Friend f = new Friend();
                    JSONObject friendObj = jsonArray.getJSONObject(i);
                    f.setMaakkiid(friendObj.getInt("maakki_id"));
                    f.setMemid(friendObj.getInt("member_id"));
                    f.setNickName(friendObj.getString("nickname"));
                    f.setIsNotify(true);
                    f.setFriendType(friendObj.getString("friend_type"));
                    f.setPicfilePath(friendObj.getString("picFileID"));
                    newlist.add(f);
                }
            } else {
                if (errCode.equals("2")) {
                    errMsg = "认证失败";
                } else if (errCode.equals("3")) {
                    errMsg = "查无此会员";
                } else if (errCode.equals("4")) {
                    errMsg = "查无任何资料";
                }else if (errCode.equals("5")) {
                    errMsg = "搜寻的关键字不可以是空白";
                }
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }
    private void InviteSponsor(Boolean hasDonloaded){
        boolean ignore=true;
        count=friendDAO.getCount();
        if(count>0){
            if(friendDAO.getbyMemberID(Integer.parseInt(cMemID))==null){
                ignore=false;
                for(int i=0;i<ignoreList.size();i++){
                    if(cMemID.equals(ignoreList.get(i))){
                        ignore=true;
                    }
                }
            }
        }else{
            if(!hasDonloaded){
                AsyncCallWS_getFriend getFriendTask=new AsyncCallWS_getFriend();
                getFriendTask.execute("All");
            }
        }

        if(ignore){return;}
        String title,message;
        title="邀请 "+cName+" 为好友？";
        message="先列入邀请名单中，必须等到 "+cName+" 同意后，才会是你的好友！";
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //getType="Single";
                        //showAlertDialog("Parameters",getType+cMemID);
                        //AsyncCallWS_getFriend getFriendTask = new AsyncCallWS_getFriend();
                        //getFriendTask.execute(getType,cMemID);
                        //else if(getType.equals("Single")){
                            //showAlertDialog("Hi","got it!");
                        AsyncCallWS_getMemberProfile getMemberProfileTask=new AsyncCallWS_getMemberProfile();
                        getMemberProfileTask.execute();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(count==0){
                            getType="All";
                            renewList(context,getType);
                        }else{
                            getType = "Friend";
                            renewList(context, getType);
                        }
                        ignoreList.add(cMemID);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    private class AsyncCallWS_getFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String getType = params[0];
            String friend_id =  params[1];
            getFriend(getType,friend_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("Parameters:","getType:"+getType+"/getType2:"+getType2+newlist.size());
            if(getType.equals("All")){
                no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_friends));
                if(friendDAO.getCount() > 0){
                    friendDAO.deleteAll();
                    friendDAO=new FriendDAO(context);
                }
            }else{
                no_friends.setImageDrawable(getResources().getDrawable(R.drawable.no_result));
            }
            if(listFriend.size()>0){
                listFriend.clear();
            }
            for (int i = 0; i < newlist.size(); i++) {
                listFriend.add(newlist.get(i));
                if (getType.equals("All")) {
                    friendDAO.insert(newlist.get(i));
                }
            }
            if (newlist.size() == 0) {
                listview.setVisibility(View.GONE);
                no_friends.setVisibility(View.VISIBLE);
            } else {
                no_friends.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                if (newlist.size() > 10) {
                    fab.setVisibility(View.VISIBLE);
                }
                if(getType.equals("InviteMe")){
                    setOnClick();
                    adapter.notifyDataSetChanged();
                }else if(getType2.equals("after_addFriend")){
                    getType = "InviteFriend";
                    renewList(context, getType);
                }else if(getType2.equals("after_Block")) {
                    getType = "Block";
                    renewList(context, getType);
                }else if(getType2.equals("InviteFriend")) {
                    InviteSponsor(hasDonloaded);
                }else if(getType.equals("All")){
                    //if(count==0){
                        setOnClick();
                    //}
                    getType="Friend";
                    renewList(context,getType);
                }

            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
    private void getFriend(String getType,String friend_id) {
        //Create
        newlist = new ArrayList<Friend>();
        this.getType = getType;
        String METHOD_NAME = "getFriend";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        String timeStamp = String.valueOf(new Date().getTime());
        //String friend_id = "0";
        String encryptStr = MemID + "Community-M@@kki.cc" + timeStamp + friend_id + getType;
        String identifyStr = ServiceUtil.getHashCode(encryptStr).toUpperCase();
        //Token=MemID + "Community-M@@kki.cc" + timestamp + friend_id + getType
        request.addProperty("member_id", MemID);
        request.addProperty("friend_id", friend_id);
        request.addProperty("getType", getType);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
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
            JSONArray jsonArray = json_read.getJSONArray("friendList");
            String errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
                //將資料丟進JSONObject
                //接下來選擇型態使用get並填入key取值
                ChatDAO chatDAO=new ChatDAO(context);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Friend f = new Friend();
                    JSONObject friendObj = jsonArray.getJSONObject(i);
                    if(!friendObj.getString("friend_type").equals("X")){
                        f.setMaakkiid(friendObj.getInt("maakki_id"));
                        f.setMemid(friendObj.getInt("member_id"));
                        if(chatDAO.getLastChatbyContactID(friendObj.getInt("member_id"))!=null){
                            f.setLastMessageTime(chatDAO.getLastChatbyContactID(friendObj.getInt("member_id")).getCreateDate());
                        }
                        if(friendDAO.getbyMemberID(friendObj.getInt("member_id"))!=null){
                            if(friendDAO.getbyMemberID(friendObj.getInt("member_id")).getLastChatTime()!=null){
                                f.setLastChatTime(friendDAO.getbyMemberID(friendObj.getInt("member_id")).getLastChatTime());
                            }
                        }
                        f.setNickName(friendObj.getString("nickname"));
                        f.setIsNotify(true);
                        f.setFriendType(friendObj.getString("friend_type"));
                        f.setPicfilePath(friendObj.getString("picFileID"));
                        newlist.add(f);
                    }
                }
            } else {
                if (errCode.equals("2")) {
                    errMsg = "认证失败";
                } else if (errCode.equals("3")) {
                    errMsg = "查无此会员";
                } else if (errCode.equals("4")) {
                    errMsg = "查无任何资料";
                }
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }
    private class AsyncCallWS_getMemberProfile extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getMemberProfile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("errMsg",errMsg);
            Friend f=new Friend();
            f.setFriendType("I");
            f.setNickName(cName);
            f.setMemid(Integer.parseInt(cMemID));
            f.setMaakkiid(friend.getMaakkiid());
            f.setPicfilePath(friend.getPicfilePath());
            friendDAO.insert(f);
            AsyncCallWS_friendAction friendActionTask=new AsyncCallWS_friendAction();
            friendActionTask.execute("addFriend",cMemID);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getMemberProfile() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        friend=new Friend();
        String METHOD_NAME = "getMemberProfile";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String timeStamp = String.valueOf(new Date().getTime());
        //String target_maakki_id="0";
        String encryptStr = cMemID + "Community-M@@kki.cc" + timeStamp.trim();
        //target_maakki_id = et_target.getText().toString().trim();
        //String target_maakki_id=et_target.getText().toString().trim();
        String identifyStr = ServiceUtil.getHashCode(encryptStr).toUpperCase();
        request.addProperty("mid", cMemID);
        request.addProperty("isMemberID", true);
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
            Log.e("Error", e.getMessage());
        }

        String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        String errCode;
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "查无资料";
            }
            friend.setMemid(Integer.parseInt(json_read.getString("member_id")));
            friend.setMaakkiid(Integer.parseInt(json_read.getString("maakki_id")));
            friend.setNickName(json_read.getString("nickname"));
            friend.setPicfilePath(json_read.getString("picFileID"));
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }
}
