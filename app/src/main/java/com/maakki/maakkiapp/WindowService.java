package com.maakki.maakkiapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class WindowService extends Service {
    //private final String TAG = this.getClass().getSimpleName();
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View mWindowView;
    ImageView iv_ChatIcon;
    TextView tv_msg;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private String getType,cMemID,cName,nMessage;
    Long ally_id;
    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i(TAG, "onCreate");
        initWindowParams();
        initView();
        initClick();
    }

    /**
     * 是否拦截
     * @return true:拦截;false:不拦截.
     */
    private boolean needIntercept() {
        if (Math.abs(mStartX - mEndX) > 30 || Math.abs(mStartY - mEndY) > 30) {
            return true;
        }
        return false;
    }

    private void initWindowParams() {
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        // 更多type：https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#TYPE_PHONE
        if (Build.VERSION.SDK_INT < 26) {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        //wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmParams.format = PixelFormat.TRANSLUCENT;
        // 更多falgs:https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_NOT_FOCUSABLE
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        //wmParams.gravity = Gravity.CENTER;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = 330;
        wmParams.y = 25;
        /*wmParams.x = 0;
        wmParams.y = 0;*/
    }

    private void initView() {
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.layout_window, null);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        tv_msg = (TextView) mWindowView.findViewById(R.id.tv_msg);
        iv_ChatIcon = (ImageView) mWindowView.findViewById(R.id.iv_ChatIcon);
        tv_msg.startAnimation(myAnim);
        iv_ChatIcon.startAnimation(myAnim);
    }

    private void addWindowView2Window() {
        //if(!((Activity) getApplicationContext()).isFinishing()){
            //show dialog
            try {
                mWindowManager.addView(mWindowView, wmParams);
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),"Errmsg:"+e.toString(),Toast.LENGTH_LONG).show();
            }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            getType = bundle.getString("getType");
            cMemID = bundle.getString("cMemID");
            cName = bundle.getString("cName");
            ally_id= bundle.getLong("ally_id");
            nMessage=bundle.getString("nMessage");
        }else{
            getType="";
        }
        String[] classname_no_Window={"FriendList","Chat_red"};
        //if(!isSpecificClassAtBackground(getApplicationContext(),classname_no_Window)){
             addWindowView2Window();
        //}
        if(getType.equals("StartAllySponsor")){
            tv_msg.setText("联盟赞助准备开打？");
            tv_msg.setVisibility(View.VISIBLE);
            iv_ChatIcon.setVisibility(View.GONE);
        }else{
            iv_ChatIcon.setVisibility(View.VISIBLE);
            tv_msg.setText("");
            tv_msg.setVisibility(View.GONE);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowView  != null) {
            //移除悬浮窗口
            //Log.i(TAG, "removeView");
            mWindowManager.removeView(mWindowView);
        }
        //Log.i(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void initClick() {
        iv_ChatIcon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        //mPercentTv.setText("DOWN/"+mStartX+"."+mStartY);
                        /*wmParams.x = mStartX - mWindowView.getMeasuredWidth() / 2;
                        wmParams.y = mStartY;
                        mWindowManager.updateViewLayout(mWindowView, wmParams);*/
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mEndX = (int) event.getRawX();
                        mEndY = (int) event.getRawY();
                        if (needIntercept()) {
                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            /*wmParams.x = (int) event.getRawX() - mWindowView.getMeasuredWidth() / 2;
                            wmParams.y = (int) event.getRawY() - mWindowView.getMeasuredHeight() / 2;*/
                            wmParams.x = mEndX-mStartX+330;
                            wmParams.y = mEndY-mStartY+30;
                        mWindowManager.updateViewLayout(mWindowView, wmParams);
                            //return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        /*if (needIntercept()) {
                            return true;
                        }*/
                        //wmParams.x = mEndX-mStartX+330;
                        //wmParams.y = mEndY-mStartY+30;
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(WindowService.this,FriendList.class);
                i= new Intent(WindowService.this,AllyActivity.class);
                i.putExtra("ally_id",ally_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                mWindowManager.removeView(mWindowView);
            }
        });
        iv_ChatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i= new Intent(WindowService.this,FriendList.class);
                    if(getType.equals("InviteFriend")){
                        Bundle bundle=new Bundle();
                        bundle.putString("getType", getType);
                        bundle.putString("cName", cName);
                        bundle.putString("cMemID",cMemID);
                        i.putExtras(bundle);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }else if(getType.equals("getRedEnvelope_NotFinished")){
                        Bundle bundle=new Bundle();
                        //bundle.putString("getType", getType);
                        bundle.putBoolean("isRedenvelope", true);
                        bundle.putString("sendernickname", cName);
                        bundle.putString("Contactid", cMemID);
                        bundle.putString("nMessage", nMessage);
                        i = new Intent(getApplicationContext(), PreNotificationList.class);
                        i.putExtras(bundle);
                    }else if(getType.equals("StartAllySponsor")){
                        i= new Intent(WindowService.this,AllyActivity.class);
                        i.putExtra("ally_id",ally_id);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    startActivity(i);
                    mWindowManager.removeView(mWindowView);
            }
        });
    }
    private boolean isSpecificClassAtBackground(final Context context,final String[] classname) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            //if (!topActivity.getPackageName().equals(context.getPackageName())) {
                //if(Arrays.asList(classname).contains(topActivity.getClassName().split(".")[3])){
            String s=topActivity.getClassName();
            boolean contains = false;
            for(int i=0;i<classname.length;i++){
                if(s.contains(classname[i])){
                    contains=true;
                }
            }
            return contains;
                //}
            //}
        }
        return false;
    }

}
