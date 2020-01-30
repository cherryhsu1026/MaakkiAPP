package com.maakki.maakkiapp;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by ryan on 2017/7/17.
 */

public class PreNotificationList extends AppCompatActivity {
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebService.asmx";
    //RelativeLayout newitem;
    ListView listview;
    Button btn, btn_online;
    TextView tv, title, tv_message, time;
    ImageView icon, no_notification;
    Integer count;
    FloatingActionButton fab;
    private Long notification_id = 0l, notification_millisecs = 0l;
    private boolean isRedenvelope = false, isAscending = false;
    private String sendernickname = "", message = "", contactnick = "", mMemID,mMaakkiID;
    private Boolean isActivate;
    private Context context;
    private List<PreNotification> listPreNotification, newlist;
    private PreNotificationDAO prevDAO;
    private PreNotification prev_notification;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private NotificationAdapter adapter;
    private BroadcastReceiver receiver;
    String cMemid = "";
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    int index_notification;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    AlertDialog alertDialog = new AlertDialog.Builder(PreNotificationList.this).create();
                    alertDialog.setTitle("将要删除通知记录");
                    alertDialog.setMessage("您确定要删除所有的通知记录吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    listview.setVisibility(View.INVISIBLE);
                                    fab.setVisibility(View.INVISIBLE);
                                    listPreNotification.clear();
                                    no_notification.setVisibility(View.VISIBLE);
                                    prevDAO.deleteAll();
                                    listPreNotification.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    break;
                case R.id.blockchain:
                    Intent i =new Intent(PreNotificationList.this,BlockChain_InquiryNotFinished.class);
                    startActivity(i);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prenotificationlist);
        context = this;
        mMaakkiID=SharedPreferencesHelper.getSharedPreferencesString(context,SharedPreferencesHelper.SharedPreferencesKeys.key0,"");
        ShortcutBadger.with(getApplicationContext()).remove();
        listPreNotification = new ArrayList<PreNotification>();
        prevDAO = new PreNotificationDAO(this);
        prev_notification = new PreNotification();
        if (getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            //String getType = bundle.getString("getType");
            isRedenvelope = bundle.getBoolean("isRedenvelope");
            sendernickname = bundle.getString("sendernickname");
            cMemid = bundle.getString("Contactid");
            notification_id = bundle.getLong("notification_id",0l);
            notification_millisecs = bundle.getLong("notification_millisecs",0l);
            message = bundle.getString("nMessage");
            //if(!getType.equals("getRedEnvelope_NotFinished")){
                if (isRedenvelope) {
                    if (notification_id>0){
                        if(prevDAO.get(notification_id) != null) {
                            if (prevDAO.get(notification_id).getLastModify() == notification_millisecs) {
                                isRedenvelope = true;
                            }else{
                                isRedenvelope=false;
                                //Toast.makeText(context, "这个红包领过了.." , Toast.LENGTH_LONG).show();
                                String msg="这个红包领过了..";
                                ServiceUtil.showToast(context,msg);
                            }
                        } else {
                            isRedenvelope=false;
                            //Toast.makeText(context, "这个红包领过了.." , Toast.LENGTH_LONG).show();
                            String msg="这个红包领过了..";
                            ServiceUtil.showToast(context,msg);
                        }
                    }else{isRedenvelope=true;}
                }
            //}
        }
        //Toast.makeText(context,"notification_id:"+notification_id,Toast.LENGTH_LONG).show();
        if (isRedenvelope) {
            showRedEnvelope("要领取这个赞助红包吗？", message);
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //Intent intent = new Intent(PreNotificationList.this, MainActivity.class);
                //startActivity(intent);
            }
        });
        //
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
        title = (TextView) findViewById(R.id.title);
        tv_message = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        icon = (ImageView) findViewById(R.id.icon);
        listview = (ListView) findViewById(R.id.listview);
        no_notification = (ImageView) findViewById(R.id.no_friend);
        tv = (TextView) findViewById(R.id.tv);
        count = prevDAO.getCount();
        listPreNotification = prevDAO.getAll();
        adapter = new NotificationAdapter(this, R.layout.list_item, listPreNotification);
        btn_online = (Button) findViewById(R.id.isOnline);
        btn = (Button) findViewById(R.id.fire);
        listview.setAdapter(adapter);
        if (count > 0) {
            Collections.reverse(listPreNotification);
            //listtop=new ArrayList<PrevNotification>();
            tv.setText("(" + count.toString() + ")");
            btn.setVisibility(View.VISIBLE);
            if (count > 1) {
                fab.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < listPreNotification.size(); i++) {
                PreNotification p = listPreNotification.get(i);
                if (p.getid() == notification_id) {
                    index_notification = i;
                }
            }
            setOnClick();
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

                    Collections.reverse(listPreNotification);
                    adapter.notifyDataSetChanged();
                }
            });
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listview.setVisibility(View.INVISIBLE);
                    Button btn = (Button) findViewById(R.id.fire);
                    //btn.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    count = 0;
                    tv.setText("");
                    no_notification.setVisibility(View.VISIBLE);
                    prevDAO.deleteAll();
                    listPreNotification.clear();
                }
            });
        } else {
            tv.setText("");
            no_notification.setVisibility(View.VISIBLE);
        }

        //check if isOnlineOnSignalR
        //AsyncCallWS_isOnlineOnSignalR isOnlineOnSignalRTask = new AsyncCallWS_isOnlineOnSignalR();
        //isOnlineOnSignalRTask.execute();
        // your oncreate code should be
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_FROM_DEFAULTSERVICE");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                prevDAO = new PreNotificationDAO(context);
                newlist = new ArrayList<PreNotification>();
                newlist = prevDAO.getAll();
                PreNotification p = newlist.get(newlist.size() - 1);
                if (isAscending) {
                    listPreNotification.add(p);
                } else {
                    listPreNotification.add(0, p);
                }
                count=prevDAO.getCount();
                //tv.setText("(" + count + ")");
                adapter.notifyDataSetChanged();
                setOnClick();
                //count++;
                if (count > 0) {
                    if (count == 1) {
                        listview.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (count == 2) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    tv.setText("(" + count + ")");
                    no_notification.setVisibility(View.INVISIBLE);
                }
            }
        };
        registerReceiver(receiver, filter);

        //Message for 22545 about who's Inquiring for redEnvelope
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("INVOKE_InquiryNotFinishedRE_CORESERVICE");
        BroadcastReceiver receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess =intent.getStringExtra("What");
                //Toast.makeText(context,mess,Toast.LENGTH_LONG).show();
                //showAlertDialog("Blockchain",mess);
            }
        };
        registerReceiver(receiver2, filter2);
        //
        no_notification.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void setOnClick() {
        listview.setOnTouchListener(swipeDetector);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                PreNotification p = listPreNotification.get(position);
                adapter.notifyDataSetChanged();
                if (!swipeDetector.swipeDetected()) {
                    //Toast.makeText(getApplicationContext(), "position:"+position+"/id:"+id, Toast.LENGTH_SHORT).show();
                    //if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                    Integer mt = p.getmessagetype();
                    String redUrl = StaticVar.webURL+"community/ecard.aspx";
                    Intent intent = new Intent(PreNotificationList.this, WebMain2.class);
                    Bundle bundle = new Bundle();
                    mMemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
                    bundle.putString("mMemID", mMemID);
                    switch (mt) {
                        case 1:
                            cMemid = p.getmessage().split("/")[0];
                            message = p.getmessage().split("/")[1];
                            v.setVisibility(View.GONE);
                            deleteCell(v, position);
                            prevDAO.delete(p.getid());
                            sendernickname=p.gettitle();
                            showRedEnvelope("要领取这个赞助红包吗？", message);
                            break;
                        case 2:
                            if (p.getmessage().contains("领取了你的赞助红包") ||
                                    p.getmessage().contains("发的赞助红包") ||
                                    p.getmessage().contains("你和这个红包，距离只有不到一毫米..") ||
                                    p.getmessage().contains(",这个红包领不了。") ||
                                    p.getmessage().contains(" 指定赞助了你 ")) {

                                //Toast.makeText(getApplicationContext(), "p.getmessage():"+p.getmessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                intent = new Intent(PreNotificationList.this, Chat_red.class);
                                bundle.putString("isPrivate", "true");
                                String[] strArray = p.gettitle().split("/");
                                bundle.putString("nMemID", strArray[0]);
                                bundle.putString("Contactid", strArray[0]);
                                bundle.putString("nName", strArray[1]);
                                bundle.putString("nMessage", p.getmessage());
                                bundle.putString("isCustomer", "false");
                                bundle.putString("redirUrl", redUrl);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            break;
                        case 4:
                            redUrl = StaticVar.webURL+"community/NotifyMain.aspx";
                            break;
                        case 5:
                            redUrl = p.gettitle();
                            //Toast.makeText(getApplicationContext(), "redUrl:"+redUrl, Toast.LENGTH_SHORT).show();
                            break;
                        case 6:
                            redUrl = StaticVar.webURL+"MGS/MGSMainpage.aspx";
                        case 7:
                            //点击来自使用者的客服要求通知
                            intent = new Intent(PreNotificationList.this, Chat_CustomerService.class);
                            bundle.putString("isPrivate", "true");
                            String[] stArray = p.gettitle().split("/");
                            bundle.putString("nMemID", stArray[0]);
                            bundle.putString("Contactid", stArray[0]);
                            bundle.putString("nName", stArray[1]);
                            bundle.putString("nMessage", p.getmessage());
                            bundle.putString("isCustomer", "true");
                            break;
                        case 8:
                            //点击来自玛吉客服人员的通知
                            intent = new Intent(PreNotificationList.this, Chat_red.class);
                            bundle.putString("isPrivate", "true");
                            String[] stA = p.gettitle().split("/");
                            bundle.putString("nMemID", stA[0]);
                            bundle.putString("Contactid", stA[0]);
                            bundle.putString("nName", stA[1]);
                            bundle.putString("nMessage", p.getmessage());
                            bundle.putString("isCustomer", "true");
                            break;
                        case 10:
                            //点击看来自920的通知
                            intent = new Intent(PreNotificationList.this, imaimai_order.class);
                            break;
                        case 11:
                            //点击看来自IA申请的通知
                            intent = new Intent(PreNotificationList.this, Open_IA.class);
                            break;
                        case 13:
                            bundle.putString("getType", "InviteMe");
                            intent = new Intent(PreNotificationList.this, FriendList.class);
                            break;
                        case 14:
                            intent = new Intent(PreNotificationList.this, AllyActivity.class);
                            break;
                        default:
                    }
                    if (mt > 2 & mt!=12) {
                        bundle.putString("redirUrl", redUrl);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "p.getid():"+p.getid() , Toast.LENGTH_LONG).show();
                    v.setVisibility(View.GONE);
                    deleteCell(v, position);
                    prevDAO.delete(p.getid());
                }
            }
        });
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            //@Override
            public void onAnimationEnd(Animation arg0) {
                listPreNotification.remove(index);
                count--;
                if (count < 2) {
                    fab.setVisibility(View.INVISIBLE);
                    if (count < 1) {
                        btn.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                    }
                }
                if (count == 0) {
                    no_notification.setVisibility(View.VISIBLE);
                }
                tv.setText("(" + count + ")");
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
        getMenuInflater().inflate(R.menu.menu_prenotifictionlist, menu);
        if(mMaakkiID.equals("22545")||mMaakkiID.equals("17141")||mMaakkiID.equals("1")){
            menu.getItem(0).setVisible(true);
        }
        return true;
    }

    public Boolean checkNotificationRunning() {
        //CoreAlarm corealarm=new CoreAlarm();
        //isActivate=corealarm.isMyServiceRunning(getApplicationContext(), CoreService.class);
        Long currenttime = (new Date()).getTime();
        Long LastMessageTime = SharedPreferencesHelper.getSharedPreferencesLong(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key11, 0);
        //Long LastMessageTime = new CoreService().getLastMessageTime();
        Long intervalTime = currenttime - LastMessageTime;
        isActivate = false;
        if (intervalTime < 1000 * 20) {
            isActivate = true;
        }
        //Toast.makeText(getApplicationContext(), "intervalTime:"+intervalTime+"/"+LastMessageTime, Toast.LENGTH_LONG).show();
        return isActivate;
    }

    public class ViewHolder {
        public TextView text_title;
        public TextView text_message;
        public TextView text_time;
        public ImageView image_icon;
        public int messagetype;
        public boolean needInflate;
        public boolean isVisible;
    }

    public class NotificationAdapter extends ArrayAdapter<PreNotification> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public NotificationAdapter(Context context, int textViewResourceId, List<PreNotification> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            PreNotification p = (PreNotification) getItem(position);

            if (convertView == null) {
                //Toast.makeText(PreNotificationList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(PreNotificationList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(PreNotificationList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();

            int mt = p.getmessagetype();
            holder.messagetype = mt;
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            String title = "";
            switch (mt) {
                case 1:
                    //String[] strArray0=p.gettitle().split("/");
                    contactnick = p.gettitle();
                    title = contactnick + " 发了一个赞助红包：";
                    icon = R.drawable.redenvelope;
                    break;
                case 2:
                    String[] strArray = p.gettitle().split("/");
                    contactnick = strArray[1];
                    title = contactnick + "私訊給你：";
                    if (contactnick.equals("玛吉梦想众筹")) {
                        title = contactnick + "通知你：";
                        icon = R.drawable.maakki_mdf;
                    } else {
                        icon = R.drawable.privatechat;
                    }
                    break;
                case 3:
                    icon = R.drawable.systemnotification;
                    if (p.getmessage().contains("成为BO")) {
                        icon = R.drawable.bo;
                    } else if (p.getmessage().contains("成为CS")) {
                        icon = R.drawable.cs;
                    }
                    //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.warm_gray));
                    break;
                case 5:
                    title = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "") + "，Maakki通知你：";
                    if (p.getmessage().contains("赞助")) {
                        icon = R.drawable.favorite;
                    }
                    break;
                case 6:
                    if (p.getmessage().contains("挂买")) {
                        icon = R.drawable.mgs_upward;
                    } else {
                        icon = R.drawable.mgs_downward;
                    }
                    //icon=R.drawable.mgs_icon;
                    //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.warm_blue));
                    break;
                case 7:
                    String[] str1 = p.gettitle().split("/");
                    contactnick = str1[1];
                    title = contactnick + "发客服信息：";
                    icon = R.drawable.customerservice_inq;
                    //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.warm_red));
                    break;
                case 8:
                    String[] str2 = p.gettitle().split("/");
                    contactnick = str2[1];
                    title = contactnick + "回复信息：";
                    icon = R.drawable.icon_customerservice;
                    //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.warm_red));
                    break;
                case 10:
                    title = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "") + "，imaimai通知你：";
                    icon = R.drawable._imaimai;
                    break;
                case 11:
                    title = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "") + "，imaimai通知你：";
                    icon = R.drawable.ia;
                    break;
                case 12:
                    String[] strArray1 = p.gettitle().split("/");
                    contactnick = strArray1[1];
                    title = contactnick + "私訊給你：";
                    if (contactnick.equals("玛吉梦想众筹")) {
                        title = contactnick + "通知你：";
                        icon = R.drawable.maakki_mdf;
                    } else {
                        icon = R.drawable.privatechat;
                    }
                    break;
                case 13:
                    title = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key2, "") + "，Maakki通知你：";
                    break;
                default:
            }
            if (mt == 2|| mt==12) {
                String mpicfile = p.getPicfile();
                String pic_url =StaticVar.webURL+"ashx/showImage.ashx?file_id=" + mpicfile + "&width=45&height=45&forcibly=Y&dimg=Y";
                imageLoader.displayImage(pic_url, holder.image_icon);
            } else if(mt==14){
                Bitmap bitmap = BitmapFactory.decodeFile(p.getPicfile());
                holder.image_icon.setImageBitmap(bitmap);
            }else {
                holder.image_icon.setImageResource(icon);
            }
            if (!title.equals("")) {
                holder.text_title.setText(title);
            } else {
                holder.text_title.setText(p.gettitle());
            }
            if (mt > 1) {
                holder.text_message.setText(p.getmessage());
            } else {
                holder.text_message.setText(p.getmessage().split(":")[6]);
            }
            Long time = p.getLastModify();
            String fm = "";
            Date date = new Date(time);
            String dateFormat = "MM-dd\nHH:mm";
            DateFormat formatter = new SimpleDateFormat(dateFormat);
            fm = formatter.format(date);
            //new SimpleDateFormat(dateFormat).format(new Date(p.getLastModify()));
            holder.text_time.setText(fm);
            //}
            return view;
            //return convertView;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_title = (TextView) view.findViewById(R.id.title);
            vh.text_message = (TextView) view.findViewById(R.id.message);
            vh.text_time = (TextView) view.findViewById(R.id.time);
            vh.needInflate = false;
            view.setTag(vh);
        }


    }

    private void showRedEnvelope(String title, String message) {
        String mess[] = message.split(":");
        final String maakkiid = mess[1];
        final String envelope_id = mess[2];
        final String sponsorAmt = mess[3];
        final String currency = mess[4];
        //showAlertDialog1("message",message);
        message = mess[6];
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = PreNotificationList.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.redenvelope_form, null);
        alertDialog.setView(view);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
        TextView tv_sponsorAmt = (TextView) view.findViewById(R.id.tv_sponsorAmt);
        tv_sponsorAmt.setText(sponsorAmt + " " + currency + "(i)");
        TextView tv_slogan = (TextView) view.findViewById(R.id.tv_greeting);
        tv_slogan.setText(message);
        TextView tv_sendernickname = (TextView) view.findViewById(R.id.tv_sendernickname);
        tv_sendernickname.setText(sendernickname);
        TextView tv_senderdate = (TextView) view.findViewById(R.id.tv_senderdate);
        tv_senderdate.setText(getCurrentDate());
        TextView tv_senderlocation = (TextView) view.findViewById(R.id.tv_senderlocation);
        if(!mess[5].trim().isEmpty()){
            tv_senderlocation.setText("于 " + mess[5]);
        }else{tv_senderlocation.setText("");}
        ImageView iv_envelope = (ImageView) view.findViewById(R.id.iv_envelope);
        iv_envelope.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification_id > 0) {
                    //listPreNotification.remove(prevDAO.getbyNotificationiID(notification_id));
                    prevDAO.delete(notification_id);
                    listPreNotification.remove(index_notification);
                    count=prevDAO.getCount();
                    tv.setText("(" + count.toString() + ")");
                    //listPreNotification=prevDAO.getAll();
                    adapter.notifyDataSetChanged();
                }
                SignalRUtil.setHubConnect(context);
                SignalRUtil.agreeToTakeSponsor(cMemid, maakkiid, envelope_id);
                //SharedPreferencesHelper.putSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key6,new Date().getTime());
                alertDialog.dismiss();
                // Create a child thread.
                /*Thread childThread = new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1500);
                        }catch (InterruptedException e){
                            //ignore
                        }
                        Long timemillisecs=new Date().getTime();
                        Long received_time=SharedPreferencesHelper.getSharedPreferencesLong(context,SharedPreferencesHelper.SharedPreferencesKeys.key6,0l);
                        Long IntervalTime=timemillisecs-received_time;
                        if(IntervalTime>3500){
                            final String msg=sendernickname+" 没回应，这个红包没法领..";
                            //Toast.makeText(context,sendernickname+" 没回应，这个红包没法领..",Toast.LENGTH_LONG).show();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ServiceUtil.showToast(context,msg);
                                }
                            });
                        }
                    }
                };
                // Start the thread.
                childThread.start();*/
            }
        });
    }


    private String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dt1.format(d.getTime());
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(PreNotificationList.this).create();
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

}
