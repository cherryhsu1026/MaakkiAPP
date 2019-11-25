package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/4/19.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;
import java.util.Date;

public class POSPrefsActivity extends PreferenceActivity {
    private static String appVersion;
    private static String BOCurrency, StoreName, Discount, isPercent, MR, errMsg;
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    private Preference pfMR;
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            pfMR = findPreference("MR");

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) preference;
//                //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
                etp.setSummary(stringValue);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            if (preference.getKey().equals("credit_options")) {
                BOCurrency = stringValue;
                //pma.editBOCurrencyofCheck(BOCurrency);
            } else if (preference.getKey().equals("StoreName")) {
                StoreName = stringValue;
                //pma.editToolbar(StoreName);
            } else if (preference.getKey().equals("Discount")) {
                Discount = stringValue;
            } else if (preference.getKey().equals("isPercent")) {
                isPercent = stringValue;
                if(Boolean.parseBoolean(isPercent)){
                    setPreferenceSummary(pfMR,MR+" %");
                }else{
                    setPreferenceSummary(pfMR,MR+" "+BOCurrency+"(i)");
                }
                /*revisepfMR(isPercent);*/
            } else if (preference.getKey().equals("MR")) {
                MR = stringValue;
                if(Boolean.parseBoolean(isPercent)){
                    preference.setSummary(MR+" %");
                }else{
                    preference.setSummary(MR+" "+BOCurrency+"(i)");
                }
            }
            AsyncCallWS_setBOData setBODataTask = new AsyncCallWS_setBOData();
            setBODataTask.execute();
            return true;
        }
    };
    protected void onPause(){
       super.onPause();
        /*showAlertDialog("HiHi","12345");
        AsyncCallWS_setBOData setBODataTask = new AsyncCallWS_setBOData();
        setBODataTask.execute();*/
    }

    private static void setPreferenceSummary(Preference preference, String value) {
        preference.setSummary(value);
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

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(POSPrefsActivity.this, POS_MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }
        setupSimplePreferencesScreen();
        //Toast.makeText(getApplicationContext(), "welcome_message:"+welMsg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_pos);
        bindPreferenceSummaryToValue(findPreference("credit_options"));
        bindPreferenceSummaryToValue(findPreference("StoreName"));
        bindPreferenceSummaryToValue(findPreference("Discount"));
        bindPreferenceSummaryToValue(findPreference("isPercent"));
        bindPreferenceSummaryToValue(findPreference("MR"));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        BOCurrency = prefs.getString("credit_options", "RMB");
        Preference pfcredit_options = findPreference("credit_options");
        pfcredit_options.setSummary(BOCurrency);

        StoreName = prefs.getString("StoreName", "");
        Preference pfStoreName = findPreference("StoreName");
        pfStoreName.setSummary(StoreName);

        Discount = prefs.getString("Discount", "0");
        Preference pfDiscount = findPreference("Discount");
        pfDiscount.setSummary(Discount);

        isPercent = prefs.getString("isPercent", "true");
        Preference pfisPercent = findPreference("isPercent");
        ListPreference listPreference = (ListPreference) pfisPercent;
        int index = listPreference.findIndexOfValue(isPercent);
        pfisPercent.setSummary(
                index >= 0
                        ? listPreference.getEntries()[index]
                        : null
        );
        MR = prefs.getString("MR", "0");
        pfMR = findPreference("MR");
        pfMR.setSummary(MR);
        if(Boolean.parseBoolean(isPercent)){
            pfMR.setSummary(MR+" %");
        }else{
            pfMR.setSummary(MR+" "+BOCurrency+"(i)");
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        //Toast.makeText(getApplicationContext(), preference.getSummary(),Toast.LENGTH_LONG).show();
        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first

        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }
        //pfMR = findPreference("MR");
        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void getWebService_setBOData() {
        //Create request(int bo_id, Int64 timeStamp, String identifyStr)
        String METHOD_NAME = "setBOData";
        String SOAP_ACTION = "http://www.maakki.com/setBOData";
        //String METHOD_NAME = "getOnlineList";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String bo_maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        //a(int bo_id, int discount, int MR, bool isPercent, string currency, string storeName, Int64 timeStamp, String identifyStr)
        String timeStamp = String.valueOf(new Date().getTime());
        String identifyStr = getHashCode(bo_maakkiid.trim() + "M@@kki.cc" + timeStamp.trim() + BOCurrency + MR).toUpperCase();
        //identifyStr bo_id+"M@@kki.cc"+timeStamp  (hash256)
        request.addProperty("bo_id", bo_maakkiid);
        request.addProperty("discount", Discount);
        request.addProperty("MR", MR);
        PropertyInfo p = new PropertyInfo();
        p.setName("isPercent");
        p.setValue(Boolean.valueOf(isPercent));
        p.setType(Boolean.class);
        request.addProperty(p);/**/
        //request.addProperty("isPercent", Boolean.valueOf(isPercent));
        request.addProperty("currency", BOCurrency);
        request.addProperty("storeName", StoreName);
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
        } catch (Exception e) {
            //BOCurrency=e.toString()+"/"+identifyStr+"/"+tmp;
        }/* */
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

    private class AsyncCallWS_setBOData extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_setBOData();
            //POS_MainActivity.editToolbar(StoreName);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String Msg;
            if (!errMsg.equals("")) {
                Msg = errMsg;
            } else {
                Msg = "Succeed!";
            }
            //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), BOCurrency , Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(POSPrefsActivity.this).create();
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
