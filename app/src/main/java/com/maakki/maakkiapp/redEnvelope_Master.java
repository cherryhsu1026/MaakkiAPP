package com.maakki.maakkiapp;

public class redEnvelope_Master {
    private String currency,slogan;
    private Integer receiver_no;
    private Boolean isAverage,isAnonymous,isFinish,isFriendLimited;
    private Double donateAmt,takenAmt;
    private String iCreditPassword,picfile,urllink;
    private long id,create_date;

    public redEnvelope_Master() {}

    public void setId( long id){this.id=id;}
    public long getId(){return id;}
    //红包数目
    public void setReceiver_no(int receiver_no){this.receiver_no=receiver_no;}
    public int getReceiver_no(){return receiver_no;}

    public String getiCreditPassword() {
        return iCreditPassword;
    }
    public void setiCreditPassword(String iCreditPassword) {
        this.iCreditPassword = iCreditPassword;
    }

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
