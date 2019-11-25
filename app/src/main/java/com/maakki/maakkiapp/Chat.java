package com.maakki.maakkiapp;

/**
 * Created by ryan on 2018/12/28.
 */

public class Chat {
    private String contenttext,filepath,uniquegroupId;
    private int senderid, receiverid,filetype;
    private Long id,create_date;

    public Chat(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getSenderid() {return senderid;}
    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public int getReceiverid() {
        return receiverid;
    }
    public void setReceiverid(int receiverid) {
        this.receiverid = receiverid;
    }

    public String getContentText() {
        return contenttext;
    }
    public void setContentText(String contenttext) {
        this.contenttext = contenttext;
    }

    public String getFilePath(){return filepath;}
    public void setFilePath(String filepath){this.filepath=filepath;}

    public int getFileType(){return filetype;}
    public void setFileType(int filetype){this.filetype=filetype;}

    //Integer.toHexString(int_value);Founder's id_timeStamp(GMT+8:00)
    public String getUniqueGroupId(){ return uniquegroupId;}
    public void setUniqueGroupId(String uniquegroupId){ this.uniquegroupId=uniquegroupId;}

    public Long getCreateDate() {
        return create_date;
    }
    public void setCreateDate(Long create_date) {this.create_date = create_date;
    }
}
