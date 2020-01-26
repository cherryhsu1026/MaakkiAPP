package com.maakki.maakkiapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class SignalRUtil {
    private static int intervalMessageTime = 1000 * 2;
    private static final int Timer_Check_INTERVAL = 1000 * 60; // 60 seconds
    private static final int DAEMON_CHECK_INTERVAL = 1000 * 3;
    private static final String HUB_URL = StaticVar.webURL+"/";
    private static final String HUB_NAME = "maakkiHub";
    private static final String HUB_Method_Connection = "userConnected";
    private static final String HUB_chat_private_Send = "chatSend";
    private static final String HUB_Hidden_Signal_Send = "Hidden_Signal_Send";
    private static final String HUB_chat_public_Send = "chat_public";
    private static final String HUB_EVENT_Receive = "chatReceive";
    private static HubProxy mHub;
    private static SignalRFuture<Void> mSignalRFuture;
    private static String mMemID, mMaakkiID;
    private static String mName = "";
    private static String mPicfile = "";
    private static Long currenttime, LastMessageTime, intervalTime;
    private Context context;

    public static void setHubConnect(Context context) {
        HubConnection connection = new HubConnection(HUB_URL);
        mHub = connection.createHubProxy(HUB_NAME);
        mSignalRFuture = connection.start(new ServerSentEventsTransport(connection.getLogger()));
        mMemID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key1, "");
        mMaakkiID = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        mName = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key2, "");
        mPicfile = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key3, "");
        //開啟連線
        try {
            mSignalRFuture.get();
            mHub.invoke(HUB_Method_Connection, mMemID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessageToMe() {
        ArrayList L = new ArrayList();
        String mess = "MeSsFrOmAlaRm";
        //L.add(Contactid);
        L.add(mMemID);
        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendSponsorNoticeToSomeone(String receiver_id, String sponsorAmt) {
        ArrayList L = new ArrayList();
        L.add(receiver_id);
        String mess = "sendSpooSorNoticeToReCeIv:" + sponsorAmt;

        //chat.server.chatSend(L, $('#hfLonigMemberID').val(), mName, picfile, $('#message').val())
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendAllyInformationtoGetUniqueAllyID(Ally ally, String ImageStr) {
        ArrayList L = new ArrayList();
        L.add(198947);
        String mess = "sendAllyInFotoGetUquealLyId:" +
                ally.getId() +
                ":" + ally.getFounder_id() +
                ":" + ally.getDonationAmt() +
                ":" + ally.getAllyStatus() +
                ":" + ally.getKickStartTime() +
                ":" + ally.getToken() +
                ":" + ImageStr;
        String mType = "sendAllyInformationtoGetUniqueAllyID";
        String sender = mMemID + ":" + mPicfile + ":" + mName;
        try {
            //mHub.invoke(HUB_chat_private_Send, L, mMemID, ally.getAlly_name(), ImageStr, mess).get();
            //Hidden_Signal_Send(List<string> groupID, string memberid, string mType, string message, string subMessage)
            mHub.invoke(HUB_Hidden_Signal_Send, L, sender, mType, ally.getAlly_name(), mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToSomeone(String cMemid) {
        ArrayList L = new ArrayList();
        String mess = "MeSsFrOmHanDsHaKGin:";
        L.add(cMemid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static boolean CheckSomeoneOnline(final Context context, String cMemId) {
        String messAge = "";
        LastMessageTime = 0L;
        setHubConnect(context);
        sendMessageToSomeone(cMemId);
        messAge += cMemId;
        String getSupplierAns = SharedPreferencesHelper.getSharedPreferencesString(context, SharedPreferencesHelper.SharedPreferencesKeys.key14, "0:0");
        messAge += "/" + getSupplierAns;
        if (cMemId.equals(getSupplierAns.split(":")[0])) {
            LastMessageTime = Long.parseLong(getSupplierAns.split(":")[1]);
        }
        currenttime = new Date().getTime();
        intervalTime = currenttime - LastMessageTime;
        messAge += "/" + LastMessageTime + "/" + intervalTime;
        if (intervalTime > intervalMessageTime) {
            return false;
        } else {
            return true;
        }
    }

    private void sendLastTimeCustomerServiceOnline() {
        String mess = "sEnLatTiMCutMeSVieOLe:" + (new Date()).getTime();
        try {
            mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendRedenvelope(Boolean isAnonymous, String maakkiid, String envelopeid, String sponsorAmt, String currency, String cityname, String greetings) {
        String mess = "seNdReDeNvEloQpe:" + maakkiid + ":" + envelopeid + ":" + sponsorAmt + ":" + currency + ":" + cityname + ":" + greetings;
        if (isAnonymous) {
            mName = "Anonymous";
        }
        try {
            mHub.invoke(HUB_chat_public_Send, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void InquiryNotFinishedRE() {
        ArrayList L=new ArrayList();
        L.add(198947);
        String mType = "InquiryNotFinishedRE";
        if (!mPicfile.equals("00003.jpg")) {
            try {
                //Hidden_Signal_Send(List<string> groupID, string memberid, string mType, string message, string subMessage)
                mHub.invoke(HUB_Hidden_Signal_Send, L, mMemID, mType, mName, mPicfile).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
                     //feedBackredEnelopeInfo_notFinishedYet(mMaakkiID,cMemID,reDAO.getNotFinishedRE(cMemID));
    public static void feedBackredEnelopeInfo_notFinishedYet(String cMaakkiid, String cMemid, redEnvelope_Master rem) {
        ArrayList L = new ArrayList();
        L.add(cMemid);
        String sponsorAmt = formatDoubleToString(Double.parseDouble(String.valueOf(rem.getDonateAmt())) / rem.getReceiver_no());
        String greeting="恭喜发财，平安喜乐！";
        if(rem.getSlogan()!=null){
            greeting=rem.getSlogan();
        }
        String message = "feedBackredEnelopeInfo_notFinishedYet:" +cMaakkiid + ":" + rem.getId() + ":" + sponsorAmt + ":" + rem.getCurrency() + ":" + "" + ":" + greeting;
        if (rem.getIsAnonymous()) {
            mName = "Anonymous";
        }
        String mType = "feedBackredEnelopeInfo_notFinishedYet";
        String subMessage = "";
        String sender = mMemID + ":" + mPicfile + ":" + mName;
        try {
            mHub.invoke(HUB_Hidden_Signal_Send, L, sender, mType, message, subMessage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static void feedBackredEnelopeInfo_NF(String cMaakkiid, String cMemid, redEnvelope_Master rem,String cityname) {
        ArrayList L = new ArrayList();
        L.add(cMemid);
        String sponsorAmt = formatDoubleToString(Double.parseDouble(String.valueOf(rem.getDonateAmt())) / rem.getReceiver_no());
        String greeting="恭喜发财，平安喜乐！";
        /*if(rem.getSlogan()!=null){
            greeting=rem.getSlogan();
        }*/
        String message = "feedBackredEnelopeInfo_NF:" +cMaakkiid + ":" + rem.getId() + ":" + sponsorAmt + ":" + rem.getCurrency() + ":" + cityname + ":" + greeting;
        if (rem.getIsAnonymous()) {
            mName = "Anonymous";
        }
        String mType = "feedBackredEnelopeInfo_NF";
        String subMessage = "";
        String sender = mMemID + ":" + mPicfile + ":" + mName;
        try {
            mHub.invoke(HUB_Hidden_Signal_Send, L, sender, mType, message, subMessage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendRedenvelope_friendlimited(ArrayList L, Boolean isAnonymous, String maakkiid, String envelopeid, String sponsorAmt, String currency, String cityname, String greetings) {
        String message = "seNdReDeNvEloQpe:" + maakkiid + ":" + envelopeid + ":" + sponsorAmt + ":" + currency + ":" + cityname + ":" + greetings;
        if (isAnonymous) {
            mName = "Anonymous";
        }
        String mType = "seNdReDeNvEloQpe";
        String subMessage = "";
        String sender = mMemID + ":" + mPicfile + ":" + mName;

        try {
            //Hidden_Signal_Send(List<string> groupID, string memberid, string mType, string message, string subMessage)
            mHub.invoke(HUB_Hidden_Signal_Send, L, sender, mType, message, subMessage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void agreeToTakeSponsor(String cMemid, String maakkiid, String envelope_id) {
        ArrayList L = new ArrayList();
        String mess = "agrEeToTaKesPONsor:" + mMaakkiID + ":" + mMemID + ":" + maakkiid + ":" + envelope_id;
        L.add(cMemid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //public static void feedBackSpondorResult(String cMemid,String mMemID,String  mPicfile,String mName,String result) {
    public static void feedBackSponsorResult(String cMemid, String result) {
        ArrayList L = new ArrayList();
        String mess = "fiDbAkSpoNtorResUlT:" + result;
        L.add(cMemid);
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
            //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static void feedbackUniqueAllyIdtofounder(String MemID_founder, String idStr) {
        ArrayList L = new ArrayList();
        L.add(MemID_founder);
        //mMemID="198947";
        String mess = "fEdbaKUniKALlyIdtoFoUNdr:" + idStr;
        try {
            mHub.invoke(HUB_chat_private_Send, L, mMemID, mName, mPicfile, mess).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}