package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
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
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Ally_buildup extends Activity {
    private SharedPreferences.Editor prefs;
    private String mCameraPhotoPath="";
    private String filename, role_validIAReferral, ed_text_ra_cashier, ed_text_rn_cashier, strFeedback2, role_execute, ed_text_ra, ed_text_rn, strBankData, bank_name_getBankData, branch_name_getBankData, account_no_getBankData, account_name_getBankData, maakki_id_account, role_getBankData, cashier_account_no_latest, maakki_account, nickname, confirm_date, cellphone, applicant_nickname, ignoreCancelLock, isUpgrade, cancel_date, cashierRemittedTime, cashierRemittedName, cashierRemittedAccount, remittance_time, remittance_name, remittance_account, cashier_account_name, cashier_account_no, cashier_branch_name, cashier_bank_name, apply_date, apply_no, condition, act, role, territory, area, locality, sublocality,
            area_GPS = "", maakki_id, timeStamp, errCode, errMsg, identifyStr, phoneAreaCode, VerificationCode;
    private int hour,minute,applyID_cashier, applyID_mine, applyID_admin, cashier_id, apply_status, next_applyID, previous_applyID, apply_id, referral_id, applicant_id, location_id;
    private CheckBox checkBox, checkBox_anonymous,checkbox_buildupbyic;
    private RelativeLayout rl_footer, RL_done, RL_condition_status, RL_condition_day, RL_condition_cashier, RL_condition_selected, RL_CB, RL_bottom;
    private LinearLayout LL_NoData, ll_LocationInfo, LL_remittance_report_cashier, LL_remittance_report_buyer, ll_RemittanceInfo, ll_BankInfo, LL_addSponsor, LL_Application, ll_NextStep, next_option_buyer, next_option_cashier, next_option_admin;
    private ImageView iv_list,iv_icon,iv_clear, ivTarget, iv_submit, iv_back;
    private TextView tv_icon,tv_donatetimedialog,tv_envelope_piece, tv_slogan, tv_date, tv_checkrules, tv_targetStr, tv_donatetime, tv_nickname, tv_17142, tv_72338, tv_yesterday, tv_today, tv_0, tv_10, tv_n50, tv_40, tv_41, tv_42, tv_50,
            tv_selected_no, tv_area, tv_locality, tv_sublocality, tv_cashin_cashier, tv_confirm_order_admin, tv_orderno, tv_RemittanceTime, tv_RemittanceTime_Cashier, tv_cancel, tv_cancel_cashier, tv_cancel_admin, tv_RemittanceInfo, tv_report, tv_report_cashier, tv_BankInfo, tv_NextStepTime, tv_NextStep, tv_location, tv_result, tv_getVerificode;
    private EditText et_allyname,et_envelope_no, et_iCredits, et_password, et_password2, et_phonenumber, et_verificode, et_introducer;
    private boolean isDown, hasChangedtoUSD, isTarget, isAnonymous,isBuildupbyic, isSpecific, is12, is50, is42, is10, is11, is20, is40, is41, is72338, istoday, isyesterday, isArea, isLocality, isSublocality, isn50, is0, is17142, hasPasswordChecked, hasPhonenumberChecked, hasVericode, hasIntroducerChecked;
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL =StaticVar.webURL+ "/WebServiceIA.asmx";
    private final String URL_WS = StaticVar.webURL+"/WebService.asmx";
    private List<_IAApplyData> listIAApplyData;
    private double erUSD = 1l, erRMB = 1l, erHKD = 1l, erMYR = 1l, erJPY = 1l, memberFG = 0l, actualSponsorAmt = 0l, iCredit = 0l;
    private String realPath,picFileID, member_id, strCurrency, default_sponsoriCredit, targetStr, target_maakki_id, currency = "RMB";
    private String redUrl = StaticVar.webURL+"/MCoins/MCoinsQuery.aspx";
    private List<exchangeRate> rateArr;
    //1:指定赞助 2:顺序赞助 3:红包赞助
    private int sponsorType = 1, envelope_no = 1;
    private Context context;
    //private redEnvelopeMasterDAO2 rEDAO;
    private redEnvelope_Master redEnvelope_master;

    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CLIP_PHOTO = 3;
    private static final int REQUEST_PHOTO_GALLERY = 1;// 從相冊中選擇
    private static final int PHOTO_REQUEST_CUT = 5;// 結果
    private File OutputFile;
    private File tempFile,tmpFile;
    private static int RESULT_LOAD_IMG = 1;
    private Uri imageUri=null;
    int id;
    private Bitmap icon_bitmap=null;
    public final int REQUEST_CODE_FOR_PERMISSIONS = 654;
    Ally ally;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ally_buildup);
        // Hiding the action bar
        getActionBar().hide();
        context = this;
        //rEDAO = new redEnvelopeMasterDAO2(this);
        redEnvelope_master = new redEnvelope_Master();
        rateArr = new ArrayList<>();
        tv_targetStr = (TextView) findViewById(R.id.tv_targetStr);
        tempFile= new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        AsyncCallWS_getExchangeRate getExchangeRateTask = new AsyncCallWS_getExchangeRate();
        getExchangeRateTask.execute();
        strCurrency = currency;
        isAnonymous = false;
        isBuildupbyic = false;
        isTarget = true;
        checkBox_anonymous = (CheckBox) findViewById(R.id.checkbox_anonymous);
        checkBox_anonymous.setOnCheckedChangeListener(chklistener_anonymous);
        checkbox_buildupbyic= (CheckBox) findViewById(R.id.checkbox_buildupbyic);
        checkbox_buildupbyic.setOnCheckedChangeListener(chklistener_buildupbyic);
        //isSpecific = false;
        LL_addSponsor = (LinearLayout) findViewById(R.id.LL_addSponsor);
        LL_Application = (LinearLayout) findViewById(R.id.LL_Application);
        ivTarget = (ImageView) findViewById(R.id.ivTarget);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_icon = (TextView) findViewById(R.id.tv_icon);

        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent[] intentArray;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //File tmpFile = new File(Environment.getExternalStorageDirectory(),"image.jpg");
                //Uri outputFileUri = Uri.fromFile(tempFile);
                //Uri outputFileUri = Uri.fromFile(tmpFile);
                filename=getPhotoFileName();
                tmpFile = new File(Environment.getExternalStorageDirectory(),filename);
                Uri outputFileUri = Uri.fromFile(tmpFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                intentArray = new Intent[]{intent};
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, REQUEST_CODE_TAKE_PHOTO);
            }
        });

        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        tv_donatetime = (TextView) findViewById(R.id.tv_KickStartTime);
        tv_slogan = (TextView) findViewById(R.id.tv_greeting);
        et_allyname = (EditText) findViewById(R.id.et_greeting);
        et_allyname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_allyname.setHint("");
                }else{
                    if(et_allyname.getText().toString().isEmpty()){
                        showAlertDialog("请输入联盟名称","请为你的联盟取一个响亮的名字！");
                    }
                }
            }
        });

        tv_donatetimedialog=(TextView) findViewById(R.id.tv_donatetimedialog);
        showTimePickerDialog();
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_list= (ImageView) findViewById(R.id.iv_list);
        iv_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Ally_buildup.this,AllyList.class);
                startActivity(i);
            }
        });
        et_iCredits = (EditText) findViewById(R.id.et_iCredits);
        et_iCredits.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_iCredits.getText().toString().isEmpty()) {
                        if (Double.parseDouble(et_iCredits.getText().toString().trim()) > memberFG) {
                            showAlertDialog("赞助点数过多", "超过您目前已经建立的FG(众筹目标)");
                        }
                    }
                    //else{et_iCredits.setText(formatDoubleToString(Double.parseDouble(et_iCredits.getText().toString())));}
                }
            }
        });

        //tv_currency = (TextView) findViewById(R.id.tv_currency);

        //et_donatetime = (EditText) findViewById(R.id.et_donatetime);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        et_password = (EditText) findViewById(R.id.et_password);
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
        RL_CB = (RelativeLayout) findViewById(R.id.RL_CB);
        rl_footer = (RelativeLayout) findViewById(R.id.rl_footer);
        isDown = false;
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(chklistener);
        tv_checkrules = (TextView) findViewById(R.id.tv_checkrules);
        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfAllSet()){
                    checkBox.setChecked(true);
                }else{checkBox.setChecked(false);}
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
        RelativeLayout RL_CB_buildupbyic = (RelativeLayout) findViewById(R.id.RL_CB_buildupbyic);
        RL_CB_buildupbyic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_buildupbyic.isChecked()) {
                    checkbox_buildupbyic.setChecked(false);
                } else {
                    checkbox_buildupbyic.setChecked(true);
                }
            }
        });
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfAllSet()){
                    if(checkBox.isChecked()){
                        tobuildupAlly();
                    }else{
                        showAlertDialog("请确认明白并同意服务条款", "确定已经详读并完整明白且同意玛吉网众筹规则与服务条款的所有规则后，请点选灰色文字方块。");
                    }
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

    private void tobuildupAlly(){
        AllyDAO allyDAO=new AllyDAO(context);
        ally=new Ally();
        ally.setAlly_name(et_allyname.getText().toString());
        //尚未取得全球联盟编号。暂设为0
        ally.setToken(getToken());
        //1：接受申请 2.不接受申请，只能由团长邀请 3.已经解散
        int ally_status=1;
        if(checkBox_anonymous.isChecked()){ally_status=2;}
        ally.setAllyStatus(ally_status);
        ally.setFounder_id(Integer.parseInt(maakki_id));
        ally.setIconpath(realPath);
        ally.setKickStartTime(tv_donatetimedialog.getText().toString().split(" ")[0]+":"+tv_donatetimedialog.getText().toString().split(" ")[2]);
        ally.setDonationAmt(Integer.parseInt(et_iCredits.getText().toString()));
        ally.setLastModifyTime(new Date().getTime());
        //get new ally with ally.getId()
        ally=allyDAO.insert(ally);
        Intent i=new Intent(Ally_buildup.this,AllyList.class);
        startActivity(i);
        hour=Integer.parseInt(tv_donatetimedialog.getText().toString().split(" ")[0]);
        minute=Integer.parseInt(tv_donatetimedialog.getText().toString().split(" ")[2]);
        setAllyAlarm(hour,minute,ally);
        childThread.start();
    }
    private void setAllyAlarm(int hour,int minute,Ally ally) {
        //Intent i = new Intent("com.maakki.maakkiapp.AllyAlarm");
        Intent i = new Intent(context, AllyAlarm.class);
        i.putExtra("ally_id",ally.getId());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private Thread childThread = new Thread(){
        @Override
        public void run() {
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e){
                //ignore
            }
            //SignalRUtil.sendAllyInformationtoGetUniqueAllyID(ally,getBase64String(realPath));
            AsyncCallWS_base64Test base64TestTask=new AsyncCallWS_base64Test();
            base64TestTask.execute();
        }
    };
    private String getBase64String(String realpath) {

        // give your image file url in mCurrentPhotoPath
        Bitmap bitmap = BitmapFactory.decodeFile(realpath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private void decodeBase64AndSetImage(String completeImageData, ImageView imageView) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }
    private String getToken(){
        timeStamp = String.valueOf(new Date().getTime());
        String iCredits=et_iCredits.getText().toString().trim();
        String iCreditPassword = et_password.getText().toString().trim();
        String encryptStr = maakki_id + "Ally-M@@kki.cc" + timeStamp.trim() + iCredits + iCreditPassword;
        String token = getHashCode(encryptStr).toUpperCase();
        return token;
    }
    @Override
    protected void onStop() {
        super.onStop();
        //RL_done.setVisibility(View.GONE);
        //et_password.setText("");
        //LL_addSponsor.setVisibility(View.VISIBLE);
        //finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        et_iCredits.setText("");
        et_allyname.setText("");
        et_password.setText("");
        et_iCredits.setHint("赞助的 i点数");
        et_allyname.setHint("为你的联盟取一个名字");
        et_password.setHint("请输入转点密码");
        checkBox.setChecked(false);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        AsyncCallWS_getiCreditBalance getiCreditBalanceTask = new AsyncCallWS_getiCreditBalance();
        getiCreditBalanceTask.execute();
        AsyncCallWS_getMemberFG getMemberFGTask=new AsyncCallWS_getMemberFG();
        getMemberFGTask.execute();
        SignalRUtil.setHubConnect(context);/**/
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
    private CheckBox.OnCheckedChangeListener chklistener_buildupbyic = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkbox_buildupbyic.isChecked()) {
                isBuildupbyic = true;
            } else {
                isBuildupbyic = false;
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
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_accent));
            } else {
                SlideToDown();
                isDown = true;
                iv_submit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
            }
        }
    };

    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(Ally_buildup.this).create();
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
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "MDF-M@@kki.cc" + timeStamp.trim();
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
            /*if (errMsg.equals("")) {
                double sponsorUSD = Double.parseDouble(et_iCredits.getText().toString().trim());
                //double sponsorUSD=Double.parseDouble(et_iCredits.getText().toString().trim());
                if (sponsorUSD > memberFG) {
                    showAlertDialog("赞助的点数过多", "超过您目前建立的FG(众筹目标)：" + memberFG + "USD(i)");
                }
                //AsyncCallWS_getMemberProfile getMemberProfileTask = new AsyncCallWS_getMemberProfile();
                //getMemberProfileTask.execute();
            }*/
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
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "MDF-M@@kki.cc" + timeStamp.trim();
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

    private Boolean checkIfAllSet() {
        boolean isCorrect = true;
        if(et_allyname.getText().toString().isEmpty()){
            showAlertDialog("请输入联盟名称","请为你的联盟取一个响亮的名字！");
            isCorrect=false;
        }if(icon_bitmap==null){
            showAlertDialog("请设定联盟图志","请为你的联盟打造一个专属图腾！");
            isCorrect=false;
        }else{
            if (!et_iCredits.getText().toString().trim().isEmpty()) {
                if (!et_password.getText().toString().trim().isEmpty()) {
                    if (Double.parseDouble(et_iCredits.getText().toString().trim()) > memberFG) {
                        showAlertDialog("赞助点数过多", "超过您目前已经建立的FG(众筹目标)");
                        isCorrect=false;
                    }
                } else {
                    showAlertDialog("请输入转点密码", "您忘了输入转点密码！");
                    isCorrect=false;
                }
            } else {
                showAlertDialog("请输入赞助点数", "请输入您所要赞助的点数！");
                isCorrect=false;
            }
        }
        return isCorrect;
    }
    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }
    private void getiCreditBalance() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "getiCreditBalance";
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
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
                //tv_currency.setText(strCurrency + "(i)");
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
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
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
            String tmp = soapPrimitive.toString();
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
                //getRedEnvelope();
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
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
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
                errMsg = "请再次确认你所输入的密码是否正确无误！";
            }
        } catch (Exception e) {
        }
    }

    private String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dt1.format(d.getTime());
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
    // Create and show a TimePickerDialog when click button.
    private void showTimePickerDialog()
    {
        // Get open TimePickerDialog button.
        //Button timePickerDialogButton = (Button)findViewById(R.id.timePickerDialogButton);
        tv_donatetimedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                //final Calendar c = Calendar.getInstance();
                hour = 8;
                minute = 0;
                if(!tv_donatetimedialog.getText().toString().equals("设定每日众筹时间")){
                    hour = Integer.parseInt(tv_donatetimedialog.getText().toString().split(" ")[0]);
                    minute = Integer.parseInt(tv_donatetimedialog.getText().toString().split(" ")[2]);
                }
                // Create a new instance of TimePickerDialog and return it
                new TimePickerDialog(Ally_buildup.this, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {

                        StringBuffer strBuf = new StringBuffer();
                        //strBuf.append("You select time is ");
                        strBuf.append(hour);
                        strBuf.append(" 点 ");
                        strBuf.append(minute);
                        strBuf.append(" 分");
                        //TextView timePickerValueTextView = (TextView)findViewById(R.id.timePickerValue);
                        //tv_donatetimedialog.setTextColor();
                        tv_donatetimedialog.setText(strBuf.toString());
                    }
                }, hour, minute, true).show();
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_FOR_PERMISSIONS){
            //You need to handle permission results, if user didn't allow them.
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            String mess="";
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    //results=new Uri[]{Uri.fromFile(tmpFile)};
                    //icon_bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    icon_bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/"+filename);
                    realPath=Environment.getExternalStorageDirectory() + "/"+filename;
                    results=new Uri[]{getImageUri(icon_bitmap)};
                    //iv_icon.setImageBitmap(bitmap);
                    //String path=Environment.getExternalStorageDirectory() + "/image.jpg";
                    //results=new Uri[]{Uri.parse(path)};
                } else {

                    // SDK < API11
                    if (Build.VERSION.SDK_INT < 11)
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                        // SDK > 19 (Android 4.4)
                    else
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

                    String dataString = data.getDataString();
                    //mess="data is null:"+dataString;
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
                //showAlertDialog("realPath",realPath);
                startPhotoZoom(results[0]);
            }
        }else{
            if (data != null)
                sentPicToNext(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public Uri getImageUri(Bitmap inImage) {
        String path =
                MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage,
                        "Title", null);
        return Uri.parse(path);
    }
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap mbmp = (Bitmap) data.getExtras().get("data");
            iv_icon.setImageBitmap(mbmp);
        }
    }*/
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是設置在開启的intent中設置顯示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是寬高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁圖片的寬高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 將進行剪裁後的圖片傳遞到下一個界面上
    private void sentPicToNext(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            icon_bitmap = bundle.getParcelable("data");
            if (icon_bitmap==null) {
                iv_icon.setImageResource(R.drawable.no_picture);
            }else {
                iv_icon.setImageBitmap(icon_bitmap);
                saveImage(icon_bitmap);
            }
            /*ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                icon_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photodata = baos.toByteArray();
                System.out.println(photodata.toString());
                // Intent intent = new Intent();
                // intent.setClass(RegisterActivity.this, ShowActivity.class);
                // intent.putExtra("photo", photodata);
                // startActivity(intent);
                // finish();
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }*/
        }
    }
    private void saveImage(Bitmap bitmap) {
        SaveImageTask saveImageTask = new SaveImageTask(context, filePath -> onSaveComplete(filePath));
        saveImageTask.execute(bitmap);
    }
    private void onSaveComplete(File filePath) {
        if(filePath == null){
            //Log.w(TAG, "onSaveComplete: file dir error");
            return;
        }
        realPath=filePath.getPath();
    }
    public void getImage(Bitmap bm) {
        //Log.i("Hi..", "In getImage "+bm);
        //dbh = new DatabaseHandler(this);
        //int id = (int) dbh.insertBitmap(bm);
        iv_icon.setImageBitmap(bm);
    }
    private boolean hasCarema() {
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA )
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT )) {
            Toast. makeText(this, "no camera found", Toast.LENGTH_SHORT).show();
            return false ;
        }
        return true ;
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
    //寫檔
    private void writeDataToFile(String filename, String data){
        try {
            FileOutputStream fout = this.openFileOutput(filename, Context.MODE_PRIVATE);
            fout.write(data.getBytes());
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //讀檔
    private String readDataFromFile(String filename){
        String result = null;
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream fin = this.openFileInput(filename);
            byte[] data = new byte[fin.available()];
            while (fin.read(data) != -1) {
                sb.append(new String(data));
            }
            fin.close();
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private class AsyncCallWS_base64Test extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            base64Test();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(!errMsg.isEmpty()){showAlertDialog("errMsg",errMsg);}
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void base64Test() {
        //Create request get920TradeInfo(int maakki_id, int tradeID, Int64 timeStamp, String identifyStr)
        //friend=new Friend();
        String METHOD_NAME = "base64Test";
        String SOAP_ACTION = StaticVar.webURL+"/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("base64Str", getBase64String(realPath));
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
        String errCode;
        try {
            json_read = new JSONObject(tmp);
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "查无资料";            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }
}

