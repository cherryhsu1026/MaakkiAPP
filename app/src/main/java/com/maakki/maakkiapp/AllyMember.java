package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class AllyMember {
    private int maakki_id,member_status,order;
    private long id,lastchecktime;
    private boolean isSkip;
    private String uniqueallyId;
    public AllyMember(){
    }
    //serial id
    public long getId(){ return id; }
    public void setId(long id){ this.id=id;}
    //联盟独特id
    public String getUniqueAllyId(){ return uniqueallyId;}
    public void setUniqueAllyId(String uniqueallyId){ this.uniqueallyId=uniqueallyId;}
    //盟主id
    public int getMaakki_id(){ return maakki_id; }
    public void setMaakki_id(int maakki_id){ this.maakki_id=maakki_id; }
    //0:FG盟主 1:点数盟主 11.资料管理员 12.一般会员 13.邀请中 14.申请中 15.已拒绝（可接受再申请）16.申请退出 17.已经退出 99.黑名单（拒绝再接受申请）
    //除了盟主之外，前两名都是 Data_keeper，负责保管资料
    public int getMember_status() { return member_status; }
    public void setMember_status(int member_status){ this.member_status=member_status; }
    //赞助的顺序
    public int getOrder() { return order; }
    public void setOredr(int order){ this.order=order; }
    //是否跳过赞助
    public boolean getIsSkip() { return isSkip; }
    public void setIsSkip(boolean isSkip){ this.isSkip=isSkip; }
    //最近赞助时间
    public Long getLastCheckTime() { return lastchecktime; }
    public void setLastCheckTime(Long lastchecktime){ this.lastchecktime=lastchecktime; }
}
