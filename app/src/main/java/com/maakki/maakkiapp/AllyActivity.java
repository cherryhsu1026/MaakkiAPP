package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
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
import java.util.Date;
import java.util.List;

public class AllyActivity extends Activity {
    private SharedPreferences.Editor prefs;
    private String mCameraPhotoPath="";
    private String filename, role_validIAReferral, ed_text_ra_cashier, ed_text_rn_cashier, strFeedback2, role_execute, ed_text_ra, ed_text_rn, strBankData, bank_name_getBankData, branch_name_getBankData, account_no_getBankData, account_name_getBankData, maakki_id_account, role_getBankData, cashier_account_no_latest, maakki_account, nickname, confirm_date, cellphone, applicant_nickname, ignoreCancelLock, isUpgrade, cancel_date, cashierRemittedTime, cashierRemittedName, cashierRemittedAccount, remittance_time, remittance_name, remittance_account, cashier_account_name, cashier_account_no, cashier_branch_name, cashier_bank_name, apply_date, apply_no, condition, act, role, territory, area, locality, sublocality,
            area_GPS = "", maakki_id, timeStamp, errCode, errMsg, identifyStr, phoneAreaCode, VerificationCode;
    private int hour,minute,applyID_cashier, applyID_mine, applyID_admin, cashier_id, apply_status, next_applyID, previous_applyID, apply_id, referral_id, applicant_id, location_id;
    private CheckBox checkBox, checkBox_anonymous,checkbox_buildupbyic;
    private RelativeLayout rl_footer, RL_done, RL_condition_status, RL_condition_day, RL_condition_cashier, RL_condition_selected, RL_CB, RL_bottom;
    private LinearLayout ll_NextStep, next_option_buyer, next_option_cashier, next_option_admin;
    private ImageView iv_allyicon,iv_edit,iv_icon,iv_submit;
    private TextView tv_DonationAmt,tv_allyname,tv_icon,tv_envelope_piece, tv_date, tv_checkrules, tv_targetStr, tv_KickStartTime, tv_nickname, tv_17142, tv_72338, tv_yesterday, tv_today, tv_0, tv_10, tv_n50, tv_40, tv_41, tv_42, tv_50,
            tv_selected_no, tv_area, tv_locality, tv_sublocality, tv_cashin_cashier, tv_confirm_order_admin, tv_orderno, tv_RemittanceTime, tv_RemittanceTime_Cashier, tv_cancel, tv_cancel_cashier, tv_cancel_admin, tv_RemittanceInfo, tv_report, tv_report_cashier, tv_BankInfo, tv_NextStepTime, tv_NextStep, tv_location, tv_result, tv_getVerificode;
    private EditText et_envelope_no, et_iCredits,et_phonenumber, et_verificode, et_introducer;
    private boolean isDown, hasChangedtoUSD, isTarget, isAnonymous,isBuildupbyic, isSpecific, is12, is50, is42, is10, is11, is20, is40, is41, is72338, istoday, isyesterday, isArea, isLocality, isSublocality, isn50, is0, is17142, hasPasswordChecked, hasPhonenumberChecked, hasVericode, hasIntroducerChecked;
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebServiceIA.asmx";
    private final String URL_WS = StaticVar.webURL+"WebService.asmx";
    private List<_IAApplyData> listIAApplyData;
    private double erUSD = 1l, erRMB = 1l, erHKD = 1l, erMYR = 1l, erJPY = 1l, memberFG = 0l, actualSponsorAmt = 0l, iCredit = 0l;
    private String realPath,picFileID, member_id, strCurrency, default_sponsoriCredit, targetStr, target_maakki_id, currency = "RMB";
    private String redUrl = StaticVar.webURL+"MCoins/MCoinsQuery.aspx";
    private List<exchangeRate> rateArr;
    //1:指定赞助 2:顺序赞助 3:红包赞助
    //private int sponsorType = 1, envelope_no = 1;
    private Context context;
    //private redEnvelopeMasterDAO2 rEDAO;
    //private redEnvelope_Master redEnvelope_master;
    private AllyDAO allyDAO;
    private Ally ally;
    private Long allyId;
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
    //Ally ally;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allyactivity);
        // Hiding the action bar
        getActionBar().hide();
        context = this;
        //rEDAO = new redEnvelopeMasterDAO2(this);
        //redEnvelope_master = new redEnvelope_Master();
       if (getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            allyId = bundle.getLong("ally_id");
        }
        allyDAO=new AllyDAO(context);
        ally=allyDAO.get(allyId);
        iv_allyicon = (ImageView) findViewById(R.id.iv_allyicon);
        tv_KickStartTime = (TextView) findViewById(R.id.tv_KickStartTime);
        tv_allyname=(TextView) findViewById(R.id.tv_allyname);
        tv_DonationAmt=(TextView) findViewById(R.id.tv_DonationAmt);
        String mess="ally_id:"+ally.getId();
        //showAlertDialog("Ally.id",mess);
        if(ally!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(ally.getIconpath());
            iv_allyicon.setImageBitmap(ServiceUtil.getRoundedCornerBitmap(bitmap));
            tv_allyname.setText(ally.getAlly_name());
            tv_KickStartTime.setText(ally.getKickStartTime());
            tv_DonationAmt.setText(String.valueOf(ally.getDonationAmt()));
        }
        iv_edit= (ImageView) findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AllyActivity.this,AllyList.class);
                startActivity(i);
            }
        });
        /*rateArr = new ArrayList<>();
        tv_targetStr = (TextView) findViewById(R.id.tv_targetStr);
        tempFile= new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        AsyncCallWS_getExchangeRate getExchangeRateTask = new AsyncCallWS_getExchangeRate();
        getExchangeRateTask.execute();
        strCurrency = currency;
        isAnonymous = false;
        isBuildupbyic = false;
        isTarget = true;

        //tv_slogan = (TextView) findViewById(R.id.tv_greeting);
        //tv_currency = (TextView) findViewById(R.id.tv_currency);
        //et_donatetime = (EditText) findViewById(R.id.et_donatetime);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        rl_footer = (RelativeLayout) findViewById(R.id.rl_footer);
        isDown = false;
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        RL_done = (RelativeLayout) findViewById(R.id.RL_done);
        tv_date = (TextView) findViewById(R.id.tv_date);
        ImageView iv_nextRE = (ImageView) findViewById(R.id.iv_nextRE);
        ImageView iv_Prev = (ImageView) findViewById(R.id.iv_Prev);
        iv_Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreNotificationList.class);
                startActivity(intent);
            }
        });*/
    }
    private Thread childThread = new Thread(){
        @Override
        public void run() {
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e){
                //ignore
            }
            SignalRUtil.sendAllyInformationtoGetUniqueAllyID(ally,getBase64String(realPath));
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
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        AsyncCallWS_getiCreditBalance getiCreditBalanceTask = new AsyncCallWS_getiCreditBalance();
        getiCreditBalanceTask.execute();
        AsyncCallWS_getMemberFG getMemberFGTask=new AsyncCallWS_getMemberFG();
        getMemberFGTask.execute();
        SignalRUtil.setHubConnect(context);*/
    }


    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(AllyActivity.this).create();
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
        String SOAP_ACTION = StaticVar.namespace + METHOD_NAME;
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
        String SOAP_ACTION =  StaticVar.namespace + METHOD_NAME;
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

}

