package com.maakki.maakkiapp;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class Utils {
    public static String zeros(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('0'); }
        return builder.toString();
    }

    //region 呼叫 WebService

    /**
     * 呼叫 WebService
     *
     * @param wsURL      WebService 的網址（含程式, 例：http://192.168.1.1/WebService.asmx)
     * @param namespace  命名空間的名稱
     * @param methodName 方法名稱
     * @param requestVar 傳遞的參數 (可傳空的HashMap 物件進來)
     * @return 結果 (String)
     */
    public static String callWebService(String wsURL, String namespace, String methodName, HashMap<String, String> requestVar) {
        String SOAP_ACTION = namespace + methodName;
        SoapObject request = new SoapObject(namespace, methodName);

        //加上傳遞的參數
        if (!requestVar.isEmpty()) {
            for (Object key : requestVar.keySet()) {
                //Log.e("cherry",key+":"+requestVar.get(key));
                request.addProperty(key.toString(), requestVar.get(key));
            }
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(wsURL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
        } catch (IOException e) {
            Log.e("Cherry", "androidHttpTransport.call IOException:" + e.toString()+ ",method name="+methodName);
        } catch (XmlPullParserException e) {
            Log.e("Cherry", "androidHttpTransport.call XmlPullParserException:" + e.toString()+ ",method name="+methodName);
        }

        SoapPrimitive soapPrimitive = null;
        try {

            soapPrimitive = (SoapPrimitive) envelope.getResponse();
        } catch (Exception e) {
            Log.e("Cherry", "call webservice error:" + e.toString()+ ",method name="+methodName);
        }

        String tmp = soapPrimitive == null ? "" : soapPrimitive.toString();
        if (!tmp.isEmpty() && tmp.indexOf("{") >= 0)
            tmp = tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
        return tmp;
    }
    //endregion
}
