package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class addSponsor extends Activity {
    private SharedPreferences.Editor prefs;
    private String Sponsor_target_Maakkiid, role_validIAReferral, ed_text_ra_cashier, ed_text_rn_cashier, strFeedback2, role_execute, ed_text_ra, ed_text_rn, strBankData, bank_name_getBankData, branch_name_getBankData, account_no_getBankData, account_name_getBankData, maakki_id_account, role_getBankData, cashier_account_no_latest, maakki_account, nickname, confirm_date, cellphone, applicant_mName, ignoreCancelLock, isUpgrade, cancel_date, cashierRemittedTime, cashierRemittedName, cashierRemittedAccount, remittance_time, remittance_name, remittance_account, cashier_account_name, cashier_account_no, cashier_branch_name, cashier_bank_name, apply_date, apply_no, condition, act, role, territory, area, locality, sublocality,
            area_GPS = "", maakki_id, timeStamp, errCode, errMsg, identifyStr, phoneAreaCode, VerificationCode;
    private int applyID_cashier, applyID_mine, applyID_admin, cashier_id, apply_status, next_applyID, previous_applyID, apply_id, referral_id, applicant_id, location_id;
    private CheckBox checkBox, checkBox_anonymous,checkbox_friendlimited,checkbox_silent;
    private RelativeLayout rl_footer, RL_done, RL_condition_status, RL_condition_day, RL_condition_cashier, RL_condition_selected,RL_CB_silent,RL_CB_friendlimited, RL_CB, RL_bottom;
    private LinearLayout LL_NoData, ll_LocationInfo, LL_remittance_report_cashier, LL_remittance_report_buyer, ll_RemittanceInfo, ll_BankInfo, LL_addSponsor, LL_Application, ll_NextStep, next_option_buyer, next_option_cashier, next_option_admin;
    private ImageView ivAlly, iv_clear, ivTarget, iv_submit, iv_back;
    private TextView tv_envelope_piece, tv_greeting, tv_envelope_no, tv_piece, tv_date, tv_checkrules, tv_targetStr, tv_target, tv_nickname, tv_currency, tv_17142, tv_72338, tv_yesterday, tv_today, tv_0, tv_10, tv_n50, tv_40, tv_41, tv_42, tv_50,
            tv_selected_no, tv_area, tv_locality, tv_sublocality, tv_cashin_cashier, tv_confirm_order_admin, tv_orderno, tv_RemittanceTime, tv_RemittanceTime_Cashier, tv_cancel, tv_cancel_cashier, tv_cancel_admin, tv_RemittanceInfo, tv_report, tv_report_cashier, tv_BankInfo, tv_NextStepTime, tv_NextStep, tv_location, tv_result, tv_getVerificode;
    private EditText et_slogan, et_envelope_no, et_iCredits, et_target, et_password, et_password2, et_phonenumber, et_verificode, et_introducer;
    private boolean isDown, hasChangedtoUSD, isTarget, isAnonymous,isSilent,isFriendlimited, isSpecific, is12, is50, is42, is10, is11, is20, is40, is41, is72338, istoday, isyesterday, isArea, isLocality, isSublocality, isn50, is0, is17142, hasPasswordChecked, hasPhonenumberChecked, hasVericode, hasIntroducerChecked;
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebServiceIA.asmx";
    private final String URL_WS = StaticVar.webURL+ "WebService.asmx";
    private List<Friend> listFriend;    private double erUSD = 1l, erRMB = 1l, erHKD = 1l, erMYR = 1l, erJPY = 1l, memberFG = 0l, actualSponsorAmt = 0l, iCredit = 0l;
    private String picFileID, member_id, strCurrency, default_sponsoriCredit, targetStr, target_maakki_id, currency = "RMB";
    private String redUrl =  StaticVar.webURL+"MCoins/MCoinsQuery.aspx";
    private List<exchangeRate> rateArr;
    //1:指定赞助 2:顺序赞助 3:红包赞助
    private int sponsorType = 1, envelope_no = 1;
    private Context context;
    private redEnvelopeMasterDAO rEDAO;
    private redEnvelope_Master redEnvelope_master;
    private FriendDAO friendDAO;
    private int REQUEST_CODE=99;
    private ActivityScoreRecord activityscorerecord;
    private ActivityScoreRecordDAO asrDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addsponsor);
        // Hiding the action bar
        getActionBar().hide();
        context = this;
        friendDAO =new FriendDAO(context);
        redEnvelope_master = new redEnvelope_Master();
        activityscorerecord=new ActivityScoreRecord();
        asrDAO=new ActivityScoreRecordDAO(context);
        rateArr = new ArrayList<>();
        tv_targetStr = findViewById(R.id.tv_targetStr);

        AsyncCallWS_getExchangeRate getExchangeRateTask = new AsyncCallWS_getExchangeRate();
        getExchangeRateTask.execute();
        strCurrency = currency;
        isAnonymous = false;
        isFriendlimited=false;
        isSilent=false;
        isTarget = true;
        checkBox_anonymous = findViewById(R.id.checkbox_anonymous);
        checkBox_anonymous.setOnCheckedChangeListener(chklistener_anonymous);
        //
        checkbox_friendlimited= findViewById(R.id.checkbox_friendlimited);
        checkbox_friendlimited.setOnCheckedChangeListener(chklistener_friendlimited);
        //
        checkbox_silent= findViewById(R.id.checkbox_silent);
        checkbox_silent.setOnCheckedChangeListener(chklistener_silent);
        //isSpecific = false;
        LL_addSponsor = findViewById(R.id.LL_addSponsor);
        LL_Application = findViewById(R.id.LL_Application);
        ivTarget = findViewById(R.id.ivTarget);
        iv_clear = findViewById(R.id.iv_clear);
        tv_target = findViewById(R.id.tv_target);
        tv_envelope_no = findViewById(R.id.tv_envelope_no);
        et_envelope_no = findViewById(R.id.et_envelope_no);
        tv_envelope_piece = findViewById(R.id.tv_envelope_piece);
        tv_piece = findViewById(R.id.tv_piece);
        tv_greeting = findViewById(R.id.tv_greeting);
        et_slogan = findViewById(R.id.et_greeting);
        et_slogan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_slogan.setHint("");
                }
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_slogan.setText("");
                et_slogan.setHint("恭喜发财，平安喜乐！");
            }
        });
        ivTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (sponsorType) {
                    case 1:
                        //切换成红包赞助
                        sponsorType = 3;
                        break;
                    case 2:
                        //切换成红包赞助
                        sponsorType = 3;
                        break;
                    case 3:
                        //切换成指定赞助
                        sponsorType = 1;
                        break;
                }
                setView(sponsorType);
            }
        });
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_iCredits = findViewById(R.id.et_iCredits);
        et_iCredits.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_iCredits.getText().toString().isEmpty()) {
                        if (Double.parseDouble(et_iCredits.getText().toString().trim()) > iCredit) {
                            showAlertDialog("赞助点数过多", "超过您目前所能支配的点数");
                        } else if (!checkIfiCreditSetCorrectly()) {
                            if (sponsorType < 3) {
                                showAlertDialog("赞助点数过少", "赞助点数必须至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                            } else {
                                showAlertDialog("赞助点数过少", "赞助点数必须每个红包平均至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                            }
                        }
                    }
                }
            }
        });
        et_envelope_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (et_envelope_no.getText().toString().trim().isEmpty()) {
                        showAlertDialog("请填入红包数目", "请填入你要发出的红包数目！");
                    } else if (et_iCredits.getText().toString().trim().isEmpty()) {
                        showAlertDialog("请填入赞助点数", "赞助点数必须每个红包平均至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                    } else if (!checkIfiCreditSetCorrectly()) {
                        showAlertDialog("平均每个红包点数过少", "赞助点数每个红包平均必须至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                    }
                }
            }
        });
        tv_currency = findViewById(R.id.tv_currency);
        tv_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currency.equals("USD")) {
                    if (hasChangedtoUSD) {
                        hasChangedtoUSD = false;
                        tv_currency.setText(currency + "(i)");
                        strCurrency = currency;
                    } else {
                        hasChangedtoUSD = true;
                        tv_currency.setText("USD(i)");
                        strCurrency = "USD";
                    }
                }
            }
        });
        et_target = findViewById(R.id.et_target);
        tv_nickname = findViewById(R.id.tv_nickname);
        et_target.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (sponsorType == 1 & !hasFocus & !et_target.getText().toString().isEmpty()) {
                    AsyncCallWS_getMemberFG getMemberFGTask = new AsyncCallWS_getMemberFG();
                    getMemberFGTask.execute();
                }
                //showAlertDialog("parameters","sposorType:"+sponsorType+"\nhasFocus:"+hasFocus+"\net_target:"+et_target.getText().toString().isEmpty());
            }
        });

        et_password = findViewById(R.id.et_password);
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_password.getText().toString().trim().isEmpty()) {
                        AsyncCallWS_check_iCreditPassword check_iCreditPasswordTask = new AsyncCallWS_check_iCreditPassword();
                        check_iCreditPasswordTask.execute();
                    } else {
                        showAlertDialog("请输入转点密码", "您忘了输入转点密码！");
                    }
                }
            }
        });
        RL_CB = findViewById(R.id.RL_CB);
        rl_footer = findViewById(R.id.rl_footer);
        isDown = false;
        RL_bottom = findViewById(R.id.RL_bottom);
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(chklistener);
        tv_checkrules = findViewById(R.id.tv_checkrules);
        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_iCredits.getText().toString().trim().isEmpty()) {
                    if (!et_password.getText().toString().trim().isEmpty()) {
                        if (sponsorType == 3) {
                            if (et_envelope_no.getText().toString().trim().isEmpty()) {
                                showAlertDialog("请输入红包数目", "您选择的是发红包赞助，请输入红包数目！");
                                checkBox.setChecked(false);
                                return;
                            }
                        }
                        if (checkIfiCreditSetCorrectly()) {
                            checkBox.setChecked(true);
                        } else if (sponsorType < 3) {
                            showAlertDialog("赞助点数过少", "赞助点数必须至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                        } else {
                            showAlertDialog("赞助点数过少", "赞助点数必须每个红包平均至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                        }
                    } else {
                        showAlertDialog("请输入转点密码", "您忘了输入转点密码！");
                        checkBox.setChecked(false);
                    }
                } else {
                    showAlertDialog("请输入赞助点数", "请输入您所要赞助的点数！");
                    checkBox.setChecked(false);
                }
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                }
            }
        });
        RelativeLayout RL_CB_anonymous = (RelativeLayout) findViewById(R.id.RL_CB_anonymous);
        RL_CB_anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox_anonymous.isChecked()) {
                    checkBox_anonymous.setChecked(false);
                } else {
                    checkBox_anonymous.setChecked(true);
                }
            }
        });
        RL_CB_friendlimited = (RelativeLayout) findViewById(R.id.RL_CB_friendlimited);
        RL_CB_friendlimited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_friendlimited.isChecked()) {
                    checkbox_friendlimited.setChecked(false);
                } else {
                    checkbox_friendlimited.setChecked(true);
                }
            }
        });
        RL_CB_silent = (RelativeLayout) findViewById(R.id.RL_CB_silent);
        RL_CB_silent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_silent.isChecked()) {
                    checkbox_silent.setChecked(false);
                } else {
                    checkbox_silent.setChecked(true);
                }
            }
        });
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_iCredits.getText().toString().length() > 0) {
                    if (!et_password.getText().toString().trim().isEmpty()) {
                        if (checkBox.isChecked()) {
                            iv_submit.setVisibility(View.GONE);
                            if (sponsorType < 3) {
                                if (sponsorType == 1 & et_target.getText().toString().trim().isEmpty()) {
                                    showAlertDialog("请输入指定赞助对象", "您选择的是指定赞助模式，请选择您要指定的赞助对象卡号！");
                                    iv_submit.setVisibility(View.VISIBLE);
                                } else {
                                    AsyncCallWS_MDFSponsor MDFSponsorTask = new AsyncCallWS_MDFSponsor();
                                    MDFSponsorTask.execute();
                                    if (sponsorType == 1) {
                                        SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key20, et_target.getText().toString().trim());
                                    }
                                }
                            } else {
                                //Do the things what redEnvelope mode need to do
                                if (sponsorType == 3) {
                                    if (et_envelope_no.getText().toString().trim().isEmpty()) {
                                        showAlertDialog("请输入红包数目", "您选择的是发红包赞助，请输入红包数目！");
                                        checkBox.setChecked(false);
                                        iv_submit.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                }
                                if (checkIfiCreditSetCorrectly()) {
                                    AsyncCallWS_check_iCreditPassword3 check_iCreditPasswordTask3 = new AsyncCallWS_check_iCreditPassword3();
                                    check_iCreditPasswordTask3.execute();
                                } else {
                                    showAlertDialog("赞助点数过少", "赞助点数必须每个红包平均至少" + getMinAmt(strCurrency) + " " + strCurrency + "(i)");
                                    checkBox.setChecked(false);
                                    iv_submit.setVisibility(View.VISIBLE);
                                }
                            }
                            SharedPreferencesHelper.putSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key21, hasChangedtoUSD);
                            SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key22, et_iCredits.getText().toString().trim());
                            SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key23, String.valueOf(sponsorType));
                        } else {
                            showAlertDialog("请确认明白并同意服务条款", "确定已经详读并完整明白且同意玛吉网众筹规则与服务条款的所有规则后，请点选灰色文字方块。");
                        }
                    } else {
                        showAlertDialog("请输入转点密码", "您忘了输入转点密码！");
                    }
                } else {
                    showAlertDialog("请输入赞助点数", "请输入您所要赞助的点数！");
                }
            }
        });
        RL_done = (RelativeLayout) findViewById(R.id.RL_done);
        ImageView iv_result = (ImageView) findViewById(R.id.iv_result);
        iv_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WebMain2.class);
                intent.putExtra("redirUrl", redUrl);
                startActivity(intent);
            }
        });
        tv_date = (TextView) findViewById(R.id.tv_date);
        ImageView iv_nextRE = (ImageView) findViewById(R.id.iv_nextRE);
        iv_nextRE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_done.setVisibility(View.GONE);
                LL_addSponsor.setVisibility(View.VISIBLE);
            }
        });
        ImageView iv_Prev = (ImageView) findViewById(R.id.iv_Prev);
        iv_Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreNotificationList.class);
                startActivity(intent);
            }
        });
    }

    private void getRedEnvelope() {
        //1
        redEnvelope_master.setCreateDate(new Date().getTime());
        String greetings = "";
        if (!et_slogan.getText().toString().trim().isEmpty()) {
            greetings = et_slogan.getText().toString().trim();
        } else {
            greetings = "恭喜发财，平安喜乐！";
        }
        ;
        //2
        redEnvelope_master.setSlogan(greetings);
        //redEnvelope_master.setReceiver_no(Integer.parseInt(et_envelope_no.getText().toString().trim()));
        //3
        redEnvelope_master.setReceiver_no(envelope_no);
        //4
        redEnvelope_master.setCurrency(strCurrency);
        //5
        redEnvelope_master.setDonateAmt(Double.parseDouble(et_iCredits.getText().toString().trim()));
        //6
        redEnvelope_master.setTakenAmt(0.0);
        //7
        redEnvelope_master.setIsAverage(true);
        //8
        redEnvelope_master.setIsAnonymous(checkBox_anonymous.isChecked());
        //9
        redEnvelope_master.setIsFinish(false);
        redEnvelope_master.setIsIsFriendLimited(isFriendlimited);

        redEnvelope_master.setiCreditPassword(et_password.getText().toString().trim());
        redEnvelopeMasterDAO rEDAO = new redEnvelopeMasterDAO(this);
        Long envelope_id = rEDAO.insert(redEnvelope_master).getId();

        String sponsorAmt = formatDoubleToString(Double.parseDouble(et_iCredits.getText().toString().trim()) / envelope_no);
        //Toast.makeText(context,"isFriendlimited:"+isFriendlimited,Toast.LENGTH_LONG).show();
        //if isSilent=true;none receive notification
        int score=Integer.parseInt(et_iCredits.getText().toString())*10000;
        if(isFriendlimited){
            activityscorerecord.setScore(score);
        }else{
            activityscorerecord.setScore(score*10);
        }
        activityscorerecord.setScoreType(1);
        activityscorerecord.setCreateTime(new Date().getTime());
        asrDAO.insert(activityscorerecord);
        if(!isSilent){
            if(isFriendlimited){
                ArrayList L = new ArrayList();
                L=friendDAO.getFriendMemberList();
                SignalRUtil.sendRedenvelope_friendlimited(L,isAnonymous, maakki_id, String.valueOf(envelope_id), sponsorAmt, strCurrency, getCityName(), greetings);/**/

            }else {
                //String mMemID=SharedPreferencesHelper.getSharedPreferencesString(context,SharedPreferencesHelper.SharedPreferencesKeys.key1,"0");
                //String mName=SharedPreferencesHelper.getSharedPreferencesString(context,SharedPreferencesHelper.SharedPreferencesKeys.key2,"0");
                SignalRUtil.sendRedenvelope(isAnonymous, maakki_id, String.valueOf(envelope_id), sponsorAmt, strCurrency, getCityName(), greetings);
            }
        }
        LL_addSponsor.setVisibility(View.GONE);
        RL_done.setVisibility(View.VISIBLE);
        /*redEnvelope_Master newRE=rEDAO.get(envelope_id);
        String cMemID="90";
        boolean isFriend=false;
        if(friendDAO.getbyMemberID(Integer.parseInt(cMemID))!=null){
            if(friendDAO.getbyMemberID(Integer.parseInt(cMemID)).getFriendType().equals("F")){
                isFriend=true;
            }
        }
        String mess="id:"+newRE.getId()+
                "\niCredit:"+newRE.getDonateAmt()+
                "\nisFriendLimited:"+newRE.getIsFriendLimited()+
                "\nisFinished:"+newRE.getIsFriendLimited()+
                "\nFriendType:"+isFriend+
                "\nisSilent:"+isSilent;
        showAlertDialog("Redenvelope details",mess);*/
        //int count=rEDAO.getCount();
        //Toast.makeText(getApplicationContext(), "赞助点数：" + actualSponsorAmt + " USD(i)", Toast.LENGTH_LONG).show();
        targetStr =
                //envelope_id+"("+count+")+
                "您的这笔赞助：" + et_iCredits.getText().toString().trim() + " " + strCurrency + "(i)\n必须等到有人领取，才会生效。";
        tv_targetStr.setText(targetStr);
        tv_date.setText(getCurrentTime());
        iv_submit.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RL_done.setVisibility(View.GONE);
        et_password.setText("");
        LL_addSponsor.setVisibility(View.VISIBLE);
        //finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBox.setChecked(false);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        AsyncCallWS_getiCreditBalance getiCreditBalanceTask = new AsyncCallWS_getiCreditBalance();
        getiCreditBalanceTask.execute();
        Sponsor_target_Maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key20, "");
        sponsorType = Integer.parseInt(SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key23, "1"));
        default_sponsoriCredit = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key22, "");
        if (sponsorType == 3) {
            if (!default_sponsoriCredit.isEmpty()) {
                et_iCredits.setText(default_sponsoriCredit);
            }
        }
        setView(sponsorType);
        //Limit the iv_submit Buttoon Appear after 6:00
        TimeZone china = TimeZone.getTimeZone("GMT+08:00");
        Calendar cal = Calendar.getInstance(china);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if ((hour < 6)) {
            iv_submit.setVisibility(View.GONE);
        } else {
            iv_submit.setVisibility(View.VISIBLE);
            SlideToAbove();
        }
        SignalRUtil.setHubConnect(context);
        setivAlly();
    }

    private void setivAlly() {
        ivAlly = (ImageView) findViewById(R.id.ivAlly);
        String[] PiId = getResources().getStringArray(R.array.Project_involved_Makki_id);
        int count = PiId.length;
        Boolean isPi = false;
        for (int i = 0; i < count; i++) {
            if (PiId[i].equals(maakki_id)) {
                isPi = true;
            }
        }
        if (isPi) {
            ivAlly.setVisibility(View.VISIBLE);
        } else {
            ivAlly.setVisibility(View.GONE);
        }
        ivAlly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(addSponsor.this,AllyList.class);
                startActivity(i);
            }
        });
    }

    private void setView(int sponsorType) {
        switch (sponsorType) {
            case 2:
                //sponsorType = 2;
                ivTarget.setImageDrawable(getResources().getDrawable(R.drawable.baseline_autorenew_black_18dp));
                iv_submit.setImageDrawable(getResources().getDrawable(R.drawable.baseline_autorenew_white_18dp));
                tv_target.setVisibility(View.GONE);
                et_target.setVisibility(View.GONE);
                tv_nickname.setVisibility(View.GONE);
                RL_CB_friendlimited.setVisibility(View.GONE);
                RL_CB_silent.setVisibility(View.GONE);
                break;
            case 3:
                //切换成红包赞助
                //sponsorType = 3;
                ivTarget.setImageDrawable(getResources().getDrawable(R.drawable.reicon));
                iv_submit.setImageDrawable(getResources().getDrawable(R.drawable.baseline_mail_white_18dp));
                tv_target.setVisibility(View.GONE);
                et_target.setVisibility(View.GONE);
                tv_nickname.setVisibility(View.GONE);
                tv_envelope_no.setVisibility(View.VISIBLE);
                et_envelope_no.setVisibility(View.VISIBLE);
                tv_piece.setVisibility(View.VISIBLE);
                tv_greeting.setVisibility(View.VISIBLE);
                et_slogan.setVisibility(View.VISIBLE);
                iv_clear.setVisibility(View.VISIBLE);
                RL_CB_friendlimited.setVisibility(View.VISIBLE);
                RL_CB_silent.setVisibility(View.VISIBLE);
                break;
            case 1:
                //切换成指定赞助
                //sponsorType = 1;
                ivTarget.setImageDrawable(getResources().getDrawable(R.drawable.baseline_face_black_18dp));
                iv_submit.setImageDrawable(getResources().getDrawable(R.drawable.baseline_face_white_18dp));
                tv_target.setVisibility(View.VISIBLE);
                et_target.setVisibility(View.VISIBLE);
                tv_nickname.setVisibility(View.VISIBLE);
                tv_envelope_no.setVisibility(View.GONE);
                et_envelope_no.setVisibility(View.GONE);
                tv_piece.setVisibility(View.GONE);
                tv_greeting.setVisibility(View.GONE);
                et_slogan.setVisibility(View.GONE);
                iv_clear.setVisibility(View.GONE);
                RL_CB_friendlimited.setVisibility(View.GONE);
                RL_CB_silent.setVisibility(View.GONE);
                break;
        }
    }

    private CheckBox.OnCheckedChangeListener chklistener_anonymous = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox_anonymous.isChecked()) {
                isAnonymous = true;
            } else {
                isAnonymous = false;
            }
        }
    };
    private CheckBox.OnCheckedChangeListener chklistener_friendlimited = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkbox_friendlimited.isChecked()) {
                if(friendDAO.getFriendMemberList().size()>0){
                    isFriendlimited = true;
                }else{
                    checkbox_friendlimited.setChecked(false);
                    AlertDialog_nofriend();
                }
            } else {
                isFriendlimited = false;
                checkbox_friendlimited.setChecked(false);
            }
        }
    };

    private CheckBox.OnCheckedChangeListener chklistener_silent= new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkbox_silent.isChecked()) {
                isSilent=true;
            } else {
                isSilent = false;
            }
        }
    };

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox.isChecked()) {
                AsyncCallWS_check_iCreditPassword2 check_iCreditPasswordTask = new AsyncCallWS_check_iCreditPassword2();
                check_iCreditPasswordTask.execute();
                if (isDown) {
                    SlideToAbove();
                    isDown = false;
                }
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
            } else {
                SlideToDown();
                isDown = true;
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
            }
        }
    };

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(addSponsor.this).create();
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

    private void getMemberFG() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getMemberFG";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        target_maakki_id = et_target.getText().toString().trim();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = target_maakki_id.trim() + "MDF-M@@kki.cc" + timeStamp.trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", target_maakki_id);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
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
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            }
            memberFG = json_read.getDouble("memberFG");
            nickname = json_read.getString("nickname");
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_getMemberFG extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getMemberFG();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                double sponsorUSD = getSponsorUSD(Double.parseDouble(et_iCredits.getText().toString().trim()));
                //double sponsorUSD=Double.parseDouble(et_iCredits.getText().toString().trim());
                tv_nickname.setText(nickname);
                if (sponsorUSD > memberFG) {
                    showAlertDialog("您赞助的点数过多", "超过" + nickname + "(" + target_maakki_id + ")目前建立的FG(众筹目标)：" + memberFG + "USD(i)");
                }
                AsyncCallWS_getMemberProfile getMemberProfileTask = new AsyncCallWS_getMemberProfile();
                getMemberProfileTask.execute();
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

    private class AsyncCallWS_getMemberProfile extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getMemberProfile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("errorMsg","errCode:"+errCode);
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
        String METHOD_NAME = "getMemberProfile";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = target_maakki_id + "Community-M@@kki.cc" + timeStamp.trim();
        //target_maakki_id = et_target.getText().toString().trim();
        //String target_maakki_id=et_target.getText().toString().trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("mid", target_maakki_id);
        request.addProperty("isMemberID", false);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
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
                errMsg = "查无资料";
            }
            member_id = json_read.getString("member_id");
            nickname = json_read.getString("nickname");
            picFileID = json_read.getString("picFileID");
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    private void setExchangeRate() {
        for (int i = 0; i < rateArr.size(); i++) {
            if (rateArr.get(i).getCurrency().equals("USD")) {
                erUSD = rateArr.get(i).getRate();
            } else if (rateArr.get(i).getCurrency().equals("RMB")) {
                erRMB = rateArr.get(i).getRate();
            } else if (rateArr.get(i).getCurrency().equals("HKD")) {
                erHKD = rateArr.get(i).getRate();
            } else if (rateArr.get(i).getCurrency().equals("JPY")) {
                erJPY = rateArr.get(i).getRate();
            } else if (rateArr.get(i).getCurrency().equals("MYR")) {
                erMYR = rateArr.get(i).getRate();
            }
        }
    }

    private double getSponsorUSD(double sponsor) {
        double sponsor_usd;
        //Toast.makeText(getApplicationContext(),"erUSD:"+erUSD+"/"+currency,Toast.LENGTH_LONG).show();
        if (strCurrency.equals("NTD")) {
            sponsor_usd = sponsor / erUSD;
        } else if (strCurrency.equals("RMB")) {
            sponsor_usd = multiply(sponsor, erRMB) / erUSD;
        } else if (strCurrency.equals("HKD")) {
            sponsor_usd = multiply(sponsor, erHKD) / erUSD;
        } else if (strCurrency.equals("MYR")) {
            sponsor_usd = multiply(sponsor, erMYR) / erUSD;
        } else if (strCurrency.equals("JPY")) {
            sponsor_usd = multiply(sponsor, erJPY) / erUSD;
        } else {
            sponsor_usd = sponsor;
        }
        //sponsor_usd=sponsor;
        return sponsor_usd;
    }

    private String getMinAmt(String currency) {
        Double MinAm;
        //Toast.makeText(getApplicationContext(),"erUSD:"+erUSD+"/"+currency,Toast.LENGTH_LONG).show();
        if (currency.equals("NTD")) {
            MinAm = erUSD;
        } else if (currency.equals("RMB")) {
            MinAm = erUSD / erRMB;
        } else if (currency.equals("HKD")) {
            MinAm = erUSD / erHKD;
        } else if (currency.equals("MYR")) {
            MinAm = erUSD / erMYR;
        } else if (currency.equals("JPY")) {
            MinAm = erUSD / erJPY;
        } else {
            MinAm = 1.0;
        }
        return formatDoubleToString(MinAm);
    }

    private Boolean checkIfiCreditSetCorrectly() {
        boolean isCorrect = true;
        if (!et_iCredits.getText().toString().trim().isEmpty()) {
            if (sponsorType < 3) {
                if (getSponsorUSD(Double.parseDouble(et_iCredits.getText().toString().trim())) < 1.0) {
                    isCorrect = false;
                }
            } else {
                if (!et_envelope_no.getText().toString().trim().isEmpty()) {
                    if (Integer.parseInt(et_envelope_no.getText().toString().trim()) > 0) {
                        envelope_no = Integer.parseInt(et_envelope_no.getText().toString().trim());
                    }
                    if (getSponsorUSD(Double.parseDouble(et_iCredits.getText().toString().trim())) / envelope_no < 1.0) {
                        isCorrect = false;
                    }
                }
            }
        } else {
            isCorrect = false;
        }
        /*if(!isCorrect){
            String mess="sponsorType"+sponsorType+
                    "\nicredits:"+et_iCredits.getText().toString().trim()
                    +"\nenvelope_no:"+envelope_no
                    +"\nstrCurrency:"+strCurrency
                    +"\ngetSponsorUSD:"+getSponsorUSD(Double.parseDouble(et_iCredits.getText().toString().trim()));
            showAlertDialog("error",mess);
        }*/
        return isCorrect;
    }

    private void getiCreditBalance() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getiCreditBalance";
        //String SOAP_ACTION =  NAMESPACE + METHOD_NAME;
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);
        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
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
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            }
            iCredit = json_read.getDouble("iCredit");
            currency = json_read.getString("currency");

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_getiCreditBalance extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getiCreditBalance();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                hasChangedtoUSD = SharedPreferencesHelper.getSharedPreferencesBoolean(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key21, false);
                if (hasChangedtoUSD) {
                    strCurrency = "USD";
                } else {
                    strCurrency = currency;
                }
                tv_currency.setText(strCurrency + "(i)");
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

    private void getExchangeRate() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getExchangeRate";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        //Create HTTP call object
        errMsg = "";
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);
        SoapPrimitive soapPrimitive = null;
        try {

            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
            //String tmp = soapPrimitive.toString();
            String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
            //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
            tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
            JSONObject json_read;
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("rateArr");
            for (int i = 0; i < jsonArray.length(); i++) {
                exchangeRate er = new exchangeRate();
                JSONObject listObj = jsonArray.getJSONObject(i);
                er.setCurrency(listObj.getString("currency"));
                er.setRate(listObj.getDouble("rate"));
                rateArr.add(er);
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }

    }

    private class AsyncCallWS_getExchangeRate extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getExchangeRate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            /*if(errMsg.isEmpty()){
                Toast.makeText(getApplicationContext(),"ER:"+rateArr.size(),Toast.LENGTH_LONG).show();
            }else{
                showAlertDialog("Access Error",errMsg);
            }*/
            setExchangeRate();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private String getSponsorCurrency(String su) {
        double sponsor = Double.parseDouble(su);
        double sponsor_currency = 0l;
        if (currency.equals("NTD")) {
            sponsor_currency = multiply(sponsor, erUSD);
        } else if (currency.equals("RMB")) {
            sponsor_currency = multiply(sponsor, erUSD) / erRMB;
        } else if (currency.equals("HKD")) {
            sponsor_currency = multiply(sponsor, erUSD) / erHKD;
        } else if (currency.equals("MYR")) {
            sponsor_currency = multiply(sponsor, erUSD) / erMYR;
        } else if (currency.equals("JPY")) {
            sponsor_currency = multiply(sponsor, erUSD) / erJPY;
        } else {
            sponsor_currency = sponsor;
        }
        //tv_currency.setText(sponsor_currency+"/"+erUSD);
        return String.valueOf(sponsor_currency);
    }

    private class AsyncCallWS_check_iCreditPassword extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            check_iCreditPassword();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.isEmpty()) {
                showAlertDialog("转点密码输入错误", errMsg);
            }
        }

        @Override
        protected void onPreExecute() {
            //RL_next.setVisibility(View.GONE);
            //RL_bottom.setVisibility(View.VISIBLE);
            //cash_option.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private class AsyncCallWS_check_iCreditPassword2 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            check_iCreditPassword();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.isEmpty()) {
                showAlertDialog("转点密码错误", errMsg);
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class AsyncCallWS_check_iCreditPassword3 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            check_iCreditPassword();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.isEmpty()) {
                showAlertDialog("转点密码错误", errMsg);
                checkBox.setChecked(false);
                iv_submit.setVisibility(View.VISIBLE);
            } else {
                //发送红包
                getRedEnvelope();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void check_iCreditPassword() {
        String METHOD_NAME = "check_iCreditPassword";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        //String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String iCreditPassword = et_password.getText().toString().trim();
        String encryptStr = maakki_id + "MDF-M@@kki.cc" + timeStamp.trim() + iCreditPassword;
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("iCreditPassword", iCreditPassword);
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);

        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
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
                errMsg = "请再次确认你所输入的密码是否正确无误！";
            }
        } catch (Exception e) {
        }
    }

    private class AsyncCallWS_MDFSponsor extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            MDFSponsor();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.isEmpty()) {
                showAlertDialog("赞助失败", errMsg + "(" + errCode + ")");
            } else {
                LL_addSponsor.setVisibility(View.GONE);
                RL_done.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(), "赞助点数：" + actualSponsorAmt + " USD(i)", Toast.LENGTH_LONG).show();
                targetStr = "赞助点数：" + formatDoubleToString(actualSponsorAmt) + " USD(i)\n\n" + targetStr;
                tv_targetStr.setText(targetStr);
                tv_date.setText(getCurrentTime());
                if (sponsorType == 1) {
                    //Toast.makeText(context,"member_id:"+member_id,Toast.LENGTH_LONG).show();
                    SignalRUtil.setHubConnect(context);
                    SignalRUtil.sendSponsorNoticeToSomeone(member_id, formatDoubleToString(actualSponsorAmt) + " USD(i):" + isAnonymous);
                }
            }
            iv_submit.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //RL_next.setVisibility(View.GONE);
            //RL_bottom.setVisibility(View.VISIBLE);
            //cash_option.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dt1.format(d.getTime());
    }


    private void MDFSponsor() {
        String METHOD_NAME = "MDFSponsor";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String sponsorAmt = et_iCredits.getText().toString().trim();
        /*if (hasChangedtoUSD) {
            sponsorAmt = getSponsorCurrency(et_iCredits.getText().toString().trim());
        }*/
        String target_id = "0";
        if (sponsorType != 2 & (!et_target.getText().toString().trim().isEmpty())) {
            target_id = et_target.getText().toString().trim();
        }
        ;
        String iCreditPassword = et_password.getText().toString().trim();
        String encryptStr = maakki_id + "MDF-M@@kki.cc" + timeStamp.trim() + target_id + sponsorAmt + iCreditPassword;
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("target_id", target_id);
        request.addProperty("sponsorAmt", sponsorAmt);
        request.addProperty("isUSD", hasChangedtoUSD);
        request.addProperty("iCreditPassword", iCreditPassword);
        request.addProperty("isAnonymous", String.valueOf(isAnonymous));
        request.addProperty("timeStamp", timeStamp);
        request.addProperty("identifyStr", identifyStr);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);

        SoapPrimitive soapPrimitive = null;
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
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
                errMsg = "MDF开放赞助时间为：北京时间 06:00~24:00 !";
            } else if (errCode.equals("4")) {
                errMsg = "查无赞助者卡号）";
            } else if (errCode.equals("5")) {
                errMsg = "请先设定转点密码！";
            } else if (errCode.equals("6")) {
                errMsg = "转点密码错误";
            } else if (errCode.equals("7")) {
                errMsg = "赞助点数必须大于1USD(i)";
            } else if (errCode.equals("8")) {
                errMsg = "i点余额不足";
            } else if (errCode.equals("9")) {
                errMsg = "指定赞助对象尚未建立FG(众筹目标)";
            } else if (errCode.equals("10")) {
                errMsg = "超过今日可赞助额度";
            } else if (errCode.equals("11")) {
                errMsg = "今日所有的FG(众筹目标)均已被满足";
            } else if (errCode.equals("12")) {
                errMsg = "可赞助的FG(众筹目标)不足！";
            } else if (errCode.equals("13")) {
                errMsg = "系统繁忙中，请稍后再行赞助！";
            } else if (errCode.equals("14")) {
                errMsg = "系统繁忙中，请稍后再行赞助！";
            } else if (errCode.equals("15")) {
                errMsg = "您没有参与MDF的权限";
            } else if (errCode.equals("16")) {
                errMsg = "请勿指定自己为赞助对象";
            }
            actualSponsorAmt = json_read.getDouble("actualSponsorAmt");
            targetStr = json_read.getString("targetStr");
        } catch (Exception e) {
        }

    }

    private String formatDoubleToString(Double d) {
        DecimalFormat df;
        BigDecimal a = new BigDecimal(d.toString());
        if (d > 10) {
            df = new DecimalFormat(",###,##0");
        } else {
            df = new DecimalFormat(",###,##0.00");
        }
        df.format(a);
        return df.format(a);
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


    public void SlideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                3.2f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        rl_footer.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rl_footer.clearAnimation();
                /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_footer.getWidth(), rl_footer.getHeight());
                lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_footer.setLayoutParams(lp);*/
                rl_footer.setVisibility(View.VISIBLE);
            }
        });

    }

    public void SlideToDown() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 3.2f);
        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        rl_footer.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_footer.clearAnimation();
                /*RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_footer.getWidth(), rl_footer.getHeight());
                lp.setMargins(0, rl_footer.getWidth(), 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rl_footer.setLayoutParams(lp);*/
                rl_footer.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getCityName() {
        String strCityName = "";
        Context c = getApplication();
        GPSTracker gps = new GPSTracker(c);
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses;
        String strLat = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key7, "");
        String strLon = SharedPreferencesHelper.getSharedPreferencesString(c, SharedPreferencesHelper.SharedPreferencesKeys.key8, "");
        if (gps.canGetLocation & !strLat.equals("") && !strLon.equals("")) {
            Double latitude = Double.parseDouble(strLat);
            Double longitude = Double.parseDouble(strLon);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.get(0).getLocality() != null) {
                    strCityName += addresses.get(0).getLocality();
                }
                //Toast.makeText(context,"State/City:"+stateName+"/"+cityName,Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (Exception e) {

            }
        }
        return strCityName;
    }
    private void AlertDialog_nofriend(){
        AlertDialog alertDialog = new AlertDialog.Builder(addSponsor.this).create();
        alertDialog.setTitle("无法启动好友名单");
        alertDialog.setMessage("现在就先建立建立好友列表？");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "好的",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent i=new Intent(addSponsor.this,FriendList.class);
                        //startActivity(i);
                        //startActivityForResult(i,REQUEST_CODE);
                        AsyncCallWS_getFriend getFriendTask=new AsyncCallWS_getFriend();
                        getFriendTask.execute();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "再说",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            friendDAO=new FriendDAO(context);
            Toast.makeText(context,"I'm back",Toast.LENGTH_LONG).show();
        }
    }*/
    private class AsyncCallWS_getFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            getFriend();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for(int i=0;i<listFriend.size();i++){
                friendDAO.insert(listFriend.get(i));
            }
            if(listFriend.size()>0){
                ServiceUtil.showToast(context,"已经建立了好友名单");
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
    private void getFriend() {
        listFriend=new ArrayList<>();
        String METHOD_NAME = "getFriend";
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String MemID = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        String timeStamp = String.valueOf(new Date().getTime());
        String getType="All";
        String friend_id = "0";
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
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            errMsg += e.getMessage();
        }
        //String tmp = soapPrimitive.toString();
        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();

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
                        listFriend.add(f);
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
}
