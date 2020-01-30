package com.maakki.maakkiapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ryan on 2017/7/17.
 */

public class StoreList extends AppCompatActivity {
    private Context context;
    double latitude = 0;
    double longitude = 0;
    ListView listview;
    Button btn;
    TextView tv, title, message, time;
    ImageView icon, no_store;
    Integer count;
    FloatingActionButton fab;
    boolean isAscending = false;
    GPSTracker gps;
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private List<Store> listStore, newlist;
    private StoreDAO storeDAO;
    private SwipeDetector swipeDetector = new SwipeDetector();
    private StoreAdapter adapter;
    private BroadcastReceiver receiver, locationReceiver;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.room:
                    Intent intent = new Intent(StoreList.this, MapsActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prenotificationlist);
        context=this;
        no_store = (ImageView) findViewById(R.id.no_friend);
        no_store.setImageDrawable(getResources().getDrawable(R.drawable.no_store));
        gps = new GPSTracker(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        myToolbar.setTitle("玛吉商店列表");
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
        //get latest latitude & Longitude;
        //location=
        //location=getLocation();
        //Toast.makeText(getApplicationContext(), "3."+latitude+":"+longitude,Toast.LENGTH_LONG).show();
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            //gps.showSettingsAlert();
        }
        fab = (FloatingActionButton) findViewById(R.id.setting_button);
        fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
        //fab.bringToFront();
        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        icon = (ImageView) findViewById(R.id.icon);
        listview = (ListView) findViewById(R.id.listview);

        listStore = new ArrayList<Store>();
        storeDAO = new StoreDAO(this);
        tv = (TextView) findViewById(R.id.tv);

        listStore = storeDAO.getAll();
        for (int i = 0; i < listStore.size(); i++) {
            Store s = listStore.get(i);
            Double D = distance(latitude, longitude, s.getLatitude(), s.getLongitude());
            s.setDistance(D);
        }
        //sort ArrayList by distance Ascending
        Collections.sort(listStore, new DistanceDescendingComparator());
        //Collections.reverse(listStore);
        //listtop=new ArrayList<PrevNotification>();
        adapter = new StoreAdapter(this, R.layout.list_item, listStore);
        count = storeDAO.getCount();
        btn = (Button) findViewById(R.id.fire);
        if (count > 0) {
            tv.setText("发现" + count.toString() + "家玛吉商店");
            btn.setVisibility(View.VISIBLE);
            if (count > 1) {
                fab.setVisibility(View.VISIBLE);
            }
        } else {
            tv.setText("");
            no_store.setVisibility(View.VISIBLE);
        }
        listview.setAdapter(adapter);
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

                Collections.reverse(listStore);
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
                storeDAO.deleteAll();
                listStore.clear();
                no_store.setVisibility(View.VISIBLE);

            }
        });
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("INVOKE_ONLOCATIONCHANGED");
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getApplicationContext(),"Hi", Toast.LENGTH_SHORT).show();
                Location l = gps.getLocation();
                //Re-Calculate distance
                for (int i = 0; i < listStore.size(); i++) {
                    Store s = listStore.get(i);
                    Double D = distance(gps.getLatitude(), gps.getLongitude(), s.getLatitude(), s.getLongitude());
                    s.setDistance(D);
                }
                //sort ArrayList by distance Ascending
                if (isAscending) {
                    Collections.sort(listStore, new DistanceAscendingComparator());
                } else {
                    Collections.sort(listStore, new DistanceDescendingComparator());
                }
                adapter.notifyDataSetChanged();
            }
        };
        registerReceiver(locationReceiver, filter2);

        // your oncreate code should be
        IntentFilter filter = new IntentFilter();
        filter.addAction("INVOKE_STORELIST");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getApplicationContext(),"Hi", Toast.LENGTH_SHORT).show();
                storeDAO = new StoreDAO(getApplicationContext());
                newlist = new ArrayList<Store>();
                newlist = storeDAO.getAll();
                Store s = newlist.get(newlist.size() - 1);
                s.setDistance(distance(latitude, longitude, s.getLatitude(), s.getLongitude()));
                listStore.add(s);
                if (isAscending) {
                    Collections.sort(listStore, new DistanceAscendingComparator());
                } else {
                    Collections.sort(listStore, new DistanceDescendingComparator());
                }
                adapter.notifyDataSetChanged();
                //setOnClick();
                count++;
                //Toast.makeText(getApplicationContext(), "count:"+count , Toast.LENGTH_SHORT).show();
                //count=1，表示原先历史通知数目为0，listview是View.Gone
                if (count > 0) {
                    if (count == 1) {
                        listview.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);
                    }
                    if (count == 2) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    tv.setText("发现" + count + "家玛吉商店");
                    no_store.setVisibility(View.INVISIBLE);
                }
                //addCell();
            }
        };
        registerReceiver(receiver, filter);
        no_store.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setOnClick() {
        listview.setOnTouchListener(swipeDetector);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Store s = listStore.get(position);
                if (!swipeDetector.swipeDetected()) {
                    //Toast.makeText(getApplicationContext(), "position:"+position+"/id:"+id, Toast.LENGTH_SHORT).show();
                    //if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                    //String sn = s.getStoreName();
                    String mid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "22545");
                    //String redUrl = StaticVar.webURL+"/community/ecard.aspx";
                    Integer sid = s.getStoreID();
                    String redUrl = StaticVar.webURL+"BOStoreData.aspx?entrepot_id=" + sid.toString() + "&mid=" + mid;
                    //Toast.makeText(getApplicationContext(), "redUri="+redUrl, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StoreList.this, WebMain2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("redirUrl", redUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    //Toast.makeText(getApplicationContext(), "p.getid():"+p.getid() , Toast.LENGTH_LONG).show();
                    v.setVisibility(View.GONE);
                    deleteCell(v, position);
                    storeDAO.delete(s.getId());
                    //
                    //}
                }
            }
        });
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            //@Override
            public void onAnimationEnd(Animation arg0) {
                listStore.remove(index);
                count--;
                if (count < 2) {
                    fab.setVisibility(View.INVISIBLE);
                    if (count < 1) {
                        btn.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                    }
                }
                tv.setText("共有" + count + "家玛吉商店");
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
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    private double distance(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;

        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r)
                * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d;
    }

    private String FormatString(Double dist) {
        String sd = "";
        Double D;
        if (dist < 1) {
            int i = (int) Math.floor(dist * 1000);
            sd = i + " M";
        } else {
            if (dist < 10) {
                sd = String.format("%.2f", dist);
            } else {
                if (dist < 100) {
                    sd = String.valueOf(Math.floor(dist * 10) / 10);
                } else {
                    sd = String.valueOf(Math.round(dist));
                }
            }
            sd += " KM";
        }
        return sd;
    }

    public class ViewHolder {
        public TextView text_storename;
        public TextView text_address;
        public TextView text_time;
        public ImageView image_icon;
        public boolean needInflate;
    }

    public class StoreAdapter extends ArrayAdapter<Store> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public StoreAdapter(Context context, int textViewResourceId, List<Store> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //RecyclerView.ViewHolder holder;
            //LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            final View view;
            ViewHolder holder;
            Store s = (Store) getItem(position);

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
            String mpicfile = s.getPicfile();
            String pic_url = StaticVar.webURL+"function/getImage.aspx?file_id=" + mpicfile + "&width=45&height=45&forcibly=Y&dimg=Y";
            imageLoader.displayImage(pic_url, holder.image_icon);
            holder.text_storename.setText(s.getStoreName());
            holder.text_address.setText(s.getAddress());
            holder.text_time.setText(FormatString(s.getDistance()));
            return view;
        }
        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_storename = (TextView) view.findViewById(R.id.title);
            vh.text_address = (TextView) view.findViewById(R.id.message);
            vh.text_time = (TextView) view.findViewById(R.id.time);
            vh.needInflate = false;
            view.setTag(vh);
        }
    }
    public class DistanceDescendingComparator implements Comparator<Store> {
        public int compare(Store left, Store right) {
            return (left.distance < right.distance) ? -1 : (left.distance > right.distance) ? 1 : 0;
        }
    }

    public class DistanceAscendingComparator implements Comparator<Store> {
        public int compare(Store left, Store right) {
            return (left.distance > right.distance) ? -1 : (left.distance < right.distance) ? 1 : 0;
        }
    }
    public void showSettingsAlert() {
        final int errortype=gps.getErrorType();
        String app_name="Maakki.10";
        String title = "请授权" + app_name + "定位功能";
        String message = "进入【应用管理】，授权" + app_name + "获得定位权限\n\n1.在列表中找到" + app_name + "\n2.点选【应用程式权限】\n3.【读取位置权限】→允许";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        // Setting Dialog Message
        if (errortype > 1) {
            title = "请开启定位功能";
            message = "手机定位功能尚未开启，请现在开启！";
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                if (errortype > 1) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        alertDialog.show();
    }

}
