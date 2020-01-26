package com.maakki.maakkiapp;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class imaimai_Main extends Activity {

    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    private ImageView imgLogo;
    private TextView tv_slogan, tv_content1, tv_1, tv_2, tv_3, tv_4, tv_next;
    private ImageView ivBack;
    private String currency, errMsg = "", days = "15", maakki_id, timeStamp, identifyStr;
    private Double initialiCredit, endingiCredit, midtermIn, midtermOut, cost, income, balance, oCost, sell, initialAmt, endingAmt, GOC = 0.0, initialRate, endingRate, bonus, revenue;
    private LinearLayout llayout;
    private RelativeLayout RL_CB, RL_bottom, RL_next;
    private SwipeDetector swipeDetector;
    private CheckBox checkBox;
    private Boolean isReadStatement,
            isPage2 = false,
            isPage3 = false,
            isPage4 = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imaimai_main);
        // Hiding the action bar
        getActionBar().hide();
        swipeDetector = new SwipeDetector();
        //SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key6, true);
        //layout=(LinearLayout) findViewById(R.id.layout);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        //tv_question = (TextView) findViewById(R.id.tv_guestion);
        tv_slogan = (TextView) findViewById(R.id.tv_greeting);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_4 = (TextView) findViewById(R.id.tv_4);
        tv_content1 = (TextView) findViewById(R.id.tv_content1);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        RL_CB = (RelativeLayout) findViewById(R.id.RL_CB);
        RL_next = (RelativeLayout) findViewById(R.id.RL_next);
        tv_next = (TextView) findViewById(R.id.tv_next);
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);
        checkBox.setButtonDrawable(id);

        ivBack = (ImageView) findViewById(R.id.imV_back);/**/
        llayout = (LinearLayout) findViewById(R.id.page_option);

        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1Onclick();
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv2Onclick();
            }
        });
        tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv3Onclick();
            }
        });
        tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv4Onclick();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String redUrl="http://www.maakki.com/MCoins/MCoinsQuery.aspx";
                Intent intent = new Intent(imaimai_Main.this, WebMain2.class);
                intent.putExtra("redirUrl", redUrl);
                startActivity(intent);
            }
        });

/*        imgLogo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //finish();
                Intent i = new Intent(imaimai_Main.this, GOCMain2.class);
                startActivity(i);
            }
        });*/
        setTvText();

        tv_content1.setOnTouchListener(swipeDetector);
        tv_content1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                        if (days.equals("90")) {
                            tv3Onclick();
                        } else if (days.equals("60")) {
                            tv2Onclick();
                        } else if (days.equals("30")) {
                            tv1Onclick();
                        }
                    } else if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        if (days.equals("15")) {
                            tv2Onclick();
                        } else if (days.equals("30")) {
                            tv3Onclick();
                        } else if (days.equals("60")) {
                            tv4Onclick();
                        }
                    }
                }
            }
        });

        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    RL_bottom.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setChecked(true);
                    RL_bottom.setVisibility(View.GONE);
                }

            }
        });
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
        checkBox.setOnCheckedChangeListener(chklistener);
    }

    private void tv1Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "15";
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_content1.setText(getApplicationContext().getResources().getString(R.string.statement_content1));
        RL_CB.setVisibility(View.GONE);
        checkBox.setChecked(false);
    }

    private void tv2Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "30";
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_content1.setText(getApplicationContext().getResources().getString(R.string.statement_content2));
        RL_CB.setVisibility(View.GONE);
        isPage2 = true;
        checkBox.setChecked(false);
    }

    private void tv3Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "60";
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_content1.setText(getApplicationContext().getResources().getString(R.string.statement_content3));
        RL_CB.setVisibility(View.GONE);
        isPage3 = true;
        checkBox.setChecked(false);
    }

    private void tv4Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        days = "90";
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_content1.setText(getApplicationContext().getResources().getString(R.string.statement_content4));
        //It's necessary to go through page2&page3,then RL_CB appears;
        if (!isPage2) {
            if (!isPage3) {
                Toast.makeText(getApplicationContext(), "你跳过了第二页和第三页", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "你跳过了第二页", Toast.LENGTH_LONG).show();
            }
        } else if (!isPage3) {
            Toast.makeText(getApplicationContext(), "你跳过了第三页", Toast.LENGTH_LONG).show();
        } else {
            RL_CB.setVisibility(View.VISIBLE);
        }
        final Boolean isRead = SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key15, false);
        if (isRead) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        //iCreditGOCTask.execute();
        //Toast.makeText(getApplicationContext(), "order_id:"+order_id+"\nlistOrderDetail.size():"+listOrderDetail.size(), Toast.LENGTH_LONG).show();
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

    private String formatDoubleToString(Double d) {
        DecimalFormat df;
        BigDecimal a = new BigDecimal(d.toString());
        if (d > 10) {
            df = new DecimalFormat(",###,##0");
        } else if (d > 1) {
            df = new DecimalFormat(",###,##0.0");
        } else {
            df = new DecimalFormat(",###,##0.00");
        }
        df.format(a);
        return df.format(a);
    }

    private void setTvText() {
        String content = "期末点数 = 期初点数 + 期中增加点数 - 期中减少点数\n\n"
                + "期中增加点数 = 期中转进点数(其他会员转点进来的点数) + 商店销售所收的点数\n\n"
                + "期中减少点数 = 期中转出点数(其他会员转点出去的点数) + 消费购物所花的点数 + 回收的点数\n\n"
                + "收益 = (期末点数 * 期末回收率 + 期中减少点数 * 减少当日回收率) - (期初点数 * 期初回收率 + 期中买进点数 * 买进当日回收率) + (系统给予的奖励点数 * 获得当日的回收率)\n\n"
                + "成本 = (期初点数 * 期初回收率 + 期中增加点数 * 增加当日回收率)\n\n"
                + "成本收益率 GOC = (收益 / 成本) * 100 %\n\n"
                + "所有数字系基于市场乐观发展所作的估算，不代表您实际拥有。更多说明请参考服务条款。";
        //tv_content2.setText(content);
    }

    public double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox.isChecked()) {
                RL_CB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                //iv_customerservice.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tips_circle_purple));
                RL_next.setVisibility(View.VISIBLE);
                RL_bottom.setVisibility(View.GONE);
                SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
                Integer date=Integer.parseInt(dt.format(new Date()));
                SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key15, true);
                SharedPreferencesHelper.putSharedPreferencesInt(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key17, date);

            } else {
                RL_CB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
                RL_next.setVisibility(View.GONE);
                RL_bottom.setVisibility(View.VISIBLE);
                SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key15, false);
                SharedPreferencesHelper.putSharedPreferencesInt(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key17, 0);
            }

        }

    };

    public void goNext() {
        Intent i = new Intent(imaimai_Main.this, GOCMain2.class);
        startActivity(i);
    }
}