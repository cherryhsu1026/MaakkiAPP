package com.maakki.maakkiapp;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;

public class GOCMain2 extends Activity {

    private final String NAMESPACE = "http://www.maakki.com/";
    private final String URL = "http://www.maakki.com/WebService.asmx";
    Scene mAScene, mAnotherScene;
    ViewGroup mSceneRoot;
    Transition mATransition;
    Animation startAnimation;
    Integer SceneCode = 0;//Start_Scene_Code=0,End_Scene_Code=1;
    private ImageView imgLogo;
    private TextView tv_question, tv_slogan, tv_goc, tv_content1, tv_content2, tv_gain, tv_30, tv_60, tv_90, tv_15, tv_url;
    private ImageView ivBack;
    private String currency, errMsg = "", days = "15", maakki_id, timeStamp, identifyStr;
    private Double initialiCredit, endingiCredit, midtermIn, midtermOut, cost, income, balance, oCost, sell, initialAmt, endingAmt, GOC = 0.0, initialRate, endingRate, bonus, revenue;
    private LinearLayout llayout, scenellayout;
    private FrameLayout frameLayout;
    private SwipeDetector swipeDetector;

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

    //Boolean isPI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goc_main2);
        // Hiding the action bar
        getActionBar().hide();
        swipeDetector = new SwipeDetector();
        startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        //imageView.startAnimation(startAnimation);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);

        //frameLayout = (FrameLayout) findViewById(R.id.scene_root);
        // Create the scene root for the scenes in this app
        mSceneRoot = (ViewGroup) findViewById(R.id.scene_root);
        //mSceneRoot.startAnimation(startAnimation);
        // Create the scenes
        mAScene = Scene.getSceneForLayout(mSceneRoot, R.layout.a_scene, this);
        mAnotherScene = Scene.getSceneForLayout(mSceneRoot, R.layout.another_scene, this);
        mATransition =
                TransitionInflater.from(this).inflateTransition(R.transition.fade_transition);
        TransitionManager.beginDelayedTransition(mSceneRoot);

        /*String[] PIId = getApplicationContext().getResources().getStringArray(R.array.Project_involved_Makki_id);
        int count = PIId.length;
        String Maakkiid = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        for (int i = 0; i < count; i++) {
            if (PIId[i].equals(Maakkiid)) {
                isPI = true;
            }
        }*/
        setAScene();
    }

    private void setAScene() {
        tv_question = (TextView) findViewById(R.id.tv_question);
        tv_slogan = (TextView) findViewById(R.id.tv_greeting);
        tv_gain = (TextView) findViewById(R.id.tv_gain);
        tv_30 = (TextView) findViewById(R.id.tv_30);
        tv_60 = (TextView) findViewById(R.id.tv_60);
        tv_90 = (TextView) findViewById(R.id.tv_90);
        tv_15 = (TextView) findViewById(R.id.tv_15);
        tv_goc = (TextView) findViewById(R.id.tv_goc);
        tv_content1 = (TextView) findViewById(R.id.tv_content1);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        llayout = (LinearLayout) findViewById(R.id.days_option);
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_url.setText(getIntervalDate(-15));
        tv_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv30Onclick();
            }
        });
        tv_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv60Onclick();
            }
        });
        tv_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv90Onclick();
            }
        });
        tv_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv15Onclick();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String redUrl = StaticVar.webURL+"/MCoins/MCoinsQuery.aspx";
                Intent intent = new Intent(GOCMain2.this, WebMain2.class);
                intent.putExtra("redirUrl", redUrl);
                startActivity(intent);
            }
        });
        tv_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAnotherScene();
            }
        });
        tv_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAnotherScene();
            }
        });
        tv_goc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAnotherScene();
            }
        });
        tv_content1.setOnTouchListener(swipeDetector);
        tv_content1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (swipeDetector.swipeDetected()) {
                    //goAnotherScene();
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                        if (days.equals("90")) {
                            tv60Onclick();
                        } else if (days.equals("60")) {
                            tv30Onclick();
                        } else if (days.equals("30")) {
                            tv15Onclick();
                        }
                    } else if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        if (days.equals("15")) {
                            tv30Onclick();
                        } else if (days.equals("30")) {
                            tv60Onclick();
                        } else if (days.equals("60")) {
                            tv90Onclick();
                        }
                    }
                }
            }
        });
        //Toast.makeText(getApplicationContext(), "isPI：" +isPI, Toast.LENGTH_LONG).show();
        //if (isPI) {
            imgLogo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    imgLogo.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(GOCMain2.this, imaimai_order.class);
                    startActivity(intent);
                }
            });
        //}
        AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        iCreditGOCTask.execute();

    }

    private void tv15Onclick() {
        tv_30.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_60.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_90.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_15.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        days = "15";
        tv_url.setText(getIntervalDate(-15));
        tv_30.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_60.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_90.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_15.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        iCreditGOCTask.execute();
    }

    private void tv30Onclick() {
        tv_30.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_60.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_90.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_15.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "30";
        tv_url.setText(getIntervalDate(-30));
        tv_30.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_60.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_90.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_15.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        iCreditGOCTask.execute();
    }

    private void tv60Onclick() {
        tv_30.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_60.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_90.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_15.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "60";
        tv_url.setText(getIntervalDate(-60));
        tv_30.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_60.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_90.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_15.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        iCreditGOCTask.execute();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //imgLogo.setVisibility(View.VISIBLE);
        SlideToDown(imgLogo);
        //Toast.makeText(getApplicationContext(), "order_id:"+order_id+"\nlistOrderDetail.size():"+listOrderDetail.size(), Toast.LENGTH_LONG).show();
    }

    private void tv90Onclick() {
        tv_30.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_60.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        tv_90.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_white));
        tv_15.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.textview_border_grey));
        days = "90";
        tv_url.setText(getIntervalDate(-90));
        tv_30.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_60.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        tv_90.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        tv_15.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_gray));
        AsyncCallWS_iCreditGOC iCreditGOCTask = new AsyncCallWS_iCreditGOC();
        iCreditGOCTask.execute();
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

    private void getWebService_iCreditGOC() {
        //Create request
        String METHOD_NAME = "iCreditGOC";
        String SOAP_ACTION = "http://www.maakki.com/iCreditGOC";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        maakki_id = SharedPreferencesHelper.getSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key0, "");
        //iCreditGOC(int maakki_id, int days, Int64 timeStamp, String identifyStr)
        timeStamp = String.valueOf(new Date().getTime());
        String encryptStr = maakki_id.trim() + "M@@kki.cc" + timeStamp.trim() + String.valueOf(days).trim();
        identifyStr = getHashCode(encryptStr).toUpperCase();
        request.addProperty("maakki_id", maakki_id);
        request.addProperty("days", days);
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
            currency = json_read.getString("currency");
            initialiCredit = json_read.getDouble("initialiCredit");
            endingiCredit = json_read.getDouble("endingiCredit");
            initialRate = json_read.getDouble("initialRate");
            endingRate = json_read.getDouble("endingRate");
            initialAmt = json_read.getDouble("initialAmt");
            endingAmt = json_read.getDouble("endingAmt");
            midtermIn = json_read.getDouble("midtermIn");
            midtermOut = json_read.getDouble("midtermOut");
            cost = json_read.getDouble("cost");
            income = json_read.getDouble("income");
            bonus = json_read.getDouble("bonus");
            revenue = json_read.getDouble("revenue");
            GOC = json_read.getDouble("GOC");
        } catch (Exception e) {
        }/**/
        //nickname=soapPrimitive.toString();
    }

    private String formatDoubleToString(Double d) {
        DecimalFormat df;
        BigDecimal a = new BigDecimal(d.toString());
        if (d > 10) {
            df = new DecimalFormat(",###,##0");
        } else if (d > 1) {
            df = new DecimalFormat(",###,##0.0");
        } else {
            df = new DecimalFormat(",###,##0.00");
        }
        df.format(a);
        return df.format(a);
    }

    private void setAnotherScene() {
        String content = "期末点数 = 期初点数 + 期中增加点数 - 期中减少点数\n\n"
                + "期中增加点数 = 期中转进点数(其他会员转点进来的点数) + 商店销售所收的点数\n\n"
                + "期中减少点数 = 期中转出点数(其他会员转点出去的点数) + 消费购物所花的点数 + 回收的点数\n\n"
                + "收益 = (期末点数 * 期末回收率 + 期中减少点数 * 减少当日回收率) - (期初点数 * 期初回收率 + 期中买进点数 * 买进当日回收率) + (系统给予的奖励点数 * 获得当日的回收率)\n\n"
                + "成本 = (期初点数 * 期初回收率 + 期中增加点数 * 增加当日回收率)\n\n"
                + "成本收益率 GOC = (收益 / 成本) * 100 %\n\n"
                + "所有数字系基于市场乐观发展所作的估算，不代表您实际拥有。更多说明请参考服务条款。";
        tv_content2 = (TextView) findViewById(R.id.tv_content2);
        tv_content2.setText(content);
        tv_content2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAScene();
            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAScene();
            }
        });
        mSceneRoot.startAnimation(startAnimation);
        mSceneRoot.setVisibility(View.VISIBLE);

        //scenellayout=(LinearLayout)findViewById(R.id.scenellayout);
        //scenellayout.startAnimation(startAnimation);
        //scenellayout.setVisibility(View.VISIBLE);
        //tv_question=(TextView) findViewById(R.id.tv_question);
        //tv_question.setVisibility(View.GONE);
    }

    private void goAnotherScene() {
        SceneCode = 1;
        mSceneRoot.setVisibility(View.INVISIBLE);
        mATransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setAnotherScene();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
        TransitionManager.go(mAnotherScene, mATransition);
        //goToScene(mAnotherScene);
    }

    private void goAScene() {
        SceneCode = 0;
        mSceneRoot.setVisibility(View.INVISIBLE);
        mATransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setAScene();
                //mSceneRoot.startAnimation(startAnimation);
                //mSceneRoot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
        TransitionManager.go(mAScene, mATransition);
        //goToScene(mAScene);
    }

    private void goToScene(Scene scene) {
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(1000);
        Fade fadeOut = new Fade(Fade.OUT);
        fadeOut.setDuration(1000);
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.setDuration(1000);
        TransitionSet transition = new TransitionSet();
        transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        transition
                .addTransition(fadeOut)
                .addTransition(changeBounds)
                .addTransition(fadeIn);
        TransitionManager.go(scene, transition);
    }

    public double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    private class AsyncCallWS_iCreditGOC extends AsyncTask<String, Void, Void> {
        //TextView tv_nickname=(TextView) view.findViewById(R.id.text_nickname);

        @Override
        protected Void doInBackground(String... params) {
            //Log.i(TAG, "doInBackground");
            getWebService_iCreditGOC();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String Msg = "计算完成";

            if (!errMsg.equals("")) {
                Msg = errMsg;
                tv_goc.setText(Msg);
            } else {
                tv_goc.setText(formatDoubleToString(GOC) + " %");
                tv_gain.setText(formatDoubleToString(revenue) + " " + currency);
                tv_content1.setText(
                        "期末点数估值：" + formatDoubleToString(endingAmt) + " " + currency + "\n"
                                + "期初点数估值：" + formatDoubleToString(initialAmt) + " " + currency + "\n\n"
                                + "期末的回收率：" + formatDoubleToString(endingRate) + " %\n"
                                + "期初的回收率：" + formatDoubleToString(initialRate) + " %\n\n"
                                + "期中增加点数：" + formatDoubleToString(midtermIn) + " " + currency + "(i)\n"
                                + "期中减少点数：" + formatDoubleToString(midtermOut) + " " + currency + "(i)\n\n"
                                + "期中投入成本：" + formatDoubleToString(cost) + " " + currency + "\n"
                                + "期中取得收获：" + formatDoubleToString(income) + " " + currency
                );
                //if(SceneCode==0){
                tv_question.setVisibility(View.VISIBLE);
                scenellayout = (LinearLayout) findViewById(R.id.scenellayout);
                //scenellayout.startAnimation(startAnimation);
                scenellayout.setVisibility(View.VISIBLE);
                //}
                //Toast.makeText(getApplicationContext(),endingRate.toString(),Toast.LENGTH_LONG).show();
                SharedPreferencesHelper.putSharedPreferencesString(getApplicationContext(), SharedPreferencesHelper.SharedPreferencesKeys.key16, endingRate.toString());

            }

        }

        @Override
        protected void onPreExecute() {
            tv_gain.setText("");
            tv_goc.setText("");
            tv_question.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i(TAG, "onProgressUpdate");
        }
    }

    private String getIntervalDate(int days) {
        Calendar c = Calendar.getInstance();
        days++;
        c.add(Calendar.DATE, days);
        Date d = c.getTime();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy.MM.dd");
        return dt.format(d) + "~" + dt.format(new Date());
    }
    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }
    public void SlideToDown(final View view) {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -5.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
        });
    }

}