package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class AllySponsorRecord {
    private int maakki_id,icredit;
    private long id,createdate;
    private String uniqueallyId;
    public AllySponsorRecord(){
    }
    //serial id
    public long getId(){ return id; }
    public void setId(long id){ this.id=id;}
    //联盟独特id
    public String getUniqueAllyId(){ return uniqueallyId;}
    public void setUniqueAllyId(String uniqueallyId){ this.uniqueallyId=uniqueallyId;}
    //maakkiid
    public int getMaakki_id(){ return maakki_id; }
    public void setMaakki_id(int maakki_id){ this.maakki_id=maakki_id; }
    //USD(i)
    public int getiCredit() { return icredit; }
    public void setiCredit(int icredit){ this.icredit=icredit; }
    //赞助时间
    public Long getCreate_date() { return createdate; }
    public void setCreate_date(Long lastchecktime){ this.createdate=lastchecktime; }
}
