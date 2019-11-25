package com.maakki.maakkiapp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class imaimai_order extends Activity {
    //private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefs;
    private Thread coreThread = null;
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    private final String URL_IA = "http://www.maakki.com/WebServiceIA.asmx";
    private TextView tv_13, tv_area, tv_locality, tv_sublocality, tv_n50, tv_0, tv_17142, tv_72338, tv_NextStepTime, tv_today, tv_yesterday, tv_RemittanceTime_Cashier, tv_RemittanceTime, tv_cashin_cashier, tv_refund_admin, tv_cancel_cashier, tv_report_cashier, tv_confirm_order_admin, tv_confirm_cashout_admin, tv_cancel_admin, tv_BankInfo, tv_errMsg, tv_location;
    private ImageView ivSetup, ivImaimai, ivIdentity, ivQuestion, ivAdmin, ivCashier, iv_right, iv_left, ivList, imgLogo, ivBack, imgLogo_imaimai;
    private TextView tv_selected_amount, tv_selected_no, tv_refund_imaimai, tv_20, tv_50, tv_11, tv_12, tv_42, tv_41, tv_40, tv_10, tv_RemittanceInfo, tv_confirm_report_cashier, tv_cancel_report_cashier, tv_refund_cashier, tv_confirm_report, tv_cancel_report, tv_refund, tv_cancel, tv_report, tv_NextStep, tv_Exclamation, tv_ordertime, tv_orderno, tv_next, tv_content1, tv_content2, tv_content3, tv_content4, tv_content5, tv_ordermain, tv_1, tv_2, tv_3, tv_4, tv_icreditsrecallrate;
    private String location_id, refund_bank_name, refund_branch_name, refund_account_name, refund_account_no, cashier_account_no_latest, cashierRemittedTime, cashierRemittedAccount, cashierRemittedName, condition, buyer_id, bank_name_getBankData, branch_name_getBankData, account_no_getBankData, account_name_getBankData;
    private String introducer_id, strFeedback2_cashier, role_getBankData, maakki_id_account, strBankData, trade_no, TimeStamp1, role_execute, errCode, role_getTradeInfo, role_getTradeList, territory_gps, area_gps, locality_gps, sublocality_gps, territory, area, locality, sublocality, refundAmt, recycle_iCredit, ed_text_ra, ed_text_rn, ed_text_ra_cashier, ed_text_rn_cashier, strFeedback1, strFeedback2, act, applyRefund_date, refund_date, trade_date, create_date, cashier_account_name, remittance_account, remittance_name, remittance_time, cashier_bank_name, cashier_branch_name, cashier_account_no, strContent, currency, errMsg = "", maakki_id, timeStamp, identifyStr;
    private Double iCredit, dailyAmount, PersonalBalance, balance, PersonalLimit, initialiCredit, endingiCredit, iCreditsRecallRate, iCreditsRecallRate_record;
    private LinearLayout ll_LocationInfo, ll_Rule_imaimai, ll_RemittanceInfo, ll_accentBar, next_option_imaimai, ll_NextStep, ll_BankInfo, LL_remittance_report_cashier, LL_remittance_report_buyer, next_option_buyer, next_option_cashier, next_option_admin, layout_order, layout_icreditrate, cash_option;
    private RelativeLayout RL_main, RL_no920, RL_condition_selected, RL_condition_cashier, rl_order, RL_condition_day, RL_condition_status, RL_order, RL_CB, RL_bottom, RL_next;
    private SwipeDetector swipeDetector;
    private CheckBox checkBox;
    private int selected_no = 0, selected_amt = 0, tradeStatus_mine, tradeID_imaimai, tradeID_admin, tradeID_mine, tradeID_cashier, cashier_id, days, previous_tradeID, next_tradeID, leader1_id, leader2_id, leader3_id, referral_id, amount = 100, amount_record, tradeID, tradeStatus;
    private Boolean isImaimai, isCashier, isAdmin, isImaimaiGrant, is13, is12, isArea, isLocality, isSublocality, isn50, is0, is17142, is72338, is20, istoday, isyesterday, is50, is11, is41, is40, is10, isSpecific, is42, hasMore_imaimai, hasMore_admin, hasMore_mine, hasMore_cashier, hasBalance;
    private List<_920TradeData> listTradeData;
    private EditText et_remittance_account_cashier, et_remittance_name_cashier, et_remittance_account, et_remittance_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imaimai_order);
        // Hiding the action bar
        getActionBar().hide();
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        isSpecific = false;
        swipeDetector = new SwipeDetector();
        iCreditsRecallRate = Double.parseDouble(SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key16, "0"));
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        imgLogo.startAnimation(startAnimation);
        imgLogo_imaimai = (ImageView) findViewById(R.id.imgLogo_imaimai);
        ivList = (ImageView) findViewById(R.id.ivList);
        ivCashier = (ImageView) findViewById(R.id.ivCashier);
        ivAdmin = (ImageView) findViewById(R.id.ivAdmin);
        ivQuestion = (ImageView) findViewById(R.id.ivQuestion);
        ivIdentity = (ImageView) findViewById(R.id.ivIdentity);
        ivImaimai = (ImageView) findViewById(R.id.ivImaimai);
        ivSetup = (ImageView) findViewById(R.id.ivSetup);
        tv_today = (TextView) findViewById(R.id.tv_today);
        tv_yesterday = (TextView) findViewById(R.id.tv_yesterday);
        tv_selected_no = (TextView) findViewById(R.id.tv_selected_no);
        tv_selected_amount = (TextView) findViewById(R.id.tv_selected_amount);
        tv_72338 = (TextView) findViewById(R.id.tv_72338);
        tv_17142 = (TextView) findViewById(R.id.tv_17142);
        tv_refund_imaimai = (TextView) findViewById(R.id.tv_refund_imaimai);
        tv_0 = (TextView) findViewById(R.id.tv_0);
        tv_13 = (TextView) findViewById(R.id.tv_13);
        tv_12 = (TextView) findViewById(R.id.tv_12);
        tv_11 = (TextView) findViewById(R.id.tv_11);
        tv_42 = (TextView) findViewById(R.id.tv_42);
        tv_41 = (TextView) findViewById(R.id.tv_41);
        tv_40 = (TextView) findViewById(R.id.tv_40);
        tv_10 = (TextView) findViewById(R.id.tv_10);
        tv_50 = (TextView) findViewById(R.id.tv_50);
        tv_n50 = (TextView) findViewById(R.id.tv_n50);
        tv_20 = (TextView) findViewById(R.id.tv_20);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_4 = (TextView) findViewById(R.id.tv_4);

        tv_icreditsrecallrate = (TextView) findViewById(R.id.tv_icreditsrecallrate);
        tv_icreditsrecallrate.setText(iCreditsRecallRate.toString() + " %");
        tv_content1 = (TextView) findViewById(R.id.tv_content1);
        tv_content2 = (TextView) findViewById(R.id.tv_content2);
        tv_content3 = (TextView) findViewById(R.id.tv_content3);
        tv_content4 = (TextView) findViewById(R.id.tv_content4);
        tv_content5 = (TextView) findViewById(R.id.tv_content5);
        tv_orderno = (TextView) findViewById(R.id.tv_orderno);
        tv_ordertime = (TextView) findViewById(R.id.tv_ordertime);
        tv_ordermain = (TextView) findViewById(R.id.tv_ordermain);
        tv_refund = (TextView) findViewById(R.id.tv_refund);
        tv_report = (TextView) findViewById(R.id.tv_report);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel_cashier = (TextView) findViewById(R.id.tv_cancel_cashier);
        tv_report_cashier = (TextView) findViewById(R.id.tv_report_cashier);
        tv_refund_cashier = (TextView) findViewById(R.id.tv_refund_cashier);
        tv_cashin_cashier = (TextView) findViewById(R.id.tv_cashin_cashier);
        tv_confirm_order_admin = (TextView) findViewById(R.id.tv_confirm_order_admin);
        tv_confirm_cashout_admin = (TextView) findViewById(R.id.tv_confirm_cashout_admin);
        tv_cancel_admin = (TextView) findViewById(R.id.tv_cancel_admin);
        tv_refund_admin = (TextView) findViewById(R.id.tv_refund_admin);
        tv_errMsg = (TextView) findViewById(R.id.tv_errMsg);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_Exclamation = (TextView) findViewById(R.id.tv_Exclamation);
        tv_BankInfo = (TextView) findViewById(R.id.tv_BankInfo);
        tv_RemittanceInfo = (TextView) findViewById(R.id.tv_RemittanceInfo);
        tv_RemittanceTime = (TextView) findViewById(R.id.tv_RemittanceTime);
        tv_NextStepTime = (TextView) findViewById(R.id.tv_NextStepTime);
        tv_RemittanceTime_Cashier = (TextView) findViewById(R.id.tv_RemittanceTime_Cashier);
        ll_RemittanceInfo = (LinearLayout) findViewById(R.id.ll_RemittanceInfo);
        ll_Rule_imaimai = (LinearLayout) findViewById(R.id.ll_Rule_imaimai);
        ll_accentBar = (LinearLayout) findViewById(R.id.ll_accentBar);
        ll_BankInfo = (LinearLayout) findViewById(R.id.ll_BankInfo);
        ll_LocationInfo = (LinearLayout) findViewById(R.id.ll_LocationInfo);
        ll_NextStep = (LinearLayout) findViewById(R.id.ll_NextStep);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        tv_NextStep = (TextView) findViewById(R.id.tv_NextStep);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_locality = (TextView) findViewById(R.id.tv_locality);
        tv_sublocality = (TextView) findViewById(R.id.tv_sublocality);
        RL_main = (RelativeLayout) findViewById(R.id.RL_main);
        RL_CB = (RelativeLayout) findViewById(R.id.RL_CB);
        RL_next = (RelativeLayout) findViewById(R.id.RL_next);
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);
        RL_order = (RelativeLayout) findViewById(R.id.rl_order);
        RL_condition_status = (RelativeLayout) findViewById(R.id.RL_condition_status);
        RL_condition_day = (RelativeLayout) findViewById(R.id.RL_condition_day);
        RL_condition_cashier = (RelativeLayout) findViewById(R.id.RL_condition_cashier);
        RL_condition_selected = (RelativeLayout) findViewById(R.id.RL_condition_selected);
        RL_no920 = (RelativeLayout) findViewById(R.id.RL_no920);
        RL_no920.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_no920.setVisibility(View.GONE);
                RL_main.setVisibility(View.VISIBLE);
                ivList.setVisibility(View.VISIBLE);
            }
        });
        //rl_order = (RelativeLayout) findViewById(R.id.rl_order);
        checkBox.setButtonDrawable(id);
        ivBack = (ImageView) findViewById(R.id.imV_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        LL_remittance_report_buyer = (LinearLayout) findViewById(R.id.LL_remittance_report_buyer);
        LL_remittance_report_cashier = (LinearLayout) findViewById(R.id.LL_remittance_report_cashier);
        layout_icreditrate = (LinearLayout) findViewById(R.id.layout_icreditrate);
        layout_order = (LinearLayout) findViewById(R.id.layout_order);
        cash_option = (LinearLayout) findViewById(R.id.cash_option);
        next_option_buyer = (LinearLayout) findViewById(R.id.next_option_buyer);
        next_option_cashier = (LinearLayout) findViewById(R.id.next_option_cashier);
        next_option_admin = (LinearLayout) findViewById(R.id.next_option_admin);
        next_option_imaimai = (LinearLayout) findViewById(R.id.next_option_imaimai);
        et_remittance_account_cashier = (EditText) findViewById(R.id.et_remittance_account_cashier);
        et_remittance_name_cashier = (EditText) findViewById(R.id.et_remittance_name_cashier);
        et_remittance_account = (EditText) findViewById(R.id.et_remittance_account);
        et_remittance_name = (EditText) findViewById(R.id.et_remittance_name);
        tv_Exclamation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_Exclamation.setVisibility(View.GONE);
                Intent i = new Intent(imaimai_order.this, imaimai_Main.class);
                startActivity(i);
            }
        });
        ivSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivSetup.setVisibility(View.INVISIBLE);
                Intent i = new Intent(imaimai_order.this, BindBankData.class);
                startActivity(i);
            }
        });
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
                finish();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_content1.setText(getAmtoficredits(100) + " RMB(i)");
        tv_content2.setText(getFormulate(100));
        tv_content3.setText(getSummary(100));
        tv_content4.setText(getApplicationContext().getResources().getString(R.string.imaimai_order_content4));
        tv_content5.setText(getExpireDate());
        ivQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivList.setVisibility(View.GONE);
                ivSetup.setVisibility(View.GONE);
                ivCashier.setVisibility(View.GONE);
                ivAdmin.setVisibility(View.GONE);
                ivImaimai.setVisibility(View.GONE);
                tv_location.setVisibility(View.GONE);
                ivQuestion.setVisibility(View.INVISIBLE);
                imgLogo.setVisibility(View.GONE);
                imgLogo_imaimai.setVisibility(View.VISIBLE);
                layout_icreditrate.setVisibility(View.GONE);
                tv_content2.setVisibility(View.GONE);
                RL_CB.setVisibility(View.GONE);
                RL_bottom.setVisibility(View.VISIBLE);
                cash_option.setVisibility(View.GONE);
                RL_next.setVisibility(View.GONE);
                tv_Exclamation.setVisibility(View.VISIBLE);
                tv_content1.setText(getApplicationContext().getResources().getString(R.string.Rules_topic));
                //tv_content4.setText(getApplicationContext().getResources().getString(R.string.Rules_content));
                ll_Rule_imaimai.setVisibility(View.VISIBLE);
                tv_content4.setVisibility(View.GONE);
                tv_content5.setVisibility(View.GONE);
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBox.setChecked(false);
                        setOrderView();
                    }
                });

            }
        });
        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    cash_option.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setChecked(true);
                    cash_option.setVisibility(View.GONE);
                }

            }
        });
        checkBox.setOnCheckedChangeListener(chklistener);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                alertDialog.setTitle("确认申购");
                alertDialog.setMessage("您确定要申购920吗？\n请确定您完全明白920专案的所有规则，如有任何疑虑，请按 略过 离开本程序，勿往下进行！");
                if (tradeStatus_mine == 0) {
                    alertDialog.setTitle("请勿重复申购");
                    alertDialog.setMessage("您还有订单尚未汇款到指定银行账号，确定要再新增一笔申购订单吗？");
                }
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_Order_imaimai Order_imaimaiTask = new AsyncCallWS_Order_imaimai();
                                Order_imaimaiTask.execute();
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
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivList.setVisibility(View.INVISIBLE);
                act = "latest";
                role_getTradeInfo = "mine";
                AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                get920TradeInfoTask.execute("mine", act, "0");
            }
        });
        ivCashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCashier.setVisibility(View.INVISIBLE);
                act = "latest";
                role_getTradeInfo = "cashier";
                AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                get920TradeInfoTask.execute("cashier", act, "0");
            }
        });
        ivAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAdmin.setVisibility(View.INVISIBLE);
                act = "latest";
                role_getTradeInfo = "admin";
                AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                get920TradeInfoTask.execute("admin", act, "0");
            }
        });
        ivImaimai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivImaimai.setVisibility(View.INVISIBLE);
                act = "latest";
                role_getTradeInfo = "imaimai";
                AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                get920TradeInfoTask.execute("imaimai", act, "0");
            }
        });
        getCityName();
        getBalancenLatestOrder();
        ivIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ivIdentity.setVisibility(View.INVISIBLE);
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                String bonus_date = df.format(new Date().getTime());
                Bundle bundle = new Bundle();
                bundle.putString("role", role_getTradeInfo);
                bundle.putString("bonus_date", bonus_date);
                if (role_getTradeInfo.equals("mine")) {
                    bundle.putString("query_type", "bonus");
                } else {
                    bundle.putString("query_type", "remittance");
                }
                Intent i = new Intent(imaimai_order.this, GetRemitList.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Limit the iv_submit Buttoon Appear after 6:00
        TimeZone china=TimeZone.getTimeZone("GMT+08:00");
        Calendar cal  = Calendar.getInstance(china);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if ((hour<9)){
            //SlideToDown(ivBack);
            tv_next.setVisibility(View.GONE);
        }else{
            SlideToAbove(ivBack);
            tv_next.setVisibility(View.VISIBLE);
        }
        resetOriginal();
    }

    private void getBalancenLatestOrder() {
        AsyncCallWS_get920Balance get920BalanceTask = new AsyncCallWS_get920Balance();
        get920BalanceTask.execute();
        //act = "latest";
        tradeID_cashier = 0;
        tradeID_mine = 0;
        tradeID_admin = 0;
        tradeID_imaimai = 0;
        role_getTradeInfo = "";
        isCashier=false;
        isAdmin=false;
        isImaimai=false;
        //AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
        //get920TradeInfoTask.execute("mine", act, "0");
        //role_getTradeInfo="cashier";
        //AsyncCallWS_get920TradeInfo get920TradeInfoTask1 = new AsyncCallWS_get920TradeInfo();
        //get920TradeInfoTask1.execute("cashier", act, "0");
        if (maakki_id.equals("72338")||maakki_id.equals("101029")) {
            isCashier = true;
        }else if (maakki_id.equals("10006") || maakki_id.equals("1")) {
            isAdmin = true;
        } else if (maakki_id.equals("17142")) {
            //Toast.makeText(getApplicationContext(), "act:"+act, Toast.LENGTH_LONG).show();
            //AsyncCallWS_get920TradeInfo get920TradeInfoTask2 = new AsyncCallWS_get920TradeInfo();
            //get920TradeInfoTask2.execute("imaimai", act, "0");
            isCashier=true;
            isImaimai = true;
        }
        if (isCashier) {
            ivCashier.setVisibility(View.VISIBLE);
        }
        if (isAdmin) {
            ivAdmin.setVisibility(View.VISIBLE);
        }
        if (isImaimai) {
            ivImaimai.setVisibility(View.VISIBLE);
        }
    }

    private void tv1Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        amount = 100;
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_content1.setText(getAmtoficredits(amount) + " RMB(i)");
        tv_content2.setText(getFormulate(amount));
        tv_content3.setText(getSummary(amount));
        checkBox.setChecked(false);
    }

    private void tv2Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        amount = 500;
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_content1.setText(getAmtoficredits(amount) + " RMB(i)");
        tv_content2.setText(getFormulate(amount));
        tv_content3.setText(getSummary(amount));
        checkBox.setChecked(false);
    }

    private void tv3Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        amount = 1000;
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_content1.setText(getAmtoficredits(amount) + " RMB(i)");
        tv_content2.setText(getFormulate(amount));
        tv_content3.setText(getSummary(amount));
        checkBox.setChecked(false);
    }

    private void tv4Onclick() {
        tv_1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_accent));
        tv_4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
        amount = 2000;
        tv_1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        tv_4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_content1.setText(getAmtoficredits(amount) + " RMB(i)");
        tv_content2.setText(getFormulate(amount));
        tv_content3.setText(getSummary(amount));
        checkBox.setChecked(false);
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

    private String getFormulate(int cash) {
        String content = "(" + cash + "/" + iCreditsRecallRate + ")*100/1.1";
        return content;
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

    private String getSummary(int cash) {
        String content = "我完全明白并同意玛吉网服务条款的所有规则，以人民币" + cash;
        content += "元申购920专案的" + getAmtoficredits(cash) + " RMB(i)，承诺60分种内完成汇款程序。";
        return content;
    }

    private String getExpireDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);
        Date d = c.getTime();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        return dt1.format(d);
    }

    private String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return dt1.format(d.getTime());
    }

    private String getAmtoficredits(int cash) {
        String sc = String.valueOf(cash);
        String s100 = String.valueOf(100);
        String s1_1 = String.valueOf(1.1);
        //Double d=divide(multiply(divide(Double.parseDouble(String.valueOf(cash)),iCreditsRecallRate),Double.parseDouble(s100)),Double.parseDouble(s1_1));
        Double d = cash / iCreditsRecallRate * 100 / 1.1;
        return formatDoubleToString(d);
    }

    private void getPersonalBalance() {
        String METHOD_NAME = "get920PersonalBalance";
        String SOAP_ACTION = "http://www.maakki.com/get920PersonalBalance";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("maakki_id", maakki_id);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        //SoapObject balanceObject = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            //balanceObject = (SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;
            PersonalBalance = Double.parseDouble(result.getProperty(0).toString());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            errMsg = e.getMessage();
        }
        //balance=Double.parseDouble(balanceObject.getProperty(0).toString());
    }

    private class AsyncCallWS_getPersonalBalance extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            getPersonalBalance();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Toast.makeText(getApplicationContext(),"errMsg:"+errMsg,Toast.LENGTH_LONG).show();
            CheckOptionDisplay();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private class AsyncCallWS_get920Balance extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_get920Balance();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            AsyncCallWS_getPersonalBalance get920PersonalBalanceTask = new AsyncCallWS_getPersonalBalance();
            get920PersonalBalanceTask.execute();
            //CheckOptionDisplay(balance);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_get920Balance() {
        //Create request
        String METHOD_NAME = "get920Balance";
        String SOAP_ACTION = "http://www.maakki.com/get920Balance";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
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
        try {
            json_read = new JSONObject(tmp);
            errMsg = json_read.getString("errMsg");
            dailyAmount = json_read.getDouble("dailyAmount");
            balance = json_read.getDouble("balance");
        } catch (Exception e) {
        }/**/
        //nickname=soapPrimitive.toString();
    }

    private class AsyncCallWS_Order_imaimai extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_order_imaimai();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            act = "this";
            role_getTradeInfo = "mine";
            if (tradeID == 0) {
                showAlertDialog("申购失败", errMsg);
            } else {
                AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                get920TradeInfoTask.execute(role_getTradeInfo, act, String.valueOf(tradeID));
            }
        }

        @Override
        protected void onPreExecute() {
            RL_next.setVisibility(View.GONE);
            RL_bottom.setVisibility(View.VISIBLE);
            cash_option.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_order_imaimai() {
        String METHOD_NAME = "_920Trade";
        String SOAP_ACTION = "http://www.maakki.com/_920Trade";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "M@@kki.cc" + timeStamp.toString().trim() + String.valueOf(amount).trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("territory", territory_gps);
        request.addProperty("area", area_gps);
        request.addProperty("locality", locality_gps);
        request.addProperty("sublocality", sublocality_gps);
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("amount", amount);
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
        try {
            json_read = new JSONObject(tmp);
            errMsg = json_read.getString("errMsg");
            errCode = json_read.getString("errCode");
            tradeID = json_read.getInt("tradeID");
            tradeStatus = json_read.getInt("tradeStatus");
            iCredit = json_read.getDouble("iCredit");
        } catch (Exception e) {
        }/**/
        //nickname=soapPrimitive.toString();
    }

    private class AsyncCallWS_get920TradeInfo extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            String role = args[0];
            String act = args[1];
            int tradeid = Integer.parseInt(args[2]);
            getWebService_get920TradeInfo(role, act, tradeid);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (act.equals("this") || isSpecific) {
                DisplaySpecifiedOrder();
            } else {
                if (role_getTradeInfo.equals("mine")) {
                    if (tradeID_mine > 0) {
                        DisplaySpecifiedOrder();
                    } else {
                        RL_no920.setVisibility(View.VISIBLE);
                        RL_main.setVisibility(View.GONE);
                    }
                }else if (role_getTradeInfo.equals("cashier")) {
                    if (tradeID_cashier > 0) {
                        DisplaySpecifiedOrder();
                    } else {
                        RL_no920.setVisibility(View.VISIBLE);
                        RL_main.setVisibility(View.GONE);
                    }
                }else if (role_getTradeInfo.equals("admin")) {
                    if (tradeID_admin > 0) {
                        DisplaySpecifiedOrder();
                    } else {
                        RL_no920.setVisibility(View.VISIBLE);
                        RL_main.setVisibility(View.GONE);
                    }
                }else if (role_getTradeInfo.equals("imaimai")) {
                    if (tradeID_imaimai > 0) {
                        DisplaySpecifiedOrder();
                    } else {
                        RL_no920.setVisibility(View.VISIBLE);
                        RL_main.setVisibility(View.GONE);
                    }
                }
                //Toast.makeText(getApplicationContext(),"tradeID:"+tradeID_mine+"/"+tradeID_cashier+"/"+tradeID_admin+"/"+errMsg,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"trade_imaimai:"+tradeID_imaimai,Toast.LENGTH_LONG).show();
                //if (tradeID_mine > 0) {
                //ivList.setVisibility(View.VISIBLE);
                //if (maakki_id.equals("1") || maakki_id.equals("17141")) {
                //ivSetup.setVisibility(View.VISIBLE);
                //Save the cashier_account_no deposited last time
                //SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(),SharedPreferencesHelper.SharedPreferencesKeys.key18,cashier_account_no);
                //}
                //hasMore_mine = true;
                //} else {
                //hasMore_mine = false;
                //}
                //Toast.makeText(getApplicationContext(),"tradeID_mine:"+tradeID_mine,Toast.LENGTH_LONG).show();

/*                if (tradeID_cashier > 0) {
                    ivCashier.setVisibility(View.VISIBLE);
                    hasMore_cashier = true;
                } else {
                    hasMore_cashier = false;
                }
                if (tradeID_admin > 0) {
                    ivAdmin.setVisibility(View.VISIBLE);
                    hasMore_admin = true;
                } else {
                    hasMore_admin = false;
                }
                if (tradeID_imaimai > 0) {
                    ivImaimai.setVisibility(View.VISIBLE);
                    hasMore_imaimai = true;
                } else {
                    hasMore_imaimai = false;
                }*/

            }

        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"condition:"+condition,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_get920TradeInfo(String role, String act, int tradeid) {
        String METHOD_NAME = "get920TradeInfo";
        String SOAP_ACTION = "http://www.maakki.com/get920TradeInfo";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "M@@kki.cc" + timeStamp + String.valueOf(tradeid).trim() + act + role;
        //string encryptStr = maakki_id.ToString().Trim() + "M@@kki.cc" + timeStamp.ToString().Trim() + tradeID.ToString().Trim();

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("act", act);
        request.addProperty("role", role);
        request.addProperty("tradeID", tradeid);
        request.addProperty("condition", condition);
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
        try {
            json_read = new JSONObject(tmp);
            errMsg = json_read.getString("errMsg");
            errCode = json_read.getString("errCode");
            tradeID = json_read.getInt("trade_id");
            buyer_id = json_read.getString("buyer_id");
            trade_no = json_read.getString("trade_no");
            previous_tradeID = json_read.getInt("previous_tradeID");
            next_tradeID = json_read.getInt("next_tradeID");
            tradeStatus = json_read.getInt("trade_status");
            amount_record = json_read.getInt("amount");
            iCreditsRecallRate_record = json_read.getDouble("rRate");
            iCredit = json_read.getDouble("iCredit");
            create_date = json_read.getString("create_date");
            trade_date = json_read.getString("trade_date");
            refund_date = json_read.getString("refund_date");
            applyRefund_date = json_read.getString("applyRefund_date");
            referral_id = json_read.getInt("referral_id");
            leader3_id = json_read.getInt("leader3_id");
            leader2_id = json_read.getInt("leader2_id");
            leader1_id = json_read.getInt("leader1_id");
            cashier_id = json_read.getInt("cashier_id");
            cashier_bank_name = json_read.getString("cashier_bank_name");
            cashier_branch_name = json_read.getString("cashier_branch_name");
            cashier_account_no = json_read.getString("cashier_account_no");
            cashier_account_name = json_read.getString("cashier_account_name");
            cashierRemittedAccount = json_read.getString("cashierRemittedAccount");
            cashierRemittedName = json_read.getString("cashierRemittedName");
            cashierRemittedTime = json_read.getString("cashierRemittedTime");
            remittance_account = json_read.getString("remittance_account");
            remittance_name = json_read.getString("remittance_name");
            remittance_time = json_read.getString("remittance_time");
            refund_bank_name = json_read.getString("refund_bank_name");
            refund_branch_name = json_read.getString("refund_branch_name");
            refund_account_name = json_read.getString("refund_account_name");
            refund_account_no = json_read.getString("refund_account_no");
            territory = json_read.getString("territory");
            area = json_read.getString("area");
            locality = json_read.getString("locality");
            sublocality = json_read.getString("sublocality");
            isImaimaiGrant = json_read.getBoolean("isImaimaiGrant");
            location_id = json_read.getString("location_id");
        } catch (Exception e) {
            errMsg += e.getMessage();
        }

        if (act.equals("latest")) {
            if (role.equals("mine")) {
                role_getTradeInfo = "mine";
                if (tradeID > 0) {
                    tradeID_mine = tradeID;
                }
                cashier_account_no_latest = cashier_account_no;
                tradeStatus_mine = tradeStatus;
                //get referral_id
                String ref = "";
                if (referral_id > 0) {
                    ref = String.valueOf(referral_id);
                }
                prefs.putString("introducer_id", ref);
                prefs.commit();
            } else if (role.equals("cashier")) {
                if (tradeID > 0) {
                    tradeID_cashier = tradeID;
                }
            } else if (role.equals("admin")) {
                if (tradeID > 0) {
                    tradeID_admin = tradeID;
                }
            } else if (role.equals("imaimai")) {
                if (tradeID > 0) {
                    tradeID_imaimai = tradeID;
                }
            }
        }

    }

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox.isChecked()) {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_accent));
                //ivBack.setImageDrawable(getResources().getDrawable(R.drawable.baseline_done_white_18dp));
                RL_bottom.setVisibility(View.GONE);
                RL_next.setVisibility(View.VISIBLE);
                tv_content4.setVisibility(View.VISIBLE);
                tv_content5.setVisibility(View.VISIBLE);
            } else {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_warm_grey));
                //ivBack.setImageDrawable(getResources().getDrawable(R.drawable.baseline_reply_white_18dp));
                RL_bottom.setVisibility(View.VISIBLE);
                RL_next.setVisibility(View.GONE);
                tv_content4.setVisibility(View.GONE);
                tv_content5.setVisibility(View.GONE);
            }
        }

    };

    private void CheckOptionDisplay() {
        Double bal;
        if (balance > PersonalBalance) {
            bal = PersonalBalance;
        } else {
            bal = balance;
        }
        if (bal >= 100) {
            tv_1.setVisibility(View.VISIBLE);
            if (bal >= 500) {
                tv_2.setVisibility(View.VISIBLE);
                if (bal >= 1000) {
                    tv_3.setVisibility(View.VISIBLE);
                    if (bal >= 2000) {
                        tv_4.setVisibility(View.VISIBLE);
                    }
                }
            }
            tv_location.setVisibility(View.VISIBLE);
            RL_CB.setVisibility(View.VISIBLE);
            hasBalance = true;
        } else {
            tv_location.setVisibility(View.GONE);
            tv_content4.setText("今日额度已告用罄，明日请早！");
            tv_content4.setVisibility(View.VISIBLE);
            hasBalance = false;
        }
    }

    private void resetLocationCondition() {
        isArea = false;
        isLocality = false;
        isSublocality = false;
        setTvColorChange(true, tv_area);
        setTvColorChange(true, tv_locality);
        setTvColorChange(true, tv_sublocality);
        RL_condition_selected.setVisibility(View.GONE);
    }

    private void DisplaySpecifiedOrder() {
        isSpecific = true;
        if (errCode.equals("4")) {
            tv_content4.setText("查无资料！");
            tv_content4.setVisibility(View.VISIBLE);
            layout_order.setVisibility(View.GONE);
            next_option_admin.setVisibility(View.GONE);
            next_option_cashier.setVisibility(View.GONE);
            next_option_buyer.setVisibility(View.GONE);
            return;
        } else {
            tv_content4.setVisibility(View.GONE);
            layout_order.setVisibility(View.VISIBLE);
        }
        ll_Rule_imaimai.setVisibility(View.GONE);
        ll_NextStep.setVisibility(View.VISIBLE);
        ll_accentBar.setVisibility(View.GONE);
        layout_order.setVisibility(View.VISIBLE);
        layout_icreditrate.setVisibility(View.GONE);
        tv_content1.setVisibility(View.GONE);
        tv_content2.setVisibility(View.GONE);
        tv_location.setVisibility(View.GONE);
        tv_Exclamation.setVisibility(View.GONE);
        RL_bottom.setVisibility(View.VISIBLE);
        cash_option.setVisibility(View.GONE);
        RL_next.setVisibility(View.GONE);
        RL_CB.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        //SlideToAbove(ivBack);
        //decide icon
        if (role_getTradeInfo.equals("cashier")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp));
        } else if (role_getTradeInfo.equals("admin")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_face_black_18dp));
        } else if (role_getTradeInfo.equals("mine")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_search_black_18dp));
        } else if (role_getTradeInfo.equals("imaimai")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_android_black_18dp));
        }
        if (tradeID > 0) {
            tv_orderno.setVisibility(View.VISIBLE);
        } else {
            tv_orderno.setVisibility(View.INVISIBLE);
        }
        tv_orderno.setText(String.valueOf(trade_no) + " " + String.valueOf(tradeStatus));
        tv_ordertime.setText(create_date);
        strFeedback1 = "";
        strFeedback2 = "";
        if (role_getTradeInfo.equals("admin")) {
            strFeedback1 += "玛吉卡号：" + buyer_id + "\n";
        }
        strFeedback1 += "申购金额：" + amount_record + " RMB";
        if (!role_getTradeInfo.equals("cashier")) {
            strFeedback1 += "\n点回收率：" + iCreditsRecallRate_record + " %\n"
                    + "购得点数：" + iCredit + " RMB(i)";
        }

        if (!applyRefund_date.equals("")) {
            if (refund_date.equals("")) {
                tv_NextStepTime.setText(applyRefund_date);
            } else {
                tv_NextStepTime.setText(refund_date);
            }
        }
        //Toast.makeText(getApplicationContext(),"refund_date:"+refund_date,Toast.LENGTH_LONG).show();
        if (!role_getTradeInfo.equals("imaimai") & !role_getTradeInfo.equals("cashier") & (!sublocality.equals("") || !locality.equals(""))) {
            //strFeedback1 += "\n" + area + " " + locality + " " + sublocality+" ("+location_id+")";
            ll_LocationInfo.setVisibility(View.VISIBLE);
            tv_area.setText(area);
            tv_locality.setText(locality);
            if (!sublocality.equals("")) {
                tv_sublocality.setText(sublocality);
                tv_sublocality.setVisibility(View.VISIBLE);
            } else {
                tv_sublocality.setVisibility(View.GONE);
            }
        } else {
            ll_LocationInfo.setVisibility(View.GONE);
        }
        if (role_getTradeInfo.equals("admin")) {
            strFeedback1 += "\n介绍卡号：" + referral_id + "";
            strFeedback1 += "\n金流中心：" + cashier_id;
        }
                        /*+ "leader3_id:" + leader3_id + "\n"
                        + "leader2_id:" + leader2_id + "\n"
                        + "leader1_id:" + leader1_id + "\n"*/
        maakki_id_account = String.valueOf(cashier_id);
        strBankData =
                cashier_bank_name + "\n"
                        + cashier_branch_name + "\n"
                        + cashier_account_no + "\n"
                        + cashier_account_name;
        if (!remittance_account.equals("")) {
            strFeedback2 = "买家汇款信息：\n汇款账号：(末五码)" + remittance_account
                    + "\n汇款姓名：" + remittance_name;
            tv_RemittanceInfo.setText(strFeedback2);
            tv_RemittanceTime.setText(remittance_time);
            if (!role_getTradeInfo.equals("imaimai")) {
                ll_RemittanceInfo.setVisibility(View.VISIBLE);
            }
        } else {
            ll_RemittanceInfo.setVisibility(View.GONE);
        }
        tv_NextStepTime.setVisibility(View.GONE);
        //identify the subscriber & tradeStatus
        if (tradeStatus == 0) {
            if (role_getTradeInfo.equals("mine")) {
                tv_NextStep.setText("下一步：请即刻汇款到指定账号");
                if (!cashier_account_no_latest.equals("") & !cashier_account_no_latest.equals(cashier_account_no)) {
                    String str = "请注意这次交易指定的银行账号与上次不同，仔细看清楚，以免造成您不必要的困扰或损失！" + cashier_account_no + "/" + cashier_account_no_latest;
                    showAlertDialog("指定汇款的账号改变了", str);
                }
            } else {
                tv_NextStep.setText("等待买家汇款到以下指定账号后回报..");
            }
            tv_BankInfo.setText(strBankData);
            //ll_NextStep.setVisibility(View.VISIBLE);
            ll_BankInfo.setVisibility(View.VISIBLE);
            //买家可以取消订单或是汇报汇款
            tv_cancel.setVisibility(View.VISIBLE);
            tv_report.setText("回报汇款");
            tv_report.setVisibility(View.VISIBLE);
            tv_refund.setVisibility(View.GONE);
            //Cashier
            if (checkIftimeStampDelayed(create_date)) {
                tv_cancel_cashier.setVisibility(View.VISIBLE);
            } else {
                tv_cancel_cashier.setVisibility(View.GONE);
            }
            //showAlertDialog("确认时间","请判断订单时间:\n"+TimeStamp1);
            //Admin
            tv_cancel_admin.setVisibility(View.VISIBLE);
            tv_confirm_cashout_admin.setVisibility(View.GONE);
        } else if (tradeStatus == 10 || tradeStatus == 11 || tradeStatus == 12 || tradeStatus == 13) {
            if (role_getTradeInfo.equals("cashier") || role_getTradeInfo.equals("admin")) {
                strBankData = "金流中心汇款信息：\n汇款账号：(末五码)" + cashierRemittedAccount
                        + "\n汇款姓名：" + cashierRemittedName;
                tv_RemittanceTime_Cashier.setText(cashierRemittedTime);
                if (!(tradeStatus == 12 || tradeStatus == 13)) {
                    tv_BankInfo.setText(strBankData);
                    ll_BankInfo.setVisibility(View.VISIBLE);
                } else {
                    ll_BankInfo.setVisibility(View.GONE);
                }
            }
            //ll_BankInfo.setVisibility(View.GONE);
            tv_report.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            //买家退货
            if (!checkifRefunddateexpired(create_date)) {
                //tv_refund.setText(""+checkifRefunddateexpired(create_date));
                tv_refund.setVisibility(View.VISIBLE);
            } else {
                tv_refund.setVisibility(View.GONE);
            }
            //cashier
            tv_cancel_cashier.setVisibility(View.GONE);
            //admin
            if (role_getTradeInfo.equals("admin")) {
                String hint = "";
                if (tradeStatus == 10 || tradeStatus == 12) {
                    tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
                    hint = "已经拨点，请汇款给";
                } else {
                    tv_confirm_cashout_admin.setVisibility(View.GONE);
                    hint = "已经拨点并已确认汇款给";

                }
                if (isImaimaiGrant) {
                    hint += "爱买卖！";
                } else {
                    hint += "MGS！";
                }
                tv_NextStep.setText(hint);
            } else if (role_getTradeInfo.equals("imaimai")) {
                String hint = "";
                if (tradeStatus == 10 || tradeStatus == 12) {
                    tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
                    hint = "已经拨点，请静候汇款！";
                } else {
                    tv_confirm_cashout_admin.setVisibility(View.GONE);
                    hint = "已经拨点并已汇款，请确认！";

                }
                tv_NextStep.setText(hint);
            } else {
                tv_NextStep.setText("这笔申购交易已经完成！");
            }
            tv_cancel_admin.setVisibility(View.GONE);
        } else if (tradeStatus == 20 || tradeStatus == 21) {
            if (tradeStatus == 20) {
                tv_NextStep.setText("买家已申请退货！退款程序进行中..");
                if (role_getTradeInfo.equals("imaimai")) {
                    tv_NextStep.setText("买家已申请退货！请即刻汇款到以下银行账户！");
                }
            } else {
                tv_NextStep.setText("已完成退货退款程序！");
            }
            tv_NextStepTime.setVisibility(View.VISIBLE);
            tv_RemittanceTime_Cashier.setText(cashierRemittedTime);
            if (role_getTradeInfo.equals("imaimai")) {
                strBankData =
                        refund_bank_name + "\n"
                                + refund_branch_name + "\n"
                                + refund_account_no + "\n"
                                + refund_account_name;
                tv_BankInfo.setText(strBankData);
                ll_BankInfo.setVisibility(View.VISIBLE);
            } else {
                ll_BankInfo.setVisibility(View.GONE);
            }
            tv_RemittanceTime_Cashier.setVisibility(View.GONE);
            tv_report.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            tv_refund.setVisibility(View.GONE);
            //cashier
            tv_cancel_cashier.setVisibility(View.GONE);
            //admin
            tv_confirm_cashout_admin.setVisibility(View.GONE);
            tv_cancel_admin.setVisibility(View.GONE);
        } else if (tradeStatus == 40 || tradeStatus == 41 || tradeStatus == 42) {
            ll_BankInfo.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            tv_refund.setVisibility(View.GONE);
            tv_cancel_cashier.setVisibility(View.GONE);
            tv_confirm_cashout_admin.setVisibility(View.GONE);
            tv_NextStep.setText("已回报汇款信息，金流确认中..");
            if (tradeStatus == 40) {
                if (role_getTradeInfo.equals("cashier")) {
                    tv_NextStep.setText("买家已回报汇款信息，请您确认是否收款？");
                } else if (role_getTradeInfo.equals("admin")) {
                    tv_NextStep.setText("买家已回报汇款信息，金流中心确认是否收到汇款中..");
                }
                tv_report.setText("编辑汇款");
                tv_report.setVisibility(View.VISIBLE);
                tv_cancel_admin.setVisibility(View.GONE);
                tv_confirm_cashout_admin.setVisibility(View.GONE);
            } else if (tradeStatus == 41) {
                if (role_getTradeInfo.equals("cashier")) {
                    tv_NextStep.setText("已确认收款，请转汇到以下指定银行账户，并回报！");
                    maakki_id_account = "0";
                    role_getBankData = "cashier";
                    AsyncCallWS_getBankData _getBankDataTask = new AsyncCallWS_getBankData();
                    _getBankDataTask.execute();
                    ll_BankInfo.setVisibility(View.VISIBLE);
                } else if (role_getTradeInfo.equals("admin")) {
                    tv_NextStep.setText("金流中心已确认收款，等待回报汇款！）");
                }
                tv_report.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.GONE);
            } else if (tradeStatus == 42) {
                if (role_getTradeInfo.equals("cashier") || role_getTradeInfo.equals("admin")) {
                    strBankData = "金流中心汇款信息：\n汇款账号：(末五码)" + cashierRemittedAccount
                            + "\n汇款姓名：" + cashierRemittedName;
                    tv_RemittanceTime_Cashier.setText(cashierRemittedTime);
                    tv_BankInfo.setText(strBankData);
                    ll_BankInfo.setVisibility(View.VISIBLE);
                    if (role_getTradeInfo.equals("cashier")) {
                        tv_NextStep.setText("系统确认中..");
                    } else if (role_getTradeInfo.equals("admin")) {
                        tv_RemittanceInfo.setText("买家已经完成汇款");
                        tv_NextStep.setText("金流已回报汇款信息，请确定收款后，确认订单完成拨点！");
                    }
                }
                tv_report.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.VISIBLE);
            }
        } else if (tradeStatus > 49) {
            tv_report.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            tv_refund.setVisibility(View.GONE);
            tv_cancel_cashier.setVisibility(View.GONE);
            tv_cancel_admin.setVisibility(View.GONE);
            tv_confirm_cashout_admin.setVisibility(View.GONE);
            String msg = "";
            if (tradeStatus == 50) {
                msg = "在汇款前买方取消交易\n(交易成立后1小时内)";
            } else if (tradeStatus == 51) {
                msg = "在汇款前买方取消交易\n(交易成立后超过1小时)";
            } else if (tradeStatus == 52) {
                msg = "未依约于1小时内汇款\n(系统已取消本交易)";
            } else if ((tradeStatus == 53)) {
                msg = "取消交易，系统启动退款程序！";
            } else if ((tradeStatus == 54)) {
                msg = "买家完成汇后取消交易，金流处理退款程序中..";
            } else if ((tradeStatus == 55)) {
                msg = "取消交易，启动退款程序";
            }
            tv_NextStep.setText(msg);
            ll_NextStep.setVisibility(View.VISIBLE);
            ll_BankInfo.setVisibility(View.GONE);
        }
        //40:買家已回報匯款資訊
        if (tradeStatus == 20) {
            tv_refund_imaimai.setVisibility(View.VISIBLE);
        } else {
            tv_refund_imaimai.setVisibility(View.GONE);
        }
        //40:買家已回報匯款資訊
        if (tradeStatus == 40) {
            tv_cashin_cashier.setVisibility(View.VISIBLE);
        } else {
            tv_cashin_cashier.setVisibility(View.GONE);
        }
        //41:小金流確認已收到款
        if (tradeStatus == 41) {
            tv_report_cashier.setVisibility(View.VISIBLE);
        } else {
            tv_report_cashier.setVisibility(View.GONE);
        }
        //42:小金流已经汇款给系统款
        if (tradeStatus == 42) {
            tv_confirm_order_admin.setVisibility(View.VISIBLE);
        } else {
            tv_confirm_order_admin.setVisibility(View.GONE);
        }
        //tv_orderno.setText(trade_no);
        if (tradeStatus == 53) {
            tv_refund_admin.setVisibility(View.VISIBLE);
        } else {
            tv_refund_admin.setVisibility(View.GONE);
        }
        //54:完成匯款後取消交易，系统已退款給小金流
        if (tradeStatus == 54) {
            tv_refund_cashier.setVisibility(View.VISIBLE);
        } else {
            tv_refund_cashier.setVisibility(View.GONE);
        }
        String strErr = "";
        if (!errMsg.equals("")) {
            if (act.equals("this")) {
                strErr = "订单失效：" + errMsg;
            }
            strFeedback1 = "";
            tv_NextStep.setText(strErr);
            ll_NextStep.setVisibility(View.VISIBLE);
        }
        tv_ordermain.setText(strFeedback1);
        tv_content4.setVisibility(View.GONE);
        tv_content5.setVisibility(View.GONE);
        setSwipeNextPageBack();
        //会员功能
        if (role_getTradeInfo.equals("mine")) {
            LL_remittance_report_buyer.setVisibility(View.GONE);
            next_option_buyer.setVisibility(View.VISIBLE);
            //SlideToAbove(next_option_buyer);
            next_option_cashier.setVisibility(View.GONE);
            next_option_admin.setVisibility(View.GONE);
            next_option_imaimai.setVisibility(View.GONE);
            tv_confirm_report = (TextView) findViewById(R.id.tv_confirm_report);
            tv_cancel_report = (TextView) findViewById(R.id.tv_cancel_report);
            setCondition();
            tv_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("确认已经完成汇款");
                    alertDialog.setMessage("您确定已经利用手机银行或是其他方法完成转账到指定的银行账号，再进行本回报！\n\n请特别注意，如果您尚未完成转账，请勿往下进行！");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    /*if (!remittance_account.equals("")) {
                                        et_remittance_account.setText(remittance_account);
                                    }
                                    if (!remittance_name.equals("")) {
                                        et_remittance_name.setText(remittance_name);
                                    }*/
                                    ll_NextStep.setVisibility(View.GONE);
                                    ll_BankInfo.setVisibility(View.GONE);
                                    ivBack.setVisibility(View.GONE);
                                    LL_remittance_report_buyer.setVisibility(View.VISIBLE);
                                    next_option_buyer.setVisibility(View.GONE);
                                    role_getBankData = "cashier";
                                    maakki_id_account = maakki_id;
                                    AsyncCallWS_getBankData getBankDataTask = new AsyncCallWS_getBankData();
                                    getBankDataTask.execute();
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
            //买家确认送出汇款信息
            tv_confirm_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_text_ra = et_remittance_account.getText().toString().trim();
                    ed_text_rn = et_remittance_name.getText().toString().trim();
                    if (ed_text_ra.isEmpty() || ed_text_ra == null || ed_text_ra.length() == 0 || ed_text_ra.equals("") || ed_text_ra.length() != 5) {
                        String err = "请输入汇款帐号末5码";
                        if (ed_text_ra.length() > 0 && (5 - ed_text_ra.length() > 0)) {
                            err += "，少了" + String.valueOf(5 - ed_text_ra.length()) + "码！";
                        }
                        showAlertDialog("输入信息有误", err);
                    } else if (ed_text_rn.isEmpty() || ed_text_rn == null || ed_text_rn.length() == 0 || ed_text_rn.equals("")) {
                        showAlertDialog("输入信息有误", "请输入汇款帐号姓名");
                    } else {
                        AsyncCallWS_920remittanceUpdate _920remittanceUpdateTask = new AsyncCallWS_920remittanceUpdate();
                        _920remittanceUpdateTask.execute();
                    }
                }
            });
            tv_cancel_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_NextStep.setVisibility(View.VISIBLE);
                    if (tradeStatus == 0) {
                        ll_BankInfo.setVisibility(View.VISIBLE);
                    } else {
                        ll_BankInfo.setVisibility(View.GONE);
                    }
                    LL_remittance_report_buyer.setVisibility(View.GONE);
                    next_option_buyer.setVisibility(View.VISIBLE);
                    ivBack.setVisibility(View.VISIBLE);
                    //SlideToAbove(ivBack);
                }
            });

            //买家取消订单
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    role_execute = "oneself";
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("取消订单");
                    alertDialog.setMessage("您确定要取消这笔申购吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920cancelTrade _920cancelTradeTask = new AsyncCallWS_920cancelTrade();
                                    _920cancelTradeTask.execute();

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
            //买家退货
            tv_refund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("申请退货");
                    alertDialog.setMessage("申请退货将会停止您接下来30日的申购权利，您确定要退货吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920applyRefund _920applyRefundTask = new AsyncCallWS_920applyRefund();
                                    _920applyRefundTask.execute();
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
        } else if (role_getTradeInfo.equals("cashier")) {
            setCondition();
            LL_remittance_report_cashier.setVisibility(View.GONE);
            tv_confirm_report_cashier = (TextView) findViewById(R.id.tv_confirm_report_cashier);
            tv_cancel_report_cashier = (TextView) findViewById(R.id.tv_cancel_report_cashier);
            //金流回报汇款信息
            tv_report_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_NextStep.setVisibility(View.GONE);
                    ll_BankInfo.setVisibility(View.GONE);
                    LL_remittance_report_cashier.setVisibility(View.VISIBLE);
                    next_option_cashier.setVisibility(View.GONE);
                    ivBack.setVisibility(View.GONE);
                    //account_no_getBankData = "member";
                    role_getBankData = "cashier";
                    maakki_id_account = maakki_id;
                    AsyncCallWS_getBankData getBankDataTask = new AsyncCallWS_getBankData();
                    getBankDataTask.execute();
                    //取得云端汇款信息


                }
            });
            //金流确认送出汇款信息
            tv_confirm_report_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_text_ra_cashier = et_remittance_account_cashier.getText().toString().trim();
                    ed_text_rn_cashier = et_remittance_name_cashier.getText().toString().trim();
                    if (ed_text_ra_cashier.isEmpty() || ed_text_ra_cashier == null || ed_text_ra_cashier.length() == 0 || ed_text_ra_cashier.equals("") || ed_text_ra_cashier.length() != 5) {
                        String err = "请输入汇款帐号末5码";
                        if (ed_text_ra_cashier.length() > 0 && (5 - ed_text_ra_cashier.length() > 0)) {
                            err += "，少了" + String.valueOf(5 - ed_text_ra_cashier.length()) + "码！";
                        }
                        showAlertDialog("输入信息有误", err);
                    } else if (ed_text_rn_cashier.isEmpty() || ed_text_rn_cashier == null || ed_text_rn_cashier.length() == 0 || ed_text_rn_cashier.equals("")) {
                        showAlertDialog("输入信息有误", "请输入汇款帐号姓名");
                    } else {
                        AsyncCallWS_920cashierRemit _920cashierRemitTask = new AsyncCallWS_920cashierRemit();
                        _920cashierRemitTask.execute();
                    }
                }
            });
            tv_cancel_report_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_NextStep.setVisibility(View.VISIBLE);
                    ll_BankInfo.setVisibility(View.VISIBLE);
                    LL_remittance_report_cashier.setVisibility(View.GONE);
                    next_option_cashier.setVisibility(View.VISIBLE);
                    ivBack.setVisibility(View.VISIBLE);
                    //SlideToAbove(ivBack);
                }
            });
            next_option_buyer.setVisibility(View.GONE);
            next_option_cashier.setVisibility(View.VISIBLE);
            //SlideToAbove(next_option_cashier);
            next_option_admin.setVisibility(View.GONE);
            next_option_imaimai.setVisibility(View.GONE);
            tv_cashin_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("确认收款");
                    alertDialog.setMessage("您确定已经收到了这笔申购的汇款吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920cashierConfirm _920cashierConfirm = new AsyncCallWS_920cashierConfirm();
                                    _920cashierConfirm.execute();
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
            tv_cancel_cashier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    role_execute = "cashier";
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("取消订单");
                    alertDialog.setMessage("您确定要取消这笔申购吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920cancelTrade _920cancelTradeTask = new AsyncCallWS_920cancelTrade();
                                    _920cancelTradeTask.execute();
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

        } else if (role_getTradeInfo.equals("admin")) {
            next_option_buyer.setVisibility(View.GONE);
            next_option_cashier.setVisibility(View.GONE);
            next_option_admin.setVisibility(View.VISIBLE);
            //SlideToAbove(next_option_admin);
            next_option_imaimai.setVisibility(View.GONE);
            setCondition();
            tv_cancel_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    role_execute = "admin";
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("取消订单");
                    alertDialog.setMessage("您确定要取消这笔申购吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920cancelTrade _920cancelTradeTask = new AsyncCallWS_920cancelTrade();
                                    _920cancelTradeTask.execute();
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
            tv_confirm_order_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_BankInfo.setVisibility(View.VISIBLE);
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("确认订单，并拨点");
                    alertDialog.setMessage("您确定有收到来自金流中心的汇款吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_confirm920Trade confirm920TradeTask = new AsyncCallWS_confirm920Trade();
                                    confirm920TradeTask.execute();
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
            tv_confirm_cashout_admin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_confirm_cashout_admin.setVisibility(View.GONE);
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("确认汇款");
                    alertDialog.setMessage("您确定已经汇款给拨点单位了吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_RemitToiMaimai _RemitToiMaimaiTask = new AsyncCallWS_RemitToiMaimai();
                                    _RemitToiMaimaiTask.execute();
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
        } else if (role_getTradeInfo.equals("imaimai")) {
            next_option_imaimai.setVisibility(View.VISIBLE);
            //SlideToAbove(next_option_imaimai);
            next_option_buyer.setVisibility(View.GONE);
            next_option_cashier.setVisibility(View.GONE);
            next_option_admin.setVisibility(View.GONE);
            setCondition();
            tv_refund_imaimai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_refund_imaimai.setVisibility(View.GONE);
                    AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
                    alertDialog.setTitle("确认完成退款款");
                    alertDialog.setMessage("您确定已经退款给买家了吗？");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AsyncCallWS_920Refund _920RefundTask = new AsyncCallWS_920Refund();
                                    _920RefundTask.execute();
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
        }

    }

    private void setSwipeNextPageBack() {
        //next page
        layout_order.setOnTouchListener(swipeDetector);
        layout_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act = "this";
                if (swipeDetector.swipeDetected()) {
                    resetLocationCondition();
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                        tradeID = previous_tradeID;
                        if (tradeID > 0) {
                            AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                            get920TradeInfoTask.execute(role_getTradeInfo, act, String.valueOf(tradeID));
                        }
                    } else if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        tradeID = next_tradeID;
                        if (tradeID > 0) {
                            AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                            get920TradeInfoTask.execute(role_getTradeInfo, act, String.valueOf(tradeID));
                        }
                    }
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(false);
                setOrderView();
            }
        });
        if (previous_tradeID > 0) {
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetLocationCondition();
                    iv_left.setVisibility(View.INVISIBLE);
                    act = "this";
                    tradeID = previous_tradeID;
                    AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                    get920TradeInfoTask.execute(role_getTradeInfo, act, String.valueOf(tradeID));
                }
            });

        } else {
            iv_left.setVisibility(View.INVISIBLE);
        }
        if (next_tradeID > 0) {
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetLocationCondition();
                    iv_right.setVisibility(View.INVISIBLE);
                    act = "this";
                    tradeID = next_tradeID;
                    AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
                    get920TradeInfoTask.execute(role_getTradeInfo, act, String.valueOf(tradeID));
                }
            });
        } else {
            iv_right.setVisibility(View.INVISIBLE);
        }
    }

    private void setOrderView() {
        resetOriginal();
        isSpecific = false;
        RL_condition_status.setVisibility(View.GONE);
        RL_condition_day.setVisibility(View.GONE);
        RL_condition_cashier.setVisibility(View.GONE);
        RL_condition_selected.setVisibility(View.GONE);
        ll_Rule_imaimai.setVisibility(View.GONE);
        ll_accentBar.setVisibility(View.VISIBLE);
        LL_remittance_report_buyer.setVisibility(View.GONE);
        ll_NextStep.setVisibility(View.GONE);
        ll_BankInfo.setVisibility(View.GONE);
        next_option_buyer.setVisibility(View.GONE);
        next_option_cashier.setVisibility(View.GONE);
        next_option_admin.setVisibility(View.GONE);
        tv_Exclamation.setVisibility(View.GONE);
        tv_content1.setText(getAmtoficredits(amount) + " RMB(i)");
        tv_content2.setText(getFormulate(amount));
        tv_content3.setText(getSummary(amount));
        imgLogo.setVisibility(View.VISIBLE);

        ivQuestion.setVisibility(View.VISIBLE);
        imgLogo_imaimai.setVisibility(View.GONE);
        layout_icreditrate.setVisibility(View.VISIBLE);
        tv_content1.setVisibility(View.VISIBLE);
        tv_content2.setVisibility(View.VISIBLE);
        RL_CB.setVisibility(View.VISIBLE);
        RL_next.setVisibility(View.VISIBLE);
        tv_content4.setText(getApplicationContext().getResources().getString(R.string.imaimai_order_content4));

        ivList.setVisibility(View.VISIBLE);
        ivSetup.setVisibility(View.VISIBLE);
        if (isCashier) {
            ivCashier.setVisibility(View.VISIBLE);
        }
        if (isAdmin) {
            ivAdmin.setVisibility(View.VISIBLE);
        }
        if (isImaimai) {
            ivImaimai.setVisibility(View.VISIBLE);
        }
        if (checkBox.isChecked()) {
            tv_content4.setVisibility(View.VISIBLE);
            tv_content5.setVisibility(View.VISIBLE);
            RL_bottom.setVisibility(View.GONE);
        } else {
            tv_content4.setVisibility(View.GONE);
            tv_content5.setVisibility(View.GONE);
            RL_bottom.setVisibility(View.VISIBLE);
            cash_option.setVisibility(View.VISIBLE);
        }
        cash_option.setVisibility(View.VISIBLE);
        layout_order.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //如果今日额度已告用罄
        if (!hasBalance) {
            RL_CB.setVisibility(View.GONE);
            tv_content4.setText("今日额度已告用罄，明日请早！");
            tv_content4.setVisibility(View.VISIBLE);
            tv_location.setVisibility(View.GONE);
        } else {
            tv_location.setVisibility(View.VISIBLE);
        }
    }

    private class AsyncCallWS_920remittanceUpdate extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920remittanceUpdate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                strFeedback2 = "买家汇款信息：\n汇款账号：(末五码)" + ed_text_ra
                        + "\n汇款姓名：" + ed_text_rn;
                tv_RemittanceInfo.setText(strFeedback2);
                tv_RemittanceTime.setText(getCurrentTime());
                ll_RemittanceInfo.setVisibility(View.VISIBLE);
                LL_remittance_report_buyer.setVisibility(View.GONE);
                tv_report.setText("编辑汇款");
                tv_cancel.setVisibility(View.GONE);
                //tv_refund.setVisibility(View.VISIBLE);
                next_option_buyer.setVisibility(View.VISIBLE);
                tv_NextStep.setText("已回报汇款信息，请静候处理..");
                ll_NextStep.setVisibility(View.VISIBLE);
                ll_BankInfo.setVisibility(View.GONE);
                tradeStatus = 40;
                tv_orderno.setText(trade_no + " 40");
                ivBack.setVisibility(View.VISIBLE);
                //SlideToAbove(ivBack);
            } else {
                tv_NextStep.setText("回报汇款信息失败：" + errMsg);
            }

        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_920remittanceUpdate() {
        String METHOD_NAME = "_920remittanceUpdate";
        String SOAP_ACTION = "http://www.maakki.com/_920remittanceUpdate";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "M@@kki.cc" + timeStamp.toString().trim() + String.valueOf(tradeID).trim() + ed_text_ra;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
        request.addProperty("remittance_account", ed_text_ra);
        request.addProperty("remittance_name", ed_text_rn);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "汇款帐号超过5个字元";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "交易已完成并锁定，无法更改汇款资料";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货，不能更改汇款资讯";
            } else if (errCode.equals("7")) {
                errMsg = "交易处理中（金流中心已确认或正在回报给系统）";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消，无法更改汇款资料";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_920cancelTrade extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920cancelTrade();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String nextstep = "";
            if (errMsg.equals("")) {
                nextstep = "您已经取消了这笔交易！";
                ll_BankInfo.setVisibility(View.GONE);
                //如果是买家取消
                next_option_buyer.setVisibility(View.GONE);
                next_option_cashier.setVisibility(View.GONE);
                //买家，金流中心都已经完成汇款，系统可以决定取消订单《进行退款
                if (tradeStatus == 0) {
                    if (role_getTradeInfo.equals("mine")) {
                        tv_orderno.setText(trade_no);
                    } else {
                        tv_orderno.setText(trade_no + " 52");
                    }
                }
                if (tradeStatus == 42 & role_getTradeInfo.equals("admin")) {
                    tv_refund_admin.setVisibility(View.VISIBLE);
                    tv_orderno.setText(trade_no + " 53");
                }
                tv_cancel_admin.setVisibility(View.GONE);
                tv_confirm_cashout_admin.setVisibility(View.GONE);
            } else {
                nextstep = errMsg;
            }
            tv_NextStep.setText(nextstep);
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_920cancelTrade() {
        String METHOD_NAME = "_920cancelTrade";
        String SOAP_ACTION = "http://www.maakki.com/_920cancelTrade";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "M@@kki.cc" + timeStamp.toString().trim() + String.valueOf(tradeID).trim() + role_execute;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
        request.addProperty("role", role_execute);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "参数内容错误" + role_execute;
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "交易已完成并锁定，不能取消交易，只能做退货处理！";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货，不能取消交易！";
            } else if (errCode.equals("7")) {
                errMsg = "交易处理中，不能取消交易！";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消，无法再次取消交易！";
            } else if (errCode.equals("9")) {
                errMsg = "操作者不是此笔交易的金流中心;不能取消交易！";
            } else if (errCode.equals("10")) {
                errMsg = "操作者没有权限可以将此笔交易取消。（不具后台管理权限）";
            } else if (errCode.equals("11")) {
                errMsg = "金流中心不能取消60分钟内的交易！";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    public class AsyncCallWS_getBankData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_getBankData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.equals("")) {
                tv_BankInfo.setText("取得银行账户失败：" + errMsg);
            } else {
                strBankData =
                        bank_name_getBankData + "\n"
                                + branch_name_getBankData + "\n"
                                + account_no_getBankData + "\n"
                                + account_name_getBankData;
                tv_BankInfo.setText(strBankData);
                //ll_BankInfo.setVisibility(View.VISIBLE);
                if (role_getTradeInfo.equals("cashier")) {
                    tv_report_cashier.setVisibility(View.VISIBLE);
                    if (!account_no_getBankData.equals("")) {
                        et_remittance_account_cashier.setText(account_no_getBankData.substring(account_no_getBankData.length() - 5));
                    }
                    if (!account_name_getBankData.equals("")) {
                        et_remittance_name_cashier.setText(account_name_getBankData);
                    }
                } else if (role_getTradeInfo.equals("admin")) {
                    tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
                } else if (role_getTradeInfo.equals("mine")) {
                    tv_report.setVisibility(View.VISIBLE);
                    if (!account_no_getBankData.equals("")) {
                        et_remittance_account.setText(account_no_getBankData.substring(account_no_getBankData.length() - 5));
                    }
                    if (!account_name_getBankData.equals("")) {
                        et_remittance_name.setText(account_name_getBankData);
                    }
                }
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_getBankData() {
        String METHOD_NAME = "getBankData";
        String SOAP_ACTION = "http://www.maakki.com/getBankData";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id_account.trim() + "M@@kki.cc" + timeStamp.trim() + role_getBankData;
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id_account);
        request.addProperty("role", role_getBankData);
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
        try {
            json_read = new JSONObject(tmp);
            bank_name_getBankData = json_read.getString("bank_name");
            branch_name_getBankData = json_read.getString("branch_name");
            account_no_getBankData = json_read.getString("account_no");
            account_name_getBankData = json_read.getString("account_name");
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "参数内容错误" + role_getBankData;
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_920cashierRemit extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920cashierRemit();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                strFeedback2_cashier = "金流中心汇款信息：\n汇款账号：(末五码)" + ed_text_ra_cashier
                        + "\n汇款姓名：" + ed_text_rn_cashier;
                //tv_content6.setText(strFeedback1 + strFeedback2+strFeedback2_cashier);
                ll_BankInfo.setVisibility(View.VISIBLE);
                tv_BankInfo.setText(strFeedback2_cashier);
                tv_RemittanceTime_Cashier.setText(getCurrentTime());
                LL_remittance_report_cashier.setVisibility(View.GONE);
                //tv_report.setText("编辑汇款");
                //tv_cancel.setVisibility(View.GONE);
                //tv_refund.setVisibility(View.VISIBLE);
                tv_orderno.setText(trade_no + " 42");
                next_option_cashier.setVisibility(View.GONE);
                tv_NextStep.setText("已回报汇款信息，请静候处理..");
                ll_NextStep.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.VISIBLE);
                //SlideToAbove(ivBack);
            } else {
                tv_NextStep.setText("回报汇款信息失败：" + errMsg);
            }

        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_920cashierRemit() {
        String METHOD_NAME = "_920cashierRemit";
        String SOAP_ACTION = "http://www.maakki.com/_920cashierRemit";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(tradeID).trim() + ed_text_ra_cashier;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
        request.addProperty("remittance_account", ed_text_ra_cashier);
        request.addProperty("remittance_name", ed_text_rn_cashier);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "汇款帐号超过5个字元";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "交易已完成并锁定，无法更改汇款资料";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货，不能更改汇款资讯";
            } else if (errCode.equals("7")) {
                errMsg = "交易处理中（金流中心已确认或正在回报给系统）";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消，无法更改汇款资料";
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_920applyRefund extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920applyRefund();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("这笔交易已经申请退货！\n退款金额：" + refundAmt + "\n回收点数：" + recycle_iCredit + "\n爱买卖退款程序进行中..");
                ll_BankInfo.setVisibility(View.GONE);
                next_option_buyer.setVisibility(View.GONE);
            } else {
                tv_NextStep.setText(errMsg);
            }

        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_920applyRefund() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "_920applyRefund";
        String SOAP_ACTION = "http://www.maakki.com/_920applyRefund";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "M@@kki.cc" + timeStamp.toString().trim() + String.valueOf(tradeID).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            refundAmt = json_read.getString("refund");
            recycle_iCredit = json_read.getString("recycle_iCredit");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "Token认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "尚未绑定银行账户";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "已申请退货！";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货！";
            } else if (errCode.equals("7")) {
                errMsg = "交易处理中，不能退货！";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消！";
            } else if (errCode.equals("9")) {
                errMsg = "操作者不是此笔交易的买家，不能退货！";
            } else if (errCode.equals("10")) {
                errMsg = "已经超过退货期限！";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_920cashierConfirm extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920cashierConfirm();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("已经回报确认收款信息，请即刻转汇到以下指定银行账户，完成后请回报！");
                //取得云端汇款银行账户信息
                maakki_id_account = "0";
                role_getBankData = "cashier";
                tradeStatus = 41;
                tv_orderno.setText(trade_no + " 41");
                ll_BankInfo.setVisibility(View.VISIBLE);
                AsyncCallWS_getBankData _getBankDataTask = new AsyncCallWS_getBankData();
                _getBankDataTask.execute();

            } else {
                tv_NextStep.setText("回报确认收款信息失败：" + errMsg);
            }
            tv_cashin_cashier.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_920cashierConfirm() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "_920cashierConfirm";
        String SOAP_ACTION = "http://www.maakki.com/_920cashierConfirm";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(tradeID).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "买家尚未回报汇款信息！";
            } else {
                errMsg += errCode;
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }

    private class AsyncCallWS_confirm920Trade extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_confirm920Trade();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("已经确认这笔交易，请汇款给爱买卖以下指定的银行帐号，完成后请确认！");
                //取得云端汇款银行账户信息
                maakki_id_account = "17142";
                role_getBankData = "cashier";
                tradeStatus = 10;
                tv_orderno.setText(trade_no + " 10");
                AsyncCallWS_getBankData _getBankDataTask = new AsyncCallWS_getBankData();
                _getBankDataTask.execute();
                tv_confirm_order_admin.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.GONE);
                //下一步，汇款给爱买卖
                tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
                //ivBack.setVisibility(View.GONE);

            } else {
                tv_NextStep.setText("回报确认收款信息失败：" + errMsg);
                tv_confirm_order_admin.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_confirm920Trade() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "confirm920Trade";
        String SOAP_ACTION = "http://www.maakki.com/confirm920Trade";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(tradeID).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "权限不足！";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料！";
            } else if (errCode.equals("5")) {
                errMsg = "交易已确认并拨点，请勿重复确认！";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货！";
            } else if (errCode.equals("7")) {
                errMsg = "金流中心尚未汇款！";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消！";
            } else if (errCode.equals("9")) {
                errMsg = "爱买卖i点余额不足！";
            } else {
                errMsg += errCode;
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }

    private void getWebService_920Refund() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "_920Refund";
        String SOAP_ACTION = "http://www.maakki.com/_920Refund";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(tradeID).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "权限不足！";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料！";
            } else if (errCode.equals("5")) {
                errMsg = "这笔订单并未申请退货！";
            } else if (errCode.equals("6")) {
                errMsg = "已完成退货退款！";
            } else if (errCode.equals("7")) {
                errMsg = "交易进行中！";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消！";
            } else {
                errMsg += errCode;
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }

    private class AsyncCallWS_920Refund extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_920Refund();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("已经退款给买家了！");
                tv_confirm_cashout_admin.setVisibility(View.GONE);
                tradeStatus = 21;
                tv_orderno.setText(trade_no + " 21");
                //ivBack.setVisibility(View.VISIBLE);
            } else {
                tv_NextStep.setText("回报退款失败：" + errMsg);
                //tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_RemitToiMaimai() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "RemitToiMaimai";
        String SOAP_ACTION = "http://www.maakki.com/RemitToiMaimai";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(tradeID).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("tradeID", tradeID);
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
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "权限不足！";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料！";
            } else if (errCode.equals("5")) {
                errMsg = "已回报汇款给爱买卖，请勿重复！";
            } else if (errCode.equals("6")) {
                errMsg = "交易已退货！";
            } else if (errCode.equals("7")) {
                errMsg = "金流中心尚未汇款！";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消！";
            } else if (errCode.equals("9")) {
                errMsg = "尚未确认拨点！";
            } else {
                errMsg += errCode;
            }
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
    }

    private class AsyncCallWS_RemitToiMaimai extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_RemitToiMaimai();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("这笔交易已经完成汇款给爱买卖了！");
                tv_confirm_cashout_admin.setVisibility(View.GONE);
                tradeStatus = 11;
                tv_orderno.setText(trade_no + " 11");
                //ivBack.setVisibility(View.VISIBLE);
            } else {
                tv_NextStep.setText("确认汇款执行失败：" + errMsg);
                tv_confirm_cashout_admin.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void get920TradeList() {
        //Create request
        days = 60;

        String METHOD_NAME = "get920TradeList";
        String SOAP_ACTION = "http://www.maakki.com/get920TradeList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "M@@kki.cc" + timeStamp.toString().trim() + role_getTradeInfo + String.valueOf(days);
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role_getTradeInfo);
        request.addProperty("days", days);
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
            JSONArray jsonArray = json_read.getJSONArray("tradeList");
            errMsg = json_read.getString("errMsg");
            //將資料丟進JSONObject
            //接下來選擇型態使用get並填入key取值
            ArrayList<String> list = new ArrayList<String>();
            //JSONArray jsonArray = (JSONArray)jsonObject;
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.get(i).toString());
                }
            }/**/
            for (int i = 0; i < jsonArray.length(); i++) {
                _920TradeData t = new _920TradeData();
                JSONObject tradelistObj = jsonArray.getJSONObject(i);
                //Object property = tradelist.getProperty(i);
                //if (property instanceof SoapObject) {
                //SoapObject tradelistObj = (SoapObject) property;
                t.setTrade_id(tradelistObj.getInt("trade_id"));
                t.setMaakki_id(tradelistObj.getInt("maakki_id"));
                t.setNickname(tradelistObj.getString("nickname"));
                t.setTrade_status(tradelistObj.getInt("trade_status"));
                t.setAmount(tradelistObj.getInt("amount"));
                t.setrRate(tradelistObj.getDouble("rRate"));
                t.setiCredit(tradelistObj.getDouble("iCredit"));
                t.setCreate_date(tradelistObj.getString("create_date"));
                t.setTrade_date(tradelistObj.getString("trade_date"));
                t.setGrant_date(tradelistObj.getString("grant_date"));
                t.setCancel_date(tradelistObj.getString("cancel_date"));
                t.setApplyRefund_date(tradelistObj.getString("applyRefund_date"));
                t.setRefund_date(tradelistObj.getString("refund_date"));
                t.setReferral_id(tradelistObj.getInt("referral_id"));
                t.setLeader3_id(tradelistObj.getInt("leader3_id"));
                t.setLeader2_id(tradelistObj.getInt("leader2_id"));
                t.setLeader1_id(tradelistObj.getInt("leader1_id"));
                t.setCashier_id(tradelistObj.getInt("cashier_id"));
                t.setCashier_bank_name(tradelistObj.getString("cashier_bank_name"));
                t.setCashier_branch_name(tradelistObj.getString("cashier_branch_name"));
                t.setCashier_account_name(tradelistObj.getString("cashier_account_name"));
                t.setCashier_account_no(tradelistObj.getString("cashier_account_no"));
                t.setRemittance_account(tradelistObj.getString("remittance_account"));
                t.setRemittance_name(tradelistObj.getString("remittance_name"));
                t.setRemittance_time(tradelistObj.getString("remittance_time"));
                t.setTerritory(tradelistObj.getString("territory"));
                t.setArea(tradelistObj.getString("area"));
                t.setLocality(tradelistObj.getString("locality"));
                t.setSublocality(tradelistObj.getString("sublocality"));
                if (CheckifSeleted(t)) {
                    listTradeData.add(t);
                    selected_amt += tradelistObj.getInt("amount");
                }
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_get920TradeList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String condition=params[0];
            get920TradeList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("Calculated","selected_no:"+String.valueOf(listTradeData.size()+"\nselsected_amount:"+formatDoubleToString(Double.parseDouble(String.valueOf(String.valueOf(selected_amt))))));
            if (listTradeData.size() > 0) {
                tv_selected_no.setText(String.valueOf(listTradeData.size()));
                tv_selected_amount.setText(formatDoubleToString(Double.parseDouble(String.valueOf(String.valueOf(selected_amt)))));
                RL_condition_selected.setVisibility(View.VISIBLE);
            } else {
                RL_condition_selected.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPreExecute() {
            listTradeData = new ArrayList<>();
            RL_condition_selected.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private void getCityName() {
        Context c = getApplication();
        Geocoder geocoder;
        geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses;
        String strLat = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key7, "");
        String strLon = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key8, "");
        //Toast.makeText(this,"LatLong:"+strLat+"/"+strLon,Toast.LENGTH_LONG).show();
        if (!strLat.equals("") && !strLon.equals("")) {
            Double latitude = Double.parseDouble(strLat);
            Double longitude = Double.parseDouble(strLon);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                territory_gps = addresses.get(0).getCountryName();
                if (territory_gps == null) {
                    territory_gps = "";
                }
                area_gps = addresses.get(0).getAdminArea();
                if (area_gps == null) {
                    area_gps = "";
                }
                locality_gps = addresses.get(0).getLocality();
                if (locality_gps == null) {
                    locality_gps = "";
                }
                sublocality_gps = addresses.get(0).getSubLocality();
                if (sublocality_gps == null) {
                    sublocality_gps = "";
                }
                tv_location.setText(area_gps + " " + locality_gps + " " + sublocality_gps);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //return strCityName;
    }

    private boolean checkifRefunddateexpired(String datetime) {
        String str = "";
        boolean isExpired = false;
        TimeStamp1 = "NO VALUE";
        String str_date = datetime.split(" ")[0];
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Long expiration = 0l;
        Long intervaltime = 0l;
        try {
            Date date = (Date) formatter.parse(str_date);
            expiration = 30 * 24 * 60 * 60 * 1000l;
            intervaltime = new Date().getTime() - date.getTime();
            if (intervaltime > expiration) {
                isExpired = true;
            }
        } catch (Exception e) {
        }
        return isExpired;
        //return intervaltime+"/"+expiration;
    }

    private Boolean checkIftimeStampDelayed(String datetime) {
        boolean isDelayed = false;
        TimeStamp1 = "NO VALUE";
        Date date;
        String[] t = datetime.split(" ");
        int sec = Integer.parseInt(t[1].split(":")[2]);
        int min = Integer.parseInt(t[1].split(":")[1]);
        int hour = Integer.parseInt(t[1].split(":")[0]);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            date = (Date) formatter.parse(datetime);
            Long l = new Date().getTime() - date.getTime();
            if (l > 3600000) {
                isDelayed = true;
            }
            TimeStamp1 = formatter.format(new Date().getTime()) + "\n" + datetime + "\nhour:" + hour + "\nmin:" + min + "\nsec:" + sec + "\n" + new Date().getTime() + "\n" + date.getTime() + "\n" + String.valueOf(l) + "\n" + isDelayed;
        } catch (Exception e) {
        }
        return isDelayed;
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(imaimai_order.this).create();
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

    private void setCondition() {
        if (!role_getTradeInfo.equals("mine")) {
            RL_condition_status.setVisibility(View.VISIBLE);
            RL_condition_day.setVisibility(View.VISIBLE);
            if (role_getTradeInfo.equals("admin")) {
                RL_condition_cashier.setVisibility(View.VISIBLE);
                //RL_condition_selected.setVisibility(View.VISIBLE);
            }
        }
        tv_sublocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSublocality = setTvColorChange(isSublocality, tv_sublocality);
                if (isSublocality) {
                    isLocality = setTvColorChange(false, tv_locality);
                    isArea = setTvColorChange(false, tv_area);
                }
                executeLocation();
            }
        });
        tv_locality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSublocality) {
                    isLocality = setTvColorChange(isLocality, tv_locality);
                    if (isLocality) {
                        isArea = setTvColorChange(false, tv_area);
                    }
                    executeLocation();
                }
            }
        });
        tv_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSublocality & !isLocality) {
                    isArea = setTvColorChange(isArea, tv_area);
                    executeLocation();
                }
            }
        });
        tv_17142.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is17142 = setTvColorChange(is17142, tv_17142);
                executeCondition();
            }
        });

        tv_72338.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is72338 = setTvColorChange(is72338, tv_72338);
                executeCondition();
            }
        });
        tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                istoday = setTvColorChange(istoday, tv_today);
                executeCondition();
            }
        });
        tv_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isyesterday = setTvColorChange(isyesterday, tv_yesterday);
                executeCondition();
            }
        });
        tv_n50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isn50 = setTvColorChange(isn50, tv_n50);
                if (isn50) {
                    is0 = setTvColorChange(true, tv_0);
                    is10 = setTvColorChange(true, tv_10);
                    is11 = setTvColorChange(true, tv_11);
                    is12 = setTvColorChange(true, tv_12);
                    is13 = setTvColorChange(true, tv_13);
                    is20 = setTvColorChange(true, tv_20);
                    is40 = setTvColorChange(true, tv_40);
                    is41 = setTvColorChange(true, tv_41);
                    is42 = setTvColorChange(true, tv_42);
                    is50 = setTvColorChange(true, tv_50);
                    tv_0.setVisibility(View.GONE);
                    tv_10.setVisibility(View.GONE);
                    tv_11.setVisibility(View.GONE);
                    tv_12.setVisibility(View.GONE);
                    tv_13.setVisibility(View.GONE);
                    tv_20.setVisibility(View.GONE);
                    tv_40.setVisibility(View.GONE);
                    tv_41.setVisibility(View.GONE);
                    tv_42.setVisibility(View.GONE);
                    tv_50.setVisibility(View.GONE);
                } else {
                    tv_0.setVisibility(View.VISIBLE);
                    tv_10.setVisibility(View.VISIBLE);
                    tv_11.setVisibility(View.VISIBLE);
                    tv_12.setVisibility(View.VISIBLE);
                    tv_13.setVisibility(View.VISIBLE);
                    tv_20.setVisibility(View.VISIBLE);
                    tv_40.setVisibility(View.VISIBLE);
                    tv_41.setVisibility(View.VISIBLE);
                    tv_42.setVisibility(View.VISIBLE);
                    tv_50.setVisibility(View.VISIBLE);
                }
                executeCondition();
            }
        });
        tv_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is50) {
                    is50 = false;
                } else {
                    is50 = true;
                }
                if (is50) {
                    tv_50.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_50.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_50.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_50.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is42) {
                    is42 = false;
                } else {
                    is42 = true;
                }
                if (is42) {
                    tv_42.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_42.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_42.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_42.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is41) {
                    is41 = false;
                } else {
                    is41 = true;
                }
                if (is41) {
                    tv_41.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_41.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_41.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_41.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is40) {
                    is40 = false;
                } else {
                    is40 = true;
                }
                if (is40) {
                    tv_40.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_40.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_40.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_40.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is20) {
                    is20 = false;
                } else {
                    is20 = true;
                }
                if (is20) {
                    tv_20.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_20.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_20.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_20.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is13 = setTvColorChange(is13, tv_13);
                executeCondition();
            }
        });
        tv_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is12 = setTvColorChange(is12, tv_12);
                executeCondition();
            }
        });
        tv_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is11) {
                    is11 = false;
                } else {
                    is11 = true;
                }
                if (is11) {
                    tv_11.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_11.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_11.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_11.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is10) {
                    is10 = false;
                } else {
                    is10 = true;
                }
                if (is10) {
                    tv_10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    tv_10.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                } else {
                    tv_10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyscale3));
                    tv_10.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tv_border_grey));
                }
                executeCondition();
            }
        });
        tv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is0 = setTvColorChange(is0, tv_0);
                executeCondition();
            }
        });
    }

    private void executeCondition() {
        String strCondition_status = "";
        String strCondition_day = "";
        String strCondition_cashier = "";
        condition = "";
        List<String> list = new ArrayList<>();
        if (is0) {
            list.add("0");
        } else {
            list.remove("0");
        }
        if (is10) {
            list.add("10");
        } else {
            list.remove("10");
        }
        if (is11) {
            list.add("11");
        } else {
            list.remove("11");
        }
        if (is12) {
            list.add("12");
        } else {
            list.remove("12");
        }
        if (is13) {
            list.add("13");
        } else {
            list.remove("13");
        }
        if (is20) {
            list.add("20");
            list.add("21");
        } else {
            list.remove("20");
            list.remove("21");
        }
        if (is40) {
            list.add("40");
        } else {
            list.remove("40");
        }
        if (is41) {
            list.add("41");
        } else {
            list.remove("41");
        }
        if (is42) {
            list.add("42");
        } else {
            list.remove("42");
        }
        if (is50) {
            list.add("50");
            list.add("51");
            list.add("52");
            list.add("53");
            list.add("54");
            list.add("55");
        } else {
            list.remove("50");
            list.remove("51");
            list.remove("52");
            list.remove("53");
            list.remove("54");
            list.remove("55");
        }
        //following must be last
        if (isn50) {
            list.remove("50");
            list.remove("51");
            list.remove("52");
            list.remove("53");
            list.remove("54");
            list.remove("55");
            list.add("0");
            list.add("10");
            list.add("11");
            list.add("12");
            list.add("13");
            list.add("20");
            list.add("21");
            list.add("40");
            list.add("41");
            list.add("42");
        }
        for (int i = 0; i < list.size(); i++) {
            strCondition_status += list.get(i);
            if (i < list.size() - 1) {
                strCondition_status += ",";
            }
        }
        if (!strCondition_status.equals("")) {
            strCondition_status = "trade_status=" + strCondition_status;
        } else {
            strCondition_status = "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date d = c.getTime();
        if (isyesterday) {
            strCondition_day = "trade_date=" + df.format(d.getTime());
            if (istoday) {
                strCondition_day += "," + df.format(new Date().getTime());
            }
        } else {
            strCondition_day = "";
            if (istoday) {
                strCondition_day += "trade_date=" + df.format(new Date().getTime());
            }
        }
        //set strCondition_cashier
        List<String> list_cashier = new ArrayList<>();
        if (is17142) {
            list_cashier.add("17142");
        } else {
            list_cashier.remove("17142");
        }
        if (is72338) {
            list_cashier.add("72338");
        } else {
            list_cashier.remove("72338");
        }
        for (int i = 0; i < list_cashier.size(); i++) {
            strCondition_cashier += list_cashier.get(i);
            if (i < list_cashier.size() - 1) {
                strCondition_cashier += ",";
            }
        }
        if (!strCondition_cashier.equals("")) {
            strCondition_cashier = "cashier_id=" + strCondition_cashier;
        } else {
            strCondition_cashier = "";
        }
        //set Condition combination
        if (!strCondition_status.equals("")) {
            condition += strCondition_status;
        }
        if (!strCondition_cashier.equals("")) {
            if (!condition.equals("")) {
                condition += ";";
            }
            condition += strCondition_cashier;
        }
        if (!strCondition_day.equals("")) {
            if (!condition.equals("")) {
                condition += ";";
            }
            condition += strCondition_day;
        }

        act = "latest";
        //showAlertDialog("Condition",condition);
        AsyncCallWS_get920TradeInfo get920TradeInfoTask = new AsyncCallWS_get920TradeInfo();
        get920TradeInfoTask.execute(role_getTradeInfo, "latest", "0");
        //CalculateSeletedResult(condition);
        executeLocation();
    }

    private void executeLocation() {
        selected_amt = 0;
        AsyncCallWS_get920TradeList get920TradeListTask = new AsyncCallWS_get920TradeList();
        get920TradeListTask.execute();
    }

    private Boolean CheckifSeleted(_920TradeData td) {
        Boolean isSelected = false;
        //selected_no = 0;
        //selected_amt = 0;
        String[] conditions = condition.split(";");
        ArrayList<String> l_cashier = new ArrayList<>();
        ArrayList<String> l_tradestatus = new ArrayList<>();
        ArrayList<String> l_day = new ArrayList<>();
        for (int j = 0; j < conditions.length; j++) {
            if (conditions[j].split("=")[0].equals("cashier_id")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_cashier.add(con[k]);
                }
            }
            if (conditions[j].split("=")[0].equals("trade_date")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_day.add(con[k]);
                }
            }
            if (conditions[j].split("=")[0].equals("trade_status")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_tradestatus.add(con[k]);
                }
            }
        }
        boolean isSeleted_cashier = true;
        if (l_cashier.size() > 0) {
            isSeleted_cashier = false;
            for (int i = 0; i < l_cashier.size(); i++) {
                if (String.valueOf(td.getCashier_id()).equals(l_cashier.get(i))) {
                    isSeleted_cashier = true;
                }
            }
        }
        boolean isSeleted_tradestatus = true;
        if (l_tradestatus.size() > 0) {
            isSeleted_tradestatus = false;
            for (int i = 0; i < l_tradestatus.size(); i++) {
                if (String.valueOf(td.getTrade_status()).equals(l_tradestatus.get(i))) {
                    isSeleted_tradestatus = true;
                }
            }
        }
        boolean isSeleted_day = true;
        if (l_day.size() > 0) {
            isSeleted_day = false;
            for (int i = 0; i < l_day.size(); i++) {
                if (String.valueOf(td.getTrade_date().split(" ")[0]).equals(l_day.get(i))) {
                    isSeleted_day = true;
                }
            }
        }
        boolean isSelected_location = false;
        if (isArea) {
            if (area.equals(td.getArea())) {
                if (isLocality) {
                    if (locality.equals(td.getLocality())) {
                        if (isSublocality) {
                            if (sublocality.equals(td.getSublocality())) {
                                isSelected_location = true;
                            }
                        } else {
                            isSelected_location = true;
                        }
                    }
                } else {
                    isSelected_location = true;
                }
            }
        } else {
            isSelected_location = true;
        }
        if (isSeleted_cashier & isSeleted_tradestatus & isSeleted_day & isSelected_location) {
            isSelected = true;
        } else {
            isSelected = false;
        }
        return isSelected;
    }

    private void resetOriginal() {
        cashier_account_no_latest = "";
        tradeStatus_mine = 9;
        RL_condition_selected.setVisibility(View.GONE);
        isArea = setTvColorChange(true, tv_area);
        isLocality = setTvColorChange(true, tv_locality);
        isSublocality = setTvColorChange(true, tv_sublocality);
        is72338 = setTvColorChange(true, tv_72338);
        is17142 = setTvColorChange(true, tv_17142);
        ;
        istoday = setTvColorChange(true, tv_today);
        isyesterday = setTvColorChange(true, tv_yesterday);
        ;
        is40 = setTvColorChange(true, tv_40);
        ;
        is0 = setTvColorChange(true, tv_0);
        ;
        is10 = setTvColorChange(true, tv_10);
        is11 = setTvColorChange(true, tv_11);
        is12 = setTvColorChange(true, tv_12);
        is13 = setTvColorChange(true, tv_13);
        is20 = setTvColorChange(true, tv_20);
        is41 = setTvColorChange(true, tv_41);
        is42 = setTvColorChange(true, tv_42);
        is50 = setTvColorChange(true, tv_50);
        isn50 = setTvColorChange(true, tv_n50);
        condition = "";

        //Toast.makeText(getApplicationContext(),"condition:"+condition,Toast.LENGTH_LONG).show();

    }

    public void SlideToAbove(final View view) {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                3.2f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.clearAnimation();
                /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_footer.getWidth(), rl_footer.getHeight());
                lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_footer.setLayoutParams(lp);*/
                view.setVisibility(View.VISIBLE);
            }
        });

    }
    public void SlideToDown(final View view) {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 3.2f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.clearAnimation();
                /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_footer.getWidth(), rl_footer.getHeight());
                lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_footer.setLayoutParams(lp);*/
                view.setVisibility(View.INVISIBLE);
            }
        });

    }

}