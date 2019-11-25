package com.maakki.maakkiapp;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.ByteArrayOutputStream;

/**
 * Created by ryan on 2016/8/16.
 */
public class ActivityScoreRecord {

    private int score,score_type;
    private String identifier;
    private long id,createtime;

    public ActivityScoreRecord(){
    }
    //serial id
    public long getId(){ return id; }
    public void setId(long id){ this.id=id;}
    //
    public int getScore(){ return score; }
    public void setScore(int score){ this.score=score;}

    //scoreType
    //1:发红包：public 每 USD(i)得到 100K
    //2:交新朋友 （负数代表失去新朋友）
    //3:920购点 每 USD(i) 得到 5K
    public int getScoreType(){ return score_type;}
    public void setScoreType(int score_type){ this.score_type=score_type;}
    //辨证码
    public String getIdentifier(){ return identifier;}
    public void setIdentifier(int String){ this.identifier=identifier;}
    //创建日期
    public Long getCreateTime() { return createtime; }
    public void setCreateTime(Long createtime){ this.createtime=createtime; }

}
