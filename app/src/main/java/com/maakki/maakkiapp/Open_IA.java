package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class Open_IA extends Activity {
    private SharedPreferences.Editor prefs;
    private String role_validIAReferral,ed_text_ra_cashier,ed_text_rn_cashier,strFeedback2,role_execute,ed_text_ra,ed_text_rn,strBankData, bank_name_getBankData, branch_name_getBankData, account_no_getBankData, account_name_getBankData, maakki_id_account, role_getBankData, cashier_account_no_latest, maakki_account, nickname, confirm_date, cellphone, applicant_nickname, ignoreCancelLock, isUpgrade, cancel_date, cashierRemittedTime, cashierRemittedName, cashierRemittedAccount, remittance_time, remittance_name, remittance_account, cashier_account_name, cashier_account_no, cashier_branch_name, cashier_bank_name, apply_date, apply_no, condition, act, role, territory, area, locality, sublocality,
            area_GPS="", locality_GPS="", sublocality_GPS="", maakki_id, timeStamp, errCode, errMsg, identifyStr, phoneAreaCode, VerificationCode;
    private int applyID_cashier, applyID_mine, applyID_admin, cashier_id, apply_status, next_applyID, previous_applyID, apply_id, referral_id, applicant_id, location_id;
    private CheckBox checkBox;
    private RelativeLayout RL_condition_status,RL_condition_day,RL_condition_cashier,RL_condition_selected,RL_CB, RL_bottom;
    private LinearLayout LL_NoData,ll_LocationInfo,LL_remittance_report_cashier,LL_remittance_report_buyer,ll_RemittanceInfo,ll_BankInfo,LL_ApplyNew, LL_Application,ll_NextStep,next_option_buyer,next_option_cashier,next_option_admin;
    private ImageView ivBack,ivCashier, ivList, ivAdmin, iv_submit, iv_back;
    private TextView tv_17142,tv_72338,tv_yesterday,tv_today,tv_0,tv_10,tv_n50,tv_40,tv_41,tv_42,tv_50,
            tv_selected_no,tv_area,tv_locality,tv_sublocality,tv_cashin_cashier,tv_confirm_order_admin,tv_orderno,tv_RemittanceTime,tv_RemittanceTime_Cashier ,tv_cancel,tv_cancel_cashier,tv_cancel_admin,tv_RemittanceInfo, tv_report, tv_report_cashier, tv_BankInfo, tv_NextStepTime, tv_NextStep, tv_location, tv_result, tv_getVerificode;
    private EditText et_account, et_nickname, et_password, et_password2, et_phonenumber, et_verificode, et_introducer;
    private boolean isSpecific,is12,is50,is42,is10,is11,is20,is40,is41,is72338,istoday,isyesterday,isArea,isLocality,isSublocality,isn50,is0, is17142,hasPasswordChecked, hasPhonenumberChecked, hasVericode, hasIntroducerChecked;
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebServiceIA.asmx";
    private final String URL_WS = "http://www.maakki.com/WebService.asmx";
    private Spinner spinner;
    private EditText et_remittance_name_cashier, et_remittance_name, et_remittance_account, et_remittance_account_cashier;
    private List<_IAApplyData> listIAApplyData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_ia);
        // Hiding the action bar
        getActionBar().hide();
        isSpecific = false;
        LL_ApplyNew = (LinearLayout) findViewById(R.id.LL_ApplyNew);
        LL_Application = (LinearLayout) findViewById(R.id.LL_Application);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        applyID_admin = 0;
        applyID_mine = 0;
        applyID_cashier = 0;
        ivAdmin = (ImageView) findViewById(R.id.ivAdmin);
        ivAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                act = "this";
                role = "admin";
                apply_id = applyID_admin;
                //showAlertDialog("Parameters","role:"+role+"\nact:"+act+"apply_id:"+String.valueOf(apply_id));
                AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
            }
        });
        ivList = (ImageView) findViewById(R.id.ivList);
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                act = "this";
                role = "mine";
                apply_id = applyID_mine;
                AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
            }
        });
        ivCashier = (ImageView) findViewById(R.id.ivCashier);
        ivCashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                act = "this";
                role = "cashier";
                apply_id = applyID_cashier;
                AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
            }
        });
        condition = "";
        hasIntroducerChecked = true;
        hasPhonenumberChecked = false;
        hasPasswordChecked = false;
        //has_getVerificationCode = false;
        VerificationCode = "0";
        phoneAreaCode = "86";
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(Open_IA.this,
                R.array.area_option_array, R.layout.spinner_text);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //final String[] statusArray = getResources().getStringArray(R.array.editcategory_status_array);
        spinner.setAdapter(arrayadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //int option = position;
                if (position == 0) {//Mainland China
                    phoneAreaCode = "86";
                } else if (position == 1) {//Taiwan
                    phoneAreaCode = "886";
                } else if (position == 2) {//HongKong
                    phoneAreaCode = "852";
                } else if (position == 3) {//Macau
                    phoneAreaCode = "853";
                } else if (position == 4) {//USA
                    phoneAreaCode = "1";
                } else if (position == 5) {//Japan
                    phoneAreaCode = "81";
                } else if (position == 6) {//India
                    phoneAreaCode = "91";
                } else if (position == 7) {//South Korea
                    phoneAreaCode = "82";
                } else if (position == 8) {//Malaysia
                    phoneAreaCode = "60";
                } else if (position == 9) {//Singapore
                    phoneAreaCode = "65";
                } else if (position == 10) {//Cambodia
                    phoneAreaCode = "855";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_account = (EditText) findViewById(R.id.et_account);
        /*et_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!et_account.getText().toString().isEmpty()){
                        if (et_account.getText().toString().trim().length() > 2) {
                            AsyncCallWS_validMaakkiAccount validMaakkiAccountTask = new AsyncCallWS_validMaakkiAccount();
                            validMaakkiAccountTask.execute();
                        } else {
                            showAlertDialog("帐号设定错误", "玛吉帐号必须设定为至少3个字元");
                            //et_account.requestFocus();
                        }
                    }
                }
            }
        });*/
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus & !et_password2.getText().toString().isEmpty()) {
                    if (!et_password2.getText().toString().trim().equals(et_password.getText().toString().trim())) {
                        showAlertDialog("密码设定错误", "两次设定的密码不一致，请再次确认密码正确与否！");
                    } else {
                        hasPasswordChecked = true;
                    }
                }
            }
        });
        et_password2 = (EditText) findViewById(R.id.et_password2);
        et_password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus & !et_password.getText().toString().isEmpty()) {
                    if (!et_password2.getText().toString().trim().equals(et_password.getText().toString().trim())) {
                        showAlertDialog("密码设定错误", "两次设定的密码不一致，请再次确认密码正确与否！");
                    } else {
                        hasPasswordChecked = true;
                    }
                }
            }
        });
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        et_verificode = (EditText) findViewById(R.id.et_verificode);
        tv_getVerificode = (TextView) findViewById(R.id.tv_getVerificode);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_getVerificode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_phonenumber.getText().toString().isEmpty()) {
                    tv_getVerificode.setVisibility(View.GONE);
                    //has_getVerificationCode = true;
                    AsyncCallWS_getVerificationCode getVerificationCodeTask = new AsyncCallWS_getVerificationCode();
                    getVerificationCodeTask.execute();
                } else {
                    showAlertDialog("手机号码栏位空白", "请输入正确的手机号码，并通过验证程序！");
                }
            }
        });
        et_verificode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_verificode.getText().toString().trim().equals(VerificationCode)) {
                        tv_result.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_accent));
                        tv_result.setText("验证码错误");
                    } else {
                        tv_result.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_solid_maakki_red));
                        tv_result.setText("验证成功");
                        hasPhonenumberChecked = true;
                    }
                }
            }
        });
        et_introducer = (EditText) findViewById(R.id.et_introducer);
        et_introducer.setText(maakki_id);
        et_introducer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_introducer.getText().toString().trim().isEmpty()) {
                        //role_validIAReferral="notme";
                        AsyncCallWS_validIAReferral validIAReferralTask = new AsyncCallWS_validIAReferral();
                        validIAReferralTask.execute();
                    } else {
                        showAlertDialog("介绍人卡号错误", "介绍人卡号不得为空白");
                    }
                }
            }
        });
        RL_CB = (RelativeLayout) findViewById(R.id.RL_CB);
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        checkBox.setButtonDrawable(id);
        checkBox.setOnCheckedChangeListener(chklistener);
        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_account.getText().toString().isEmpty()){
                    if (et_account.getText().toString().trim().length() > 2) {
                        AsyncCallWS_validMaakkiAccount validMaakkiAccountTask = new AsyncCallWS_validMaakkiAccount();
                        validMaakkiAccountTask.execute();
                    } else {
                        showAlertDialog("帐号设定错误", "玛吉帐号必须设定为至少3个字元");
                    }
                }
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                if (!hasIntroducerChecked) {
                    //role_validIAReferral="notme";
                    AsyncCallWS_validIAReferral validIAReferralTask = new AsyncCallWS_validIAReferral();
                    validIAReferralTask.execute();
                }
            }
        });
        tv_location = (TextView) findViewById(R.id.tv_location);


        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_account.getText().toString().length() > 2) {
                    if (et_nickname.getText().toString().length() > 1) {
                        if (!et_verificode.getText().toString().isEmpty()) {
                            if (!et_phonenumber.getText().toString().isEmpty()) {
                                if (hasPhonenumberChecked || et_verificode.getText().toString().equals(VerificationCode)) {
                                    if (hasPasswordChecked) {
                                        if (checkBox.isChecked()) {
                                            if (hasIntroducerChecked) {
                                                //Toast.makeText(getApplicationContext(), "Data input completed!", Toast.LENGTH_LONG).show();
                                                AsyncCallWS_applyNewIA applyNewIATask = new AsyncCallWS_applyNewIA();
                                                applyNewIATask.execute();
                                            } else {
                                                showAlertDialog("介绍人卡号错误", "请输入正确的介绍人卡号！");
                                            }
                                        } else {
                                            showAlertDialog("请确认申请者同意服务条款", "确定已经让申请者详读并完整明白且同意玛吉网服务条款的所有规则后，请点选灰色文字方块。");
                                        }
                                    } else {
                                        showAlertDialog("密码设定错误", "两次设定的密码不一致，请再次确认密码正确与否！");
                                    }
                                } else {
                                    showAlertDialog("手机号码错误", "请输入正确的手机号码，并通过验证程序！");
                                }
                            } else {
                                showAlertDialog("手机号码栏位空白", "请输入正确的手机号码，并通过验证程序！");
                            }
                        } else {
                            showAlertDialog("验证码输入错误", "请注意手机短信息，取得验证码，输入后通过验证程序！");
                        }

                    } else {
                        showAlertDialog("昵称输入错误", "请输入您在玛吉的昵称，昵称至少需两个字元！");
                    }
                } else {
                    showAlertDialog("帐号设定错误", "请设定帐号，帐号至少需有三个字元！");
                }
            }
        });
        getCityName();
        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        //int width = displayMetrics.widthPixels;
        //showAlertDialog("Height & Width","Height:"+height+"\nWidth:"+width);

    }

    @Override
    protected void onStart() {
        super.onStart();
        act = "latest";
        AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
        getIAApplyInfoTask.execute("mine", act, "0");
        AsyncCallWS_getIAApplyInfo getIAApplyInfoTask2 = new AsyncCallWS_getIAApplyInfo();
        getIAApplyInfoTask2.execute("cashier", act, "0");
        if (maakki_id.equals("1") || maakki_id.equals("10006")) {
            AsyncCallWS_getIAApplyInfo getIAApplyInfoTask1 = new AsyncCallWS_getIAApplyInfo();
            getIAApplyInfoTask1.execute("admin", act, "0");
        }
        next_option_buyer= (LinearLayout) findViewById(R.id.next_option_buyer);
        next_option_cashier= (LinearLayout) findViewById(R.id.next_option_cashier);
        next_option_admin= (LinearLayout) findViewById(R.id.next_option_admin);;
        tv_NextStep = (TextView) findViewById(R.id.tv_NextStep);
        tv_NextStepTime = (TextView) findViewById(R.id.tv_NextStepTime);
        tv_BankInfo = (TextView) findViewById(R.id.tv_BankInfo);
        tv_report = (TextView) findViewById(R.id.tv_report);
        tv_report_cashier = (TextView) findViewById(R.id.tv_report_cashier);
        tv_RemittanceInfo = (TextView) findViewById(R.id.tv_RemittanceInfo);
        tv_orderno = (TextView) findViewById(R.id.tv_orderno);
        tv_confirm_order_admin = (TextView) findViewById(R.id.tv_confirm_order_admin);
        tv_cashin_cashier = (TextView) findViewById(R.id.tv_cashin_cashier);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_locality = (TextView) findViewById(R.id.tv_locality);
        tv_sublocality = (TextView) findViewById(R.id.tv_sublocality);
        et_remittance_name = (EditText) findViewById(R.id.et_remittance_name);
        et_remittance_account = (EditText) findViewById(R.id.et_remittance_account);
        et_remittance_account_cashier = (EditText) findViewById(R.id.et_remittance_account_cashier);
        et_remittance_name_cashier = (EditText) findViewById(R.id.et_remittance_name_cashier);
        ll_NextStep = (LinearLayout) findViewById(R.id.ll_NextStep);
        ll_BankInfo = (LinearLayout) findViewById(R.id.ll_BankInfo);
        ll_RemittanceInfo = (LinearLayout) findViewById(R.id.ll_RemittanceInfo);
        ll_LocationInfo= (LinearLayout) findViewById(R.id.ll_LocationInfo);
        LL_NoData =(LinearLayout)findViewById(R.id.LL_NoData);
        LL_remittance_report_buyer=(LinearLayout)findViewById(R.id.LL_remittance_report_buyer);
        LL_remittance_report_cashier=(LinearLayout)findViewById(R.id.LL_remittance_report_cashier);
        ivBack = (ImageView) findViewById(R.id.imV_back);
        tv_RemittanceTime = (TextView) findViewById(R.id.tv_RemittanceTime);
        tv_RemittanceTime_Cashier = (TextView) findViewById(R.id.tv_RemittanceTime_Cashier);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel_cashier = (TextView) findViewById(R.id.tv_cancel_cashier);
        tv_cancel_admin = (TextView) findViewById(R.id.tv_cancel_admin);
        tv_selected_no= (TextView) findViewById(R.id.tv_selected_no);
        tv_17142= (TextView) findViewById(R.id.tv_17142);
        tv_72338= (TextView) findViewById(R.id.tv_72338);
        tv_yesterday= (TextView) findViewById(R.id.tv_yesterday);
        tv_today= (TextView) findViewById(R.id.tv_today);
        tv_0= (TextView) findViewById(R.id.tv_0);
        tv_10= (TextView) findViewById(R.id.tv_10);
        tv_n50= (TextView) findViewById(R.id.tv_n50);
        tv_40= (TextView) findViewById(R.id.tv_40);
        tv_41= (TextView) findViewById(R.id.tv_41);
        tv_42= (TextView) findViewById(R.id.tv_42);
        tv_50= (TextView) findViewById(R.id.tv_50);
        RL_condition_selected= (RelativeLayout) findViewById(R.id.RL_condition_selected);
        RL_condition_status= (RelativeLayout) findViewById(R.id.RL_condition_status);
        RL_condition_day= (RelativeLayout) findViewById(R.id.RL_condition_day);
        RL_condition_cashier= (RelativeLayout) findViewById(R.id.RL_condition_cashier);

        resetOriginal();
    }

    private void DisplaySpecific() {
        isSpecific = true;
        LL_Application.setVisibility(View.VISIBLE);
        LL_NoData.setVisibility(View.GONE);
        TextView tv_content4 =(TextView) findViewById(R.id.tv_content4);
        if (errCode.equals("4")) {
            LL_NoData.setVisibility(View.VISIBLE);
            LL_Application.setVisibility(View.GONE);
            next_option_admin.setVisibility(View.GONE);
            next_option_cashier.setVisibility(View.GONE);
            next_option_buyer.setVisibility(View.GONE);
            return;
        }

        LL_ApplyNew.setVisibility(View.GONE);
        //Toast.makeText(getApplicationContext(), "apply_id:" + apply_id, Toast.LENGTH_LONG).show();
        LL_Application.setVisibility(View.VISIBLE);
        ll_NextStep.setVisibility(View.VISIBLE);

        //decide icon
        ImageView ivIdentity = (ImageView) findViewById(R.id.ivIdentity);
        if (role.equals("cashier")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp));
        } else if (role.equals("admin")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_face_black_18dp));
        } else if (role.equals("mine")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_search_black_18dp));
        } else if (role.equals("imaimai")) {
            ivIdentity.setImageDrawable(getResources().getDrawable(R.drawable.baseline_android_black_18dp));
        }
        tv_orderno.setText(String.valueOf(apply_no) + " " + String.valueOf(apply_status));
        TextView tv_ordertime = (TextView) findViewById(R.id.tv_ordertime);
        tv_ordertime.setText(apply_date);
        String strFeedback1 = "";
        strFeedback2 = "";
        if (!role.equals("cashier")) {
            strFeedback1 += "申请卡号：" + applicant_id  + "\n";
            if(!applicant_nickname.equals("")){
                strFeedback1 += "申请昵稱：" + applicant_nickname  + "\n";
            }        }
        strFeedback1 += "注册帐号：" + maakki_account;
        //
        if (!role.equals("cashier")) {
            strFeedback1 += "\n注册昵称：" + nickname + "\n"
                    + "手机号码：" + cellphone + "\n"
                    + "介绍卡号：" + referral_id;
        }
        if (!role.equals("cashier") & (!sublocality.equals("") || !locality.equals(""))) {
            //strFeedback1 += "\n申购地区：" + area + " " + locality + " " + sublocality;
            ll_LocationInfo.setVisibility(View.VISIBLE);
            tv_area.setText(area);
            tv_locality.setText(locality);
            if(!sublocality.equals("")){
                tv_sublocality.setText(sublocality);
                tv_sublocality.setVisibility(View.VISIBLE);
            }else{
                tv_sublocality.setVisibility(View.GONE);
            }
        }else{
            ll_LocationInfo.setVisibility(View.GONE);
        }
        if (role.equals("admin")) {
            strFeedback1 += "\n金流中心：" + cashier_id;
        }

        TextView tv_RemittanceTime = (TextView) findViewById(R.id.tv_RemittanceTime);
        LinearLayout ll_RemittanceInfo = (LinearLayout) findViewById(R.id.ll_RemittanceInfo);


        if (!remittance_account.equals("")) {
            strFeedback2 = "买家汇款信息：\n汇款账号：(末五码)" + remittance_account
                    + "\n汇款姓名：" + remittance_name;
            tv_RemittanceInfo.setText(strFeedback2);
            tv_RemittanceTime.setText(remittance_time);
            //if (!role.equals("imaimai")) {
            ll_RemittanceInfo.setVisibility(View.VISIBLE);
            //}
        } else {
            ll_RemittanceInfo.setVisibility(View.GONE);
        }
        tv_NextStepTime.setVisibility(View.GONE);
        String strErr = "";
        TextView tv_ordermain = (TextView) findViewById(R.id.tv_ordermain);
        tv_ordermain.setText(strFeedback1);
        setViewByapply_status();
        setSwipeNextpage();
        if (role.equals("mine")) {
            setApplicantFunction();
        }else if(role.equals("cashier")){
            setCashierFunction();
        }else if(role.equals("admin")){
            setAdminFunction();
        }
        setCondition();
    }

    private void setCashierFunction(){
        //setCondition();
        LL_remittance_report_cashier.setVisibility(View.GONE);
        TextView tv_confirm_report_cashier = (TextView) findViewById(R.id.tv_confirm_report_cashier);
        TextView tv_cancel_report_cashier = (TextView) findViewById(R.id.tv_cancel_report_cashier);
        //金流回报汇款信息
        tv_report_cashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_NextStep.setVisibility(View.GONE);
                ll_BankInfo.setVisibility(View.GONE);
                LL_remittance_report_cashier.setVisibility(View.VISIBLE);
                next_option_cashier.setVisibility(View.GONE);
                ivBack.setVisibility(View.GONE);
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
                    AsyncCallWS_IACashierRemit IACashierRemitTask = new AsyncCallWS_IACashierRemit();
                    IACashierRemitTask.execute();
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
            }
        });
        next_option_buyer.setVisibility(View.GONE);
        next_option_cashier.setVisibility(View.VISIBLE);
        next_option_admin.setVisibility(View.GONE);
        tv_cashin_cashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("确认收款");
                alertDialog.setMessage("您确定已经收到了这笔申购的汇款吗？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_IACashierConfirm IAcashierConfirmTask = new AsyncCallWS_IACashierConfirm();
                                IAcashierConfirmTask.execute();
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
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("取消订单");
                alertDialog.setMessage("您确定要取消这笔申购吗？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_IAApplyCancel IAApplyCancelTask = new AsyncCallWS_IAApplyCancel();
                                IAApplyCancelTask.execute();
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

    private void setAdminFunction(){
        next_option_buyer.setVisibility(View.GONE);
        next_option_cashier.setVisibility(View.GONE);
        next_option_admin.setVisibility(View.VISIBLE);
        //setCondition();
        tv_cancel_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role_execute = "admin";
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("取消IA申请");
                alertDialog.setMessage("您确定要取消这笔IA申请吗？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_IAApplyCancel IAApplyCancelTask = new AsyncCallWS_IAApplyCancel();
                                IAApplyCancelTask.execute();
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
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("确认IA申请，并新增920申购");
                alertDialog.setMessage("您确定有收到来自金流中心的汇款吗？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_IAApplyConfirm IAApplyconfirmTask = new AsyncCallWS_IAApplyConfirm();
                                IAApplyconfirmTask.execute();
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

    private void setApplicantFunction() {
        //会员功能
        LL_remittance_report_buyer.setVisibility(View.GONE);
        next_option_buyer.setVisibility(View.VISIBLE);
        next_option_cashier.setVisibility(View.GONE);
        next_option_admin.setVisibility(View.GONE);
        TextView tv_confirm_report = (TextView) findViewById(R.id.tv_confirm_report);
        TextView tv_cancel_report = (TextView) findViewById(R.id.tv_cancel_report);

        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("确认已经完成汇款");
                alertDialog.setMessage("您确定已经利用手机银行或是其他方法完成转账到指定的银行账号，再进行本回报！\n\n请特别注意，如果您尚未完成转账，请勿往下进行！");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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
                    AsyncCallWS_ApplicantRemittanceUpdate ApplicantRemittanceUpdateTask = new AsyncCallWS_ApplicantRemittanceUpdate();
                    ApplicantRemittanceUpdateTask.execute();
                }
            }
        });
        tv_cancel_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_NextStep.setVisibility(View.VISIBLE);
                if (apply_status == 0) {
                    ll_BankInfo.setVisibility(View.VISIBLE);
                } else {
                    ll_BankInfo.setVisibility(View.GONE);
                }
                LL_remittance_report_buyer.setVisibility(View.GONE);
                next_option_buyer.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.VISIBLE);
            }
        });

        //买家取消订单
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role_execute = "oneself";
                AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
                alertDialog.setTitle("取消订单");
                alertDialog.setMessage("您确定要取消这笔申购吗？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_IAApplyCancel IAApplyCancelTask = new AsyncCallWS_IAApplyCancel();
                                IAApplyCancelTask.execute();

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

    private void setViewByapply_status() {
        //identify the subscriber & apply_status
        //String strBankData="";

        if (apply_status == 0) {
            if (role.equals("mine")) {
                tv_NextStep.setText("下一步：请即刻汇款到指定账号");
                if (!cashier_account_no_latest.equals("") & !cashier_account_no_latest.equals(cashier_account_no)) {
                    String str = "请注意这次交易指定的银行账号与上次不同，仔细看清楚，以免造成您不必要的困扰或损失！" + cashier_account_no + "/" + cashier_account_no_latest;
                    showAlertDialog("指定汇款的账号改变了", str);
                }
            } else {
                tv_NextStep.setText("等待买家汇款到以下指定账号后回报..");
            }
            strBankData =
                    cashier_bank_name + "\n"
                            + cashier_branch_name + "\n"
                            + cashier_account_no + "\n"
                            + cashier_account_name;
            tv_BankInfo.setText(strBankData);
            //ll_NextStep.setVisibility(View.VISIBLE);


            ll_BankInfo.setVisibility(View.VISIBLE);

            //买家可以取消订单或是汇报汇款
            tv_cancel.setVisibility(View.VISIBLE);

            tv_report.setText("回报汇款");
            tv_report.setVisibility(View.VISIBLE);

            //tv_refund.setVisibility(View.GONE);
            //Cashier
            if (checkIftimeStampDelayed(apply_date)) {
                tv_cancel_cashier.setVisibility(View.VISIBLE);
                tv_cancel_admin.setVisibility(View.VISIBLE);
            } else {
                tv_cancel_cashier.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.GONE);
            }
            //showAlertDialog("确认时间","请判断订单时间:\n"+TimeStamp1);
            //Admin
        } else if (apply_status == 10 ) {
            if (role.equals("cashier") || role.equals("admin")) {
                strBankData = "金流中心汇款信息：\n汇款账号：(末五码)" + cashierRemittedAccount
                        + "\n汇款姓名：" + cashierRemittedName;
                tv_RemittanceTime_Cashier.setText(cashierRemittedTime);
                tv_BankInfo.setText(strBankData);
                ll_BankInfo.setVisibility(View.VISIBLE);
            }
            //ll_BankInfo.setVisibility(View.GONE);
            tv_report.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            //买家退货
            //tv_refund.setVisibility(View.VISIBLE);
            //cashier
            tv_cancel_cashier.setVisibility(View.GONE);
            //admin
            if (role.equals("admin")) {
                tv_NextStep.setText("这笔申请已经完成，并且已经建立一笔920申购！");
            } else {
                tv_NextStep.setText("这笔申请已经完成！");
            }
            tv_cancel_admin.setVisibility(View.GONE);
        }
        else if (apply_status == 40 || apply_status == 41 || apply_status == 42) {
            ll_BankInfo.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            //tv_refund.setVisibility(View.GONE);
            tv_cancel_cashier.setVisibility(View.GONE);
            tv_NextStep.setText("已回报汇款信息，金流确认中..");
            if (apply_status == 40) {
                if (role.equals("cashier")) {
                    tv_NextStep.setText("买家已回报汇款信息，请您确认是否收款？");
                } else if (role.equals("admin")) {
                    tv_NextStep.setText("买家已回报汇款信息，金流中心确认是否收到汇款中..");
                }
                tv_report.setText("编辑汇款");
                tv_report.setVisibility(View.VISIBLE);
                tv_cancel_admin.setVisibility(View.GONE);
            } else if (apply_status == 41) {
                if (role.equals("cashier")) {
                    tv_NextStep.setText("已确认收款，请转汇到以下指定银行账户，并回报！");
                    maakki_id_account = "0";
                    role_getBankData = "cashier";
                    AsyncCallWS_getBankData _getBankDataTask = new AsyncCallWS_getBankData();
                    _getBankDataTask.execute();
                    ll_BankInfo.setVisibility(View.VISIBLE);
                } else if (role.equals("admin")) {
                    tv_NextStep.setText("金流中心已确认收款，等待回报汇款！）");
                }
                tv_report.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.GONE);
            } else if (apply_status == 42) {
                if (role.equals("cashier") || role.equals("admin")) {
                    strBankData = "金流中心汇款信息：\n汇款账号：(末五码)" + cashierRemittedAccount
                            + "\n汇款姓名：" + cashierRemittedName;
                    tv_RemittanceTime_Cashier.setText(cashierRemittedTime);
                    tv_BankInfo.setText(strBankData);
                    ll_BankInfo.setVisibility(View.VISIBLE);
                    if (role.equals("cashier")) {
                        tv_NextStep.setText("系统确认中..");
                    } else if (role.equals("admin")) {
                        tv_RemittanceInfo.setText("买家已经完成汇款");
                        tv_NextStep.setText("金流已回报以下汇款信息，请在确定收款后，确认订单同时完成拨点！");
                    }
                }
                tv_report.setVisibility(View.GONE);
                //tv_cancel_admin.setVisibility(View.VISIBLE);
            }
        } else if (apply_status > 49) {
            tv_report.setVisibility(View.GONE);
            tv_cancel.setVisibility(View.GONE);
            //tv_refund.setVisibility(View.GONE);
            tv_cancel_cashier.setVisibility(View.GONE);
            tv_cancel_admin.setVisibility(View.GONE);
            String msg = "";
            if (apply_status == 50) {
                msg = "在汇款前买方取消交易\n(交易成立后1小时内)";
            } else if (apply_status == 51) {
                msg = "在汇款前买方取消交易\n(交易成立后超过1小时)";
            } else if (apply_status == 52) {
                msg = "未依约于1小时内汇款\n(系统已取消本交易)";
            } else if ((apply_status == 53)) {
                msg = "取消交易，系统启动退款程序！";
            } else if ((apply_status == 54)) {
                msg = "买家完成汇后取消交易，金流处理退款程序中..";
            } else if ((apply_status == 55)) {
                msg = "取消交易，启动退款程序";
            }
            tv_NextStep.setText(msg);
            ll_NextStep.setVisibility(View.VISIBLE);
            ll_BankInfo.setVisibility(View.GONE);
        }
        if (apply_status == 40) {
            tv_cashin_cashier.setVisibility(View.VISIBLE);
        } else {
            tv_cashin_cashier.setVisibility(View.GONE);
        }
        //41:小金流確認已收到款
        if (apply_status == 41) {
            tv_report_cashier.setVisibility(View.VISIBLE);
        } else {
            tv_report_cashier.setVisibility(View.GONE);
        }
        //42:小金流已经汇款给系统款
        if (apply_status == 42) {
            tv_confirm_order_admin.setVisibility(View.VISIBLE);
        } else {
            tv_confirm_order_admin.setVisibility(View.GONE);
        }
    }

    private void resetLocationCondition(){
        isArea=false;
        isLocality=false;
        isSublocality=false;
        setTvColorChange(true,tv_area);
        setTvColorChange(true,tv_locality);
        setTvColorChange(true,tv_sublocality);
        RL_condition_selected.setVisibility(View.GONE);
    }

    private void setSwipeNextpage() {
        //next page
        //LinearLayout layout_order=(LinearLayout)findViewById(R.id.layout_order);
        final SwipeDetector swipeDetector = new SwipeDetector();
        LL_Application.setOnTouchListener(swipeDetector);
        LL_Application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act = "this";
                if (swipeDetector.swipeDetected()) {
                    resetLocationCondition();
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                        apply_id = previous_applyID;
                        if (apply_id > 0) {
                            AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                            getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
                        }
                    } else if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        apply_id = next_applyID;
                        if (apply_id > 0) {
                            AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                            getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
                        }
                    }
                }
            }
        });
        RL_bottom.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setApplyView();
            }
        });
        final ImageView iv_left = (ImageView) findViewById(R.id.iv_left);
        if (previous_applyID > 0) {
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetLocationCondition();
                    iv_left.setVisibility(View.INVISIBLE);
                    act = "this";
                    apply_id = previous_applyID;
                    AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                    getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
                }
            });

        } else {
            iv_left.setVisibility(View.INVISIBLE);
        }
        final ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
        if (next_applyID > 0) {
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetLocationCondition();
                    iv_right.setVisibility(View.INVISIBLE);
                    act = "this";
                    apply_id = next_applyID;
                    AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                    getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
                }
            });
        } else {
            iv_right.setVisibility(View.INVISIBLE);
        }
    }

    private void setApplyView() {
        resetOriginal();
        isSpecific = false;
        LL_NoData.setVisibility(View.GONE);
        LL_ApplyNew.setVisibility(View.VISIBLE);
        LL_Application.setVisibility(View.GONE);
        RL_bottom.setVisibility(View.GONE);
        RL_condition_status.setVisibility(View.GONE);
        RL_condition_day.setVisibility(View.GONE);
        RL_condition_cashier.setVisibility(View.GONE);
        RL_condition_selected.setVisibility(View.GONE);
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
        if(!role.equals("mine")){
            RL_condition_status.setVisibility(View.VISIBLE);
            RL_condition_day.setVisibility(View.VISIBLE);
            if (role.equals("admin")) {
                RL_condition_cashier.setVisibility(View.VISIBLE);
            }
        }
        tv_sublocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSublocality = setTvColorChange(isSublocality, tv_sublocality);
                if(isSublocality){
                    isLocality = setTvColorChange(false, tv_locality);
                    isArea = setTvColorChange(false, tv_area);
                }
                executeLocation();
            }
        });
        tv_locality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSublocality){
                    isLocality = setTvColorChange(isLocality, tv_locality);
                    if(isLocality){
                        isArea = setTvColorChange(false, tv_area);
                    }
                    executeLocation();
                }
            }
        });
        tv_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSublocality&!isLocality){
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
                is0 = setTvColorChange(true, tv_0);
                is10 = setTvColorChange(true, tv_10);
                is40 = setTvColorChange(true, tv_40);
                is41 = setTvColorChange(true, tv_41);
                is42 = setTvColorChange(true, tv_42);
                is50 = setTvColorChange(true, tv_50);
                if(isn50){
                    tv_0.setVisibility(View.GONE);
                    tv_10.setVisibility(View.GONE);
                    tv_40.setVisibility(View.GONE);
                    tv_41.setVisibility(View.GONE);
                    tv_42.setVisibility(View.GONE);
                    tv_50.setVisibility(View.GONE);
                }else{
                    tv_0.setVisibility(View.VISIBLE);
                    tv_10.setVisibility(View.VISIBLE);
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
        } else {
            list.remove("50");
            list.remove("51");
            list.remove("52");
        }
        //following must be last
        if(isn50){
            list.remove("50");
            list.remove("51");
            list.remove("52");
            list.add("0");
            list.add("10");
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
            strCondition_status = "apply_status=" + strCondition_status;
        } else {
            strCondition_status = "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date d = c.getTime();
        if (isyesterday) {
            strCondition_day = "apply_date=" + df.format(d.getTime());
            if (istoday) {
                strCondition_day += "," + df.format(new Date().getTime());
            }
        } else {
            strCondition_day = "";
            if (istoday) {
                strCondition_day += "apply_date=" + df.format(new Date().getTime());
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
        //showAlertDialog("Condition","condition:"+condition+"\nrole:"+role);
        AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
        getIAApplyInfoTask.execute(role, "latest", "0");
        executeLocation();
    }

    private void executeLocation() {
        listIAApplyData = new ArrayList<>();
        AsyncCallWS_getIAApplyList getIAApplyListTask = new AsyncCallWS_getIAApplyList();
        getIAApplyListTask.execute();
    }

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox.isChecked()) {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_accent));
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
            } else {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_warm_grey));
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
            }
        }
    };

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(Open_IA.this).create();
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

    private void getIAApplyList() {
        //Create request
        String METHOD_NAME = "getIAApplyList";
        String SOAP_ACTION = "http://www.maakki.com/getIAApplyList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "IA-M@@kki.cc" + timeStamp.trim() + role;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role);
        request.addProperty("condition", "");
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
        errMsg="";
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {
            errMsg+=e.getMessage();
        }


        String tmp = soapPrimitive.toString();
        //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
        tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        JSONObject json_read;
        try {
            json_read = new JSONObject(tmp);
            JSONArray jsonArray = json_read.getJSONArray("applyList");
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料！";
            }
            ArrayList<String> list = new ArrayList<String>();
            //JSONArray jsonArray = (JSONArray)jsonObject;
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.get(i).toString());
                }
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                _IAApplyData t = new _IAApplyData();
                JSONObject applylistObj = jsonArray.getJSONObject(i);
                //Object property = tradelist.getProperty(i);
                //if (property instanceof SoapObject) {
                //SoapObject tradelistObj = (SoapObject) property;
                t.setApply_id(applylistObj.getInt("apply_id"));
                t.setApplicant_id(applylistObj.getInt("applicant_id"));
                t.setApply_status(applylistObj.getInt("apply_status"));
                t.setApply_date(applylistObj.getString("apply_date"));
                t.setCancel_date(applylistObj.getString("cancel_date"));
                t.setCashier_id(applylistObj.getInt("cashier_id"));
                t.setCashier_bank_name(applylistObj.getString("cashier_bank_name"));
                t.setCashier_branch_name(applylistObj.getString("cashier_branch_name"));
                t.setCashier_account_name(applylistObj.getString("cashier_account_name"));
                t.setCashier_account_no(applylistObj.getString("cashier_account_no"));
                t.setRemittance_account(applylistObj.getString("remittance_account"));
                t.setRemittance_name(applylistObj.getString("remittance_name"));
                t.setRemittance_time(applylistObj.getString("remittance_time"));
                t.setTerritory(applylistObj.getString("territory"));
                t.setArea(applylistObj.getString("area"));
                t.setLocality(applylistObj.getString("locality"));
                t.setSublocality(applylistObj.getString("sublocality"));
                if (CheckifSeleted(t)) {
                    listIAApplyData.add(t);
                }
            }
        } catch (Exception e) {
            errMsg += "\n"+e.getMessage();
        }
    }

    private class AsyncCallWS_getIAApplyList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String condition=params[0];
            getIAApplyList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("Calculated","selected_no:"+String.valueOf(listTradeData.size()+"\nselsected_amount:"+formatDoubleToString(Double.parseDouble(String.valueOf(String.valueOf(selected_amt))))));
            if(errMsg.equals("")){
                if (listIAApplyData.size() > 0 ) {
                    tv_selected_no.setText(String.valueOf(listIAApplyData.size()));
                    //tv_selected_amount.setText(formatDoubleToString(Double.parseDouble(String.valueOf(String.valueOf(selected_amt)))));
                    RL_condition_selected.setVisibility(View.VISIBLE);
                }else{
                    RL_condition_selected.setVisibility(View.GONE);
                }
            }else{
                showAlertDialog("Error Message",errMsg);
            }

        }

        @Override
        protected void onPreExecute() {
            RL_condition_selected.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private class AsyncCallWS_IAApplyConfirm extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            IAApplyConfirm();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                tv_NextStep.setText("已经确认这笔IA申请，并已建立920申购！");
                apply_status = 10;
                tv_orderno.setText(apply_no + " 10");
                tv_confirm_order_admin.setVisibility(View.GONE);
                tv_cancel_admin.setVisibility(View.GONE);
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

    private void IAApplyConfirm() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "IAApplyConfirm";
        String SOAP_ACTION = "http://www.maakki.com/IAApplyConfirm";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "IA-M@@kki.cc" + timeStamp.trim() + String.valueOf(apply_id).trim();
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", apply_id);
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
                errMsg = "申请已确认，请勿重复确认！";
            } else if (errCode.equals("7")) {
                errMsg = "金流中心尚未汇款！";
            } else if (errCode.equals("8")) {
                errMsg = "申请已取消！";
            } else if (errCode.equals("9")) {
                errMsg = "爱买卖i点余额不足，，无法建立920交易，无法确认这次申请！";
            } else {
                errMsg = "errCode:"+errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private void getVerificationCode() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getVerificationCode";
        String SOAP_ACTION = "http://www.maakki.com/getVerificationCode";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String phone = et_phonenumber.getText().toString();
        //WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //WifiInfo wifiInf = wifiMan.getConnectionInfo();
        //int ipAddress = wifiInf.getIpAddress();
        String IP = getIPAddress(Open_IA.this);
        String encryptStr = maakki_id.trim() + "IA-M@@kki.cc" + timeStamp.trim() + phoneAreaCode + phone + IP;
        //Token：maakki_id +"IA-M@@kki.cc"+timestamp + phoneAreaCode + phone + IP
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("phoneAreaCode", phoneAreaCode);
        request.addProperty("phone", phone);
        request.addProperty("IP", IP);
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
                errMsg = "手机号码无效，只能纯数字，不能包含其它字元";
            } else if (errCode.equals("4")) {
                errMsg = "手机号码无效（码数不对）";
            } else if (errCode.equals("5")) {
                errMsg = "这个手机号码已被注册，一个手机号码只能注册一个玛吉帐号";
            } else if (errCode.equals("6")) {
                errMsg = "要求手机验证码太频繁，短时间内不可重覆要求传送手机验证码";
            } else if (errCode.equals("7")) {
                errMsg = "所有栏位皆为必填，不可留白";
            } else if (errCode.equals("8")) {
                errMsg = "交易已取消！";
            } else {
                errMsg = "errCode:"+errCode;
            }
            VerificationCode = json_read.getString("VerificationCode");
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_getVerificationCode extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getVerificationCode();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                //tv_result.setText(VerificationCode);
                tv_result.setVisibility(View.VISIBLE);
            } else {
                showAlertDialog("云服务回应错误信息", errMsg);
                tv_getVerificode.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            tv_getVerificode.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void validIAReferral() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "validIAReferral";
        String SOAP_ACTION = "http://www.maakki.com/validIAReferral";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        request.addProperty("maakki_id", et_introducer.getText().toString().trim());
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
            } else if (errCode.equals("3")) {
                errMsg = "查无资料";
            } else if (errCode.equals("4")) {
                errMsg = "这个卡号不是IA";
            } else if (errCode.equals("5")) {
                errMsg = "没有参与过920,无法运用本专案新代理商";
            } else {
                errMsg = "errCode:"+errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_validIAReferral extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            validIAReferral();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.equals("")) {
                //if(role_validIAReferral.equals("oneself")){
                    //showAlertDialog("您还是先回吧！", "有点状况请您先理解万岁："+errMsg);
                    //finish();
                //}else{
                    showAlertDialog("这个卡号不好使", "不能成为介绍人：："+errMsg);
                hasIntroducerChecked = false;
                //}

            } else {
                hasIntroducerChecked = true;
            }
        }

        @Override
        protected void onPreExecute() {
            //tv_getVerificode.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void validMaakkiAccount() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "validMaakkiAccount";
        String SOAP_ACTION = "http://www.maakki.com/validMaakkiAccount";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //timeStamp = String.valueOf(new Date().getTime());
        request.addProperty("maakki_account", et_account.getText().toString().trim());
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
            } else if (errCode.equals("3")) {
                errMsg = "帐号至少３个字元以上";
            } else if (errCode.equals("4")) {
                errMsg = "帐号只能是英数字和「-」、「_」这两个符号";
            } else if (errCode.equals("5")) {
                errMsg = "帐号不能设定为第一字为英文字母且其后皆为数字";
            } else if (errCode.equals("6")) {
                errMsg = "帐号不能超过50字元";
            } else if (errCode.equals("7")) {
                errMsg = "此帐号已有人注册 / 申请";
            } else {
                errMsg = "errCode:"+errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_validMaakkiAccount extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            validMaakkiAccount();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errMsg.equals("")) {
                showAlertDialog("哎哟，这个帐号不好使", "真报歉，"+errMsg);
            }
        }

        @Override
        protected void onPreExecute() {
            //tv_getVerificode.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private class AsyncCallWS_applyNewIA extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            applyNewIA();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            act = "this";
            role = "mine";
            if (apply_id == 0) {
                showAlertDialog("申请失败",errMsg);
            } else {
                AsyncCallWS_getIAApplyInfo getIAApplyInfoTask = new AsyncCallWS_getIAApplyInfo();
                getIAApplyInfoTask.execute(role, act, String.valueOf(apply_id));
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

    private void applyNewIA() {
        String METHOD_NAME = "applyNewIA";
        String SOAP_ACTION = "http://www.maakki.com/applyNewIA";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String maakki_account = et_account.getText().toString().trim();
        String referral_id = et_introducer.getText().toString().trim();
        String maakki_password = et_password.getText().toString().trim();
        String encryptStr = maakki_id + "IA-M@@kki.cc" + timeStamp.trim() + maakki_account + VerificationCode;
        //Token：maakki_id + "IA-M@@kki.cc" + timestamp + maakki_account + VerificationCode
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("referral_id", referral_id);
        request.addProperty("maakki_account", maakki_account);
        request.addProperty("maakki_password", maakki_password);
        request.addProperty("nickname", et_nickname.getText().toString().trim());
        request.addProperty("phoneAreaCode", phoneAreaCode);
        request.addProperty("phone", et_phonenumber.getText().toString().trim());
        request.addProperty("VerificationCode", VerificationCode);
        request.addProperty("territory", territory);
        request.addProperty("area", area_GPS);
        request.addProperty("locality", locality_GPS);
        request.addProperty("sublocality", sublocality_GPS);
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
                errMsg = "所有栏位皆为必填，不得留白";
            } else if (errCode.equals("4")) {
                errMsg = "查无申请者的资料（申请人的玛吉卡号无效）";
            } else if (errCode.equals("5")) {
                errMsg = "申请者必须申购过920才能利用本专案申请新增代理商";
            } else if (errCode.equals("6")) {
                errMsg = "介绍人必须申购过920，才能利用本专案新增代理商";
            } else if (errCode.equals("7")) {
                errMsg = "自行取消过申请，今日内不得再申请新增代理商";
            } else if (errCode.equals("8")) {
                errMsg = "申请后一个i小时后，自行取消过申请，今日不得再申请新增代理商";
            } else if (errCode.equals("9")) {
                errMsg = "未依约定于时限内汇款而被取消申请，30日内不得再申请新增代理商";
            } else if (errCode.equals("10")) {
                errMsg = "您申请注册的玛吉帐号不符合帐号命名规则";
            } else if (errCode.equals("11")) {
                errMsg = "您申请注册的玛吉帐号已经被注册了，请更换另一个帐号重新申请";
            } else if (errCode.equals("12")) {
                errMsg = "密码不符合規定（6码以上、必须含有英文及数字、且不可与帳號相同）";
            } else if (errCode.equals("13")) {
                errMsg = "这个手机号码已被注册，一支手机号码只能给注册一个玛吉帐号";
            } else if (errCode.equals("14")) {
                errMsg = "手机验证码错误";
            } else if (errCode.equals("15")) {
                errMsg = "密码或是昵称字元过长";
            }
            apply_id = json_read.getInt("apply_id");
        } catch (Exception e) {
        }

    }

    private class AsyncCallWS_getIAApplyInfo extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            String role = args[0];
            String act = args[1];
            int apply_id = Integer.parseInt(args[2]);
            getIAApplyInfo(role, act, apply_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //showAlertDialog("Location","area:"+area+"\nlocality:"+locality+"\nsublocality:"+sublocality);
            if (act.equals("this") || isSpecific) {
                DisplaySpecific();
            } else {
                //Toast.makeText(getApplicationContext(),"tradeID:"+tradeID_mine+"/"+tradeID_cashier+"/"+tradeID_admin+"/"+errMsg,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"trade_imaimai:"+tradeID_imaimai,Toast.LENGTH_LONG).show();
                if (applyID_mine > 0) {
                    ivList.setVisibility(View.VISIBLE);
                    //if (maakki_id.equals("1") || maakki_id.equals("17141")) {
                    //ivSetup.setVisibility(View.VISIBLE);
                    //Save the cashier_account_no deposited last time
                    //SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(),SharedPreferencesHelper.SharedPreferencesKeys.key18,cashier_account_no);
                    //}
                    //hasMore_mine = true;
                } else {
                    //hasMore_mine = false;
                }
                if (applyID_cashier > 0) {
                    ivCashier.setVisibility(View.VISIBLE);
                    //hasMore_cashier = true;
                } else {
                    //hasMore_cashier = false;
                }
                if (applyID_admin > 0) {
                    ivAdmin.setVisibility(View.VISIBLE);
                    //hasMore_admin = true;
                } else {
                    //hasMore_admin = false;
                }
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

    private void getIAApplyInfo(String role, String act, int apply_id) {
        String METHOD_NAME = "getIAApplyInfo";
        String SOAP_ACTION = "http://www.maakki.com/getIAApplyInfo";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "IA-M@@kki.cc" + timeStamp.trim() + String.valueOf(apply_id).trim() + act + role;
        //string encryptStr = maakki_id.ToString().Trim() + "M@@kki.cc" + timeStamp.ToString().Trim() + tradeID.ToString().Trim();

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", String.valueOf(apply_id));
        request.addProperty("act", act);
        request.addProperty("role", role);
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
            errCode = json_read.getString("errCode");
            previous_applyID = json_read.getInt("previous_applyID");
            next_applyID = json_read.getInt("next_applyID");
            apply_id = json_read.getInt("apply_id");
            apply_no = json_read.getString("apply_no");
            applicant_id = json_read.getInt("applicant_id");
            applicant_nickname = json_read.getString("applicant_nickname");
            apply_status = json_read.getInt("apply_status");
            apply_date = json_read.getString("apply_date");
            referral_id = json_read.getInt("referral_id");
            location_id = json_read.getInt("location_id");
            territory = json_read.getString("territory");
            area = json_read.getString("area");
            locality = json_read.getString("locality");
            sublocality = json_read.getString("sublocality");
            cashier_id = json_read.getInt("cashier_id");
            cashier_bank_name = json_read.getString("cashier_bank_name");
            cashier_branch_name = json_read.getString("cashier_branch_name");
            cashier_account_no = json_read.getString("cashier_account_no");
            cashier_account_name = json_read.getString("cashier_account_name");
            remittance_account = json_read.getString("remittance_account");
            remittance_name = json_read.getString("remittance_name");
            remittance_time = json_read.getString("remittance_time");
            cashierRemittedAccount = json_read.getString("cashierRemittedAccount");
            cashierRemittedName = json_read.getString("cashierRemittedName");
            cashierRemittedTime = json_read.getString("cashierRemittedTime");
            cancel_date = json_read.getString("cancel_date");
            confirm_date = json_read.getString("confirm_date");
            isUpgrade = json_read.getString("isUpgrade");
            ignoreCancelLock = json_read.getString("ignoreCancelLock");
            nickname = json_read.getString("nickname");
            maakki_account = json_read.getString("maakki_account");
            cellphone = json_read.getString("cellphone");
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
        if (act.equals("latest")) {
            if (role.equals("mine")) {
                if(apply_id>0){
                    applyID_mine = apply_id;
                }
                cashier_account_no_latest = cashier_account_no;
                //apply_status_mine = apply_status;
            } else if (role.equals("cashier")) {
                if(apply_id>0){
                    applyID_cashier = apply_id;
                }
            } else if (role.equals("admin")) {
                if(apply_id>0) {
                    applyID_admin = apply_id;
                }
            }
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
                territory = addresses.get(0).getCountryName();
                String[] taiwan = {"Taiwan", "台湾", "台灣"};
                String[] japan = {"Japan", "日本"};
                String[] america = {"America", "USA", "美国", "美國"};
                if (territory == null) {
                    territory = "";
                } else if (Arrays.asList(taiwan).contains(territory)) {
                    spinner.setSelection(1);
                    phoneAreaCode = "886";
                } else if (Arrays.asList(japan).contains(territory)) {
                    spinner.setSelection(5);
                    phoneAreaCode = "81";
                } else if (Arrays.asList(america).contains(territory)) {
                    spinner.setSelection(4);
                    phoneAreaCode = "1";
                }
                area_GPS = addresses.get(0).getAdminArea();
                if (area_GPS == null) {
                    area_GPS = "";
                }
                locality_GPS = addresses.get(0).getLocality();
                if (locality_GPS == null) {
                    locality_GPS = "";
                }
                sublocality_GPS = addresses.get(0).getSubLocality();
                if (sublocality_GPS == null) {
                    sublocality_GPS = "";
                }
                tv_location.setText(area_GPS + " " + locality_GPS + " " + sublocality_GPS);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //return strCityName;
    }

    private Boolean checkIftimeStampDelayed(String datetime) {
        boolean isDelayed = false;
        //TimeStamp1 = "NO VALUE";
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
            //TimeStamp1 = formatter.format(new Date().getTime()) + "\n" + datetime + "\nhour:" + hour + "\nmin:" + min + "\nsec:" + sec + "\n" + new Date().getTime() + "\n" + date.getTime() + "\n" + String.valueOf(l) + "\n" + isDelayed;
        } catch (Exception e) {
        }
        return isDelayed;
    }

    private class AsyncCallWS_IACashierConfirm extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            IACashierConfirm();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //errMsg="";
            if (errMsg.equals("")) {
                tv_NextStep.setText("已经回报确认收款信息，请即刻转汇到以下指定银行账户，完成后请回报！");
                //取得云端汇款银行账户信息
                maakki_id_account = "0";
                role_getBankData = "cashier";
                apply_status = 41;
                tv_orderno.setText(apply_no + " 41");
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
            //showAlertDialog("Parameters:","maakki_id:"+maakki_id+"\napply_id:"+apply_id);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void IACashierConfirm() {
        String METHOD_NAME = "IACashierConfirm";
        String SOAP_ACTION = "http://www.maakki.com/IACashierConfirm";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "IA-M@@kki.cc" + timeStamp.trim() + String.valueOf(apply_id).trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", apply_id);
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
        errMsg="";
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
            errMsg=e.getMessage();
        }

        //將資料丟進JSONObject
        //接下來選擇型態使用get並填入key取值
        try {
            String tmp = soapPrimitive.toString();
            //一開始從網路接收通常為String型態,tmp為接收到的String,為避免串流內有其他資料只需抓取{}間的內容
            tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
            JSONObject json_read;
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "申请人尚未回报汇款信息！";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            } else if (errCode.equals("5")) {
                errMsg = "申请已完成并锁定，无法更改汇款资料";
            } else if (errCode.equals("7")) {
                errMsg = "申请处理中（金流中心已确认或正在回报给系统）";
            } else if (errCode.equals("8")) {
                errMsg = "申请已取消，无法更改汇款资料";
            }else {
                errMsg = "errCode:"+errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_IACashierRemit extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            IACashierRemit();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (errMsg.equals("")) {
                String strFeedback2_cashier = "金流中心汇款信息：\n汇款账号：(末五码)" + ed_text_ra_cashier
                        + "\n汇款姓名：" + ed_text_rn_cashier;
                //tv_content6.setText(strFeedback1 + strFeedback2+strFeedback2_cashier);
                ll_BankInfo.setVisibility(View.VISIBLE);
                tv_BankInfo.setText(strFeedback2_cashier);
                tv_RemittanceTime_Cashier.setText(getCurrentTime());
                LL_remittance_report_cashier.setVisibility(View.GONE);
                //tv_report.setText("编辑汇款");
                //tv_cancel.setVisibility(View.GONE);
                //tv_refund.setVisibility(View.VISIBLE);
                tv_orderno.setText(apply_no + " 42");
                next_option_cashier.setVisibility(View.GONE);
                tv_NextStep.setText("已回报汇款信息，请静候处理..");
                ll_NextStep.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.VISIBLE);
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

    private void IACashierRemit() {
        String METHOD_NAME = "IACashierRemit";
        String SOAP_ACTION = "http://www.maakki.com/IACashierRemit";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "IA-M@@kki.cc" + timeStamp.trim() + String.valueOf(apply_id).trim() + ed_text_ra_cashier;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", apply_id);
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
                errMsg = "申请已完成并锁定，无法更改汇款资料";
            } else if (errCode.equals("7")) {
                errMsg = "申请处理中（金流中心已确认或正在回报给系统）";
            } else if (errCode.equals("8")) {
                errMsg = "申请已取消，无法更改汇款资料";
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private class AsyncCallWS_IAApplyCancel extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            IAApplyCancel();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String nextstep = "";
            if (errMsg.equals("")) {
                nextstep = "您已经取消了这次申请！";
                ll_BankInfo.setVisibility(View.GONE);
                //如果是买家取消
                next_option_buyer.setVisibility(View.GONE);
                next_option_cashier.setVisibility(View.GONE);
                //买家，金流中心都已经完成汇款，系统可以决定取消订单《进行退款
                if (apply_status == 0) {
                    if (role.equals("mine")) {
                        tv_orderno.setText(apply_no);
                    } else {
                        tv_orderno.setText(apply_no + " 52");
                    }
                }
                /*if (apply_status == 42 & role.equals("admin")) {
                    tv_refund_admin.setVisibility(View.VISIBLE);
                    tv_orderno.setText(trade_no + " 53");
                }*/
                tv_cancel_admin.setVisibility(View.GONE);
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

    private void IAApplyCancel() {
        String METHOD_NAME = "IAApplyCancel";
        String SOAP_ACTION = "http://www.maakki.com/IAApplyCancel";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "IA-M@@kki.cc" + timeStamp.trim() + String.valueOf(apply_id).trim() + role_execute;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", apply_id);
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
                errMsg = "申请已完成并锁定，不能取消！";
            } else if (errCode.equals("7")) {
                errMsg = "交易处理中，不能取消交易！";
            } else if (errCode.equals("8")) {
                errMsg = "申请已取消，无法再次取消！";
            } else if (errCode.equals("9")) {
                errMsg = "操作者不是此笔申请的金流中心;不能取消！";
            } else if (errCode.equals("10")) {
                errMsg = "操作者没有权限可以将此笔申请取消。（不具后台管理权限）";
            } else if (errCode.equals("11")) {
                errMsg = "金流中心不能取消60分钟内的申请！";
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
                if (role.equals("cashier")) {
                    tv_report_cashier.setVisibility(View.VISIBLE);
                    if (!account_no_getBankData.equals("")) {
                        et_remittance_account_cashier.setText(account_no_getBankData.substring(account_no_getBankData.length() - 5));
                    }
                    if (!account_name_getBankData.equals("")) {
                        et_remittance_name_cashier.setText(account_name_getBankData);
                    }
                } else if (role.equals("mine")) {
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
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_WS);

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

    private class AsyncCallWS_ApplicantRemittanceUpdate extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            ApplicantRemittanceUpdate();
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
                apply_status = 40;
                tv_orderno.setText(apply_no + " 40");
                ivBack.setVisibility(View.VISIBLE);
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

    private void ApplicantRemittanceUpdate() {
        String METHOD_NAME = "ApplicantRemittanceUpdate";
        String SOAP_ACTION = "http://www.maakki.com/ApplicantRemittanceUpdate";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.toString().trim() + "IA-M@@kki.cc" + timeStamp.toString().trim() + String.valueOf(apply_id).trim() + ed_text_ra;
        //maakki_id + "M@@kki.cc" + timestamp + tradeID + remittance_account

        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("apply_id", apply_id);
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
                errMsg = "申请完成并锁定，无法更改汇款资料";
            } else if (errCode.equals("7")) {
                errMsg = "申请处理中（金流中心已确认或正在回报给系统）";
            } else if (errCode.equals("8")) {
                errMsg = "申请已取消，无法更改汇款资料";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return dt1.format(d.getTime());
    }

    private Boolean CheckifSeleted(_IAApplyData td) {
        Boolean isSelected = false;
        //selected_no = 0;
        //selected_amt = 0;
        String[] conditions = condition.split(";");
        ArrayList<String> l_cashier = new ArrayList<>();
        ArrayList<String> l_applystatus = new ArrayList<>();
        ArrayList<String> l_day = new ArrayList<>();
        for (int j = 0; j < conditions.length; j++) {
            if (conditions[j].split("=")[0].equals("cashier_id")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_cashier.add(con[k]);
                }
            }
            if (conditions[j].split("=")[0].equals("apply_date")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_day.add(con[k]);
                }
            }
            if (conditions[j].split("=")[0].equals("apply_status")) {
                String[] con = conditions[j].split("=")[1].split(",");
                for (int k = 0; k < con.length; k++) {
                    l_applystatus.add(con[k]);
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
        boolean isSeleted_applystatus = true;
        if (l_applystatus.size() > 0) {
            isSeleted_applystatus = false;
            for (int i = 0; i < l_applystatus.size(); i++) {
                if (String.valueOf(td.getApply_status()).equals(l_applystatus.get(i))) {
                    isSeleted_applystatus = true;
                }
            }
        }
        boolean isSeleted_day = true;
        if (l_day.size() > 0) {
            isSeleted_day = false;
            for (int i = 0; i < l_day.size(); i++) {
                if (String.valueOf(td.getApply_date().split(" ")[0]).equals(l_day.get(i))) {
                    isSeleted_day = true;
                }
            }
        }
        boolean isSelected_location=false;
        if(isArea){
            if(area.equals(td.getArea())){
                if(isLocality){
                    if(locality.equals(td.getLocality())){
                        if(isSublocality){
                            if(sublocality.equals(td.getSublocality())){
                                isSelected_location=true;
                            }
                        }else{
                            isSelected_location=true;
                        }
                    }
                }else{
                    isSelected_location=true;
                }
            }
        }else{
            isSelected_location=true;
        }
        if (isSeleted_cashier&isSeleted_applystatus&isSeleted_day&isSelected_location) {
            isSelected = true;
        } else {
            isSelected = false;
        }
        return isSelected;
    }

    private void resetOriginal(){
        cashier_account_no_latest = "";
        //tradeStatus_mine = 9;
        isArea=false;
        isLocality=false;
        isSublocality=false;
        is72338 = false;
        is17142 = false;
        istoday = false;
        isyesterday = false;
        is40 = false;
        is0 = false;
        is10 = false;
        is11 = false;
        is12 = false;
        is20 = false;
        is41 = false;
        is42 = false;
        is50 = false;
        isn50 = false;
        condition = "";
        //Toast.makeText(getApplicationContext(),"condition:"+condition,Toast.LENGTH_LONG).show();

    }
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
