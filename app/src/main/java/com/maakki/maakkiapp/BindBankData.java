package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class BindBankData extends Activity {
    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    private static String role, strBank_name, strBranch_name, strAccount_name, strAccount_no;
    private static String errMsg, errCode, maakki_id, timeStamp, identifyStr;
    private EditText et_bank_name, et_branch_name, et_account_no, et_account_name;
    private ImageView iv_check,iv_delete,iv_done,iv_back,iv_edit;
    private RelativeLayout RL_CB,RL_noBind,RL_main,RL_function_option;
    private CheckBox checkBox;
    private TextView mTextView,tv_hint,text_account_no,text_bank_name,text_branch_name,text_account_name;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bindbankinfo);
        // Hiding the action bar
        getActionBar().hide();
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        mTextView = (TextView) findViewById(R.id.tv_progress);
        tv_hint=(TextView)findViewById(R.id.tv_hint);
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        iv_delete= (ImageView) findViewById(R.id.iv_delete);
        iv_check= (ImageView) findViewById(R.id.iv_check);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_done= (ImageView) findViewById(R.id.iv_done);
        iv_back= (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_back.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                finish();
            }
        });
        text_bank_name = (TextView) findViewById(R.id.text_bank_name);
        text_branch_name = (TextView) findViewById(R.id.text_branch_name);
        text_account_name = (TextView) findViewById(R.id.text_account_name);
        text_account_no = (TextView) findViewById(R.id.text_account_no);
        et_account_no = (EditText) findViewById(R.id.et_account_no);
        et_bank_name = (EditText) findViewById(R.id.et_bank_name);
        et_branch_name = (EditText) findViewById(R.id.et_branch_name);
        et_account_name = (EditText) findViewById(R.id.et_account_name);
        et_account_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!et_account_no.getText().toString().trim().isEmpty()) {
                        if (!et_bank_name.getText().toString().trim().isEmpty()) {
                            if (!et_branch_name.getText().toString().trim().isEmpty()) {
                                if (et_account_name.getText().toString().trim().isEmpty()) {
                                    showAlertDialog("请填入账户名称", "账户名称不能为空白");
                                    et_account_name.requestFocus();
                                }
                            } else {
                                showAlertDialog("请填入分行名称", "分行名称不能为空白");
                                et_branch_name.requestFocus();
                            }
                        } else {
                            showAlertDialog("请填入银行名称", "银行名称不能为空白");
                            et_bank_name.requestFocus();
                        }
                    } else {
                        showAlertDialog("请填入银行账号", "银行账号不能为空白");
                        et_account_no.requestFocus();
                    }
                }
            }
        });
        RL_CB = (RelativeLayout) findViewById(R.id.RL_CB);

        checkBox = (CheckBox) findViewById(R.id.checkbox);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        checkBox.setButtonDrawable(id);
        checkBox.setOnCheckedChangeListener(chklistener);
        RL_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_account_name.getText().toString().isEmpty()) {
                    showAlertDialog("请填入账户名称", "账户名称不能为空白");
                    et_account_name.requestFocus();
                }
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }

            }
        });
        iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    //Toast.makeText(getApplicationContext(), "Data input completed!", Toast.LENGTH_LONG).show();
                    AsyncCallWS_bindBank bindBankTask = new AsyncCallWS_bindBank();
                    bindBankTask.execute();
                } else {
                    showAlertDialog("请确认申请者同意服务条款", "确定已经让申请者详读并完整明白且同意玛吉网服务条款的所有规则后，请点选灰色文字方块。");
                }
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_edit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
                AlertDialog alertDialog = new AlertDialog.Builder(BindBankData.this).create();
                alertDialog.setTitle("要编辑已经绑定的银行信息？");
                alertDialog.setMessage("您确定要编辑已经绑定的银行信息？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                editBankData();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                iv_edit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                            }
                        });
                alertDialog.show();
            }
        });
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_delete.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
                AlertDialog alertDialog = new AlertDialog.Builder(BindBankData.this).create();
                alertDialog.setTitle("要删除已经绑定的银行信息？");
                alertDialog.setMessage("您确定要删除已经绑定的银行信息？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AsyncCallWS_deleteBankData deleteBankDataTask = new AsyncCallWS_deleteBankData();
                                deleteBankDataTask.execute();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "略过",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                iv_delete.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                            }
                        });
                alertDialog.show();
            }
        });
        RL_function_option = (RelativeLayout) findViewById(R.id.RL_function_option);
        RL_main = (RelativeLayout) findViewById(R.id.RL_main);
        RL_noBind = (RelativeLayout) findViewById(R.id.RL_noBind);
        RL_noBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_noBind.setVisibility(View.GONE);
                et_account_name.setText("");
                et_account_name.setHint("请输入账户名称");
                et_bank_name.setText("");
                et_bank_name.setHint("请输入银行名称");
                et_branch_name.setText("");
                et_branch_name.setHint("请输入银行的分行名称");
                et_account_no.setText("");
                et_account_no.setHint("请输入注册者的银行账号");
                et_branch_name.setVisibility(View.VISIBLE);
                et_bank_name.setVisibility(View.VISIBLE);
                et_account_name.setVisibility(View.VISIBLE);
                et_account_no.setVisibility(View.VISIBLE);
                text_account_name.setVisibility(View.GONE);
                text_account_no.setVisibility(View.GONE);
                text_bank_name.setVisibility(View.GONE);
                text_branch_name.setVisibility(View.GONE);
                tv_hint.setVisibility(View.GONE);
                RL_main.setVisibility(View.VISIBLE);
                RL_function_option.setVisibility(View.VISIBLE);
                iv_check.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                iv_check.setVisibility(View.VISIBLE);
                RL_CB.setVisibility(View.VISIBLE);
                iv_delete.setVisibility(View.GONE);
                iv_edit.setVisibility(View.GONE);
            }
        });

    }
    private void editBankData(){
        checkBox.setChecked(false);
        RL_CB.setVisibility(View.VISIBLE);
        et_account_no.setVisibility(View.VISIBLE);
        et_bank_name.setVisibility(View.VISIBLE);
        et_branch_name.setVisibility(View.VISIBLE);
        et_account_name.setVisibility(View.VISIBLE);
        text_account_no.setVisibility(View.GONE);
        text_bank_name.setVisibility(View.GONE);
        text_branch_name.setVisibility(View.GONE);
        text_account_name.setVisibility(View.GONE);
        iv_check.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
        iv_check.setVisibility(View.VISIBLE);
        iv_delete.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        iv_done.setVisibility(View.GONE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        role = "member";
        AsyncCallWS_getBankData getBankDataTask = new AsyncCallWS_getBankData();
        getBankDataTask.execute("member");
    }


    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(BindBankData.this).create();
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

    private void getWebService_bindBank() {
        String METHOD_NAME = "bindBank";
        String SOAP_ACTION = "http://www.maakki.com/bindBank";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + role + et_account_no.getText().toString().trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("role", role);
        request.addProperty("bank_name", et_bank_name.getText().toString().trim());
        request.addProperty("branch_name", et_branch_name.getText().toString().trim());
        request.addProperty("account_no", et_account_no.getText().toString().trim());
        request.addProperty("account_name", et_account_name.getText().toString().trim());
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
        }
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
            if (errMsg.equals("")) {
                text_bank_name.setText(et_bank_name.getText().toString().trim());
                text_branch_name.setText(et_branch_name.getText().toString().trim());
                text_account_no.setText(et_account_no.getText().toString().trim());
                text_account_name.setText(et_account_name.getText().toString().trim());
                et_bank_name.setVisibility(View.GONE);
                et_branch_name.setVisibility(View.GONE);
                et_account_no.setVisibility(View.GONE);
                et_account_name.setVisibility(View.GONE);
                text_bank_name.setVisibility(View.VISIBLE);
                text_branch_name.setVisibility(View.VISIBLE);
                text_account_no.setVisibility(View.VISIBLE);
                text_account_name.setVisibility(View.VISIBLE);
                RL_CB.setVisibility(View.GONE);
                iv_check.setVisibility(View.GONE);
                iv_edit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
                iv_edit.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
            } else {
                //Toast.makeText(getApplicationContext(), "银行信息绑定失败：" + errMsg, Toast.LENGTH_LONG).show();
                tv_hint.setText("银行信息绑定失败：" + errMsg);
                tv_hint.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(),"latest_tradeID:"+latest_tradeID,Toast.LENGTH_LONG).show();
            tv_hint.setVisibility(View.GONE);
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

    public String getHashCode(String Gkey) {
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

    private class AsyncCallWS_getBankData extends AsyncTask<String,Integer,Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            String role = params[0];
            getWebService_getBankData(role);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tv_hint.setVisibility(View.GONE);
            String strMsg = "";
            if (errMsg.equals("")) {
                //Toast.makeText(getApplicationContext(), "取得银行账户成功:" + strAccount_name + "/" + strBank_name, Toast.LENGTH_LONG).show();
                RL_CB.setVisibility(View.GONE);
                if (!strBank_name.equals("")) {
                    et_bank_name.setText(strBank_name);
                    text_bank_name.setText(strBank_name);
                    et_bank_name.setVisibility(View.GONE);
                    text_bank_name.setVisibility(View.VISIBLE);
                }
                if (!strBranch_name.equals("")) {
                    et_branch_name.setText(strBranch_name);
                    text_branch_name.setText(strBranch_name);
                    et_branch_name.setVisibility(View.GONE);
                    text_branch_name.setVisibility(View.VISIBLE);
                }
                if (!strAccount_name.equals("")) {
                    et_account_name.setText(strAccount_name);
                    text_account_name.setText(strAccount_name);
                    et_account_name.setVisibility(View.GONE);
                    text_account_name.setVisibility(View.VISIBLE);
                }
                if (!strAccount_no.equals("")) {
                    et_account_no.setText(strAccount_no);
                    text_account_no.setText(strAccount_no);
                    et_account_no.setVisibility(View.GONE);
                    text_account_no.setVisibility(View.VISIBLE);
                }
                //RL_noBind.setVisibility(View.VISIBLE);
                RL_main.setVisibility(View.VISIBLE);
                RL_function_option.setVisibility(View.VISIBLE);
                iv_edit.setVisibility(View.VISIBLE);
                iv_delete.setVisibility(View.VISIBLE);
            } else {
                RL_noBind.setVisibility(View.VISIBLE);
                RL_main.setVisibility(View.GONE);
                RL_function_option.setVisibility(View.GONE);
            }

            //showAlertDialog("Message:", strMsg);
            //AsyncCallWS_getBankData bindBankTask=new AsyncCallWS_getBankData();
            //bindBankTask.execute();
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), "信息同步中..", Toast.LENGTH_LONG).show();
            tv_hint.setVisibility(View.VISIBLE);
        }

        protected void onProgressUpdate(Integer... progress) {
            // Display the progress on text view
            //mTextView.setText(""+progress[0] + " %");
            // Update the progress bar
            //mProgressBar.setProgress(progress[0]);
        }
    }

    private void getWebService_getBankData(String role) {
        String METHOD_NAME = "getBankData";
        String SOAP_ACTION = "http://www.maakki.com/getBankData";
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

    //deleteBankData
    private class AsyncCallWS_deleteBankData extends AsyncTask<String,Integer,Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            deleteBankData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv_hint.setVisibility(View.GONE);
            String strMsg = "";
            if (errMsg.equals("")) {
                RL_noBind.setVisibility(View.VISIBLE);
                RL_main.setVisibility(View.GONE);
                RL_function_option.setVisibility(View.GONE);
            } else {
                strMsg = "无法删除您在本网站上绑定的银行信息：" + errMsg;
                showAlertDialog("Error Message", strMsg);
            }
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), "信息同步中..", Toast.LENGTH_LONG).show();
            //tv_hint.setVisibility(View.VISIBLE);
        }

        protected void onProgressUpdate(Integer... progress) {
            // Display the progress on text view
            //mTextView.setText(""+progress[0] + " %");
            // Update the progress bar
            //mProgressBar.setProgress(progress[0]);
        }
    }

    private void deleteBankData() {
        String METHOD_NAME = "deleteBankData";
        String SOAP_ACTION = "http://www.maakki.com/deleteBankData";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim();
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
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
            }
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
    }

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (checkBox.isChecked()) {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_accent));
                iv_check.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_maakki_red));
            } else {
                RL_CB.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_warm_grey));
                iv_check.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tips_circle_grey));
            }
        }
    };

}
