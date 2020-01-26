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

/**
 * Created by ryan on 2017/7/17.
 */

public class AllyList extends AppCompatActivity {
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = StaticVar.webURL+"/WebService.asmx";
    //RelativeLayout newitem;
    ListView listview;
    Button btn, btn_online;
    TextView tv, title, tv_message, time;
    ImageView icon, no_Ally;
    Integer count;
    FloatingActionButton fab;
    private Long notification_id = 0l, notification_millisecs = 0l;
    private boolean isRedenvelope = false, isAscending = false;
    private String sendernickname = "", message = "", contactnick = "", mMaakkiID;
    private Boolean isActivate;
    private Context context;
    private List<Ally> listAlly, newlist;
    private AllyDAO allyDAO, newDAO;
    private Ally ally;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private AllyAdapter adapter;
    private BroadcastReceiver receiver,receiver1;
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
                case R.id.build:
                    Intent intent = new Intent(AllyList.this, Ally_buildup.class);
                    startActivity(intent);
                    //Toast.makeText(context, "Check friends!",Toast.LENGTH_LONG).show();
                    //Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allylist);
        context = this;
        listAlly = new ArrayList<Ally>();
        allyDAO = new AllyDAO(this);
        ally = new Ally();
        mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setTitle("联盟列表");
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
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
        no_Ally = (ImageView) findViewById(R.id.no_friend);
        no_Ally.setImageDrawable(getResources().getDrawable(R.drawable.no_ally));
        tv = (TextView) findViewById(R.id.tv);
        count = allyDAO.getCount();
        listAlly = allyDAO.getAll();
        adapter = new AllyAdapter(this, R.layout.list_item, listAlly);
        btn_online = (Button) findViewById(R.id.isOnline);
        btn = (Button) findViewById(R.id.fire);
        listview.setAdapter(adapter);
        if (count > 0) {
            Collections.reverse(listAlly);
            //listtop=new ArrayList<PrevNotification>();
            tv.setText("(" + count.toString() + ")");
            btn.setVisibility(View.VISIBLE);
            if (count > 10) {
                fab.setVisibility(View.VISIBLE);
            }
            /*for (int i = 0; i < listAlly.size(); i++) {
                Ally p = listAlly.get(i);
                if (p.getId() == notification_id) {
                    index_notification = i;
                }
            }*/
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
                    Collections.reverse(listAlly);
                    adapter.notifyDataSetChanged();
                }
            });
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listview.setVisibility(View.INVISIBLE);
                    Button btn = (Button) findViewById(R.id.fire);
                    btn.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    count = 0;
                    tv.setText("");
                    no_Ally.setVisibility(View.VISIBLE);
                    allyDAO.deleteAll();
                    listAlly.clear();
                }
            });
        } else {
            tv.setText("");
            no_Ally.setVisibility(View.VISIBLE);
        }

        //check if isOnlineOnSignalR
        //AsyncCallWS_isOnlineOnSignalR isOnlineOnSignalRTask = new AsyncCallWS_isOnlineOnSignalR();
        //isOnlineOnSignalRTask.execute();
        // your oncreate code should be
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_AllyList_to22545");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                allyDAO = new AllyDAO(context);
                newlist = new ArrayList<Ally>();
                newlist = allyDAO.getAll();
                Ally a = newlist.get(newlist.size() - 1);
                if (isAscending) {
                    listAlly.add(a);
                } else {
                    listAlly.add(0, a);
                }
                count=allyDAO.getCount();
                //tv.setText("(" + count + ")");
                adapter.notifyDataSetChanged();
                setOnClick();
                //count++;
                if (count > 0) {
                    if (count == 1) {
                        listview.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (count > 10) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    tv.setText("(" + count + ")");
                    no_Ally.setVisibility(View.INVISIBLE);
                }
            }
        };
        registerReceiver(receiver, filter);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("INVOKE_AllyList_from22545");
        receiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //showAlertDialog1("hi","something changed");
                //listAlly.clear();
                //long id=intent.getLongExtra("ally_id");
                long id=Long.parseLong(intent.getStringExtra("ally_id"));
                String Unique_id=intent.getStringExtra("Unique_id");
                for(int i=0;i<listAlly.size();i++){
                    if(listAlly.get(i).getId()==id){
                        listAlly.get(i).setAlly_name(listAlly.get(i).getAlly_name()+"("+Unique_id+")");
                    }
                }
                allyDAO = new AllyDAO(context);
                listAlly = allyDAO.getAll();
                adapter.notifyDataSetChanged();
                setOnClick();
            }
        };
        registerReceiver(receiver1, filter1);

        no_Ally.setOnClickListener(new Button.OnClickListener() {
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
                Ally ally = listAlly.get(position);
                adapter.notifyDataSetChanged();
                if (!swipeDetector.swipeDetected()) {
                    Intent i=new Intent(AllyList.this,AllyActivity.class);
                    i.putExtra("ally_id",ally.getId());
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(), "allyId:"+ally.getId(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "p.getId():"+p.getId() , Toast.LENGTH_LONG).show();
                    v.setVisibility(View.GONE);
                    deleteCell(v, position);
                    allyDAO.delete(ally.getId());
                }
            }
        });
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            //@Override
            public void onAnimationEnd(Animation arg0) {
                listAlly.remove(index);
                count--;
                tv.setText("(" + count + ")");
                if (count == 0) {
                    no_Ally.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.GONE);
                    btn.setVisibility(View.GONE);
                    listview.setVisibility(View.GONE);
                }else if (count < 10) {
                    fab.setVisibility(View.GONE);
                }
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
        }else if(receiver1!=null){
            unregisterReceiver(receiver1);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_allylist, menu);
        return true;
    }

    public class ViewHolder {
        public TextView text_title;
        public TextView text_message;
        public TextView text_time;
        public ImageView image_icon;
        public boolean needInflate;
    }

    public class AllyAdapter extends ArrayAdapter<Ally> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public AllyAdapter(Context context, int textViewResourceId, List<Ally> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            Ally a = (Ally) getItem(position);

            if (convertView == null) {
                //Toast.makeText(AllyList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(AllyList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.list_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(AllyList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();

            //int mt = p.getmessagetype();
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            String title=a.getAlly_name();
            //if(mMaakkiID.equals("22545")){
            //    title+="("+a.getId()+")";
            //}else
            if(a.getUniqueAllyId()!=null){
                title+="("+a.getUniqueAllyId()+")";
            }
            holder.text_title.setText(title);
            //Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/"+a.getIconpath());
            Bitmap bitmap = BitmapFactory.decodeFile(a.getIconpath());
            holder.image_icon.setImageBitmap(ServiceUtil.getRoundedCornerBitmap(bitmap));
            String message=a.getKickStartTime()+"/"+a.getDonationAmt()+"USD(i)";
            if(a.getAllyStatus()==1){
                message+="/接受申请";
            }else if(a.getAllyStatus()==2){
                message+="/仅由盟主邀请";
            };
            holder.text_message.setText(message);
            Long time = a.getLastModifyTime();
            String fm = "";
            Date date = new Date(time);
            String dateFormat = "HH:mm";
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

    private void showAlertDialog1(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(AllyList.this).create();
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
