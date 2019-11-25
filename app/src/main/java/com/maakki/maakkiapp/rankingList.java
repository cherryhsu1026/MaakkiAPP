package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class rankingList {
    private String area,locality,sublocality;
    private int total,count,amt_avg;

    public int getTotal(){ return total; }
    public void setTotal(int total){ this.total=total; }

    public int getCount(){ return count; }
    public void setCount(int count){ this.count=count; }

    public int getAmt_avg (){ return amt_avg; }
    public void setAmt_avg(int amt_avg){ this.amt_avg=amt_avg; }

    public String getArea(){ return area; }
    public void setArea(String area){ this.area=area; }

    public String getLocality() { return locality; }
    public void setLocality(String locality){ this.locality=locality; }

    public String getSublocality() { return sublocality; }
    public void setSublocality(String sublocality){ this.sublocality=sublocality;}
}
