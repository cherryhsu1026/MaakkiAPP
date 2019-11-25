package com.maakki.maakkiapp;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.ByteArrayOutputStream;

/**
 * Created by ryan on 2016/8/16.
 */
public class Ally {
    private String uniqueallyId,iconpath,ally_name,kickstarttime,token;
    private int founder_id,donationamt,ally_status;
    private long id,lastmodifytime;
    public Ally(){
    }
    //serial id
    public long getId(){ return id; }
    public void setId(long id){ this.id=id;}
    //联盟独特id
    public String getUniqueAllyId(){ return uniqueallyId;}
    public void setUniqueAllyId(String uniqueallyId){ this.uniqueallyId=uniqueallyId;}
    //联盟名称
    public String getAlly_name(){return ally_name;}
    public void setAlly_name(String ally_name){this.ally_name=ally_name;}
    //联盟图案
    public String getIconpath(){return iconpath;}
    public void setIconpath(String iconpath){this.iconpath=iconpath;}

    //盟主id
    public int getFounder_id(){ return founder_id; }
    public void setFounder_id(int founder_id){ this.founder_id=founder_id;}
    //赞助金额
    public int getDonationAmt() { return donationamt; }
    public void setDonationAmt(int donationamt){ this.donationamt=donationamt;}
    //每天几点发动
    public String getKickStartTime() { return kickstarttime; }
    public void setKickStartTime(String kickstarttime){ this.kickstarttime=kickstarttime;}
    //1：接受申请 2.不接受申请，只能由团长邀请 3.已经解散
    public  int getAllyStatus() { return ally_status; }
    public void setAllyStatus(int ally_status){ this.ally_status=ally_status; }
    //创建日期
    public Long getLastModifyTime() { return lastmodifytime; }
    public void setLastModifyTime(Long lastmodifytime){ this.lastmodifytime=lastmodifytime; }
    //
    public String getToken() { return token; }
    public void setToken(String token){ this.token=token; }
}
