package com.maakki.maakkiapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;

public class ServiceUtil {
    private static String NAMESPACE = "http://www.maakki.com/";
    private static String URL = StaticVar.webURL+"/WebServiceIA.asmx";
    private static String URL_WS = StaticVar.webURL+"/WebService.asmx";
    static String errMsg,errCode;
    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }
        //logger.debug(String.format("service [%s] running status:[%s]", serviceName, isServiceRunning));
        return isServiceRunning;
    }

    public static void startService(Context context, String action) {
        //logger.debug(String.format("start service [%s]", action));
        Intent mIntent = new Intent();
        mIntent.setAction(action);
        mIntent.setPackage(context.getPackageName());
        context.startService(mIntent);
    }

    public static void stopService(Context context, String action) {
        //logger.debug(String.format("start service [%s]", action));
        Intent mIntent = new Intent();
        mIntent.setAction(action);
        mIntent.setPackage(context.getPackageName());
        context.stopService(mIntent);
    }
    public static void showAlertDialog(Context context,String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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

    public static void MDFSponsor(Context context,String maakki_id,String target_id,String cMemID,String sponsorAmt,String iCreditPassword,String isUSD,String isAnonymous) {
        String METHOD_NAME = "MDFSponsor";
        String SOAP_ACTION = "http://www.maakki.com/" + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id + "MDF-M@@kki.cc" + timeStamp.trim() + target_id + sponsorAmt + iCreditPassword;
        String identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("target_id", target_id);
        request.addProperty("sponsorAmt", sponsorAmt);
        request.addProperty("isUSD", isUSD);
        request.addProperty("iCreditPassword", iCreditPassword);
        request.addProperty("isAnonymous", isAnonymous);
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
            errMsg = "";
            if (errCode.equals("2")) {
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
                errMsg = "系统繁忙中，请稍后再试！";
            } else if (errCode.equals("14")) {
                errMsg = "系统繁忙中，请稍后再试！";
            } else if (errCode.equals("15")) {
                errMsg = "您没有参与MDF的权限";
            } else if (errCode.equals("16")) {
                errMsg = "请勿指定自己为赞助对象";
            }
            Double actualSponsor = json_read.getDouble("actualSponsorAmt");
            String message;
            if(actualSponsor>0){
                message=formatDoubleToString(actualSponsor)+" USD(i):"+isAnonymous;
            }else{
                message=errMsg+"，这个红包领不了。:"+isAnonymous;
            }
            //SignalRUtil.setHubConnect(context);
            SignalRUtil.feedBackSponsorResult(cMemID,message);
            //String targetStr = json_read.getString("targetStr");
        } catch (Exception e) {
        }
    }
    private static String formatDoubleToString(Double d) {
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

    public static String getHashCode(String Gkey) { //得到毫秒数

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
    public static void showToast(Context context,String message){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastView = inflater.inflate(R.layout.activity_toast_custom_view, null);
        //View toastView =inflater(R.layout.activity_toast_custom_view, null);
        //View toastView = getActivity().getLayoutInflater().inflate(R.layout.activity_toast_custom_view, null);
        TextView customToastText=(TextView) toastView.findViewById(R.id.customToastText);
        customToastText.setText(message);
        // Initiate the Toast instance.
        Toast toast = new Toast(context);
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }
    public static boolean isSpecificServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 20;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}