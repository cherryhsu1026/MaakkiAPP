package com.maakki.maakkiapp;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by ryan on 2017/7/17.
 */

public class GetRemitList extends AppCompatActivity {
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    Toolbar myToolbar;
    RelativeLayout RL,RL_condition_day;
    ListView listview;
    Button btn, btn_online;
    TextView tv_backward,tv_forward,tv_bonus, tv_remittance, tv_beforeyd, tv_yesterday, tv_today, title, message, time;
    ImageView iv_left,iv_right,icon, no_Remit;
    Integer count,interval=0;
    FloatingActionButton fab;
    boolean isforward,isbackward,istoday, isyesterday, isbeforeyd, isAscending = false;
    String query_type, identifyStr, receiver_id, encryptStr, timeStamp, contactnick = "", mMemID, role, errCode, bonus_date, maakki_id;
    Boolean isBonus,isRanking;
    ViewHolder holder_onClick;
    private List<_RemitData> listRemit;
    private List<CBonusData> listCBonus;
    private List<rankingList> listLocationRanking;
    private SwipeDetector swipeDetector;
    //private RemitListAdapter adapter;
    private BroadcastReceiver receiver;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.room:
                    if(isRanking){
                        menuItem.setIcon(getResources().getDrawable(R.drawable.ic_room_white_18dp));
                        isRanking=false;
                        executeLocation();
                    }else{
                        menuItem.setIcon(getResources().getDrawable(R.drawable.baseline_format_list_bulleted_white_18dp));
                        isRanking=true;
                        setTvColorChange(true,tv_bonus);
                        setTvColorChange(true,tv_remittance);
                        listLocationRanking=new ArrayList<>();
                        AsyncCallWS_getLocationRanking getLocationRankingTask = new AsyncCallWS_getLocationRanking();
                        getLocationRankingTask.execute();
                        break;
                    }

            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        setContentView(R.layout.getremitlist);
        if (!this.getIntent().getExtras().equals(null)) {
            Bundle bundle = this.getIntent().getExtras();
            role = bundle.getString("role");
            bonus_date = bundle.getString("bonus_date");
            query_type = bundle.getString("query_type");
            if (query_type.equals("bonus")) {
                isBonus = true;
            } else {
                isBonus = false;
            }
        } else {
            role = "";
            isBonus = false;
            bonus_date = "";
        }
        isRanking=false;
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(myToolbar);   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar();
        myToolbar.setOnMenuItemClickListener(onMenuItemClick);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        //fab = (FloatingActionButton) findViewById(R.id.setting_button);
        //fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
        //fab.bringToFront();
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_right.setVisibility(View.INVISIBLE);
                if(interval<0){
                    interval++;
                    executeLocation();
                }
            }

        });
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_left.setVisibility(View.INVISIBLE);
                //setTvColorChange(false,tv_forward);
                //setTvColorChange(true,tv_backward);
                interval--;
                executeLocation();
            }

        });
        tv_today = (TextView) findViewById(R.id.tv_today);
        tv_yesterday = (TextView) findViewById(R.id.tv_yesterday);
        tv_beforeyd = (TextView) findViewById(R.id.tv_beforeyd);
        tv_bonus = (TextView) findViewById(R.id.tv_bonus);
        tv_remittance = (TextView) findViewById(R.id.tv_remittance);
        setSelectCondition();
        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        time = (TextView) findViewById(R.id.time);
        icon = (ImageView) findViewById(R.id.icon);
        listview = (ListView) findViewById(R.id.listview);
        no_Remit = (ImageView) findViewById(R.id.no_Remit);
        no_Remit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RL_condition_day = (RelativeLayout) findViewById(R.id.RL_condition_day);
        //showAlertDialog("");
        if (!isBonus) {
                listRemit = new ArrayList<>();
                AsyncCallWS_getRemitList getRemitListTask = new AsyncCallWS_getRemitList();
                getRemitListTask.execute();
        } else {
            listCBonus = new ArrayList<>();
            AsyncCallWS_getqueryCBonus getqueryCBonusTask = new AsyncCallWS_getqueryCBonus();
            getqueryCBonusTask.execute();
        }/**/

    }

    private void setSelectCondition() {
        if (role.equals("mine")) {
            tv_remittance.setVisibility(View.GONE);
            tv_bonus.setVisibility(View.GONE);
        } else {
            if (isBonus) {
                setTvColorChange(false, tv_bonus);
                setTvColorChange(true, tv_remittance);
            } else {
                setTvColorChange(true, tv_bonus);
                setTvColorChange(false, tv_remittance);
            }
            tv_bonus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBonus = setTvColorChange(isBonus, tv_bonus);
                    executeLocation();
                }
            });
            tv_remittance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isRanking){
                        isBonus=true;
                        isRanking=false;
                    }
                    boolean nB = !isBonus;
                    nB = setTvColorChange(nB, tv_remittance);
                    isBonus = !nB;
                    if (nB) {
                        setTvColorChange(true, tv_bonus);
                    } else {
                        setTvColorChange(false, tv_bonus);
                    }
                    executeLocation();
                }
            });
        }
        istoday = setTvColorChange(false, tv_today);
        isyesterday = setTvColorChange(true, tv_yesterday);
        isbeforeyd = setTvColorChange(true, tv_beforeyd);
        tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                istoday = setTvColorChange(istoday, tv_today);
                if (istoday) {
                    isyesterday = setTvColorChange(true, tv_yesterday);
                    isbeforeyd = setTvColorChange(true, tv_beforeyd);
                }
                executeLocation();
            }
        });
        tv_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isyesterday = setTvColorChange(isyesterday, tv_yesterday);
                if (isyesterday) {
                    istoday = setTvColorChange(true, tv_today);
                    isbeforeyd = setTvColorChange(true, tv_beforeyd);
                }
                executeLocation();
            }
        });
        tv_beforeyd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isbeforeyd = setTvColorChange(isbeforeyd, tv_beforeyd);
                if (isbeforeyd) {
                    istoday = setTvColorChange(true, tv_today);
                    isyesterday = setTvColorChange(true, tv_yesterday);
                }
                executeLocation();
            }
        });
    }

    private String getBonus_date(int interval){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, interval);
        Date d = c.getTime();
        return df.format(d.getTime());
    }

    private void executeLocation() {
        bonus_date=getBonus_date(interval);
        if (isBonus) {
            setTvColorChange(true, tv_remittance);
            myToolbar.setTitle("奖金列表");
            listCBonus = new ArrayList<>();
            AsyncCallWS_getqueryCBonus getqueryCBonusTask = new AsyncCallWS_getqueryCBonus();
            getqueryCBonusTask.execute();
        } else {
                setTvColorChange(false,tv_remittance);
                myToolbar.setTitle("汇款列表");
                listRemit = new ArrayList<>();
                AsyncCallWS_getRemitList getRemitListTask = new AsyncCallWS_getRemitList();
                getRemitListTask.execute();
        }

    }

    private boolean setTvColorChange(Boolean bl, TextView tv) {
        if (bl) {
            bl = false;
        } else {
            bl = true;
        }
        if (bl) {
            tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            tv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_blue));
        } else {
            tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
            tv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
        }
        return bl;
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

    public class ViewHolder {
        public TextView text_title;
        public TextView text_message;
        public TextView text_time, text_remit, tv, tv_ordertime;
        public ImageView image_icon;
        public int messagetype;
        public boolean needInflate;
    }

    public class RankingListAdapter extends ArrayAdapter<rankingList> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public RankingListAdapter(Context context, int textViewResourceId, List<rankingList> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            final rankingList _R = (rankingList) getItem(position);

            if (convertView == null) {
                //Toast.makeText(PreNotificationList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(PreNotificationList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(PreNotificationList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();
            //Boolean isVisible=true;
            //try{isVisible=p.getIsVisible();}catch(Exception e){}
            //if(isVisible){
            int rt = position+1;
            holder.messagetype = rt;
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            DecimalFormat df;
            BigDecimal a = new BigDecimal(String.valueOf(_R.getTotal()));
            df = new DecimalFormat(",###,##0");
            String title = df.format(a)+ " RMB";
            switch (rt) {
                case 1:
                    icon = R.drawable.rank_1;
                    break;
                case 2:
                    icon = R.drawable.rank_2;
                    break;
                case 3:
                    icon = R.drawable.rank_3;
                    break;
                //轉給金流，讓金流中心發獎金
                case 4:
                    icon = R.drawable.rank_4;
                    break;
                case 5:
                    icon = R.drawable.rank_5;
                    break;
                case 6:
                    icon = R.drawable.rank_6;
                    break;
                case 7:
                    icon = R.drawable.rank_7;
                    break;
                case 8:
                    icon = R.drawable.rank_8;
                    break;
                case 9:
                    icon = R.drawable.rank_9;
                    break;
                case 10:
                    icon = R.drawable.rank_10;
                    break;
                case 11:
                    icon = R.drawable.rank_11;
                    break;
                case 12:
                    icon = R.drawable.rank_12;
                    break;
                case 13:
                    icon = R.drawable.rank_13;
                    break;
                case 14:
                    icon = R.drawable.rank_14;
                    break;
                case 15:
                    icon = R.drawable.rank_15;
                    break;
                case 16:
                    icon = R.drawable.rank_16;
                    break;
                case 17:
                    icon = R.drawable.rank_17;
                    break;
                case 18:
                    icon = R.drawable.rank_18;
                    break;
                case 19:
                    icon = R.drawable.rank_19;
                    break;
                case 20:
                    icon = R.drawable.rank_20;
                    break;
                default:
            }
            holder.image_icon.setImageResource(icon);
            if (!title.equals("")) {
                holder.text_title.setText(title);
            } else {
                holder.text_title.setText("No Value");
            }
            String message = "";
            if (!_R.getArea().equals("")) {
                message += _R.getArea();
            }
            if (!_R.getLocality().equals("")) {
                message += " " + _R.getLocality();
            }
            if (!_R.getSublocality().equals("")) {
                message += " " + _R.getSublocality();
            }
            holder.text_message.setText(message);
            holder.text_time.setText(_R.getCount()+"*"+_R.getAmt_avg());
            return view;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_title = (TextView) view.findViewById(R.id.title);
            vh.text_message = (TextView) view.findViewById(R.id.message);
            vh.text_time = (TextView) view.findViewById(R.id.time);
            vh.text_remit = (TextView) view.findViewById(R.id.tv_remit);
            vh.tv_ordertime = (TextView) view.findViewById(R.id.tv_ordertime);
            vh.needInflate = false;
            view.setTag(vh);
        }
    }

    public class RemitListAdapter extends ArrayAdapter<_RemitData> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public RemitListAdapter(Context context, int textViewResourceId, List<_RemitData> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            final _RemitData _R = (_RemitData) getItem(position);

            if (convertView == null) {
                //Toast.makeText(PreNotificationList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(PreNotificationList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(PreNotificationList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();
            //Boolean isVisible=true;
            //try{isVisible=p.getIsVisible();}catch(Exception e){}
            //if(isVisible){
            int rt = _R.getRemit_type();
            holder.messagetype = rt;
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            String title = String.valueOf(_R.getBonus()) + " RMB";
            switch (rt) {
                //幸福瑪吉公司的獎金(1%)
                case 1:
                    icon = R.drawable.logo_s;
                    break;
                //金流中心的獎金(2%)
                case 2:
                    icon = R.drawable.carryout;
                    break;
                //獎金發i点，所以要轉帳給愛買賣或MGS
                case 3:
                    icon = R.drawable.systemnotification;
                    break;
                //轉給金流，讓金流中心發獎金
                case 4:
                    icon = R.drawable.favorite;
                    break;
                //獎金（介紹獎金或領導獎金）
                case 5:
                    icon = R.drawable.seat;
                    break;
                default:
            }
            holder.image_icon.setImageResource(icon);
            if (!title.equals("")) {
                holder.text_title.setText(title);
            } else {
                holder.text_title.setText("No Value");
            }
            String message = "";
            if (_R.getMaakki_id() > 0) {
                message += _R.getMaakki_id() + " ";
            }
            message += _R.getNickname();
            if (!_R.getAccount_no().equals("")) {
                message += "\n" + _R.getAccount_no();
            }
            if (!_R.getBank_name().equals("")) {
                message += "\n" + _R.getBank_name();
            }
            if (!_R.getBranch_name().equals("")) {
                message += "\n" + _R.getBranch_name();
            }
            if (!_R.getAccount_name().equals("")) {
                message += "\n" + _R.getAccount_name();
            }
            holder.text_message.setText(message);
            if (_R.getRemit_date().isEmpty()) {
                holder.text_remit.setVisibility(View.VISIBLE);
                holder.tv_ordertime.setVisibility(View.GONE);
            } else {
                holder.text_remit.setVisibility(View.GONE);
                holder.tv_ordertime.setText(_R.getRemit_date());
                holder.tv_ordertime.setVisibility(View.VISIBLE);
            }

            holder.text_remit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder_onClick = holder;
                    AlertDialog alertDialog = new AlertDialog.Builder(GetRemitList.this).create();
                    alertDialog.setTitle("确认已经汇款");
                    alertDialog.setMessage("您确定已经汇款了吗？\n请确定这笔奖金您已经汇款了，如有任何疑虑或不确定，请按 略过 离开本程序，勿往下进行！"
//                            +"\nrole:"+role
//                            +"\nmaakki_id:"+maakki_id
//                            +"\nbonus_date:"+bonus_date
//                            +"\nremit_type:"+_R.getRemit_type()
//                            +"\nreceive_id:"+_R.getMaakki_id()
                    );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (role.equals("admin")) {
                                        AsyncCallWS_systemRemitCBonus systemRemitCBonusTask = new AsyncCallWS_systemRemitCBonus();
                                        systemRemitCBonusTask.execute(_R.getBonus_date(), String.valueOf(_R.getRemit_type()), String.valueOf(_R.getMaakki_id()));
                                    } else {
                                        AsyncCallWS_cashierRemitCBonus cashierRemitCBonusTask = new AsyncCallWS_cashierRemitCBonus();
                                        cashierRemitCBonusTask.execute(_R.getBonus_date(), String.valueOf(_R.getMaakki_id()));
                                    }
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
            });
            //Long time = _R.getBonus_date();
            //String fm = "";
            //Date date = new Date(time);
            //String dateFormat = "HH:mm";
            //DateFormat formatter = new SimpleDateFormat(dateFormat);
            //fm = formatter.format(date);
            //new SimpleDateFormat(dateFormat).format(new Date(p.getLastModify()));
            holder.text_time.setText(_R.getBonus_date());
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
            vh.text_remit = (TextView) view.findViewById(R.id.tv_remit);
            vh.tv_ordertime = (TextView) view.findViewById(R.id.tv_ordertime);
            vh.needInflate = false;
            view.setTag(vh);
        }
    }

    public class BonusListAdapter extends ArrayAdapter<CBonusData> {
        //
        private LayoutInflater mInflater;
        //
        private int resId;

        public BonusListAdapter(Context context, int textViewResourceId, List<CBonusData> objects) {
            super(context, textViewResourceId, objects);
            this.resId = textViewResourceId;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder holder;
            final CBonusData _R = (CBonusData) getItem(position);

            if (convertView == null) {
                //Toast.makeText(PreNotificationList.this, "convertView==null", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder) convertView.getTag()).needInflate) {
                //Toast.makeText(PreNotificationList.this, "needInflate==true", Toast.LENGTH_SHORT).show();
                view = mInflater.inflate(R.layout.listremit_item, parent, false);
                setViewHolder(view);
            } else {
                //Toast.makeText(PreNotificationList.this, "else", Toast.LENGTH_SHORT).show();
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();
            //Boolean isVisible=true;
            //try{isVisible=p.getIsVisible();}catch(Exception e){}
            //if(isVisible){
            int rt = _R.getBonus_from();
            holder.messagetype = rt;
            int icon = R.drawable.quote;
            //holder.image_icon.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTitleText));
            String title = String.valueOf(_R.getBonus()) + " RMB";
            switch (rt) {
                case 1:
                    icon = R.drawable._imaimai;
                    break;
                case 2:
                    icon = R.drawable.ia;
                    break;
                case 3:
                    icon = R.drawable.favorite;
                    break;
                default:
            }
            holder.image_icon.setImageResource(icon);
            if (!title.equals("")) {
                holder.text_title.setText(title);
            } else {
                holder.text_title.setText("No Value");
            }
            String message = "";
            if (_R.getisGrantCash()) {
                if (!_R.getAccount_no().equals("")) {
                    message +=_R.getAccount_no();
                }
                if (!_R.getBank_name().equals("")) {
                    message += "\n" + _R.getBank_name();
                }
                if (!_R.getBranch_name().equals("")) {
                    message += "\n" + _R.getBranch_name();
                }
                if (!_R.getAccount_name().equals("")) {
                    message += "\n" + _R.getAccount_name();
                }
            } else {
                message += "折算i点：" + _R.getiCredit() + " RMB(i)";
            }
            holder.text_message.setText(message);
            if (_R.getRemit_date().isEmpty()) {
                holder.tv_ordertime.setVisibility(View.GONE);
            } else {
                holder.tv_ordertime.setText(_R.getRemit_date());
                holder.tv_ordertime.setVisibility(View.VISIBLE);
            }
            holder.text_remit.setVisibility(View.GONE);
            holder.text_time.setText(_R.getBonus_date());
            return view;
        }

        private void setViewHolder(View view) {
            ViewHolder vh = new ViewHolder();
            vh.image_icon = (ImageView) view.findViewById(R.id.icon);
            vh.text_title = (TextView) view.findViewById(R.id.title);
            vh.text_message = (TextView) view.findViewById(R.id.message);
            vh.text_time = (TextView) view.findViewById(R.id.time);
            vh.text_remit = (TextView) view.findViewById(R.id.tv_remit);
            vh.tv_ordertime = (TextView) view.findViewById(R.id.tv_ordertime);
            vh.needInflate = false;
            view.setTag(vh);
        }
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

    public String getHashCode(String Gkey) { //得到毫秒数

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

    private void getLocationRanking() {
        //Create request
        String METHOD_NAME = "getLocationRanking";
        String SOAP_ACTION = "http://www.maakki.com/getLocationRanking";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim();
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        errCode="maakki_id:"+maakki_id
        +"\ntimeStamp:"+timeStamp
        +"\nidentifyStr"+identifyStr;
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {
            errCode+=e.getMessage();
        }
        try {
            String tmp = soapPrimitive.toString();
            //String tmp = soapPrimitive.toString();
            //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
            tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
            JSONObject json_read;
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("rankingList");
            errCode = json_read.getString("errCode");
            //將資料丟進JSONObject
            //接下來選擇型態使用get並填入key取值
            //ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                rankingList t = new rankingList();
                JSONObject listObj = jsonArray.getJSONObject(i);
                t.setArea(listObj.getString("area"));
                t.setLocality(listObj.getString("locality"));
                t.setSublocality(listObj.getString("sublocality"));
                t.setAmt_avg(listObj.getInt("amt_avg"));
                t.setCount(listObj.getInt("count1"));
                t.setTotal(listObj.getInt("total"));
                if (listLocationRanking.size()<20) {
                    if(!t.getArea().isEmpty()||!t.getLocality().isEmpty()||!t.getSublocality().isEmpty()){
                        listLocationRanking.add(t);
                    }
                }
            }
        } catch (Exception e) {
            errCode += e.getMessage();
        }
    }

    private class AsyncCallWS_getLocationRanking extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String condition=params[0];
            getLocationRanking();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("Calculated","selected_no:"+String.valueOf(listTradeData.size()+"\nselsected_amount:"+formatDoubleToString(Double.parseDouble(String.valueOf(String.valueOf(selected_amt))))));
            //Toast.makeText(getApplicationContext(),"count:"+String.valueOf(listRemit.size()+"/"+errCode),Toast.LENGTH_LONG).show();
            count = listLocationRanking.size();
            //showAlertDialog("count",String.valueOf(count)+"/"+errCode);
            if (count > 0) {
                //Collections.reverse(listLocationRanking);
                RankingListAdapter adapter = new RankingListAdapter(GetRemitList.this, R.layout.listremit_item, listLocationRanking);
                myToolbar.setTitle("地区排行榜");
                listview.setVisibility(View.VISIBLE);
                listview.setAdapter(adapter);
                no_Remit.setVisibility(View.GONE);
            } else {
                listview.setVisibility(View.GONE);
                no_Remit.setImageDrawable(getResources().getDrawable(R.drawable.no_locationranking));
                no_Remit.setVisibility(View.VISIBLE);
            }/**/
        }

        @Override
        protected void onPreExecute() {
            //String msg="maakki_id:"+maakki_id+"\nrole:"+role+"\nbonus_date:"+bonus_date;
            //showAlertDialog("Parameters",msg);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void getRemitList() {
        //Create request
        String METHOD_NAME = "getRemitList";
        String SOAP_ACTION = "http://www.maakki.com/getRemitList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + role + bonus_date;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role);
        request.addProperty("bonus_date", bonus_date);
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
        //String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        try {
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("remitList");
            errCode = json_read.getString("errCode");
            //將資料丟進JSONObject
            //接下來選擇型態使用get並填入key取值
            ArrayList<String> list = new ArrayList<String>();
            //JSONArray jsonArray = (JSONArray)jsonObject;
            /*if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.get(i).toString());
                }
            }*/
            for (int i = 0; i < jsonArray.length(); i++) {
                _RemitData t = new _RemitData();
                JSONObject tradelistObj = jsonArray.getJSONObject(i);
                t.setMaakki_id(tradelistObj.getInt("maakki_id"));
                t.setNickname(tradelistObj.getString("nickname"));
                t.setBonus_date(tradelistObj.getString("bonus_date"));
                t.setBonus(tradelistObj.getInt("bonus"));
                t.setRemit_type(tradelistObj.getInt("remit_type"));
                t.setBank_name(tradelistObj.getString("bank_name"));
                t.setBranch_name(tradelistObj.getString("branch_name"));
                t.setAccount_no(tradelistObj.getString("account_no"));
                t.setAccount_name(tradelistObj.getString("account_name"));
                t.setRemit_date(tradelistObj.getString("remit_date"));
                //if (CheckifSeleted(t)) {
                listRemit.add(t);
                //selected_amt += tradelistObj.getInt("amount");
                //}
            }
        } catch (Exception e) {
            errCode = e.getMessage();
        }
    }

    private class AsyncCallWS_getRemitList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String condition=params[0];
            getRemitList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            myToolbar.setTitle("汇款列表");
            count = listRemit.size();
            if (count > 0) {
                //listPreNotification = prevDAO.getAll();
                Collections.reverse(listRemit);
                //listtop=new ArrayList<PrevNotification>();
                RemitListAdapter adapter = new RemitListAdapter(GetRemitList.this, R.layout.listremit_item, listRemit);
                listview.setAdapter(adapter);
                //setItemView();
/*                fab.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAscending) {
                            isAscending = false;
                            fab.setImageResource(R.drawable.ic_arrow_downward_white_18dp);
                        } else {
                            isAscending = true;
                            fab.setImageResource(R.drawable.ic_arrow_upward_white_18dp);
                        }
                        Collections.reverse(listRemit);

                    }
                });*/
                //adapter.notifyDataSetChanged();
                listview.setVisibility(View.VISIBLE);
                no_Remit.setVisibility(View.GONE);
                //RL_condition_day.setVisibility(View.VISIBLE);

            } else {
                //tv.setText("");
                listview.setVisibility(View.GONE);
                no_Remit.setImageDrawable(getResources().getDrawable(R.drawable.no_remittance));
                no_Remit.setVisibility(View.VISIBLE);
                //RL_condition_day.setVisibility(View.GONE);
            }
            iv_left.setVisibility(View.VISIBLE);
            if(interval!=0){
                iv_right.setVisibility(View.VISIBLE);
            }else{
                iv_right.setVisibility(View.INVISIBLE);
            }        }

        @Override
        protected void onPreExecute() {
            //String msg="maakki_id:"+maakki_id+"\nrole:"+role+"\nbonus_date:"+bonus_date;
            //showAlertDialog("Parameters",msg);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void queryCBonus() {
        //Create request
        String METHOD_NAME = "queryCBonus";
        String SOAP_ACTION = "http://www.maakki.com/queryCBonus";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + bonus_date;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("bonus_date", bonus_date);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        errCode = "";
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            errCode += e.getMessage();
        }
        try {
            String tmp = soapPrimitive.toString();
            //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
            tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
            JSONObject json_read;
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("CBonusList");
            errCode += json_read.getString("errCode");
            //將資料丟進JSONObject
            //接下來選擇型態使用get並填入key取值
            //ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                CBonusData t = new CBonusData();
                JSONObject tradelistObj = jsonArray.getJSONObject(i);
                t.setBonus_date(tradelistObj.getString("bonus_date"));
                t.setBonus(tradelistObj.getInt("bonus"));
                t.setiCredit(tradelistObj.getInt("iCredit"));
                t.setisGrantCash(tradelistObj.getBoolean("isGrantCash"));
                t.setBonus_from(tradelistObj.getInt("bonus_from"));
                t.setBank_name(tradelistObj.getString("bank_name"));
                t.setBranch_name(tradelistObj.getString("branch_name"));
                t.setAccount_no(tradelistObj.getString("account_no"));
                t.setAccount_name(tradelistObj.getString("account_name"));
                t.setRemit_date(tradelistObj.getString("remit_date"));
                listCBonus.add(t);
            }
        } catch (Exception e) {
            errCode += e.getMessage();
        }/**/
    }

    private class AsyncCallWS_getqueryCBonus extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String condition=params[0];
            queryCBonus();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            myToolbar.setTitle("奖金列表");
            count = listCBonus.size();
            if (count > 0) {
                //Collections.reverse(listCBonus);
                BonusListAdapter adapter = new BonusListAdapter(GetRemitList.this, R.layout.listremit_item, listCBonus);
                listview.setAdapter(adapter);
                listview.setVisibility(View.VISIBLE);
                no_Remit.setVisibility(View.GONE);
                //RL_condition_day.setVisibility(View.VISIBLE);

            } else {
                listview.setVisibility(View.GONE);
                no_Remit.setImageDrawable(getResources().getDrawable(R.drawable.no_bonus));
                no_Remit.setVisibility(View.VISIBLE);/**/
                //RL_condition_day.setVisibility(View.GONE);
            }
            iv_left.setVisibility(View.VISIBLE);
            if(interval!=0){
                iv_right.setVisibility(View.VISIBLE);
            }else{
                iv_right.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //String msg="maakki_id:"+maakki_id+"\nrole:"+role+"\nbonus_date:"+bonus_date;
            //showAlertDialog("Parameters",msg);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }


    private void systemRemitCBonus(String bonus_date, String remit_type, String receiver_id) {
        //Create request
        String METHOD_NAME = "systemRemitCBonus";
        String SOAP_ACTION = "http://www.maakki.com/systemRemitCBonus";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + bonus_date + remit_type + receiver_id;
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("bonus_date", bonus_date);
        request.addProperty("remit_type", remit_type);
        request.addProperty("receiver_id", receiver_id);
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
        //String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
        } catch (Exception e) {
            errCode = e.getMessage();
        }
    }

    private class AsyncCallWS_systemRemitCBonus extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            String bonus_date = params[0];
            String remit_type = params[1];
            String receive_id = params[2];
            systemRemitCBonus(bonus_date, remit_type, receive_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errCode.equals("1")) {
                holder_onClick.text_remit.setVisibility(View.GONE);
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                String remit_date = df.format(new Date().getTime());
                holder_onClick.tv_ordertime.setText(remit_date);
                holder_onClick.tv_ordertime.setVisibility(View.VISIBLE);
            } else {
                String msg = "";
                if (errCode.equals("2")) {
                    msg = "认证失败";
                } else if (errCode.equals("4")) {
                    msg = "没有回报权限";
                } else if (errCode.equals("5")) {
                    msg = "日期格式错误";
                } else if (errCode.equals("6")) {
                    msg = "汇款类别错误";
                } else if (errCode.equals("8")) {
                    msg = "已经回报过汇款";
                }
                showAlertDialog("回报汇款失败", msg);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void cashierRemitCBonus(String bonus_date, String receiver_id) {
        //Create request
        this.receiver_id = receiver_id;
        this.bonus_date = bonus_date;
        String METHOD_NAME = "cashierRemitCBonus";
        String SOAP_ACTION = "http://www.maakki.com/cashierRemitCBonus";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + bonus_date + receiver_id;
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("cashier_id", maakki_id);
        request.addProperty("bonus_date", bonus_date);
        request.addProperty("receiver_id", receiver_id);
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
        //String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
        } catch (Exception e) {
            //errCode = e.getMessage();
        }
    }

    private class AsyncCallWS_cashierRemitCBonus extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            String bonus_date = params[0];
            String receiver_id = params[1];
            cashierRemitCBonus(bonus_date, receiver_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errCode.equals("1")) {
                holder_onClick.text_remit.setVisibility(View.GONE);
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                String remit_date = df.format(new Date().getTime());
                holder_onClick.tv_ordertime.setText(remit_date);
                holder_onClick.tv_ordertime.setVisibility(View.VISIBLE);
            } else {
                String msg = "";
                if (errCode.equals("2")) {
                    msg = "认证失败";
                } else if (errCode.equals("5")) {
                    msg = "日期格式错误";
                } else if (errCode.equals("7")) {
                    msg = "哎哟！别急，等大金流先汇款给你呗";
                } else if (errCode.equals("8")) {
                    msg = "已经回报过汇款";
                } else {
                    msg = "cashier_id:" + maakki_id
                            + "\nbonus_date:" + bonus_date
                            + "\nrecever_id:" + receiver_id
                            + "\ntimeStamp:" + timeStamp
                            + "\nencryptStr:" + encryptStr
                            + "\nidentifyStr:" + identifyStr
                            + "\nerrCode:" + errCode;
                }
                showAlertDialog("回报汇款失败", msg);
            }
        }

        @Override
        protected void onPreExecute() {
            /*String msg="maakki_id:"+maakki_id
                    +"\nbonus_date:"+bonus_date
                    +"\nrecever_id:"+receiver_id
                    ;
            showAlertDialog("Parameters",msg);*/
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(GetRemitList.this).create();
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
