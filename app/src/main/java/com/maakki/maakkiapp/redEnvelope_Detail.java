package com.maakki.maakkiapp;

public class redEnvelope_Detail {
    private String currency,reply;
    private Integer redenvelope_id,maakkiid;
    private Double receivedAmt;
    private long id,create_date;

    public redEnvelope_Detail() {}

    public void setId( long id){this.id=id;}
    public long getId(){return id;}

    public void setMaakkiid(int maakkiid){this.maakkiid=maakkiid;}
    public int getMaakkiid(){return maakkiid;}

    public void setRedenvelope_id(int redenvelope_id){this.redenvelope_id=redenvelope_id;}
    public int getRedenvelope_id(){return redenvelope_id;}

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReply() {
        return reply;
    }
    public void setReply(String reply) {
        this.reply = reply;
    }

    public Double getReceivedAmt() {
        return receivedAmt;
    }
    public void setReceivedAmt(Double receivedAmt) {
        this.receivedAmt = receivedAmt;
    }

    public long getCreateDate() {
        return create_date;
    }
    public void setCreateDate(long create_date) {
        this.create_date = create_date;
    }
}
