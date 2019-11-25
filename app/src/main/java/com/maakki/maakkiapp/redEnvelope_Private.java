package com.maakki.maakkiapp;

public class redEnvelope_Private {
    private String currency,slogan;

    private Boolean isAverage,isAnonymous,isFinish,isFriendLimited;
    private Double donateAmt,takenAmt;
    private String iCreditPassword,picfile,urllink;
    private int sender_id,receiver_id,receiver_no;
    private long id,create_date;

    public redEnvelope_Private() {}

    public void setId( long id){this.id=id;}
    public long getId(){return id;}

    public void setSender_id(int sender_id){this.sender_id=sender_id;}
    public int getSender_id(){return sender_id;}
    //当设定了receiver_no,此栏可以忽略
    public void setReceiver_id(int receiver_id){this.receiver_id=receiver_id;}
    public int getReceiver_id(){return receiver_id;}
    //一律是美金点
    public String getiCreditPassword() {
        return iCreditPassword;
    }
    public void setiCreditPassword(String iCreditPassword) {
        this.iCreditPassword = iCreditPassword;
    }
    //红包个数：当设定了receiver_id,此栏可忽略
    public void setReceiver_no(int receiver_no){this.receiver_no=receiver_no;}
    public int getReceiver_no(){return receiver_no;}

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSlogan() {
        return slogan;
    }
    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getPicfile() {
        return picfile;
    }
    public void setPicfile(String picfile) {
        this.picfile = picfile;
    }

    public String getUrllink() {
        return urllink;
    }
    public void setUrllink(String urllink) {
        this.urllink = urllink;
    }

    public Boolean getIsFriendLimited() {
        return isFriendLimited;
    }
    public void setIsIsFriendLimited(Boolean isFriendLimited) {
        this.isFriendLimited = isFriendLimited;
    }
    //当设定了receiver_id,此栏可忽略不计
    public Boolean getIsAverage() {
        return isAverage;
    }
    public void setIsAverage(Boolean isAverage) {
        this.isAverage = isAverage;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Boolean getIsFinish() {
        return isFinish;
    }
    public void setIsFinish(Boolean isFinish) {
        this.isFinish = isFinish;
    }

    public Double getDonateAmt() {
        return donateAmt;
    }
    public void setDonateAmt(Double donateAmt) {
        this.donateAmt = donateAmt;
    }

    public Double getTakenAmt() {
        return takenAmt;
    }
    public void setTakenAmt(Double takenAmt) {
        this.takenAmt = takenAmt;
    }

    public long getCreateDate() {
        return create_date;
    }
    public void setCreateDate(long create_date) {
        this.create_date = create_date;
    }
}
