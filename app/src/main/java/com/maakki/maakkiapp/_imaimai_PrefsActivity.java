package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/4/19.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
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
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;
import java.util.Date;

public class _imaimai_PrefsActivity extends PreferenceActivity {
    private static String strBank_name, strBranch_name, strAccount_name, strAccount_no;
    private static String errMsg, errCode, maakki_id, timeStamp, identifyStr;
    private final String NAMESPACE = StaticVar.namespace;
    private final String URL = StaticVar.webURL+"WebService.asmx";
    private SharedPreferences.Editor prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    String role, maakki_id_account;
    Preference introducer_id;
    EditTextPreference bank_name, branch_name, account_no, account_name;
    SwitchPreference enable_sound, enable_vibrate, enable_display;

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //preference.setSummary(stringValue);
            //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
            if (preference instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) preference;
                //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
                etp.setSummary(stringValue);
                if (preference.getKey().equals("bank_name")) {
                    strBank_name = stringValue;
                    //pma.editBOCurrencyofCheck(BOCurrency);
                } else if (preference.getKey().equals("branch_name")) {
                    strBranch_name = stringValue;
                    //pma.editToolbar(StoreName);
                } else if (preference.getKey().equals("account_no")) {
                    strAccount_no = stringValue;
                } else if (preference.getKey().equals("account_name")) {
                    strAccount_name = stringValue;
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            if (!strBank_name.equals("") & !strBranch_name.equals("") & !strAccount_name.equals("") & !strAccount_no.equals("")) {
                AsyncCallWS_bindBank bindBankTask = new AsyncCallWS_bindBank();
                bindBankTask.execute();
            }
            return true;
        }
    };

    private static void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) preference;
            //Toast.makeText(getApplicationContext(), "stringValue:"+stringValue, Toast.LENGTH_LONG).show();
            etp.setSummary(value);
            //preference.setSummary(value);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
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
                //AsyncCallWS_bindBank bindBankTask = new AsyncCallWS_bindBank();
                //bindBankTask.execute();
                 finish();
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        /*role_getBankData = "member";
        maakki_id_account = maakki_id;
        AsyncCallWS_getBankData1 getBankDataTask = new AsyncCallWS_getBankData1();
        getBankDataTask.execute();*/
        strAccount_name = "";
        strAccount_no = "";
        strBank_name = "";
        strBranch_name = "";
        role="member";
        introducer_id = findPreference("introducer_id");
        bank_name = (EditTextPreference) findPreference("bank_name");
        branch_name = (EditTextPreference) findPreference("branch_name");
        account_no = (EditTextPreference) findPreference("account_no");
        account_name = (EditTextPreference) findPreference("account_name");
        setupSimplePreferencesScreen();
    }

    protected void onStart() {
        super.onStart();
        AsyncCallWS_getBankData getBankDataTask = new AsyncCallWS_getBankData();
        getBankDataTask.execute("member");
    }

    protected void on() {
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml._imaimai_preference);
        bindPreferenceSummaryToValue(findPreference("introducer_id"));
        bindPreferenceSummaryToValue(findPreference("bank_name"));
        bindPreferenceSummaryToValue(findPreference("branch_name"));
        bindPreferenceSummaryToValue(findPreference("account_name"));
        bindPreferenceSummaryToValue(findPreference("account_no"));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
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

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class CustomDialogPreference extends EditTextPreference {

        public CustomDialogPreference(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CustomDialogPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void showDialog(Bundle state) {
            super.showDialog(state);
            final Resources res = getContext().getResources();
            final Window window = getDialog().getWindow();
            final int green = res.getColor(android.R.color.holo_green_dark);

            // Title
            final int titleId = res.getIdentifier("alertTitle", "id", "android");
            final View title = window.findViewById(titleId);
            if (title != null) {
                ((TextView) title).setTextColor(green);
            }

            // Title divider
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = window.findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(green);
            }

            // EditText
            final View editText = window.findViewById(android.R.id.edit);
            if (editText != null) {
                //editText.setBackground(res.getDrawable(R.drawable.));
            }
        }
    }

    private void getWebService_bindBank() {
        String METHOD_NAME = "bindBank";
        String SOAP_ACTION = StaticVar.namespace+"bindBank";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + role + strAccount_no;
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role);
        request.addProperty("bank_name", strBank_name);
        request.addProperty("branch_name", strBranch_name);
        request.addProperty("account_no", strAccount_no);
        request.addProperty("account_name", strAccount_name);
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
            errMsg=e.getMessage();
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
                errMsg = "帐号只能是纯数字，不能含有其它字元（例如： - ）";
            } else if (errCode.equals("4")) {
                errMsg = "查无资料！";
            } else if (errCode.equals("5")) {
                errMsg = "所有银行资料皆必需有值，不能是空字串！";
            } else if (errCode.equals("6")) {
                errMsg = "不是金流中心！";
            } else if (errCode.equals("7")) {
                errMsg = "银行帐号已由其它人绑定（同一银行帐号只能给一个玛吉卡号使用）！";
            } else if (errCode.equals("8")) {
                errMsg = "点不足以支付修改银行信息费i用1,000元RMB（i）！";
            } else {
                errMsg = errCode;
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }/**/
    }

    private class AsyncCallWS_bindBank extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            //String role = params[0];
            getWebService_bindBank();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            /*if (errMsg.equals("")) {
                Toast.makeText(getApplicationContext(), "银行信息绑定成功！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "银行信息绑定失败：" + errMsg, Toast.LENGTH_LONG).show();
            }*/

            /*String strParam="";
            strParam="maakki_id:"+maakki_id+"\n"
                    +"role:"+role+"\n"
                    +"bank_name:"+strBank_name+"\n"
                    +"branch_name:"+strBranch_name+"\n"
                    +"account_no："+strAccount_no+"\n"
                    +"account_name"+strAccount_name+"\n"
                    +"timeStamp:"+timeStamp+"\n"
                    +"identifyStr:"+identifyStr;
                    showAlertDialog("Params",strParam);*/
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

    private class AsyncCallWS_getBankData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            String role = params[0];
            getWebService_getBankData(role);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String strMsg="";
            if (errMsg.equals("")) {
                //Toast.makeText(getApplicationContext(), "取得银行账户成功:" + strAccount_name + "/" + strBank_name, Toast.LENGTH_LONG).show();
                strMsg="取得银行账户成功";
                if (!strBank_name.equals("")) {
                    //setPreferenceSummary(bank_name,strBank_name);
                    resetPreferenceValue("bank_name",strBank_name);

                }
                if (!strBranch_name.equals("")) {
                    //branch_name.setSummary(strBranch_name);
                    resetPreferenceValue("branch_name",strBranch_name);
                }
                if (!strAccount_name.equals("")) {
                    //setPreferenceSummary(account_name,strAccount_name);
                    resetPreferenceValue("account_name",strAccount_name);
                }
                if (!strAccount_no.equals("")) {
                    //setPreferenceSummary(account_no,strAccount_no);
                    resetPreferenceValue("account_no", strAccount_no);
                }
            } else {
                //Toast.makeText(getApplicationContext(), "取得银行账户失败：" + errMsg, Toast.LENGTH_LONG).show();
                strMsg="取得银行账户失败:"+errMsg;
            }
            //showAlertDialog("Message:",strMsg);
            //AsyncCallWS_getBankData bindBankTask=new AsyncCallWS_getBankData();
            //bindBankTask.execute();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private void getWebService_getBankData(String role) {
        String METHOD_NAME = "getBankData";
        String SOAP_ACTION = StaticVar.namespace+"getBankData";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + role;
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role);
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
            strBank_name = json_read.getString("bank_name");
            strBranch_name = json_read.getString("branch_name");
            strAccount_no = json_read.getString("account_no");
            strAccount_name = json_read.getString("account_name");
            errCode = json_read.getString("errCode");
            if (errCode.equals("1")) {
                errMsg = "";
            } else if (errCode.equals("2")) {
                errMsg = "认证失败";
            } else if (errCode.equals("3")) {
                errMsg = "参数内容错误" + role;
            } else if (errCode.equals("4")) {
                errMsg = "查无资料";
            }

        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private void resetPreferenceValue(String key,String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putString(key, value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes

        // Now we have updated shared preference value, but in activity it still hold the old value
        this.resetElementValue(key,value);
    }

    private void resetElementValue(String key,String value) {
        // First get reference to edit-text view elements
        EditTextPreference myPrefText = (EditTextPreference) super.findPreference(key);
        // Now, manually update it's value to default/empty
        myPrefText.setText(value); // Now, if you click on the item, you'll see the value you've just set here
        myPrefText.setSummary(value);
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(_imaimai_PrefsActivity.this).create();
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
